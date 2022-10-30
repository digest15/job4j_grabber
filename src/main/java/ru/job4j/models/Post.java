package ru.job4j.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Post {
    private int id;
    private String title;
    private String description;
    private String link;
    private LocalDateTime created;

    public Post(int id, String title, String description, String link, LocalDateTime created) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.link = link;
        this.created = created;
    }

    public Post(String title, String description, String link, LocalDateTime created) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.created = created;
    }

    @Override
    public String toString() {
        return String.format("%s; %s; %s;\n%s",
                title,
                link,
                created.format(DateTimeFormatter.ISO_DATE_TIME),
                description);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Post post = (Post) o;
        return id == post.id && Objects.equals(link, post.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, link);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }
}
