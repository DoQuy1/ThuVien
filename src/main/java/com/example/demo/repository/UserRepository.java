package com.example.demo.repository;


import com.example.demo.dto.request.FilterRequest;
import com.example.demo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u.email FROM User u")
    List<String> findEmails();

    @Query("SELECT u FROM User u " +
            "WHERE " +
            "(:#{#request.username} IS NULL OR :#{#request.username} = '' OR lower(u.username) LIKE concat('%', lower(:#{#request.username}), '%')) AND " +
            "(:#{#request.email} IS NULL OR :#{#request.email} = '' OR lower(u.email) LIKE concat('%', lower(:#{#request.email}), '%')) AND " +
            "(:#{#request.role} IS NULL OR :#{#request.role} = '' OR lower(u.role) LIKE concat('%', lower(:#{#request.role}), '%'))"
    )
    Page<User> findByFilter(FilterRequest request, Pageable pageable);

    @Query("select u from User u where u.id = :id")
    Optional<User> getUserById(@Param("id") Long id);

    Page<User> findByUsernameContaining(String username, Pageable pageable);
    Optional<Object> findByEmail(String email);

    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);


}

