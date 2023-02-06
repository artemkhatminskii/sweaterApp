package com.example.sweaterApp.Repository;

import com.example.sweaterApp.Models.Message;
import com.example.sweaterApp.Models.Usr;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface MessageRepository extends CrudRepository<Message, Integer> {
    Page<Message> findAll(Pageable pageable);
    Page<Message> findByTag(String tag, Pageable pageable);
    Page<Message> findByAuthor(Optional<Usr> usr, Pageable pageable);

}
