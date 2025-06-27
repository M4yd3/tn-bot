package dev.m4yd3.tn_bot.db.repository;

import dev.m4yd3.tn_bot.db.entity.Registration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistrationRepository extends CrudRepository<Registration, Long> {
    Optional<Registration> findByTelegramId(Long telegramId);
}
