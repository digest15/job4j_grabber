package ru.job4j.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Post {
    private int id;
    private String title;
    private String link;
    private LocalDateTime created;

    public Post(int id, String title, String link, LocalDateTime created) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.created = created;
    }

    @Override
    public String toString() {
        return String.format("%s; %s; %s%n",
                title,
                created.format(DateTimeFormatter.ISO_DATE_TIME),
                link);
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
        return Objects.equals(link, post.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(link);
    }
}
