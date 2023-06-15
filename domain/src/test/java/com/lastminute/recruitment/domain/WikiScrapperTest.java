package com.lastminute.recruitment.domain;

import com.lastminute.recruitment.domain.error.WikiPageNotFound;
import com.lastminute.recruitment.domain.error.WikiPageRepository;
import com.lastminute.recruitment.domain.error.WikiReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WikiScrapperTest {

    private WikiReader wikiReader;
    private WikiPageRepository repository;
    private WikiScrapper wikiScrapper;

    @BeforeEach
    public void setUp() {
        wikiReader = spy(WikiReader.class);
        repository = mock(WikiPageRepository.class);

        wikiScrapper = new WikiScrapper(wikiReader, repository);
    }

    @Test
    public void testPagesWithoutEmbeddedLinksAreRead() {
        WikiPage rootPage = prepareRootPage();

        WikiPage page1 = new WikiPage("page1", "content", "http://wikiscrapper.test/wiki/page1", List.of());
        WikiPage page2 = new WikiPage("page2", "content", "http://wikiscrapper.test/wiki/page2", List.of());

        when(wikiReader.read(rootPage.getSelfLink())).thenReturn(rootPage);
        when(wikiReader.read("http://wikiscrapper.test/wiki/page1")).thenReturn(page1);
        when(wikiReader.read("http://wikiscrapper.test/wiki/page2")).thenReturn(page2);

        wikiScrapper.read(rootPage.getSelfLink());

        verify(repository).save(rootPage);
        verify(repository).save(page1);
        verify(repository).save(page2);
    }

    @Test
    public void testPagesWithEmbeddedLinksAreReadAndPersisted() {
        WikiPage rootPage = prepareRootPage();

        WikiPage page2 = new WikiPage("page2", "content", "http://wikiscrapper.test/wiki/page2", List.of());
        WikiPage page3 = new WikiPage("page3", "content", "http://wikiscrapper.test/wiki/page3", List.of());
        WikiPage page4 = new WikiPage("page4", "content", "http://wikiscrapper.test/wiki/page4", List.of(
                page3.getSelfLink(),
                page2.getSelfLink()
        ));
        WikiPage page1 = new WikiPage("page1", "content", "http://wikiscrapper.test/wiki/page1", List.of(
                page4.getSelfLink()));

        when(wikiReader.read(rootPage.getSelfLink())).thenReturn(rootPage);
        when(wikiReader.read("http://wikiscrapper.test/wiki/page1")).thenReturn(page1);
        when(wikiReader.read("http://wikiscrapper.test/wiki/page2")).thenReturn(page2);
        when(wikiReader.read("http://wikiscrapper.test/wiki/page3")).thenReturn(page3);
        when(wikiReader.read("http://wikiscrapper.test/wiki/page4")).thenReturn(page4);

        wikiScrapper.read(rootPage.getSelfLink());

        verify(repository, times(5)).save(any());

        verify(repository).save(rootPage);
        verify(repository).save(page1);
        verify(repository).save(page2);
        verify(repository).save(page3);
        verify(repository).save(page4);
    }

    @Test
    public void testPageNotFoundNotPersisted() {
        WikiPage rootPage = prepareRootPage();

        WikiPage page1 = new WikiPage("page1", "content", "http://wikiscrapper.test/wiki/page1", List.of(
                "http://wikiscrapper.test/wiki/page-not-exists"
        ));
        WikiPage page3 = new WikiPage("page3", "content", "http://wikiscrapper.test/wiki/page3", List.of());
        WikiPage page2 = new WikiPage("page2", "content", "http://wikiscrapper.test/wiki/page2", List.of(
                page3.getSelfLink()
        ));

        when(wikiReader.read(rootPage.getSelfLink())).thenReturn(rootPage);
        when(wikiReader.read("http://wikiscrapper.test/wiki/page1")).thenReturn(page1);
        when(wikiReader.read("http://wikiscrapper.test/wiki/page2")).thenReturn(page2);
        when(wikiReader.read("http://wikiscrapper.test/wiki/page3")).thenReturn(page3);
        when(wikiReader.read("http://wikiscrapper.test/wiki/page-not-exists")).thenThrow(new WikiPageNotFound());

        wikiScrapper.read(rootPage.getSelfLink());

        verify(repository, times(4)).save(any());
        verify(repository).save(rootPage);
        verify(repository).save(page1);
        verify(repository).save(page2);
        verify(repository).save(page3);
    }

    @Test
    public void testWhenLinkIsNull() {
        wikiScrapper.read(null);

        verify(wikiReader, never()).read(any());
        verify(repository, never()).save(any());
    }

    @Test
    public void testWhenLinkIsEmpty() {
        wikiScrapper.read("");

        verify(wikiReader, never()).read(any());
        verify(repository, never()).save(any());
    }

    private WikiPage prepareRootPage() {
        return new WikiPage("Root page", "Hello world content!", "http://wikiscrapper.test/wiki/rootPage",
                List.of("http://wikiscrapper.test/wiki/page1", "http://wikiscrapper.test/wiki/page2"));
    }
}