package ru.job4j;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import ru.job4j.datetimeparsers.DateTimeParser;
import ru.job4j.datetimeparsers.HabrCareerDateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.StringJoiner;
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

                String description = retrieveDescription(link);

                System.out.printf("%s; %s; %s%n",
                        vacancyName,
                        datetime.format(DateTimeFormatter.ISO_DATE_TIME),
                        link);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String retrieveDescription(String link) {
        StringJoiner description = new StringJoiner(System.lineSeparator());
        try {
            Connection connection = Jsoup.connect(link);
            Document document = connection.get();
            Element element = document.select(".collapsible-description__content").first();
            retrievePlainText(description, element);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return description.toString();
    }

    private static void retrievePlainText(StringJoiner text, Node node) {
        List<Node> elements = node.childNodes();
        if (elements.size() > 0) {
            elements.forEach(e -> retrievePlainText(text, e));
        } else {
            boolean needSpace = false;
            if (node instanceof TextNode) {
                String value = ((TextNode) node).text();
                if (value.equals(" ")) {
                    if (needSpace) {
                        text.add(" ");
                    }
                    text.add(value);
                    needSpace = true;
                } else if (((Element) ((TextNode) node).parentNode()).tag().getName().equals("li")) {
                    text.add("- ");
                    text.add(value);
                    needSpace = false;
                }
            }
        }
    }
}
