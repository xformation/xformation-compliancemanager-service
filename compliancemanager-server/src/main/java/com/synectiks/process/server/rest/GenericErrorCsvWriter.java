/*
 * */
package com.synectiks.process.server.rest;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.synectiks.process.server.plugin.rest.ApiError;
import com.synectiks.process.server.plugin.rest.GenericError;
import com.synectiks.process.server.plugin.rest.ValidationApiError;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces(MoreMediaTypes.TEXT_CSV)
public class GenericErrorCsvWriter implements MessageBodyWriter<GenericError> {
    private final CsvMapper mapper;

    public GenericErrorCsvWriter() {
        this.mapper = new CsvMapper();
        this.mapper.enable(CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS);

        // Required for implicit "type" field
        this.mapper.enable(JsonGenerator.Feature.IGNORE_UNKNOWN);
    }

    @Override
    public long getSize(GenericError genericError, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return GenericError.class.isAssignableFrom(type)
                && MoreMediaTypes.TEXT_CSV_TYPE.isCompatible(mediaType);
    }

    @Override
    public void writeTo(GenericError genericError,
                        Class<?> type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {

        // Dirty hack for ValidationApiError which cannot be serialized as CSV because of the nested validationErrors field.
        if (genericError instanceof ValidationApiError) {
            final CsvSchema csvSchema = mapper.typedSchemaFor(ApiError.class).withHeader();
            final ApiError apiError = ApiError.create(genericError.message());
            mapper.writerFor(ApiError.class).with(csvSchema).writeValue(entityStream, apiError);
        } else {
            final CsvSchema csvSchema = mapper.typedSchemaFor(type).withHeader();
            mapper.writerFor(type).with(csvSchema).writeValue(entityStream, genericError);
        }
    }
}
