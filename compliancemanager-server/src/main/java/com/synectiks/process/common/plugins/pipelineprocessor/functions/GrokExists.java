/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;
import com.synectiks.process.server.grok.GrokPatternRegistry;

import static com.google.common.collect.ImmutableList.of;

import javax.inject.Inject;

public class GrokExists extends AbstractFunction<Boolean> {

    private static final Logger log = LoggerFactory.getLogger(GrokExists.class);
    public static final String NAME = "grok_exists";

    private final ParameterDescriptor<String, String> patternParam;
    private final ParameterDescriptor<Boolean, Boolean> doLog;

    private final GrokPatternRegistry grokPatternRegistry;

    @Inject
    public GrokExists(GrokPatternRegistry grokPatternRegistry) {
        this.grokPatternRegistry = grokPatternRegistry;

        patternParam = ParameterDescriptor.string("pattern")
                .description("The Grok Pattern which is to be tested for existance.").build();
        doLog = ParameterDescriptor.bool("log_missing").optional()
                .description("Log if the Grok Pattern is missing. Warning: Switching on this flag can lead" +
                        " to a high volume of logs.").build();
    }

    @Override
    public Boolean evaluate(FunctionArgs args, EvaluationContext context) {
        final String pattern = patternParam.required(args, context);
        final boolean logWhenNotFound = doLog.optional(args, context).orElse(false);

        if (pattern == null) {
            return null;
        }

        final boolean patternExists = grokPatternRegistry.grokPatternExists(pattern);
        if (!patternExists && logWhenNotFound) {
           log.info("Grok Pattern " + pattern + " does not exists.");
        }

        return patternExists;
    }

    @Override
    public FunctionDescriptor<Boolean> descriptor() {
       return FunctionDescriptor.<Boolean>builder()
               .name(NAME)
               .returnType(Boolean.class)
               .params(of(patternParam, doLog))
               .description("Checks if the given Grok pattern exists.")
               .build();
    }
}
