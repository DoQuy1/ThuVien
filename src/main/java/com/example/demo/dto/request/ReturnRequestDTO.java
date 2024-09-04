package com.example.demo.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ReturnRequestDTO {
    private Long borrowFormId;
    private List<BookReturnDTO> booksToReturn;
}
