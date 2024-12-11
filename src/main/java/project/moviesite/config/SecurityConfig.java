package project.moviesite.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import project.moviesite.service.KeycloakUserService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTAuthConverter jwtAuthConverter;
    private final JwtSyncFilter jwtSyncFilter;
    private final KeycloakUserService keycloakUserService;
    @Bean
    public SecurityFilterChain securityFilterChainWeb(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {
        http
                .securityMatcher("/movies-view/**", "/movie-details/**", "/login/**", "/oauth2/authorization/keycloak", "/logout/**", "/user-info/**", "watchlist/**", "favorites/**","/comments/**", "ignored/**", "rate-movie/**", "delete-rating/**", "/admin/**")
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/", "/movies-view", "/movie-details/**", "/user-info/**", "favorites/**", "ignored/**", "watchlist/**", "comments/**","/login**", "/css/**", "/js/**", "rate-movie/**", "delete-rating/**", "admin/**").permitAll()
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
                .securityMatcher("/api/**")
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests.requestMatchers("/api/**").authenticated()
                )
                .addFilterAfter(jwtSyncFilter, BearerTokenAuthenticationFilter.class) // Dodanie filtra synchronizacji
                .oauth2ResourceServer(oauth2ResourceServer ->
                        oauth2ResourceServer.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthConverter))
                );

        return http.build();
    }

    private OidcClientInitiatedLogoutSuccessHandler logoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) {
        // Tworzenie handlera wylogowania
        OidcClientInitiatedLogoutSuccessHandler logoutSuccessHandler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        String postLogoutRedirectUri = "http://localhost:8080/movies-view";
        logoutSuccessHandler.setPostLogoutRedirectUri(postLogoutRedirectUri);

        return logoutSuccessHandler;
    }


}

