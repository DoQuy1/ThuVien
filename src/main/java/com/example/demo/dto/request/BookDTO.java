package com.example.demo.dto.request;

import lombok.Data;

import java.util.Set;

@Data
public class BookDTO {
    private String title;
    private String description;
    private int quantity;
    private Set<String> authorNames;
    private Set<String> categoryNames;
}
