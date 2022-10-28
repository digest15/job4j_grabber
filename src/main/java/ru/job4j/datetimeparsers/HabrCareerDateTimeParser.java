package ru.job4j.datetimeparsers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HabrCareerDateTimeParser implements DateTimeParser {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Override
    public LocalDateTime parse(String dateInString) {
        return LocalDateTime.parse(dateInString, FORMATTER);
    }
}
