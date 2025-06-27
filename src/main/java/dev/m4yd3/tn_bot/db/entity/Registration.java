package dev.m4yd3.tn_bot.db.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

import static dev.m4yd3.tn_bot.db.entity.Registration.State.READY;

@Data
@NoArgsConstructor
@Entity
@Table(name = "registrations")
public class Registration {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "registrations_id_gen")
    @SequenceGenerator(name = "registrations_id_gen", sequenceName = "registrations_registration_id_seq", allocationSize = 1)
    @Column(name = "registration_id", nullable = false)
    private Long id;

    @Column(name = "telegram_id", nullable = false)
    private Long telegramId;

    @ColumnDefault("LOCALTIMESTAMP")
    @Column(name = "first_attempt_at", nullable = false)
    @CreationTimestamp
    private Instant firstAttemptAt;

    @Enumerated
    @ColumnDefault("0")
    @Column(name = "state", nullable = false)
    private State state;

    @Column(name = "code", length = 6)
    private String code;

    @Column(name = "email", length = Integer.MAX_VALUE)
    private String email;

    @Column(name = "blocked_at")
    private Instant blockedAt;

    @ColumnDefault("0")
    @Column(name = "email_attempts")
    private Integer emailAttempts;

    @ColumnDefault("0")
    @Column(name = "code_attempts")
    private Integer codeAttempts;

    public Registration(Long telegramId) {
        this.telegramId = telegramId;
        this.state = READY;
        this.firstAttemptAt = Instant.now();
        this.emailAttempts = 0;
        this.codeAttempts = 0;
    }

    public void reset() {
        this.state = READY;
        this.firstAttemptAt = Instant.now();
        this.blockedAt = null;
        this.emailAttempts = 0;
        this.codeAttempts = 0;
        this.email = null;
        this.code = null;
    }

    public void incrementEmailAttempts() {
        this.emailAttempts = this.emailAttempts == null ? 1 : this.emailAttempts + 1;
    }

    public void incrementCodeAttempts() {
        this.codeAttempts = this.codeAttempts == null ? 1 : this.codeAttempts + 1;
    }

    public enum State {READY, EMAIL, CODE, BLOCKED}
}