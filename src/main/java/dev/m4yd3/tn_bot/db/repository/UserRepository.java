package dev.m4yd3.tn_bot.db.repository;

import dev.m4yd3.tn_bot.db.entity.User;
import dev.m4yd3.tn_bot.model.UserCO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    boolean existsByEmailAndIsActiveTrue(String email);

    boolean existsByTelegramIdAndIsActiveTrue(Long telegramId);

    Optional<User> findByEmailAndTelegramId(String email, Long telegramId);

    Optional<User> findUserIdByTelegramId(Long telegramId);

    Optional<User> findByTelegramId(Long telegramId);

    List<User> findAllByTelegramIdIn(List<Long> telegramIds);

    @Query(
            value = "select user_id, email, is_active, first_name, last_name, middle_name, registered_at, user_name, is_excluded from users join users_chats using (user_id) where chat_id = :chatId order by is_active desc, last_name, first_name, middle_name, user_name",
            nativeQuery = true
    )
    List<UserCO> findAllInChat(Long chatId);

    @Query(value = "select * from users join users_chats using(user_id) where chat_id = :chatId", nativeQuery = true)
    List<User> getUsersInChat(Long chatId);

    List<User> findAllByUserNameIn(Collection<String> userNames);
}
