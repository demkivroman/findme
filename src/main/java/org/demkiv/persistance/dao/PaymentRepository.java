package org.demkiv.persistance.dao;

import org.demkiv.persistance.entity.PersonPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<PersonPayment, Integer> {

    @Override
    <S extends PersonPayment> S save(S entity);

    @Query(value = "select person_id from person_payment where date_add(person_payment.payedAt, interval person_payment.days day) >= now()", nativeQuery = true)
    List<Long> getPayedPersons();
}
