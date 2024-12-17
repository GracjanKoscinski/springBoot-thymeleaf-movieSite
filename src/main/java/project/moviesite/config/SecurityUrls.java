package project.moviesite.config;

import lombok.Data;

import java.util.List;

@Data
public class SecurityUrls {
    private List<String> publicUrls;
    private List<String> securityMatcherUrls;
    private String postLogoutRedirectUri;
}