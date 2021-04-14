/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.parser;

import org.junit.Ignore;

import com.synectiks.process.common.plugins.pipelineprocessor.codegen.PipelineClassloader;

@Ignore("code generation disabled")
public class CodegenPipelineRuleParserTest extends PipelineRuleParserTest {

    // runs the same tests as in PipelineRuleParserTest but with dynamic code generation turned on.
    public CodegenPipelineRuleParserTest() {
        classLoader = new PipelineClassloader();
    }
}
