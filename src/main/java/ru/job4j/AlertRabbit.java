package ru.job4j;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {

    private static final String RABBIT_PROPERTIES_PATH = "app.properties";
    private static final String RABBIT_INTERVAL = "rabbit.interval";

    public static void main(String[] args) {
        try {
            Properties rabbitCfg = readConfig(RABBIT_PROPERTIES_PATH);
            int interval = Integer.parseInt(rabbitCfg.getProperty(RABBIT_INTERVAL, "0"));

            try (Connection connection = getConnection(rabbitCfg)) {
                Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
                scheduler.start();

                JobDataMap dataMap = new JobDataMap();
                dataMap.put("connection", connection);

                JobDetail job = newJob(Rabbit.class)
                        .usingJobData(dataMap)
                        .build();
                SimpleScheduleBuilder times = simpleSchedule()
                        .withIntervalInSeconds(interval)
                        .repeatForever();
                Trigger trigger = newTrigger()
                        .startNow()
                        .withSchedule(times)
                        .build();
                scheduler.scheduleJob(job, trigger);

                Thread.sleep(10_000);
                scheduler.shutdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            Connection connection = (Connection) context.getJobDetail().getJobDataMap().get("connection");
            String sql = "insert into rabbit(created_date) values (?)";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                ps.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static Properties readConfig(String configPath) throws Exception {
        Properties cfg;
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream(configPath)) {
            cfg = new Properties();
            cfg.load(in);
        }
        return cfg;
    }

    private static Connection getConnection(Properties properties) {
        Connection connection;
        try {
            Class.forName(properties.getProperty("connection.driver_class"));
            connection = DriverManager.getConnection(
                    properties.getProperty("connection.url"),
                    properties.getProperty("connection.username"),
                    properties.getProperty("connection.password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return connection;
    }
}