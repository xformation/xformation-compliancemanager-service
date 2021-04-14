/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.codegen;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;

public interface GeneratedRule {

    String name();

    boolean when(EvaluationContext context);

    void then(EvaluationContext context);

}
