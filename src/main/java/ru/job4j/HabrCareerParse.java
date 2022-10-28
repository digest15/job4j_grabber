package ru.job4j;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.datetimeparsers.DateTimeParser;
import ru.job4j.datetimeparsers.HabrCareerDateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.IntStream;

public class HabrCareerParse {
    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer?page=", SOURCE_LINK);

    public static void main(String[] args) throws IOException {
        IntStream.range(1, 6)
                 .forEach(HabrCareerParse::parsePage);
    }

    private static void parsePage(int pageNumber) {
        Connection connection = Jsoup.connect(PAGE_LINK + pageNumber);
        try {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
