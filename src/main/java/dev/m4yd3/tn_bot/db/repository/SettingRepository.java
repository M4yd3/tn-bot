package dev.m4yd3.tn_bot.db.repository;

import dev.m4yd3.tn_bot.db.entity.Setting;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingRepository extends CrudRepository<Setting, Long> {
    Optional<Setting> findByName(String name);
}
