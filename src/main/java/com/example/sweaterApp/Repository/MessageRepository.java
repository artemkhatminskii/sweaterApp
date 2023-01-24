package com.example.sweaterApp.Repository;

import com.example.sweaterApp.Models.Message;
import com.example.sweaterApp.Models.Usr;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MessageRepository extends CrudRepository<Message, Integer> {
    List<Message> findByTag(String tag);
    Set<Message> findByAuthor(Optional<Usr> usr);

}
