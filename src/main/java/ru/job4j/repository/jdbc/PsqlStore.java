package ru.job4j.repository.jdbc;

import ru.job4j.models.Post;
import ru.job4j.repository.Store;
import ru.job4j.repository.jdbc.helpers.JdbcHelper;

import java.sql.*;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {
    private final Connection connection;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("connection.driver_class"));
            connection = DriverManager.getConnection(
                    cfg.getProperty("connection.url"),
                    cfg.getProperty("connection.username"),
                    cfg.getProperty("connection.password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement ps = connection.prepareStatement(
                "insert into post(name, description, link, created) values (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getDescription());
            ps.setString(3, post.getLink());
            ps.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            ps.execute();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    post.setId(keys.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        return JdbcHelper.executeSelect(
                connection,
                "select id, name, description, link, created from post",
                ps -> { },
                this::createPost
        );
    }

    @Override
    public Post findById(int id) {
        List<Post> posts = JdbcHelper.executeSelect(
                connection,
                "select id, name, description, link, created from post where id = ?",
                ps -> ps.setInt(1, id),
                this::createPost
        );
        return posts.size() > 0
                ? posts.get(1)
                : null;
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    private Post createPost(ResultSet rs) {
        Post post;
        try {
            post = new Post(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("link"),
                    rs.getTimestamp("created").toLocalDateTime()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return post;
    }


}
