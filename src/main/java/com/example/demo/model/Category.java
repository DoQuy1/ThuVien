package com.example.demo.model;

import lombok.*;
import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @ManyToMany(mappedBy = "categories")
    private Set<Book> books;
}
