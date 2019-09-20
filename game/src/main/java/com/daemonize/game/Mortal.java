package com.daemonize.game;

public interface Mortal<T extends Mortal> {
    int getHp();
    T setHp(int hp);
    T setMaxHp(int maxHp);
    int getMaxHp();
}
