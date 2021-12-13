package net.bumblecraft.bumblestack.utils;

@FunctionalInterface
public interface MultiConsumer<T, U> {
    public void accept(T t, U u);
}