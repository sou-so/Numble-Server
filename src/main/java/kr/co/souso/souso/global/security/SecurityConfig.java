package kr.co.souso.souso.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.souso.souso.global.error.CustomAuthenticationEntryPoint;
import kr.co.souso.souso.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsUtils;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors()
                .and()
                .csrf().disable()
                .formLogin().disable()

                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()

                // users
                .antMatchers(HttpMethod.HEAD, "/users/nickname").permitAll()
                .antMatchers(HttpMethod.POST, "/users").permitAll()
                .antMatchers(HttpMethod.POST, "/users/token").permitAll()
                .antMatchers(HttpMethod.DELETE, "/users/logout").authenticated()
                .antMatchers(HttpMethod.DELETE, "/users/leave").authenticated()
                .antMatchers(HttpMethod.HEAD, "/users/verification-codes").permitAll()
                .antMatchers(HttpMethod.POST, "/users/verification-codes").permitAll()
                .antMatchers(HttpMethod.GET, "/users").authenticated()


                // feeds & like & bookmark
                .antMatchers(HttpMethod.POST, "/feeds/**").authenticated()
                .antMatchers(HttpMethod.PATCH, "/feeds/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/feeds/**").authenticated()
                .antMatchers(HttpMethod.GET, "/feeds/**").authenticated()

                // comments
                .antMatchers(HttpMethod.POST, "/comments/**").authenticated()

                // categories
                .antMatchers(HttpMethod.GET, "/categories").permitAll()

                // swagger
                .antMatchers("/swagger*/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**").permitAll()

                .anyRequest().denyAll()

                .and()
                .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint(objectMapper))

                .and()
                .apply(new FilterConfig(jwtTokenProvider, objectMapper))

                .and()
                .build();
    }
}