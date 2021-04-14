/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.parser.errors;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;

import javax.annotation.Nullable;

public class SyntaxError extends ParseError {

    private final Object offendingSymbol;
    private final int line;
    private final int charPositionInLine;
    private final String msg;
    private final RecognitionException e;

    public SyntaxError(@Nullable Object offendingSymbol, int line, int charPositionInLine, String msg, @Nullable RecognitionException e) {
        super("syntax_error", new ParserRuleContext());

        this.offendingSymbol = offendingSymbol;
        this.line = line;
        this.charPositionInLine = charPositionInLine;
        this.msg = msg;
        this.e = e;
    }

    @Override
    public int line() {
        return line;
    }

    @Override
    public int positionInLine() {
        return charPositionInLine;
    }

    @JsonProperty("reason")
    @Override
    public String toString() {
        return msg;
    }
}
