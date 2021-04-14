/*
 * */
package com.synectiks.process.common.grn;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Provides GRN descriptor instances.
 */
public class GRNDescriptorService {
    private final Map<GRNType, GRNDescriptorProvider> descriptorProviders;

    @Inject
    public GRNDescriptorService(Map<GRNType, GRNDescriptorProvider> descriptorProviders) {
        this.descriptorProviders = descriptorProviders;
    }

    /**
     * Returns the descriptor instance for the given GRN.
     *
     * @param grn the GRN
     * @return the descriptor instance for the GRN
     */
    public GRNDescriptor getDescriptor(GRN grn) {
        final GRNDescriptorProvider provider = descriptorProviders.get(grn.grnType());
        if (provider == null) {
            throw new IllegalStateException("Missing GRN descriptor provider for GRN type: " + grn.type());
        }
        return provider.get(grn);
    }

    /**
     * Returns descriptors for the given GRN collection.
     *
     * @param grns collection of GRNs
     * @return the descriptors for the given GRNs
     */
    public Set<GRNDescriptor> getDescriptors(Collection<GRN> grns) {
        return grns.stream().map(this::getDescriptor).collect(Collectors.toSet());
    }
}
