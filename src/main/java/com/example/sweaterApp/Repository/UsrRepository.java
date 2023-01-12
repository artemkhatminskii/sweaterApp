package com.example.sweaterApp.Repository;

import com.example.sweaterApp.Models.Usr;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsrRepository extends JpaRepository<Usr, Long> {
    Usr findByUsername(String username);
}
