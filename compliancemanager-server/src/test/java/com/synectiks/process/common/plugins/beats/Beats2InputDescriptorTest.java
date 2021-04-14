/*
 * */
package com.synectiks.process.common.plugins.beats;

import org.junit.Test;

import com.synectiks.process.common.plugins.beats.Beats2Input;

import static org.assertj.core.api.Assertions.assertThat;

public class Beats2InputDescriptorTest {
    @Test
    public void descriptorNameIsCorrect() {
        assertThat(new Beats2Input.Descriptor().getName()).isEqualTo("Beats");
    }
}