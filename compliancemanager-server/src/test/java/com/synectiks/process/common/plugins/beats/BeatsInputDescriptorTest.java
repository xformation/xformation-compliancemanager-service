/*
 * */
package com.synectiks.process.common.plugins.beats;

import org.junit.Test;

import com.synectiks.process.common.plugins.beats.BeatsInput;

import static org.assertj.core.api.Assertions.assertThat;

public class BeatsInputDescriptorTest {
    @Test
    public void descriptorNameIsCorrect() {
        assertThat(new BeatsInput.Descriptor().getName()).isEqualTo("Beats (deprecated)");
    }
}
