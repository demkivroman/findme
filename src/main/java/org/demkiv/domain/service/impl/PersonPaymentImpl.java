package org.demkiv.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.FindMeServiceException;
import org.demkiv.domain.architecture.EntitySaver;
import org.demkiv.persistance.dao.PaymentRepository;
import org.demkiv.persistance.dao.PersonRepository;
import org.demkiv.persistance.entity.Person;
import org.demkiv.persistance.entity.PersonPayment;
import org.demkiv.web.model.form.PaymentForm;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PersonPaymentImpl implements EntitySaver<PaymentForm, Boolean> {

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
}
