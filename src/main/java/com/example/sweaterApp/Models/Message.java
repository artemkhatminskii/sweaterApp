package com.example.sweaterApp.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@Entity
public class Message implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotEmpty(message = "Поле сообщения не должно быть пустым")
    @Length(max = 2048, message = "Сообщение слишком длинное")
    private String text;
    @NotEmpty(message = "Поле тэга не должно быть пустым")
    private String tag;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private Usr author;

    private String filename;

    public Message() {
    }

    public Message(String text, String tag, Usr usr) {
        this.author = usr;
        this.text = text;
        this.tag = tag;
    }

    public String getAuthorName() {
        return author != null ? author.getUsername() : "<none>";
    }
    public Long getAuthorId() {
        return author != null ? author.getId() : -1;
    }
    public Usr getAuthor() {
        return author;
    }

    public void setAuthor(Usr author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
