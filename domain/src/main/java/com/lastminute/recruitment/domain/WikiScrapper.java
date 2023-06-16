package com.lastminute.recruitment.domain;

import com.lastminute.recruitment.domain.error.WikiPageNotFound;
import com.lastminute.recruitment.domain.error.WikiPageRepository;
import com.lastminute.recruitment.domain.error.WikiReader;

import java.util.ArrayList;
import java.util.List;

public class WikiScrapper {

    private final WikiReader wikiReader;
    private final WikiPageRepository repository;

    public WikiScrapper(WikiReader wikiReader, WikiPageRepository repository) {
        this.wikiReader = wikiReader;
        this.repository = repository;
    }

    public void read(String link) {
        if (link == null || link.isEmpty()) {
            return;
        }

        WikiPage rootPage = wikiReader.read(link);
        repository.save(rootPage);

        ArrayList<String> visitedPages = new ArrayList<>();
        visitedPages.add(rootPage.getSelfLink());

        traverseAndSave(rootPage, visitedPages);
    }

    private void traverseAndSave(WikiPage page, List<String> visitedPages) {
        for (String childLink : page.getLinks()) {
            if (visitedPages.contains(childLink)) {
                continue;
            }

            WikiPage readPage;
            try {
                readPage = wikiReader.read(childLink);
            } catch (WikiPageNotFound exception) {
                continue;
            }

            repository.save(readPage);
            visitedPages.add(readPage.getSelfLink());

            traverseAndSave(readPage, visitedPages);
        }
    }

}
