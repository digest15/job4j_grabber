package ru.job4j.repository.jdbc.helpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class JdbcHelper {
    private JdbcHelper() { }

    public static boolean executeUpdate(Connection connection,
                                  String sql,
                                  Customizer customizer) {
        boolean result = false;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            customizer.apply(ps);
            result = ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static <E> List<E> executeSelect(Connection connection,
                                      String sql,
                                      Customizer customizer,
                                      Function<ResultSet, E> wrapper) {
        var items = new ArrayList<E>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            customizer.apply(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(wrapper.apply(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    @FunctionalInterface
    public interface Customizer {
        void config(PreparedStatement ps) throws Exception;

        default PreparedStatement apply(PreparedStatement ps) throws Exception {
            config(ps);
            return ps;
        }
    }
}
