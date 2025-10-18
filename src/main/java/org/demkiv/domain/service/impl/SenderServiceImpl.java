package org.demkiv.domain.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.demkiv.domain.Config;
import org.demkiv.domain.FindMeServiceException;
import org.demkiv.domain.Language;
import org.demkiv.domain.architecture.EntitySender;
import org.demkiv.domain.service.SenderService;
import org.demkiv.persistance.dao.PersonRepository;
import org.demkiv.persistance.dao.SubscriptionsRepository;
import org.demkiv.persistance.entity.Finder;
import org.demkiv.persistance.entity.Person;
import org.demkiv.persistance.entity.SubscriptionStatus;
import org.demkiv.persistance.entity.Subscriptions;
import org.demkiv.web.model.EmailModel;
import org.demkiv.web.model.form.EmailForm;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SenderServiceImpl implements SenderService {

    private final MessageSource messageSource;

    private final Config config;
    private final PersonRepository personRepository;
    @Qualifier("emailSender")
    private final EntitySender<Boolean, EmailModel> emailSender;
    private final SubscriptionsRepository subscriptionsRepository;

    @Override
    public void sendEmail(EmailForm emailForm) {
        long personId = Long.parseLong(emailForm.personId());
        Person foundPerson = personRepository.findById(personId)
                .orElseThrow(() -> new FindMeServiceException("Person with id " + personId + " not found"));
        String finderEmail = foundPerson.getFinder().getEmail();
        if (StringUtils.isEmpty(finderEmail)) {
            log.error("Email could not be found");
            return;
        }

        EmailModel emailModel = EmailModel.builder()
                .emailFrom(config.getEmailFrom())
                .emailTo(finderEmail)
                .body(emailForm.body()+ "\n" +
                        emailForm.footer())
                .subject(emailForm.subject())
                .build();
        emailSender.send(emailModel);
        log.info("Email sent to {}. For person {}", finderEmail, foundPerson.getFullname());
    }

    @Override
    public boolean emailConfirmation(String token) {
        Optional<Subscriptions> foundSubscription = subscriptionsRepository.findByToken(token);
        if (foundSubscription.isPresent()) {
            Subscriptions subscription = foundSubscription.get();
            subscription.setStatus(SubscriptionStatus.CONFIRMED);
            subscriptionsRepository.save(subscription);
            return true;
        }
        return false;
    }

    @Override
    public void handleComplaintNotification(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(message);
            String notificationType = jsonNode.get("Type").asText();

            if ("SubscriptionConfirmation".equals(notificationType)) {
                String subscribeURL = jsonNode.get("SubscribeURL").asText();
                confirmSubscription(subscribeURL);
            } else if ("Notification".equals(notificationType)) {
                JsonNode messageNode = objectMapper.readTree(jsonNode.get("Message").asText());

                if (messageNode.has("complaint")) {
                    JsonNode complaintData = messageNode.path("complaint");
                    JsonNode complaintRecipients = complaintData.path("complainedRecipients");
                    for (JsonNode recipient : complaintRecipients) {
                        String email = recipient.path("emailAddress").asText();
                        processComplaint(email);
                    }
                }

                if (messageNode.has("bounce")) {
                    JsonNode bounceData = messageNode.path("bounce");
                    JsonNode bouncedRecipients = bounceData.path("bouncedRecipients");
                    for (JsonNode recipient : bouncedRecipients) {
                        String email = recipient.path("emailAddress").asText();
                        processBounce(email);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error while processing SNS notification", e);
        }
    }

    private void processComplaint(String email) {
        Optional<Subscriptions> subscriptionOpt = subscriptionsRepository.findByEmail(email);
        subscriptionOpt.ifPresent(subscription -> {
            subscription.setStatus(SubscriptionStatus.COMPLAINED);
            subscriptionsRepository.save(subscription);
            log.info("Complaint status is set to email {}", email);
        });
    }

    private void confirmSubscription(String subscribeURL) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForObject(subscribeURL, String.class);
        log.info("Subscription confirmation sent to SNS. Url {}", subscribeURL);
    }

    private void processBounce(String email) {
        Optional<Subscriptions> opt = subscriptionsRepository.findByEmail(email);
        opt.ifPresent(sub -> {
            sub.setStatus(SubscriptionStatus.UNSUBSCRIBED);
            subscriptionsRepository.save(sub);
            log.info("Bounce status is set to email {}", email);
        });
    }

    @Override
    public boolean sendPersonSubscriptionNotification(String personId, String lang) {
        return personRepository.findById(Long.parseLong(personId))
                .map(Person::getFinder)
                .filter(finder -> !StringUtils.isEmpty(finder.getEmail()))
                .map(Finder::getEmail)
                .map(email -> sendSubscriptionNotification(email, lang))
                .orElse(false);
    }

    @Override
    public boolean sendSubscriptionNotification(String email, String lang) {
        Locale locale = new Locale(Language.getLanguageCodeByName(lang));
        String SUBSCRIPTION_TEXT = messageSource.getMessage("email.subscription.text", null, locale);
        Optional<Subscriptions> foundSubscription = subscriptionsRepository.findByEmail(email);
        Subscriptions subscription = foundSubscription.orElse(null);
        if (foundSubscription.isPresent() && subscription.getStatus() == SubscriptionStatus.CONFIRMED) {
            return true;
        }

        String token = UUID.randomUUID().toString();
        String confirmationLink = config.getDomain() + "/findme/api/subscription/confirm?token=" + token;
        String emailBody = String.format(SUBSCRIPTION_TEXT, confirmationLink);
        try {
            sendConfirmationMail(emailBody, email, lang);
        }  catch (Exception e) {
            log.error("Error while sending confirmation mail {}", email, e);
            return false;
        }

        if (foundSubscription.isEmpty()) {
            storeSubscriptionToDB(token, email);
        } else {
            subscription.setToken(token);
            subscription.setEmail(email);
            subscription.setStatus(SubscriptionStatus.PENDING);
            subscriptionsRepository.save(subscription);
            log.info("Subscription is updated in database {}", subscription);
        }
        return true;
    }

    private void sendConfirmationMail(String emailBody, String email, String lang) {
        Locale locale = new Locale(Language.getLanguageCodeByName(lang));
        String subject = messageSource.getMessage("email.confirmation.subject", null, locale);
        EmailModel emailModel = EmailModel.builder()
                .emailFrom(config.getEmailFrom())
                .emailTo(email)
                .subject(subject)
                .body(emailBody)
                .build();
        emailSender.send(emailModel);
    }

    private void storeSubscriptionToDB(String token, String email) {
        Subscriptions subscriptions = Subscriptions.builder()
                .token(token)
                .email(email)
                .status(SubscriptionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        subscriptionsRepository.save(subscriptions);
        log.info("Subscription has been stored to database {}", subscriptions);
    }
}
