/*
 * */
package com.synectiks.process.server.versioncheck;

/**
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class VersionResponse {

    public int major;
    public int minor;
    public int patch;

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }

}
