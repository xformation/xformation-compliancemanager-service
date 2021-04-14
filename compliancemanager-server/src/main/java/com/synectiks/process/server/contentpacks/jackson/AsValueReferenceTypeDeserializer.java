/*
 * */
package com.synectiks.process.server.contentpacks.jackson;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.AsWrapperTypeDeserializer;

import java.io.IOException;

public class AsValueReferenceTypeDeserializer extends AsWrapperTypeDeserializer {
    public AsValueReferenceTypeDeserializer(JavaType bt, TypeIdResolver idRes, String typePropertyName, boolean typeIdVisible, JavaType defaultImpl) {
        super(bt, idRes, typePropertyName, typeIdVisible, defaultImpl);
    }

    protected AsValueReferenceTypeDeserializer(AsWrapperTypeDeserializer src, BeanProperty property) {
        super(src, property);
    }

    @Override
    public TypeDeserializer forProperty(BeanProperty prop) {
        return (prop == _property) ? this : new AsValueReferenceTypeDeserializer(this, prop);
    }

    protected Object _deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final TreeNode treeNode = p.readValueAsTree();
        final TreeNode typeNode = treeNode.path("type");
        if (!typeNode.isObject()) {

            ctxt.reportWrongTokenException(typeNode.traverse(), JsonToken.START_OBJECT, "expected START_OBJECT before the type information and deserialized value");
        }

        final TreeNode valueNode = typeNode.path("@value");
        if (!valueNode.isValueNode()) {
            ctxt.reportWrongTokenException(typeNode.traverse(), JsonToken.VALUE_STRING, "expected VALUE_STRING as type information and deserialized value");
        }

        final JsonParser jsonParser = valueNode.traverse();
        final String typeId = jsonParser.nextTextValue();
        final JsonDeserializer<Object> deser = _findDeserializer(ctxt, typeId);

        final JsonParser newParser = treeNode.traverse();
        if (newParser.nextToken() != JsonToken.START_OBJECT) {
            ctxt.reportWrongTokenException(newParser, JsonToken.START_OBJECT, "expected START_OBJECT");
        }
        return deser.deserialize(newParser, ctxt);
    }
}
