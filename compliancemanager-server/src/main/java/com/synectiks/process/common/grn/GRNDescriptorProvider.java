/*
 * */
package com.synectiks.process.common.grn;

/**
 * Provides a {@link GRNDescriptor} for the given {@link GRN}.
 */
public interface GRNDescriptorProvider {
    /**
     * Returns the {@link GRNDescriptor} for the given {@link GRN}.
     * @param grn the GRN
     * @return the descriptor for the GRN
     */
    GRNDescriptor get(GRN grn);
}
