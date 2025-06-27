package dev.m4yd3.tn_bot.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Entity
@Table(name = "settings")
@NoArgsConstructor
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "settings_id_gen")
    @SequenceGenerator(name = "settings_id_gen", sequenceName = "settings_setting_id_seq", allocationSize = 1)
    @Column(name = "setting_id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @Column(name = "value", nullable = false, length = Integer.MAX_VALUE)
    private String value;

    @Enumerated
    @Column(name = "type", nullable = false)
    private Type type;

    Setting(final String name, final String value, final Type type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public enum Type {INTEGER, PATTERN, DURATION}
}