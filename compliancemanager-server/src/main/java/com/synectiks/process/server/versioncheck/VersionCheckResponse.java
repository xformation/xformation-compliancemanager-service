/*
 * */
package com.synectiks.process.server.versioncheck;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class VersionCheckResponse {

    public String codename;
    public VersionResponse version;

    @JsonProperty("released_at")
    public String releasedAt;

    @Override
    public String toString() {
        return version + " (" + codename +  ") released at " + releasedAt;
    }

}
