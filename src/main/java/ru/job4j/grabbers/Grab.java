package ru.job4j.grabbers;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import ru.job4j.parsers.Parse;
import ru.job4j.repository.Store;

public interface Grab {
    void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException;
}
