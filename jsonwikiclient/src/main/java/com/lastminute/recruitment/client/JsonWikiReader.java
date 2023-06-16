package com.lastminute.recruitment.client;

import com.google.gson.Gson;
import com.lastminute.recruitment.domain.WikiPage;
import com.lastminute.recruitment.domain.error.WikiPageNotFound;
import com.lastminute.recruitment.domain.error.WikiReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class JsonWikiReader implements WikiReader {

    private static final JsonWikiClient CLIENT = new JsonWikiClient();
    private static final Gson JSON_PARSER = new Gson();

    @Override
    public WikiPage read(String link) {
        try {
            String filePath = CLIENT.readJson(link);
            String jsonContent = Files.readString(new File(filePath).toPath());
            return JSON_PARSER.fromJson(jsonContent, WikiPage.class);
        } catch (Exception resourceNullException) {
            throw new WikiPageNotFound();
        }
    }
}
