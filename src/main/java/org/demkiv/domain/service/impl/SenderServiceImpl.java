package org.demkiv.domain.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.Config;
import org.demkiv.domain.architecture.EntitySender;
import org.demkiv.domain.service.SenderService;
import org.demkiv.persistance.dao.PersonRepository;
import org.demkiv.persistance.dao.SubscriptionsRepository;
import org.demkiv.persistance.entity.Person;
import org.demkiv.persistance.entity.SubscriptionStatus;
import org.demkiv.persistance.entity.Subscriptions;
import org.demkiv.web.model.EmailModel;
import org.demkiv.web.model.form.EmailForm;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SenderServiceImpl implements SenderService {

    private Config config;
    private final PersonRepository personRepository;
    @Qualifier("emailSender")
    private final EntitySender<Boolean, EmailModel> emailSender;
    private final SubscriptionsRepository subscriptionsRepository;


    @Override
    public void sendEmail(EmailForm emailForm) {
        long personId = Long.parseLong(emailForm.getPersonId());
        Person foundPerson = personRepository.findById(personId).get();
        String finderEmail = foundPerson.getFinder().getEmail();
        String subject;
        if (emailForm.getSendMode().equalsIgnoreCase("email")) {
            subject = String.format("Email from Findme site on [%s]", foundPerson.getFullname());
        } else if (emailForm.getSendMode().equalsIgnoreCase("sms")) {
            subject = String.format("SMS from Findme site on [%s]", foundPerson.getFullname());
        } else {
            subject = "Email from Findme site.";
        }
        EmailModel emailModel = EmailModel.builder()
                .emailFrom(config.getEmailFrom())
                .emailTo(finderEmail)
                .body(emailForm.getBody())
                .subject(subject)
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
}
