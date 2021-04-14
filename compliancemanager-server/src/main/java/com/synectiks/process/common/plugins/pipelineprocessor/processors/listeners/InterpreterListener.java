/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.processors.listeners;

import com.synectiks.process.common.plugins.pipelineprocessor.ast.Pipeline;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.Rule;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.Stage;
import com.synectiks.process.server.plugin.Message;

import java.util.Set;

public interface InterpreterListener {
    void startProcessing();
    void finishProcessing();
    void processStreams(Message message, Set<Pipeline> pipelines, Set<String> streams);
    void enterStage(Stage stage);
    void exitStage(Stage stage);
    void evaluateRule(Rule rule, Pipeline pipeline);
    void failEvaluateRule(Rule rule, Pipeline pipeline);
    void satisfyRule(Rule rule, Pipeline pipeline);
    void dissatisfyRule(Rule rule, Pipeline pipeline);
    void executeRule(Rule rule, Pipeline pipeline);
    void finishExecuteRule(Rule rule, Pipeline pipeline);
    void failExecuteRule(Rule rule, Pipeline pipeline);
    void continuePipelineExecution(Pipeline pipeline, Stage stage);
    void stopPipelineExecution(Pipeline pipeline, Stage stage);
}
