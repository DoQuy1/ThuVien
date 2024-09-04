package com.example.demo.model;

import lombok.*;
import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@Table(name = "author")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String bio;

    @ManyToMany(mappedBy = "authors")
    private Set<Book> books;
}
