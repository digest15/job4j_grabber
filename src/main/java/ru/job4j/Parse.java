package ru.job4j;

import ru.job4j.models.Post;

import java.util.List;

public interface Parse {
    List<Post> list(String link);
}
