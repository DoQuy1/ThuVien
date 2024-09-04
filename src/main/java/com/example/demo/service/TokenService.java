package com.example.demo.service;

import com.example.demo.model.Token;
import com.example.demo.model.User;
import javax.servlet.http.HttpServletRequest;

public interface TokenService {
    Token addToken(User user, String token, HttpServletRequest request);

    Boolean isTokenExists(String jwtToken);
}