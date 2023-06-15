package com.lastminute.recruitment.client;

import com.lastminute.recruitment.domain.WikiPage;
import com.lastminute.recruitment.domain.error.WikiPageNotFound;
import com.lastminute.recruitment.domain.error.WikiReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.nio.file.Files;
import java.util.stream.Collectors;

public class HtmlWikiReader implements WikiReader {

    private static final HtmlWikiClient CLIENT = new HtmlWikiClient();

    @Override
    public WikiPage read(String link) {
        String htmlContent;
        try {
            String filePath = CLIENT.readHtml(link);

            htmlContent = Files.readString(new File(filePath).toPath());
        } catch (Exception exception) {
            throw new WikiPageNotFound();
        }

        return parsePage(htmlContent);
    }

    private WikiPage parsePage(String content) {
        Document document = Jsoup.parse(content);

        return new WikiPage(
                document.getElementsByClass("title").text(),
                document.getElementsByClass("content").text(),
                document.getElementsByTag("meta").attr("selfLink"),
                document.select(".links li a").stream()
                        .map(el -> el.attr("href"))
                        .collect(Collectors.toList())
        );
    }
}
