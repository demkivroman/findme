package org.demkiv.domain.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.FindMeServiceException;
import org.demkiv.domain.architecture.EntityFinder;
import org.demkiv.domain.architecture.EntitySaver;
import org.demkiv.domain.util.FindmeUtil;
import org.demkiv.persistance.dao.PaymentRepository;
import org.demkiv.persistance.dao.PersonRepository;
import org.demkiv.persistance.entity.Person;
import org.demkiv.persistance.entity.PersonPayment;
import org.demkiv.persistance.entity.Thumbnail;
import org.demkiv.persistance.model.response.PayedPersonInfoModel;
import org.demkiv.web.model.form.PaymentForm;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class PersonPaymentImpl implements EntitySaver<PaymentForm, Boolean>, EntityFinder<Integer, List<?>> {

    private final PaymentRepository paymentRepository;
    private final PersonRepository personRepository;

    @Override
    public Boolean saveEntity(PaymentForm entity) {

        Optional<Person> person = personRepository.findById(entity.getPersonId());
        if (person.isPresent()) {
            Person personEntity = person.get();
            PersonPayment personPayment = PersonPayment.builder()
                    .payedAt(Instant.now())
                    .person(personEntity)
                    .days(entity.getDays())
                    .costPerDay(entity.getCostPerDay())
                    .build();
            paymentRepository.save(personPayment);
            log.info("Persisted payment event: personId - {}, days - {}", entity.getPersonId(), entity.getDays());
            return true;
        }

        throw new FindMeServiceException("Person not found");
    }

    @Override
    public List<?> findEntity(Integer limit) {
        List<Long> payedPersons = paymentRepository.getPayedPersons();
        Set<Long> randomIds = FindmeUtil.randomIds(payedPersons, limit);

        return randomIds.stream()
                .map(id -> personRepository.findById(id).orElseGet(null))
                .map(this::convertPersonToPayedPersonInfoModel)
                .toList();
    }

    private PayedPersonInfoModel convertPersonToPayedPersonInfoModel(Person person) {
        Optional<Thumbnail> thumbnail = person.getThumbnails().stream().findFirst();
        LocalDate birthDay = Objects.isNull(person.getBirthday())? null : person.getBirthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int age = Objects.isNull(birthDay) ? 0 : LocalDate.now().getYear() - birthDay.getYear();
        return PayedPersonInfoModel.builder()
                .id(person.getId())
                .fullName(person.getFullname())
                .birthDay(birthDay)
                .thumbnail(thumbnail.map(Thumbnail::getUrl).orElse(null))
                .age(age)
                .build();
    }
}
