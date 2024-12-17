package project.moviesite.config;

import java.util.List;
import lombok.Data;

@Data
public class SecurityUrls {
    private List<String> publicUrls;
    private List<String> securityMatcherUrls;
    private String postLogoutRedirectUri;
}
