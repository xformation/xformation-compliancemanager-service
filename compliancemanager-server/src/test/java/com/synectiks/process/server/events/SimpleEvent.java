/*
 * */
package com.synectiks.process.server.events;

import java.util.Objects;

public class SimpleEvent {
    public String payload;

    public SimpleEvent() {}

    public SimpleEvent(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "payload=" + payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleEvent event = (SimpleEvent) o;
        return Objects.equals(payload, event.payload);
    }

    @Override
    public int hashCode() {
        return payload != null ? payload.hashCode() : 0;
    }
}