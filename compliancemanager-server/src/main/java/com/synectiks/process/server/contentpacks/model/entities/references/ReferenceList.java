/*
 * */
package com.synectiks.process.server.contentpacks.model.entities.references;

import com.google.common.collect.ForwardingList;

import java.util.ArrayList;
import java.util.List;

public class ReferenceList extends ForwardingList<Reference> implements Reference {
    private final List<Reference> list;

    public ReferenceList(List<Reference> list) {
        this.list = list;
    }

    public ReferenceList() {
        this(new ArrayList<>());
    }

    @Override
    protected List<Reference> delegate() {
        return list;
    }
}
