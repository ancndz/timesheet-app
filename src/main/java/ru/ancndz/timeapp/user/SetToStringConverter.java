package ru.ancndz.timeapp.user;

import jakarta.persistence.AttributeConverter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Конвертер для преобразования множества ролей в строку.
 *
 * @author Anton Utkaev
 * @since 2024.06.08
 */
public class SetToStringConverter implements AttributeConverter<Set<GrantedAuthority>, String> {

    public static final String DELIMITER = ";";

    @Override
    public String convertToDatabaseColumn(Set<GrantedAuthority> attribute) {
        return attribute.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(DELIMITER));
    }

    @Override
    public Set<GrantedAuthority> convertToEntityAttribute(String dbData) {
        return Set.of(dbData.split(DELIMITER))
                .stream()
                .filter(s -> !s.isEmpty())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}
