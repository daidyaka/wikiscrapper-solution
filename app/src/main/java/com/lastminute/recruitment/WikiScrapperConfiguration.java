package com.lastminute.recruitment;

import com.lastminute.recruitment.client.HtmlWikiReader;
import com.lastminute.recruitment.client.JsonWikiReader;
import com.lastminute.recruitment.domain.WikiScrapper;
import com.lastminute.recruitment.domain.error.WikiPageRepository;
import com.lastminute.recruitment.domain.error.WikiReader;
import com.lastminute.recruitment.persistence.InMemoryWikiPageRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class WikiScrapperConfiguration {

    @Bean
    @Profile("!json")
    public WikiReader htmlWikiReader() {
        return new HtmlWikiReader();
    }

    @Bean
    @Profile("json")
    public WikiReader jsonWikiReader() {
        return new JsonWikiReader();
    }

    @Bean
    public WikiPageRepository wikiPageRepository() {
        return new InMemoryWikiPageRepository();
    }

    @Bean
    public WikiScrapper wikiScrapper(WikiPageRepository wikiPageRepository, WikiReader wikiReader) {
        return new WikiScrapper(wikiReader, wikiPageRepository);
    }
}
