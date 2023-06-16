package com.lastminute.recruitment.rest;

import com.lastminute.recruitment.WikiScrapperApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("json")
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = WikiScrapperApplication.class)
class WikiScrapperJsonResourceIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testScrapWikipedia_WhenPageHasFlatStructure() throws Exception {
        String pageLink = "http://wikiscrapper.test/json/test-page1";

        mockMvc.perform(MockMvcRequestBuilders.post("/wiki/scrap")
                .contentType(MediaType.APPLICATION_JSON)
                .content(pageLink))
                .andExpect(status().isOk());
    }

    @Test
    public void testScrapWikipedia_WhenPageNotFound() throws Exception {
        String pageLink = "http://wikiscrapper.test/json/non-existing-page";

        mockMvc.perform(MockMvcRequestBuilders.post("/wiki/scrap")
                .contentType(MediaType.APPLICATION_JSON)
                .content(pageLink))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testScrapWikipedia_WhenPageHasDeepStructureAndChildNotFound() throws Exception {
        String pageLink = "http://wikiscrapper.test/json/test-page3";

        mockMvc.perform(MockMvcRequestBuilders.post("/wiki/scrap")
                .contentType(MediaType.APPLICATION_JSON)
                .content(pageLink))
                .andExpect(status().isOk());
    }

    @Test
    public void testScrapWikipedia_WhenPageLinkIsEmpty() throws Exception {
        String pageLink = "";

        mockMvc.perform(MockMvcRequestBuilders.post("/wiki/scrap")
                .contentType(MediaType.APPLICATION_JSON)
                .content(pageLink))
                .andExpect(status().isBadRequest());
    }
}