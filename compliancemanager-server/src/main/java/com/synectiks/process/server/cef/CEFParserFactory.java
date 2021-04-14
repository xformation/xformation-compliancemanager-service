package com.synectiks.process.server.cef;

public class CEFParserFactory {
  public static CEFParser create() {
    return create(new MessageFactoryImpl());
  }

  public static CEFParser create(MessageFactory messageFactory) {
    return new CEFParserImpl(messageFactory);
  }
}
