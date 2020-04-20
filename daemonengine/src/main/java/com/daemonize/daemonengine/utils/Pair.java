package com.daemonize.daemonengine.utils;

import java.util.Objects;

public final class Pair<K, V> {

    private K first;
    private V second;

    public static <K, V> Pair<K, V> create(K first, V second) {
        return new Pair<>(first, second);
    }

    public Pair(){}

    private Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }

    public Pair<K, V> setFirst(K first) {
        this.first = first;
        return this;
    }

    public Pair<K, V> setSecond(V second) {
        this.second = second;
        return this;
    }

    public K getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) {
            return false;
        }
        Pair<?, ?> p = (Pair<?, ?>) o;
        return Objects.equals(p.first, first) && Objects.equals(p.second, second);
    }
//
//    @Override
//    public int hashCode() {
//        return (first == null ? 0 : first.hashCode()) ^ (second == null ? 0 : second.hashCode());
//    }

    @Override
    public String toString() {
        return "Pair{" + String.valueOf(first) + ", " + String.valueOf(second) + "}";
    }
}