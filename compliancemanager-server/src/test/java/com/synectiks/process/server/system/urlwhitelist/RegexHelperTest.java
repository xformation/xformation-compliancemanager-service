/*
 * */
package com.synectiks.process.server.system.urlwhitelist;

import org.junit.Test;

import com.synectiks.process.server.system.urlwhitelist.RegexHelper;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class RegexHelperTest {
    RegexHelper regexHelper = new RegexHelper();

    @Test
    public void createRegexForTemplateUrl() {
        String url = "https://example.com/api/lookup?key=message_key&a=b&c=message_key&e=f";
        String template = "https://example.com/api/lookup?key=${key}&a=b&c=${key}&e=f";
        String expected = "^\\Qhttps://example.com/api/lookup?key=\\E.*?\\Q&a=b&c=\\E.*?\\Q&e=f\\E$";
        String got = regexHelper.createRegexForUrlTemplate(template, "${key}");
        assertThat(got).isEqualTo(expected);
        Pattern compiled = Pattern.compile(got, Pattern.DOTALL);
        assertThat(compiled.matcher(url).find()).isTrue();
    }

    @Test
    public void create() {
        String url = "https://example.com/api/lookup";
        String expected = "^\\Qhttps://example.com/api/lookup\\E$";
        String got = regexHelper.createRegexForUrl(url);
        assertThat(got).isEqualTo(expected);
        Pattern compiled = Pattern.compile(got, Pattern.DOTALL);
        assertThat(compiled.matcher(url).find()).isTrue();
    }
}
