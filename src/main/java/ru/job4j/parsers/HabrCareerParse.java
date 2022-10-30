package ru.job4j.parsers;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import ru.job4j.datetimeparsers.DateTimeParser;
import ru.job4j.models.Post;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HabrCareerParse implements Parse {
    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer?page=", SOURCE_LINK);

    private int countPage = 5;

    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> list() {
        return IntStream.range(1, countPage + 1)
                .mapToObj(i -> readPage(PAGE_LINK + i))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public void setReadablePageCount(int count) {
        this.countPage = count;
    }

    private List<Post> readPage(String link) {
        List<Post> posts = new ArrayList<>();
        Connection connection = Jsoup.connect(link);
        try {
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");

            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                String title = titleElement.text();

                Element linkElement = titleElement.child(0);
                String linkVacancy = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));

                Element dateElement = row.select(".vacancy-card__date")
                        .first()
                        .child(0);
                LocalDateTime created = dateTimeParser.parse(dateElement.attr("datetime"));

                String description = retrieveDescription(linkVacancy);

                posts.add(new Post(title, description, linkVacancy, created));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return posts;
    }

    private static String retrieveDescription(String link) {
        StringJoiner description = new StringJoiner("");
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
                if (((Element) ((TextNode) node).parentNode()).tag().getName().equals("li")) {
                    text.add("- " + value);
                    text.add(System.lineSeparator());
                    needSpace = false;
                } else {
                    if (needSpace) {
                        text.add(" ");
                    }
                    if (value.equals(" ")) {
                        text.add(System.lineSeparator());
                        text.add(System.lineSeparator());
                    } else {
                        text.add(value);
                    }
                    needSpace = true;
                }
            }
        }
    }
}
