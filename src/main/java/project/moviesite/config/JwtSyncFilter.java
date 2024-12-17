package project.moviesite.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import project.moviesite.service.UserSyncService;

@Component
@RequiredArgsConstructor
public class JwtSyncFilter extends OncePerRequestFilter {

    private final UserSyncService userSyncService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof Jwt jwt) {
            String sub = jwt.getClaim("sub");
            String username = jwt.getClaim("preferred_username");
            String email = jwt.getClaim("email");
            String firstName = jwt.getClaim("given_name");
            String lastName = jwt.getClaim("family_name");

            userSyncService.syncUser(sub, username, email, firstName, lastName);
        }

        filterChain.doFilter(request, response);
    }
}
