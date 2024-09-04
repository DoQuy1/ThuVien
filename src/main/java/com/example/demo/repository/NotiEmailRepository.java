package com.example.demo.repository;
import com.example.demo.model.NotiEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotiEmailRepository extends JpaRepository<NotiEmail, Long> {
    List<NotiEmail> findByStatus(String status);
}
