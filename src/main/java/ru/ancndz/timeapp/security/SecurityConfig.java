package ru.ancndz.timeapp.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.ancndz.timeapp.ui.view.LoginView;

/**
 * Конфигурация безопасности.
 * 
 * @author Anton Utkaev
 * @since 2024.05.14
 */
@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    private @Value("remember-me.cookie.name") String rememberMe;

    private @Value("server.servlet.session.cookie.name") String sessionCookie;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                auth -> auth.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/images/*.png"))
                        .permitAll())
                .rememberMe(conf -> {
                    conf.rememberMeCookieName(rememberMe);
                })
                .logout(logout -> logout.deleteCookies(sessionCookie));
        super.configure(http);
        setLoginView(http, LoginView.class);
    }

}