package com.daemonize.game.interactables.ammo;

import com.daemonize.game.Projectile;

public interface AmmoBank<P extends Projectile> {
    void deposit(AmmoClip<P> clip);
    AmmoClip<P> consume();
}
