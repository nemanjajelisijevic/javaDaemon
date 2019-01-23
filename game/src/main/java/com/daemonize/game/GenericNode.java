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

    public boolean addChild(GenericNode<T> child) {
        if (children == null) {
            children = new LinkedList<>();
        }
        return children.add(child);
    }

    public static <K> void forEach(GenericNode<K> root, Closure<K> action) {
        Return<K> actionReturn = new Return<>(root.getValue());
        forEach(actionReturn, root, action);
    }

    private static <K> void forEach(Return<K> ret, GenericNode<K> root, Closure<K> action) {
        ret.setResult(root.getValue());
        action.onReturn(ret);
        if(root.getChildren() != null) {
            for (GenericNode<K> child : root.getChildren()) {
                forEach(ret, child, action);
            }
        }
    }
}
