package com.synectiks.process.server.cef;

import java.util.Locale;
import java.util.TimeZone;

/**
 * Interface is used to implement a CEFParser. Mainly for testing purposes.
 */
public interface CEFParser {
  /**
   * Method is used to parse text from a CEF message and convert it to a Message object.
   * Timestamps are parsed with timezone UTC and the root locale (C).
   * @param input CEF formatted string
   * @return
   */
  Message parse(final String input);

  /**
   * Method is used to parse text from a CEF message and convert it to a Message object.
   * @param input CEF formatted string
   * @param timeZone Timezone for the CEF timestamp if none was given
   * @param locale Locale used for parsing the CEF timestamp
   * @return
   */
  Message parse(final String input, final TimeZone timeZone, final Locale locale);
}
