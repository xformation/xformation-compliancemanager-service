/*
 * */
package com.synectiks.process.common.grn.providers;

import com.synectiks.process.common.grn.GRN;
import com.synectiks.process.common.grn.GRNDescriptor;
import com.synectiks.process.common.grn.GRNDescriptorProvider;
import com.synectiks.process.common.plugins.views.search.views.ViewService;

import javax.inject.Inject;

public class ViewGRNDescriptorProvider implements GRNDescriptorProvider {
    private final ViewService viewService;

    @Inject
    public ViewGRNDescriptorProvider(ViewService viewService) {
        this.viewService = viewService;
    }

    @Override
    public GRNDescriptor get(GRN grn) {
        return viewService.get(grn.entity())
                .map(viewDTO -> GRNDescriptor.create(grn, viewDTO.title()))
                .orElse(GRNDescriptor.create(grn, "ERROR: View for <" + grn.toString() + "> not found!"));
    }
}
