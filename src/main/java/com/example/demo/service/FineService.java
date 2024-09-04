package com.example.demo.service;

import com.example.demo.model.BorrowForm;

public interface FineService {

    void calculateFines();

    void calculateFineForBorrowForm(BorrowForm borrowForm);
}
