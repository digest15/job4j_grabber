package ru.job4j.datetimeparsers;

import java.time.LocalDateTime;

public interface DateTimeParser {
    LocalDateTime parse(String dateInString);
}
