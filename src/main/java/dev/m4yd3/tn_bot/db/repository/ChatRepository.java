package dev.m4yd3.tn_bot.db.repository;

import dev.m4yd3.tn_bot.db.entity.Chat;
import dev.m4yd3.tn_bot.model.ChatCO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends CrudRepository<Chat, Long> {

    @Query(
            value = "select chat_id, title, is_admin, coalesce(users, '{}') as users from chats left join (select chat_id, array_agg(user_id) as users from users_chats join users using (user_id) group by chat_id) as ucc using (chat_id) order by is_admin desc, title",
            nativeQuery = true
    )
    List<ChatCO> getChatsForAdmin();

    @Query(value = "select c from Chat c where c.id = :chatId order by c.isAdmin desc, c.title")
    Optional<Chat> getChatForAdmin(Long chatId);

    Optional<Chat> findByTelegramId(Long chatTelegramId);

    @Query(value = "select * from chats join users_chats using(chat_id) where user_id = :userId", nativeQuery = true)
    List<Chat> getChatsForUser(Long userId);

    Slice<Chat> findAllByInviteLinkIsNotNullAndIsAdminTrueOrderByTitle(Pageable pageable);
}
