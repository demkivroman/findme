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
import org.demkiv.persistance.entity.PersonStatus;
import org.demkiv.persistance.model.dto.PersonDTO;
import org.demkiv.persistance.model.dto.PhotoDTO;
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
import java.util.function.Predicate;

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
                .filter(Predicate.not(this::isFound))
                .map(this::convertPersonToPayedPersonInfoModel)
                .toList();
    }

    private boolean isFound(Person  person) {
        PersonStatus status = person.getPersonStatus();
        return status.isFound();
    }

    private PayedPersonInfoModel convertPersonToPayedPersonInfoModel(Person person) {
        PersonStatus status = person.getPersonStatus();
        List<PhotoDTO> photos = person.getPhotos()
                .stream()
                .map(entity -> {
                    PhotoDTO photoDTO = PhotoDTO.builder().build();
                    photoDTO.setId(Long.valueOf(String.valueOf(entity.getId())));
                    photoDTO.setUrl(entity.getUrl());
                    return photoDTO;
                })
                .toList();
        LocalDate birthDay = Objects.isNull(person.getBirthday())? null : person.getBirthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int age = Objects.isNull(birthDay) ? 0 : LocalDate.now().getYear() - birthDay.getYear();
        String[] createdAtArr = status.getCreatedAt().toString().split("T");
        PersonDTO personDTO = PersonDTO.builder()
                .id(Long.parseLong(String.valueOf(person.getId())))
                .fullName(person.getFullname())
                .birthday(person.getBirthday()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .toString())
                .description(person.getDescription())
                .age(age)
                .date(createdAtArr[0])
                .time(createdAtArr[1])
                .build();
        return PayedPersonInfoModel.builder()
                .person(personDTO)
                .photo(photos)
                .build();
    }
}
