package com.daemonize.game;

import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.Return;

import java.util.LinkedList;
import java.util.List;

public class GenericNode<T> {

    private T value;
    private List<GenericNode<T>> children;
    private String name;

    public GenericNode<T> setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public List<GenericNode<T>> getChildren() {
        return children;
    }

    public GenericNode(T value) {
        this.value = value;
    }

    public GenericNode(T value, String name) {
        this.value = value;
        this.name = name;
    }

    public GenericNode<T> setValue(T value) {
        this.value = value;
        return this;
    }

    public boolean addChild(GenericNode<T> child) {
        if (children == null) {
            children = new LinkedList<>();
        }
        return children.add(child);
    }

    @FunctionalInterface
    public static interface Action<A> {
        void execute(A arg);
    }

    public static <Tp> void forEach(GenericNode<Tp> root, Action<Tp> action) {
        action.execute(root.value);
        if(root.getChildren() != null)
            for (GenericNode<Tp> child : root.getChildren())
                forEach(child, action);
    }
}
