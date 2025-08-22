package org.demkiv.persistance.dao;

import org.demkiv.persistance.entity.Subscriptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionsRepository extends JpaRepository<Subscriptions, Long> {

    Optional<Subscriptions> findByEmail(String email);
}
