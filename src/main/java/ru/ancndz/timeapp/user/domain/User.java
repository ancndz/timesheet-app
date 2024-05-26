package ru.ancndz.timeapp.user.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.ancndz.timeapp.core.domain.AbstractEntity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * Сущность пользователя.
 *
 * @author Anton Utkaev
 * @since 2024.05.10
 */
@Entity
@Table(name = "timetable_users")
public class User extends AbstractEntity implements UserDetails, CredentialsContainer {

    @Column
    private String username;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<GrantedAuthority> authorities;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @PrimaryKeyJoinColumn
    private UserInfo userInfo;

    public User() {
    }

    public User(final UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void addRole(final GrantedAuthority role) {
        authorities.add(role);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String email) {
        this.username = email;
    }

    public void setAuthorities(Set<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }

    public static Builder newUser() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User that = (User) o;
        return Objects.equals(getId(), that.getId())
                && Objects.equals(username, that.username)
                && Objects.equals(userInfo, that.userInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), username, userInfo);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", User.class.getSimpleName() + "[", "]").add("id='" + getId() + "'")
                .add("email=" + username)
                .add("authorities=" + authorities)
                .add("scheduleUser=" + userInfo)
                .add("isNew=" + isNew())
                .toString();
    }

    public static class Builder {

        private String password;

        private String username;

        private Set<GrantedAuthority> authorities = new HashSet<>();

        private UserInfo userInfo;

        private Builder() {
        }

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder withAuthorities(Set<GrantedAuthority> authorities) {
            this.authorities = authorities;
            return this;
        }

        public Builder withUserInfo(UserInfo userInfo) {
            this.userInfo = userInfo;
            return this;
        }

        public User build() {
            final User user = new User();

            user.setUsername(this.username);
            if (this.password != null) {
                user.setPassword(this.password);
            }
            user.setAuthorities(this.authorities);
            user.setUserInfo(this.userInfo);
            user.setId(this.userInfo != null ? this.userInfo.getId() : UUID.randomUUID().toString());
            user.setNew(true);

            return user;
        }
    }
}
