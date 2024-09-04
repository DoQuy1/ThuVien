package com.example.demo.dto.response;

import lombok.Data;

@Data
public class BookStatsResponse {
    private Long bookId;
    private String title;
    private Long count;

    public BookStatsResponse(Long bookId, String title, Long count) {
        this.bookId = bookId;
        this.title = title;
        this.count = count;
    }

    public BookStatsResponse( String title, Long count) {
        this.title = title;
        this.count = count;
    }


}
