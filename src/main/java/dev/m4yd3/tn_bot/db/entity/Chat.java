package dev.m4yd3.tn_bot.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chats")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chats_id_gen")
    @SequenceGenerator(name = "chats_id_gen", sequenceName = "chats_chat_id_seq", allocationSize = 1)
    @Column(name = "chat_id", nullable = false)
    private Long id;

    @Column(name = "telegram_id", nullable = false)
    private Long telegramId;

    @Column(name = "title", length = Integer.MAX_VALUE)
    private String title;

    @ColumnDefault("false")
    @Column(name = "is_admin", nullable = false)
    private Boolean isAdmin = false;

    @Column(name = "invite_link", length = Integer.MAX_VALUE)
    private String inviteLink;

    public Chat(Long telegramId) {
        this.telegramId = telegramId;
    }

    public Chat(Long telegramId, String title) {
        this.telegramId = telegramId;
        this.title = title;
    }

    public Chat(Long telegramId, String title, Boolean isAdmin) {
        this.telegramId = telegramId;
        this.title = title;
        this.isAdmin = isAdmin;
    }
}