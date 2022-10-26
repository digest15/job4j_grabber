package ru.job4j;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HabrCareerParse {
    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);

    public static void main(String[] args) throws IOException {
        Connection connection = Jsoup.connect(PAGE_LINK);
        Document document = connection.get();
        Elements rows = document.select(".vacancy-card__inner");

        DateTimeParser dateTimeParser = new HabrCareerDateTimeParser();
        rows.forEach(row -> {
            Element titleElement = row.select(".vacancy-card__title").first();
            String vacancyName = titleElement.text();

            Element linkElement = titleElement.child(0);
            String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));

            Element dateElement = row.select(".vacancy-card__date")
                    .first()
                    .child(0);
            LocalDateTime datetime = dateTimeParser.parse(dateElement.attr("datetime"));

            System.out.printf("%s; %s; %s%n",
                    vacancyName,
                    datetime.format(DateTimeFormatter.ISO_DATE_TIME),
                    link);
        });
    }

    private static class HabrCareerDateTimeParser implements DateTimeParser {
        @Override
        public LocalDateTime parse(String dateInString) {
            return LocalDateTime.parse(dateInString, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
    }
}
