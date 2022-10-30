package ru.job4j;

import ru.job4j.datetimeparsers.HabrCareerDateTimeParser;
import ru.job4j.parsers.HabrCareerParse;
import ru.job4j.parsers.Parse;
import ru.job4j.repository.MemStore;
import ru.job4j.repository.Store;

import java.io.IOException;

public class App {
   public static void main(String[] args) throws IOException {
       Parse parser = new HabrCareerParse(new HabrCareerDateTimeParser());
       Store store = new MemStore();
       parser.list()
               .forEach(p -> {
                   store.save(p);
                   System.out.println(p);
                   System.out.println("-------------------------------------------------------------");
               });
    }
}
