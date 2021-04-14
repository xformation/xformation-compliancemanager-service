/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.parser;

import org.junit.BeforeClass;
import org.junit.Test;

import com.synectiks.process.common.plugins.pipelineprocessor.BaseParserTest;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.Rule;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.AndExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.BooleanExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.ComparisonExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.EqualityExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.LogicalExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.NotExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.OrExpression;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.Function;
import com.synectiks.process.common.plugins.pipelineprocessor.codegen.CodeGenerator;
import com.synectiks.process.common.plugins.pipelineprocessor.codegen.compiler.JavaCompiler;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.conversion.StringConversion;
import com.synectiks.process.common.plugins.pipelineprocessor.parser.FunctionRegistry;
import com.synectiks.process.common.plugins.pipelineprocessor.parser.ParseException;
import com.synectiks.process.common.plugins.pipelineprocessor.parser.PipelineRuleParser;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.Tools;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PrecedenceTest extends BaseParserTest {

    @BeforeClass
    public static void registerFunctions() {
        final Map<String, Function<?>> functions = commonFunctions();

        functions.put(StringConversion.NAME, new StringConversion());
        functionRegistry = new FunctionRegistry(functions);
    }

    @Test
    public void orVsEquality() {
        final Rule rule = parseRule("rule \"test\" when true == false || true then end");
        final LogicalExpression when = rule.when();

        assertThat(when).isInstanceOf(OrExpression.class);
        OrExpression orEprx = (OrExpression) when;

        assertThat(orEprx.left()).isInstanceOf(EqualityExpression.class);
        assertThat(orEprx.right()).isInstanceOf(BooleanExpression.class);
    }

    @Test
    public void andVsEquality() {
        final Rule rule = parseRule("rule \"test\" when true == false && true then end");
        final LogicalExpression when = rule.when();

        assertThat(when).isInstanceOf(AndExpression.class);
        AndExpression andExpr = (AndExpression) when;

        assertThat(andExpr.left()).isInstanceOf(EqualityExpression.class);
        assertThat(andExpr.right()).isInstanceOf(BooleanExpression.class);
    }

    @Test
    public void parenGroup() {
        final Rule rule = parseRule("rule \"test\" when true == (false == false) then end");
        final LogicalExpression when = rule.when();

        assertThat(when).isInstanceOf(EqualityExpression.class);
        EqualityExpression topEqual = (EqualityExpression) when;

        assertThat(topEqual.left()).isInstanceOf(BooleanExpression.class);
        assertThat(topEqual.right()).isInstanceOf(EqualityExpression.class);

        final BooleanExpression trueExpr = (BooleanExpression) topEqual.left();
        assertThat(trueExpr.evaluateBool(null)).isTrue();
        final EqualityExpression falseFalse = (EqualityExpression) topEqual.right();
        assertThat(falseFalse.evaluateBool(null)).isTrue();
    }

    @Test
    public void comparisonVsEqual() {
        final Rule rule = parseRule("rule \"test\" when 1 > 2 == false then end");
        final LogicalExpression when = rule.when();

        assertThat(when).isInstanceOf(EqualityExpression.class);

        EqualityExpression topEqual = (EqualityExpression) when;
        assertThat(topEqual.left()).isInstanceOf(ComparisonExpression.class);
        assertThat(topEqual.right()).isInstanceOf(BooleanExpression.class);
    }

    @Test
    public void notVsAndOr() {
        final Rule rule = parseRule("rule \"test\" when !true && false then end");
        final LogicalExpression when = rule.when();

        assertThat(when).isInstanceOf(AndExpression.class);
        AndExpression and = (AndExpression) when;
        assertThat(and.left()).isInstanceOf(NotExpression.class);
        assertThat(and.right()).isInstanceOf(BooleanExpression.class);
    }

    @Test(expected = ParseException.class)
    public void literalsMustBeQuotedInFieldref() {
        final Rule rule = parseRule("rule \"test\" when to_string($message.true) == to_string($message.false) then end");
    }

    @Test
    public void quotedLiteralInFieldRef() {
        final Rule rule = parseRule("rule \"test\" when to_string($message.`true`) == \"true\" then end");
        final Message message = new Message("hallo", "test", Tools.nowUTC());
        message.addField("true", "true");
        final Message result = evaluateRule(rule, message);

        assertThat(result).isNotNull();
    }

    private static Rule parseRule(String rule) {
        final PipelineRuleParser parser = new PipelineRuleParser(functionRegistry, new CodeGenerator(JavaCompiler::new));
        return parser.parseRule(rule, true);
    }
}
