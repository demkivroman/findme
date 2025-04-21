package org.demkiv.persistance.dao;

import org.demkiv.persistance.entity.PersonPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PersonPayment, Integer> {

    @Override
    <S extends PersonPayment> S save(S entity);
}
