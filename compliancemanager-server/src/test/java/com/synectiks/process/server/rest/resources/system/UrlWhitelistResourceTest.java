/*
 * */
package com.synectiks.process.server.rest.resources.system;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.synectiks.process.server.rest.models.system.urlwhitelist.WhitelistRegexGenerationRequest;
import com.synectiks.process.server.rest.models.system.urlwhitelist.WhitelistRegexGenerationResponse;
import com.synectiks.process.server.rest.resources.system.UrlWhitelistResource;
import com.synectiks.process.server.system.urlwhitelist.RegexHelper;

import static org.assertj.core.api.Assertions.assertThat;

public class UrlWhitelistResourceTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    UrlWhitelistResource urlWhitelistResource;

    @Spy
    RegexHelper regexHelper;

    @Test
    public void generateRegexForTemplate() {
        final WhitelistRegexGenerationRequest request =
                WhitelistRegexGenerationRequest.create("https://example.com/api/lookup?key=${key}", "${key}");
        final WhitelistRegexGenerationResponse response = urlWhitelistResource.generateRegex(request);
        assertThat(response.regex()).isNotBlank();
    }

    @Test
    public void generateRegexForUrl() {
        final WhitelistRegexGenerationRequest request =
                WhitelistRegexGenerationRequest.create("https://example.com/api/lookup", null);
        final WhitelistRegexGenerationResponse response = urlWhitelistResource.generateRegex(request);
        assertThat(response.regex()).isNotBlank();
    }
}
