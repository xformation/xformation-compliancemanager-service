/*
 * */
package com.synectiks.process.server.contentpacks.facades;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synectiks.process.common.plugins.views.search.db.SearchDbService;
import com.synectiks.process.common.plugins.views.search.views.ViewDTO;
import com.synectiks.process.common.plugins.views.search.views.ViewService;
import com.synectiks.process.server.contentpacks.model.ModelType;
import com.synectiks.process.server.contentpacks.model.ModelTypes;
import com.synectiks.process.server.shared.users.UserService;

import javax.inject.Inject;

public class SearchFacade extends ViewFacade {
    public static final ModelType TYPE_V1 = ModelTypes.SEARCH_V1;

    @Inject
    public SearchFacade(ObjectMapper objectMapper, SearchDbService searchDbService, ViewService viewService, UserService userService) {
        super(objectMapper, searchDbService, viewService, userService);
    }

    @Override
    public ModelType getModelType() {
        return TYPE_V1;
    }


    @Override
    public ViewDTO.Type getDTOType() {
        return ViewDTO.Type.SEARCH;
    }
}
