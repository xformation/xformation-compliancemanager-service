/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor;

import com.google.common.collect.Maps;
import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.Rule;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.Function;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.statements.Statement;
import com.synectiks.process.common.plugins.pipelineprocessor.codegen.CodeGenerator;
import com.synectiks.process.common.plugins.pipelineprocessor.codegen.GeneratedRule;
import com.synectiks.process.common.plugins.pipelineprocessor.codegen.compiler.JavaCompiler;
import com.synectiks.process.common.plugins.pipelineprocessor.parser.FunctionRegistry;
import com.synectiks.process.common.plugins.pipelineprocessor.parser.PipelineRuleParser;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.streams.Stream;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.rules.TestName;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static com.google.common.collect.ImmutableList.of;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BaseParserTest {
    protected static final AtomicBoolean actionsTriggered = new AtomicBoolean(false);
    protected static FunctionRegistry functionRegistry;
    protected static Stream defaultStream;

    @org.junit.Rule
    public TestName name = new TestName();
    protected PipelineRuleParser parser;

    protected static HashMap<String, Function<?>> commonFunctions() {
        final HashMap<String, Function<?>> functions = Maps.newHashMap();
        functions.put("trigger_test", new AbstractFunction<Void>() {
            @Override
            public Void evaluate(FunctionArgs args, EvaluationContext context) {
                actionsTriggered.set(true);
                return null;
            }

            @Override
            public FunctionDescriptor<Void> descriptor() {
                return FunctionDescriptor.<Void>builder()
                        .name("trigger_test")
                        .returnType(Void.class)
                        .params(of())
                        .build();
            }
        });
        return functions;
    }

    @BeforeClass
    public static void init() {
        defaultStream = mock(Stream.class, "Default stream");
        when(defaultStream.isPaused()).thenReturn(false);
        when(defaultStream.getTitle()).thenReturn("default stream");
        when(defaultStream.getId()).thenReturn(Stream.DEFAULT_STREAM_ID);
    }

    @Before
    public void setup() {
        parser = new PipelineRuleParser(functionRegistry, new CodeGenerator(JavaCompiler::new));
        // initialize before every test!
        actionsTriggered.set(false);
    }

    protected EvaluationContext contextForRuleEval(Rule rule, Message message) {
        final EvaluationContext context = new EvaluationContext(message);
        final GeneratedRule generatedRule = rule.generatedRule();
        if (generatedRule != null) {
            if (generatedRule.when(context)) {
                generatedRule.then(context);
            }
        } else {
            if (rule.when().evaluateBool(context)) {
                for (Statement statement : rule.then()) {
                    statement.evaluate(context);
                }
            }
        }
        return context;
    }

    protected Message evaluateRule(Rule rule, Message message) {
        final EvaluationContext context = new EvaluationContext(message);
        final GeneratedRule generatedRule = rule.generatedRule();
        if (generatedRule != null) {
            if (generatedRule.when(context)) {
                generatedRule.then(context);
                return context.currentMessage();
            } else {
                return null;
            }
        }
        if (rule.when().evaluateBool(context)) {

            for (Statement statement : rule.then()) {
                statement.evaluate(context);
            }
            return context.currentMessage();
        } else {
            return null;
        }
    }

    @Nullable
    protected Message evaluateRule(Rule rule) {
        return evaluateRule(rule, (msg) -> {});
    }

    @Nullable
    protected Message evaluateRule(Rule rule, Consumer<Message> messageModifier) {
        final Message message = new Message("hello test", "source", DateTime.now(DateTimeZone.UTC));
        message.addStream(defaultStream);
        messageModifier.accept(message);
        return evaluateRule(rule, message);
    }

    protected String ruleForTest() {
        try {
            final URL resource = this.getClass().getResource(name.getMethodName().concat(".txt"));
            final Path path = Paths.get(resource.toURI());
            final byte[] bytes = Files.readAllBytes(path);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
