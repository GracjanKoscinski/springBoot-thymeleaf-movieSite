package project.moviesite.config;



import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Component;
import project.moviesite.service.UserSyncService;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthenticationEventListener {

    private final UserSyncService userSyncService;

    @EventListener
    public void handleAuthenticationSuccess(InteractiveAuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();

        if (authentication.getPrincipal() instanceof DefaultOidcUser oidcUser) {
            Map<String, Object> attributes = oidcUser.getAttributes();

            String sub = (String) attributes.get("sub");
            String username = (String) attributes.get("preferred_username");
            String email = (String) attributes.get("email");
            String firstName = (String) attributes.get("given_name");
            String lastName = (String) attributes.get("family_name");

            userSyncService.syncUser(sub, username, email, firstName, lastName);
        }
    }
}