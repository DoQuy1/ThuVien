package com.example.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookReturnDTO {
    private Long bookId;
    private Integer Quantity;
}
