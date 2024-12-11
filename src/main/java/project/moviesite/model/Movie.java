package project.moviesite.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is mandatory")
    private String title;
    @Positive(message = "Year must be positive")
    private int year;
    @Positive(message = "Runtime must be positive")
    private int runtime;
    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "Plot is mandatory")
    private String plot;
    @NotBlank(message = "Poster URL is mandatory")
    private String posterUrl;

    @ManyToOne
    @JoinColumn(name = "director_id", nullable = false)
    @JsonManagedReference
    private com.example.demo.model.Director director;

    @ManyToMany
    @JoinTable(
            name = "movie_genres",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    @JsonManagedReference
    private Set<Genre> genres = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "movie_actors",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    @JsonManagedReference
    private Set<Actor> actors = new HashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "movie")
    private Set<Rating> ratings = new HashSet<>();

    @OneToMany(mappedBy = "movie")
    @JsonManagedReference
    private Set<Comment> comments = new HashSet<>();

}
