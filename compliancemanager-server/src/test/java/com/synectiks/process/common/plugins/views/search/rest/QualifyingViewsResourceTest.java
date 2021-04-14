/*
 * */
package com.synectiks.process.common.plugins.views.search.rest;

import com.google.common.collect.ImmutableList;
import com.synectiks.process.common.plugins.views.search.rest.QualifyingViewsResource;
import com.synectiks.process.common.plugins.views.search.rest.ViewsRestPermissions;
import com.synectiks.process.common.plugins.views.search.views.QualifyingViewsService;
import com.synectiks.process.common.plugins.views.search.views.ViewDTO;
import com.synectiks.process.common.plugins.views.search.views.ViewParameterSummaryDTO;
import com.synectiks.process.server.plugin.database.users.User;

import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QualifyingViewsResourceTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private QualifyingViewsService qualifyingViewsService;

    @Mock
    private Subject subject;

    @Mock
    private User currentUser;

    class QualifyingViewsTestResource extends QualifyingViewsResource {
        private final Subject subject;

        QualifyingViewsTestResource(QualifyingViewsService qualifyingViewsService, Subject subject) {
            super(qualifyingViewsService);
            this.subject = subject;
        }

        @Override
        protected Subject getSubject() {
            return this.subject;
        }

        @Nullable
        @Override
        protected User getCurrentUser() {
            return currentUser;
        }
    }

    private QualifyingViewsResource qualifyingViewsResource;

    @Before
    public void setUp() throws Exception {
        this.qualifyingViewsResource = new QualifyingViewsTestResource(qualifyingViewsService, subject);
    }

    @Test
    public void returnsNoViewsIfNoneArePresent() {
        when(qualifyingViewsService.forValue()).thenReturn(Collections.emptyList());

        final Collection<ViewParameterSummaryDTO> result = this.qualifyingViewsResource.forParameter();

        assertThat(result).isEmpty();
    }

    @Test
    public void returnsNoViewsIfNoneArePermitted() {
        final ViewParameterSummaryDTO view1 = mock(ViewParameterSummaryDTO.class);
        when(view1.id()).thenReturn("view1");
        when(view1.type()).thenReturn(ViewDTO.Type.SEARCH);
        when(subject.isPermitted(ViewsRestPermissions.VIEW_READ + ":view1")).thenReturn(false);
        final ViewParameterSummaryDTO view2 = mock(ViewParameterSummaryDTO.class);
        when(view2.id()).thenReturn("view2");
        when(view2.type()).thenReturn(ViewDTO.Type.SEARCH);
        when(subject.isPermitted(ViewsRestPermissions.VIEW_READ + ":view2")).thenReturn(false);
        when(qualifyingViewsService.forValue()).thenReturn(ImmutableList.of(view1, view2));

        final Collection<ViewParameterSummaryDTO> result = this.qualifyingViewsResource.forParameter();

        assertThat(result).isEmpty();
    }

    @Test
    public void returnsSomeViewsIfSomeArePermitted() {
        final ViewParameterSummaryDTO view1 = mock(ViewParameterSummaryDTO.class);
        when(view1.id()).thenReturn("view1");
        when(view1.type()).thenReturn(ViewDTO.Type.SEARCH);
        when(subject.isPermitted(ViewsRestPermissions.VIEW_READ + ":view1")).thenReturn(false);
        final ViewParameterSummaryDTO view2 = mock(ViewParameterSummaryDTO.class);
        when(view2.id()).thenReturn("view2");
        when(view2.type()).thenReturn(ViewDTO.Type.SEARCH);
        when(subject.isPermitted(ViewsRestPermissions.VIEW_READ + ":view2")).thenReturn(true);
        when(qualifyingViewsService.forValue()).thenReturn(ImmutableList.of(view1, view2));

        final Collection<ViewParameterSummaryDTO> result = this.qualifyingViewsResource.forParameter();

        assertThat(result).containsExactly(view2);
    }

    @Test
    public void returnsAllViewsIfAllArePermitted() {
        final ViewParameterSummaryDTO view1 = mock(ViewParameterSummaryDTO.class);
        when(view1.id()).thenReturn("view1");
        when(subject.isPermitted(ViewsRestPermissions.VIEW_READ + ":view1")).thenReturn(true);
        final ViewParameterSummaryDTO view2 = mock(ViewParameterSummaryDTO.class);
        when(view2.id()).thenReturn("view2");
        when(subject.isPermitted(ViewsRestPermissions.VIEW_READ + ":view2")).thenReturn(true);
        when(qualifyingViewsService.forValue()).thenReturn(ImmutableList.of(view1, view2));

        final Collection<ViewParameterSummaryDTO> result = this.qualifyingViewsResource.forParameter();

        assertThat(result).contains(view1, view2);
    }
}
