/*
 * */
package com.synectiks.process.server.shared.security;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.synectiks.process.server.shared.security.RestPermissions;

public class RestPermissionsTest {
    @Test
    public void testReaderBasePermissions() throws Exception {
        final RestPermissions restPermissions = new RestPermissions();

        Assertions.assertThat(restPermissions.readerBasePermissions())
                .hasSize(RestPermissions.READER_BASE_PERMISSION_SELECTION.size());
    }
}
