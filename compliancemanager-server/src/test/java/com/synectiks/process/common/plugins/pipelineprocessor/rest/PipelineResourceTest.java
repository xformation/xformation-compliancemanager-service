/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.rest;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.Pipeline;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.Stage;
import com.synectiks.process.common.plugins.pipelineprocessor.db.PipelineService;
import com.synectiks.process.common.plugins.pipelineprocessor.parser.ParseException;
import com.synectiks.process.common.plugins.pipelineprocessor.parser.PipelineRuleParser;
import com.synectiks.process.common.plugins.pipelineprocessor.rest.PipelineResource;
import com.synectiks.process.common.plugins.pipelineprocessor.rest.PipelineSource;
import com.synectiks.process.common.plugins.pipelineprocessor.rest.StageSource;
import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.ws.rs.BadRequestException;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PipelineResourceTest {
    static {
        GuiceInjectorHolder.createInjector(Collections.emptyList());
    }

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private PipelineRuleParser pipelineRuleParser;

    @Mock
    private PipelineService pipelineService;

    private PipelineResource pipelineResource;

    @Before
    public void setup() {
        pipelineResource = new PipelineResource(pipelineService, pipelineRuleParser);
    }

    @Test
    public void shouldParseAPipelineSuccessfully() {
        final PipelineSource pipelineSource = PipelineSource.builder()
                .source("pipeline \"alertmanager Git Pipline\"\nstage 0 match either\n" +
                        "rule \"geo loc of dev\"\nrule \"open source dev\"\nend")
                .stages(Collections.emptyList())
                .title("alertmanager Git Pipeline")
                .build();
        final SortedSet stages = ImmutableSortedSet.of(
                Stage.builder()
                        .stage(0)
                        .ruleReferences(ImmutableList.of("geo loc of dev", "open source dev"))
                        .matchAll(false)
                        .build()
        );
        final List<StageSource> expectedStages = ImmutableList.of(
                StageSource.create(0, false, ImmutableList.of(
                        "geo loc of dev", "open source dev"
                ))
        );
        final Pipeline pipeline = Pipeline.builder()
                .name("alertmanager Git Pipeline")
                .stages(stages)
                .build();

        when(pipelineRuleParser.parsePipeline(pipelineSource.id(), pipelineSource.source()))
                .thenReturn(pipeline);

        final PipelineSource result = this.pipelineResource.parse(pipelineSource);
        verify(pipelineRuleParser).parsePipeline(pipelineSource.id(), pipelineSource.source());
        assertThat(result.source()).isEqualTo(pipelineSource.source());
        assertThat(result.stages()).isEqualTo(expectedStages);
    }

    @Test
    public void shouldNotParseAPipelineSuccessfullyIfRaisingAnError() {
        final PipelineSource pipelineSource = PipelineSource.builder()
                .source("foo")
                .stages(Collections.emptyList())
                .title("alertmanager Git Pipeline")
                .build();

        when(pipelineRuleParser.parsePipeline(pipelineSource.id(), pipelineSource.source()))
                .thenThrow(new ParseException(Collections.emptySet()));

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> this.pipelineResource.parse(pipelineSource));
    }
}
