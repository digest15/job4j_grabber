package ru.job4j;

import ru.job4j.datetimeparsers.HabrCareerDateTimeParser;
import ru.job4j.parsers.HabrCareerParse;
import ru.job4j.parsers.Parse;
import ru.job4j.repository.jdbc.PsqlStore;

import java.io.InputStream;
import java.util.Properties;

public class App {
    private static final String MAIN_PROPERTIES_PATH = "rabbit.properties";
   public static void main(String[] args) {
       Parse parser = new HabrCareerParse(new HabrCareerDateTimeParser());
       try (PsqlStore store = new PsqlStore(readConfig(MAIN_PROPERTIES_PATH))) {
           parser.list()
                   .forEach(store::save);
           store.getAll()
                   .forEach(System.out::println);
       } catch (Exception e) {
           e.printStackTrace();
       }
    }

    private static Properties readConfig(String configPath) {
        Properties cfg = new Properties();
        try (InputStream in = App.class.getClassLoader().getResourceAsStream(configPath)) {
            cfg.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cfg;
    }
}
