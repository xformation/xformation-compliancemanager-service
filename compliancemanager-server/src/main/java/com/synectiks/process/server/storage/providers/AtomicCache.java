/*
 * */
package com.synectiks.process.server.storage.providers;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class AtomicCache<T> {
    private final AtomicReference<Future<T>> value;

    public AtomicCache() {
        this.value = new AtomicReference<>();
    }

    public T get(Supplier<T> valueSupplier) throws ExecutionException, InterruptedException {
        final CompletableFuture<T> completableFuture = new CompletableFuture<>();
        final Future<T> previous = this.value.getAndAccumulate(completableFuture, (prev, cur) -> prev == null ? cur : prev);
        if (previous == null) {
            try {
                final T newValue = valueSupplier.get();
                completableFuture.complete(newValue);

                return newValue;
            } catch (Exception e) {
                completableFuture.completeExceptionally(e);
                return null;
            }
        } else {
            return previous.get();
        }
    }
}
