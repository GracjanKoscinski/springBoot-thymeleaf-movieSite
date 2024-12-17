package project.moviesite.config;

import project.moviesite.service.KeycloakUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@ImportResource("classpath:security-config.xml")
public class SecurityConfig {

    private final JWTAuthConverter jwtAuthConverter;
    private final JwtSyncFilter jwtSyncFilter;
    private final KeycloakUserService keycloakUserService;
    private final SecurityUrls securityUrls;

    @Bean
    public SecurityFilterChain securityFilterChainWeb(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {
        http
                .securityMatcher(securityUrls.getSecurityMatcherUrls().toArray(new String[0]))
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(securityUrls.getPublicUrls().toArray(new String[0])).permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(keycloakUserService)
                        )
                        .defaultSuccessUrl("/movies-view", true)
                )
                .logout(logout ->
                        logout
                                .logoutUrl("/logout")
                                .logoutSuccessHandler(logoutSuccessHandler(clientRegistrationRepository))
                                .permitAll()
                );

        return http.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChainAPI(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/protected/**", "/api/admin/**")
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests.requestMatchers("/api/protected/**", "/api/admin/**").authenticated()
                )
                .addFilterAfter(jwtSyncFilter, BearerTokenAuthenticationFilter.class) // Dodanie filtra synchronizacji
                .oauth2ResourceServer(oauth2ResourceServer ->
                        oauth2ResourceServer.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthConverter))
                );

        return http.build();
    }

    private OidcClientInitiatedLogoutSuccessHandler logoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) {
        OidcClientInitiatedLogoutSuccessHandler logoutSuccessHandler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        logoutSuccessHandler.setPostLogoutRedirectUri(securityUrls.getPostLogoutRedirectUri());
        return logoutSuccessHandler;
    }


}


