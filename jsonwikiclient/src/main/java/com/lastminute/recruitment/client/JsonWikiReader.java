package com.lastminute.recruitment.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lastminute.recruitment.domain.WikiPage;
import com.lastminute.recruitment.domain.error.WikiPageInvalidFormat;
import com.lastminute.recruitment.domain.error.WikiReader;

import java.io.File;
import java.nio.file.Files;

public class JsonWikiReader implements WikiReader {

    private static final JsonWikiClient CLIENT = new JsonWikiClient();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public WikiPage read(String link) {
        String filePath = CLIENT.readJson(link);

        try {
            String jsonContent = Files.readString(new File(filePath).toPath());
            return OBJECT_MAPPER.readValue(jsonContent, WikiPage.class);
        } catch (Exception exception) {
            throw new WikiPageInvalidFormat();
        }
    }
}
