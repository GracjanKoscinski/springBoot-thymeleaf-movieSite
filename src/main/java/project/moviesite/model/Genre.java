package project.moviesite.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "genres")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String genreName;

    @ManyToMany(mappedBy = "genres")
    @JsonBackReference
    private Set<Movie> movies = new HashSet<>();
}
