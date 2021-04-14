/*
 * */
package com.synectiks.process.server.plugin.filters;

import com.synectiks.process.server.plugin.Message;

public interface MessageFilter {
    /**
     * Process a Message
     *
     * @return true if this message should not further be handled (for example for blacklisting purposes)
     */
    boolean filter(Message msg);

    /**
     * @return The name of this filter. Should not include whitespaces or special characters.
     */
    String getName();

    /**
     * For determining the runtime order of the filter, specify a priority.
     * Lower priority values are run earlier, if two filters have the same priority, their name will be compared to
     * guarantee a repeatable order.
     *
     * @return the priority
     */
    int getPriority();
}
