/*
 * */
package com.synectiks.process.server.rest.resources.system.inputs;

import com.google.common.base.Strings;
import com.synectiks.process.server.inputs.Input;
import com.synectiks.process.server.plugin.configuration.ConfigurationRequest;
import com.synectiks.process.server.plugin.configuration.fields.ConfigurationField;
import com.synectiks.process.server.plugin.configuration.fields.TextField;
import com.synectiks.process.server.rest.models.system.inputs.responses.InputSummary;
import com.synectiks.process.server.shared.inputs.InputDescription;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.shared.security.RestPermissions;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Contains functionality that is used in multiple API resources.
 */
public class AbstractInputsResource extends RestResource {

    private final Map<String, InputDescription> availableInputs;

    public AbstractInputsResource(Map<String, InputDescription> availableInputs) {
        this.availableInputs = availableInputs;
    }

    /**
     * @return A {@link InputSummary} JSON value object for the input entity.
     */
    protected InputSummary getInputSummary(Input input) {
        final InputDescription inputDescription = this.availableInputs.get(input.getType());
        final String name = inputDescription != null ? inputDescription.getName() : "Unknown Input (" + input.getType() + ")";
        final ConfigurationRequest configurationRequest = inputDescription != null ? inputDescription.getConfigurationRequest() : null;
        final Map<String, Object> configuration = isPermitted(RestPermissions.INPUTS_EDIT, input.getId()) ?
                input.getConfiguration() : maskPasswordsInConfiguration(input.getConfiguration(), configurationRequest);
        return InputSummary.create(input.getTitle(),
                input.isGlobal(),
                name,
                input.getContentPack(),
                input.getId(),
                input.getCreatedAt(),
                input.getType(),
                input.getCreatorUserId(),
                configuration,
                input.getStaticFields(),
                input.getNodeId());
    }

    protected Map<String, Object> maskPasswordsInConfiguration(Map<String, Object> configuration, ConfigurationRequest configurationRequest) {
        if (configuration == null || configurationRequest == null) {
            return configuration;
        }
        return configuration.entrySet()
                .stream()
                .collect(
                        HashMap::new,
                        (map, entry) -> {
                            final ConfigurationField field = configurationRequest.getField(entry.getKey());
                            if (field instanceof TextField) {
                                final TextField textField = (TextField) field;
                                if (textField.getAttributes().contains(TextField.Attribute.IS_PASSWORD.toString().toLowerCase(Locale.ENGLISH))
                                        && !Strings.isNullOrEmpty((String) entry.getValue())) {
                                    map.put(entry.getKey(), "<password set>");
                                    return;
                                }
                            }
                            map.put(entry.getKey(), entry.getValue());
                        },
                        HashMap::putAll
                );
    }
}
