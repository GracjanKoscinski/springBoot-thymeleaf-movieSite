package project.moviesite.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.Set;
import lombok.Data;

@Data
public class MovieCreateRequest {
    @NotBlank(message = "Title is mandatory")
    private String title;

    @Positive(message = "Year must be positive") private int year;

    @Positive(message = "Runtime must be positive") private int runtime;

    @NotBlank(message = "Plot is mandatory")
    private String plot;

    @NotBlank(message = "Poster URL is mandatory")
    private String posterUrl;

    private Long directorId;
    private Set<Long> genreIds;
    private Set<Long> actorIds;
}
