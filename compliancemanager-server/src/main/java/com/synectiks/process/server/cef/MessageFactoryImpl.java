package com.synectiks.process.server.cef;

class MessageFactoryImpl implements MessageFactory {
  @Override
  public Message.Builder newBuilder() {
    return new MessageImpl.BuilderImpl();
  }
}
