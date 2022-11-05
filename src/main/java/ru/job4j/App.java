package ru.job4j;

import org.quartz.Scheduler;
import ru.job4j.datetimeparsers.HabrCareerDateTimeParser;
import ru.job4j.grabbers.GrabberSqlStore;
import ru.job4j.parsers.HabrCareerParse;
import ru.job4j.repository.Store;

public class App {
   public static void main(String[] args) throws Exception {
       GrabberSqlStore grab = new GrabberSqlStore();
       grab.cfg();
       Scheduler scheduler = grab.scheduler();
       Store store = grab.store();
       grab.init(new HabrCareerParse(new HabrCareerDateTimeParser()), store, scheduler);
    }
}
