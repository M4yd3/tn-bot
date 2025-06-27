package dev.m4yd3.tn_bot.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tasks_id_gen")
    @SequenceGenerator(name = "tasks_id_gen", sequenceName = "tasks_task_id_seq", allocationSize = 1)
    @Column(name = "task_id", nullable = false)
    private Long id;

    @Column(name = "value", nullable = false, length = Integer.MAX_VALUE)
    private String value;

    @Enumerated
    @ColumnDefault("0")
    @Column(name = "type", nullable = false)
    private Type type;

    @ColumnDefault("LOCALTIMESTAMP")
    @Column(name = "started_at", nullable = false)
    @CreationTimestamp
    private Instant startedAt;

    @Column(name = "ends_at")
    private Instant endsAt;

    public Task(final String value, final Type type) {
        this.value = value;
        this.type = type;
    }

    public enum Type {USER_REGISTRATION, CHAT_REGISTRATION, EMPLOYEE_FIRING}
}