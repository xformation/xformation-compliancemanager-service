/*
 * */
package com.synectiks.process.server.shared.rest.resources;

import org.junit.Test;

import com.synectiks.process.server.shared.rest.resources.ProxiedResource.MasterResponse;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;


public class ProxiedResourceTest {
    @Test
    public void masterResponse() {
        final MasterResponse<String> response1 = MasterResponse.create(true, 200, "hello world", null);

        assertThat(response1.isSuccess()).isTrue();
        assertThat(response1.code()).isEqualTo(200);
        assertThat(response1.entity()).get().isEqualTo("hello world");
        assertThat(response1.error()).isNotPresent();
        assertThat(response1.body()).isEqualTo("hello world");

        final MasterResponse<String> response2 = MasterResponse.create(false, 400, null, "error".getBytes(UTF_8));

        assertThat(response2.isSuccess()).isFalse();
        assertThat(response2.code()).isEqualTo(400);
        assertThat(response2.entity()).isNotPresent();
        assertThat(response2.error()).get().isEqualTo("error".getBytes(UTF_8));
        assertThat(response2.body()).isEqualTo("error".getBytes(UTF_8));
    }
}