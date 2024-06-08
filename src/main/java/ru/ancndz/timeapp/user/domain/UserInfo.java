package ru.ancndz.timeapp.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import ru.ancndz.timeapp.core.domain.AbstractEntity;

import java.time.LocalDate;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * Сущность информации о пользователе.
 *
 * @author Anton Utkaev
 * @since 2024.04.16
 */
@Entity
@Table(name = "timetable_user_info")
public class UserInfo extends AbstractEntity {

    @Column
    private String name;

    @Column
    @Email(regexp = ".+@.+\\..+")
    private String email;

    @Column
    private String phoneNumber;

    @Column(updatable = false)
    private LocalDate regDate;

    public UserInfo() {
    }

    public UserInfo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getRegDate() {
        return regDate;
    }

    public void setRegDate(LocalDate regDate) {
        this.regDate = regDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public static Builder newUserInfo() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserInfo that = (UserInfo) o;
        return Objects.equals(getId(), that.getId())
                && Objects.equals(name, that.name)
                && Objects.equals(email, that.email)
                && Objects.equals(phoneNumber, that.phoneNumber)
                && Objects.equals(regDate, that.regDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), name, email, phoneNumber, regDate);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", UserInfo.class.getSimpleName() + "[", "]").add("id='" + getId() + "'")
                .add("name='" + name + "'")
                .add("email='" + email + "'")
                .add("phoneNumber='" + phoneNumber + "'")
                .add("regDate=" + regDate)
                .toString();
    }

    public static class Builder {

        private String name;

        private String email;

        private String phoneNumber;

        private LocalDate regDate;

        private Builder() {
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder withRegDate(LocalDate regDate) {
            this.regDate = regDate;
            return this;
        }

        public UserInfo build() {
            final UserInfo userInfo = new UserInfo();
            userInfo.setId(UUID.randomUUID().toString());
            userInfo.setNew(true);
            userInfo.setName(name);
            userInfo.setEmail(email);
            userInfo.setPhoneNumber(phoneNumber);
            userInfo.setRegDate(this.regDate == null ? LocalDate.now() : this.regDate);
            return userInfo;
        }
    }
}
