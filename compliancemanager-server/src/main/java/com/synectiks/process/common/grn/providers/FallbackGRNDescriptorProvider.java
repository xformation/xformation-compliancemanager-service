/*
 * */
package com.synectiks.process.common.grn.providers;

import com.synectiks.process.common.grn.GRN;
import com.synectiks.process.common.grn.GRNDescriptor;
import com.synectiks.process.common.grn.GRNDescriptorProvider;

/**
 * Falback provider for GRN types that don't have a custom {@link GRNDescriptorProvider} yet.
 */
public class FallbackGRNDescriptorProvider implements GRNDescriptorProvider {
    @Override
    public GRNDescriptor get(GRN grn) {
        return GRNDescriptor.empty(grn);
    }
}
