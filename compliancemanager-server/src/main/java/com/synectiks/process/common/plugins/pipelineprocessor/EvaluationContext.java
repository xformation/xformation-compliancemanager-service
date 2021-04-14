/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.server.plugin.EmptyMessages;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.MessageCollection;
import com.synectiks.process.server.plugin.Messages;

import org.joda.time.DateTime;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EvaluationContext {

    private static final EvaluationContext EMPTY_CONTEXT = new EvaluationContext() {
        @Override
        public void addCreatedMessage(Message newMessage) {
            // cannot add messages to empty context
        }

        @Override
        public void define(String identifier, Class type, Object value) {
            // cannot define any variables in empty context
        }
    };

    @Nonnull
    private final Message message;
    @Nullable
    private Map<String, TypedValue> ruleVars;
    @Nullable
    private List<Message> createdMessages;
    @Nullable
    private List<EvalError> evalErrors;

    private EvaluationContext() {
        this(new Message("__dummy", "__dummy", DateTime.parse("2010-07-30T16:03:25Z"))); 
    }

    public EvaluationContext(@Nonnull Message message) {
        this.message = message;
    }

    public void define(String identifier, Class type, Object value) {
        if (ruleVars == null) {
            ruleVars = Maps.newHashMap();
        }
        ruleVars.put(identifier, new TypedValue(type, value));
    }

    public Message currentMessage() {
        return message;
    }

    public TypedValue get(String identifier) {
        if (ruleVars == null) {
            throw new IllegalStateException("Use of undeclared variable " + identifier);
        }
        return ruleVars.get(identifier);
    }

    public Messages createdMessages() {
        if (createdMessages == null) {
            return new EmptyMessages();
        }
        return new MessageCollection(createdMessages);
    }

    public void addCreatedMessage(Message newMessage) {
        if (createdMessages == null) {
            createdMessages = Lists.newArrayList();
        }
        createdMessages.add(newMessage);
    }

    public void clearCreatedMessages() {
        if (createdMessages != null) {
            createdMessages.clear();
        }
    }

    public static EvaluationContext emptyContext() {
        return EMPTY_CONTEXT;
    }

    public void addEvaluationError(int line, int charPositionInLine, @Nullable FunctionDescriptor descriptor, Throwable e) {
        if (evalErrors == null) {
            evalErrors = Lists.newArrayList();
        }
        evalErrors.add(new EvalError(line, charPositionInLine, descriptor, e));
    }

    public boolean hasEvaluationErrors() {
        return evalErrors != null;
    }

    public List<EvalError> evaluationErrors() {
        return evalErrors == null ? Collections.emptyList() : Collections.unmodifiableList(evalErrors);
    }

    public static class TypedValue {
        private final Class type;
        private final Object value;

        public TypedValue(Class type, Object value) {
            this.type = type;
            this.value = value;
        }

        public Class getType() {
            return type;
        }

        public Object getValue() {
            return value;
        }
    }

    public static class EvalError {
        private final int line;
        private final int charPositionInLine;
        @Nullable
        private final FunctionDescriptor descriptor;
        private final Throwable throwable;

        public EvalError(int line, int charPositionInLine, @Nullable FunctionDescriptor descriptor, Throwable throwable) {
            this.line = line;
            this.charPositionInLine = charPositionInLine;
            this.descriptor = descriptor;
            this.throwable = throwable;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            if (descriptor != null) {
                sb.append("In call to function '").append(descriptor.name()).append("' at ");
            } else {
                sb.append("At ");
            }
            return sb.append(line)
                    .append(":")
                    .append(charPositionInLine)
                    .append(" an exception was thrown: ")
                    .append(throwable.getMessage())
                    .toString();
        }
    }
}
