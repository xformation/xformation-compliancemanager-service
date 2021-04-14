/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast.statements;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;

public interface Statement {

    // TODO should this have a return value at all?
    Object evaluate(EvaluationContext context);
}
