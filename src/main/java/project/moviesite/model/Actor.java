package project.moviesite.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.moviesite.model.Movie;

import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name= "actors")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Actor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "actors")
    @JsonBackReference
    private Set<Movie> movies = new HashSet<>();

}
