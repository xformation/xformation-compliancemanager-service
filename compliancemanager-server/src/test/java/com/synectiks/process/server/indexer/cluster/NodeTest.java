/*
 * */
package com.synectiks.process.server.indexer.cluster;

import com.github.zafarkhaja.semver.ParseException;
import com.github.zafarkhaja.semver.Version;
import com.synectiks.process.server.indexer.ElasticsearchException;
import com.synectiks.process.server.indexer.cluster.Node;
import com.synectiks.process.server.indexer.cluster.NodeAdapter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

public class NodeTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private NodeAdapter nodeAdapter;

    private Node node;

    @Before
    public void setUp() throws Exception {
        this.node = new Node(nodeAdapter);
    }

    @Test
    public void returnsEmptyOptionalIfAdapterReturnsNoVersion() {
        when(nodeAdapter.version()).thenReturn(Optional.empty());

        final Optional<Version> elasticsearchVersion = node.getVersion();

        assertThat(elasticsearchVersion).isEmpty();
    }

    @Test
    public void retrievingVersionSucceedsIfElasticsearchVersionIsValid() throws Exception {
        when(nodeAdapter.version()).thenReturn(Optional.of("5.4.0"));

        final Optional<Version> elasticsearchVersion = node.getVersion();

        assertThat(elasticsearchVersion).contains(Version.forIntegers(5, 4, 0));
    }

    @Test
    public void retrievingVersionFailsIfElasticsearchVersionIsInvalid() throws Exception {
        when(nodeAdapter.version()).thenReturn(Optional.of("Foobar"));

        assertThatThrownBy(() -> node.getVersion())
                .isInstanceOf(ElasticsearchException.class)
                .hasMessageStartingWith("Unable to parse Elasticsearch version: Foobar")
                .hasCauseInstanceOf(ParseException.class);
    }
}
