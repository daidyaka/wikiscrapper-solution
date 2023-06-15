package com.lastminute.recruitment.client;

import java.util.Objects;

public class HtmlWikiClient {

    public String readHtml(String link) {
        String name = link.replace("http://wikiscrapper.test", "") + ".html";
        return Objects.requireNonNull(getClass().getResource(name)).getFile();
    }
}
