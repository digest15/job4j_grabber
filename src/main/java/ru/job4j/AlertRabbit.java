package ru.job4j;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.InputStream;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {

    private static final String CONFIG_PATH = "rabbit.properties";
    private static final String RABBIT_INTERVAL = "rabbit.interval";
    public static void main(String[] args) {
        try {
            Properties cfg = readConfig(CONFIG_PATH);
            String strInterval = cfg.getProperty(RABBIT_INTERVAL);
            int interval = strInterval == null ? 0 : Integer.parseInt(strInterval);

            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail job = newJob(Rabbit.class).build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(interval)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
        }
    }

    /**
     * @return 0 if value isn't set
     * @throws Exception if something was wrong
     */
    public static Properties readConfig(String configPath) throws Exception {
        Properties cfg;
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream(configPath)) {
            cfg = new Properties();
            cfg.load(in);
        }
        return cfg;
    }
}