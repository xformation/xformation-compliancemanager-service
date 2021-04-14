/*
 * */
package com.synectiks.process.server.rest.resources.tools;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.synectiks.process.server.rest.models.tools.requests.RegexTestRequest;
import com.synectiks.process.server.rest.models.tools.responses.RegexTesterResponse;
import com.synectiks.process.server.rest.models.tools.responses.RegexValidationResponse;
import com.synectiks.process.server.rest.resources.tools.RegexTesterResource;
import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;

import javax.ws.rs.BadRequestException;
import java.util.Collections;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class RegexTesterResourceTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private RegexTesterResource resource;

    public RegexTesterResourceTest() {
        GuiceInjectorHolder.createInjector(Collections.emptyList());
    }

    @Before
    public void setUp() throws Exception {
        resource = new RegexTesterResource();
    }

    @Test
    public void regexTesterReturns400WithInvalidRegularExpression() throws Exception {
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("Invalid regular expression: Dangling meta character '?' near index 0");
        resource.regexTester("?*foo", "test");
    }

    @Test
    public void regexTesterReturnsValidResponseIfRegExMatches() throws Exception {
        final RegexTesterResponse response = resource.regexTester("([a-z]+)", "test");
        assertThat(response.matched()).isTrue();
        assertThat(response.regex()).isEqualTo("([a-z]+)");
        assertThat(response.string()).isEqualTo("test");
        assertThat(response.match()).isEqualTo(RegexTesterResponse.Match.create("test", 0, 4));
    }

    @Test
    public void regexTesterReturnsValidResponseIfRegExDoesNotMatch() throws Exception {
        final RegexTesterResponse response = resource.regexTester("([0-9]+)", "test");
        assertThat(response.matched()).isFalse();
        assertThat(response.regex()).isEqualTo("([0-9]+)");
        assertThat(response.string()).isEqualTo("test");
        assertThat(response.match()).isNull();
    }

    @Test
    public void testRegexReturns400WithInvalidRegularExpression() throws Exception {
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("Invalid regular expression: Dangling meta character '?' near index 0");
        resource.testRegex(RegexTestRequest.create("test", "?*foo"));
    }

    @Test
    public void testRegexReturnsValidResponseIfRegExMatches() throws Exception {
        final RegexTesterResponse response = resource.testRegex(RegexTestRequest.create("test", "([a-z]+)"));
        assertThat(response.matched()).isTrue();
        assertThat(response.regex()).isEqualTo("([a-z]+)");
        assertThat(response.string()).isEqualTo("test");
        assertThat(response.match()).isEqualTo(RegexTesterResponse.Match.create("test", 0, 4));
    }

    @Test
    public void testRegexReturnsValidResponseIfRegExDoesNotMatch() throws Exception {
        final RegexTesterResponse response = resource.testRegex(RegexTestRequest.create("test", "([0-9]+)"));
        assertThat(response.matched()).isFalse();
        assertThat(response.regex()).isEqualTo("([0-9]+)");
        assertThat(response.string()).isEqualTo("test");
        assertThat(response.match()).isNull();
    }

    @Test
    public void testValidateValidRegex() {
        final RegexValidationResponse response = resource.validateRegex(".*");
        assertThat(response.regex()).isEqualTo(".*");
        assertThat(response.isValid()).isTrue();
        assertThat(response.validationMessage().isPresent()).isFalse();
    }

    @Test
    public void testValidateInvalidRegex() {
        final RegexValidationResponse response = resource.validateRegex("?*foo");
        assertThat(response.regex()).isEqualTo("?*foo");
        assertThat(response.isValid()).isFalse();
        assertThat(response.validationMessage().isPresent()).isTrue();
        assertThat(response.validationMessage().get()).isNotBlank();
    }
}
