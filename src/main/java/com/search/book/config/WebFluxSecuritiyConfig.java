package com.search.book.config;

import com.search.book.entity.User;
import com.search.book.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.List;


@Configuration
@RequiredArgsConstructor
@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
@ComponentScan({"org.springframework.security.crypto.password"})
public class WebFluxSecuritiyConfig {
    private final ApplicationContext applicationContext;

    /**
     * ServerHttpSecurity??? ????????? ??????????????? HttpSecurity??? ????????? ??????????????? ????????????.
     * ??? ???????????? ???????????? ?????? ????????? ?????? ?????? ?????? ????????? ????????? ??? ??????.
     * ??? ???????????? ????????? ????????????, ????????? ????????? ????????? ????????? ?????? ????????? ????????? ????????? ??? ??????. *
     * SecurityWebFilterChain???????????? ???????????? ?????? DefaultMethodSecurityExpressionHandler???????????? ?????? ???????????? ????????? ??????.
     * authenticationEntryPoint: ????????????????????? ????????? ????????? ??? ?????? ??? ????????? ?????????.
     * accessDeniedHandler: ????????? ???????????? ????????? ????????? ????????? ?????? ?????? ??? ?????? ??? ????????? ?????????.
     *
     * @param http * @return
     */
    @Bean
    @DependsOn({"methodSecurityExpressionHandler"})
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, JwtTokenProvider jwtTokenProvider, ReactiveAuthenticationManager reactiveAuthenticationManager) {
        DefaultMethodSecurityExpressionHandler defaultWebSecurityExpressionHandler = this.applicationContext.getBean(DefaultMethodSecurityExpressionHandler.class);
        defaultWebSecurityExpressionHandler.setPermissionEvaluator(myPermissionEvaluator());

        return http.exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec.authenticationEntryPoint((exchange, ex) -> {
            return Mono.fromRunnable(() -> {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            });
        }).accessDeniedHandler((exchange, denied) -> {
            return Mono.fromRunnable(() -> {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            });
        })).csrf().disable().formLogin().disable().httpBasic().disable().logout().disable()
                .authenticationManager(reactiveAuthenticationManager)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(exchange -> exchange.pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .pathMatchers("/", "/js/**", "/css/**", "/image/**").permitAll()
                        .pathMatchers("/login").permitAll()
                        .pathMatchers("/join").permitAll()
                        .pathMatchers("/board").permitAll()
                        .anyExchange().authenticated())
                .addFilterAt(new JwtTokenAuthenticationFilter(jwtTokenProvider), SecurityWebFiltersOrder.HTTP_BASIC).build();
    }

    @Bean
    public PermissionEvaluator myPermissionEvaluator() {
        return new PermissionEvaluator() {
            @Override
            public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
                if (authentication.getAuthorities().stream().filter(grantedAuthority -> grantedAuthority.getAuthority().equals(targetDomainObject)).count() > 0)
                    return true;
                return false;
            }

            @Override
            public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
                return false;
            }
        };
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService
                                                                               userDetailsService, PasswordEncoder passwordEncoder) {
        var authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder);
        return authenticationManager;
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService(UserRepository userRepository) {
        return userId -> {
            User user = userRepository.findOneByUserId(userId).get();
            if (user == null) return Mono.empty();

            return Mono.just(user);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
