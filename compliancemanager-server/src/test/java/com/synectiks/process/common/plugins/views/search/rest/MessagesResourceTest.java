/*
 * */
package com.synectiks.process.common.plugins.views.search.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.EventBus;
import com.synectiks.process.common.plugins.views.search.SearchDomain;
import com.synectiks.process.common.plugins.views.search.SearchExecutionGuard;
import com.synectiks.process.common.plugins.views.search.errors.PermissionException;
import com.synectiks.process.common.plugins.views.search.export.AuditContext;
import com.synectiks.process.common.plugins.views.search.export.CommandFactory;
import com.synectiks.process.common.plugins.views.search.export.ExportMessagesCommand;
import com.synectiks.process.common.plugins.views.search.export.MessagesExporter;
import com.synectiks.process.common.plugins.views.search.export.MessagesRequest;
import com.synectiks.process.common.plugins.views.search.rest.MessagesResource;
import com.synectiks.process.common.plugins.views.search.rest.PermittedStreams;
import com.synectiks.process.server.plugin.database.users.User;
import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessagesResourceTest {

    private MessagesResource sut;
    private User currentUser;
    private MessagesExporter exporter;
    private PermittedStreams permittedStreams;
    private SearchExecutionGuard executionGuard;
    private CommandFactory commandFactory;
    @SuppressWarnings("UnstableApiUsage")
    private final EventBus eventBus = mock(EventBus.class);
    private SearchDomain searchDomain;

    @BeforeEach
    void setUp() {
        GuiceInjectorHolder.createInjector(Collections.emptyList());
        currentUser = mock(User.class);
        when(currentUser.getName()).thenReturn("peterchen");
        exporter = mock(MessagesExporter.class);
        commandFactory = mock(CommandFactory.class);
        when(commandFactory.buildFromRequest(any())).thenReturn(ExportMessagesCommand.withDefaults());
        when(commandFactory.buildWithSearchOnly(any(), any())).thenReturn(ExportMessagesCommand.withDefaults());
        when(commandFactory.buildWithMessageList(any(), any(), any())).thenReturn(ExportMessagesCommand.withDefaults());
        permittedStreams = mock(PermittedStreams.class);
        when(permittedStreams.load(any())).thenReturn(ImmutableSet.of("a-default-stream"));
        executionGuard = mock(SearchExecutionGuard.class);
        searchDomain = mock(SearchDomain.class);
        sut = new MessagesTestResource(exporter, commandFactory, searchDomain, executionGuard, permittedStreams, mock(ObjectMapper.class), eventBus);

        sut.asyncRunner = c -> {
            c.accept(x -> {
            });
            return null;
        };
    }

    class MessagesTestResource extends MessagesResource {
        public MessagesTestResource(MessagesExporter exporter, CommandFactory commandFactory, SearchDomain searchDomain, SearchExecutionGuard executionGuard, PermittedStreams permittedStreams, ObjectMapper objectMapper, EventBus eventBus) {
            super(exporter, commandFactory, searchDomain, executionGuard, permittedStreams, objectMapper, eventBus);
        }

        @Nullable
        @Override
        protected User getCurrentUser() {
            return currentUser;
        }
    }

    @Test
    void appliesDefaultStreamsToRequestIfOmitted() {
        MessagesRequest request = validRequest();

        when(permittedStreams.load(any())).thenReturn(ImmutableSet.of("stream-1", "stream-2"));

        ArgumentCaptor<MessagesRequest> captor = ArgumentCaptor.forClass(MessagesRequest.class);

        when(commandFactory.buildFromRequest(captor.capture())).thenReturn(ExportMessagesCommand.withDefaults());

        sut.retrieve(request);

        MessagesRequest value = captor.getValue();
        assertThat(value.streams())
                .containsExactly("stream-1", "stream-2");
    }

    @Test
    void checksStreamPermissionsForPlainRequest() {
        MessagesRequest request = validRequest().toBuilder().streams(ImmutableSet.of("stream-1")).build();

        PermissionException exception = new PermissionException("The wurst is yet to come");
        doThrow(exception).when(executionGuard)
                .checkUserIsPermittedToSeeStreams(eq(ImmutableSet.of("stream-1")), any());

        assertThatExceptionOfType(PermissionException.class).isThrownBy(() -> sut.retrieve(request))
                .withMessageContaining(exception.getMessage());
    }

    @Test
    void passesOnlyUserNameToAuditingExporterIfExportBasedOnRequest() {
        AtomicReference<AuditContext> context = captureAuditContext();

        sut.retrieve(validRequest());

        assertAll(
                () -> assertThat(context.get().userName()).isEqualTo(currentUser.getName()),
                () -> assertThat(context.get().searchId()).isEmpty(),
                () -> assertThat(context.get().searchTypeId()).isEmpty()
        );
    }

    private AtomicReference<AuditContext> captureAuditContext() {
        AtomicReference<AuditContext> captured = new AtomicReference<>();
        sut.messagesExporterFactory = context -> {
            captured.set(context);
            return mock(MessagesExporter.class);
        };
        return captured;
    }

    private MessagesRequest validRequest() {
        return MessagesRequest.builder().build();
    }
}
