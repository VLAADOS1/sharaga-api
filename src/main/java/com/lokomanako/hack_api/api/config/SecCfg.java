package com.lokomanako.hack_api.api.config;

import com.lokomanako.hack_api.api.sec.AuthRlFlt;
import com.lokomanako.hack_api.api.sec.JwtFlt;
import com.lokomanako.hack_api.api.sec.SecDny;
import com.lokomanako.hack_api.api.sec.SecEnt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties({JwtCfg.class, AuthCfg.class})
public class SecCfg {

    @Autowired
    private JwtFlt jwtFlt;

    @Autowired
    private AuthRlFlt authRlFlt;

    @Autowired
    private SecEnt secEnt;

    @Autowired
    private SecDny secDny;

    @Bean
    public PasswordEncoder pe() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain fc(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(secEnt).accessDeniedHandler(secDny))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/auth/register",
                                "/api/v1/auth/login",
                                "/api/v1/auth/refresh",
                                "/api/v1/auth/logout"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(authRlFlt, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFlt, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
