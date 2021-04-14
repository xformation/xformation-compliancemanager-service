/*
 * */
package com.synectiks.process.server.shared.system.activities;

/**
 * @author Dennis Oelkers <dennis@torch.sh>
 */
public interface ActivityWriter {
    void write(Activity activity);
}
