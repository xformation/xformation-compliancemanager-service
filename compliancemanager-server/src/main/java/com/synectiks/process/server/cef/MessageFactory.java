package com.synectiks.process.server.cef;

/**
 * This interface allows you to create your own implementation of a message factory. A good use case would be if
 * you wanted to use a different internal storage rather than copy each one of the fields manually. Message.Builder
 * objects do not need to be threadsafe as they are only accessed in the creating thread.
 */
public interface MessageFactory {
  /**
   * Called each time a new message is processed.
   * @return A new builder for the message.
   */
  Message.Builder newBuilder();
}
