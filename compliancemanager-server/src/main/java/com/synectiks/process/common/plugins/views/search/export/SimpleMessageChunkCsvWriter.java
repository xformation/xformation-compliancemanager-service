/*
 * */
package com.synectiks.process.common.plugins.views.search.export;

import au.com.bytecode.opencsv.CSVWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.rest.MoreMediaTypes;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Provider
@Produces(MoreMediaTypes.TEXT_CSV)
public class SimpleMessageChunkCsvWriter implements MessageBodyWriter<SimpleMessageChunk> {

    private static final Logger LOG = LoggerFactory.getLogger(MessageBodyWriter.class);

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return typesMatch(type, genericType) && MoreMediaTypes.TEXT_CSV_TYPE.isCompatible(mediaType);
    }

    private boolean typesMatch(Class<?> type, Type genericType) {
        return SimpleMessageChunk.class.equals(type) || isAutoValueType(type, genericType);
    }

    private boolean isAutoValueType(Class<?> type, Type genericType) {
        return AutoValue_SimpleMessageChunk.class.equals(type) && SimpleMessageChunk.class.equals(genericType);
    }

    @Override
    public void writeTo(
            SimpleMessageChunk chunk,
            Class<?> type, Type genericType,
            Annotation[] annotations,
            MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException, WebApplicationException {

        OutputStreamWriter writer = new OutputStreamWriter(entityStream, StandardCharsets.UTF_8);

        try (CSVWriter csvWriter = new CSVWriter(writer)) {

            writeHeaderIfFirstChunk(chunk, csvWriter);

            writeValues(chunk, csvWriter);

            if (csvWriter.checkError()) {
                LOG.error("Encountered unspecified error when writing message result as CSV, result is likely malformed.");
            }
        }
    }

    private void writeHeaderIfFirstChunk(SimpleMessageChunk chunk, CSVWriter csvWriter) {
        if (chunk.isFirstChunk()) {
            csvWriter.writeNext(chunk.fieldsInOrder().toArray(new String[0]));
        }
    }

    private void writeValues(SimpleMessageChunk chunk, CSVWriter csvWriter) {
        List<String[]> valueMatrix = valuesAsStringsInOrder(chunk);
        csvWriter.writeAll(valueMatrix);
    }

    private List<String[]> valuesAsStringsInOrder(SimpleMessageChunk simpleMessageChunk) {
        return Arrays.stream(simpleMessageChunk.getAllValuesInOrder())
                .map(this::toStringArray)
                .collect(toList());
    }

    private String[] toStringArray(Object[] objects) {
        return Arrays.stream(objects)
                .map(o -> o == null ? null : o.toString())
                .toArray(String[]::new);
    }
}
