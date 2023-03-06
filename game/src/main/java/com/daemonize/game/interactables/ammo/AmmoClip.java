package com.daemonize.game.interactables.ammo;

import com.daemonize.game.Projectile;
import com.daemonize.game.ProjectileDaemon;

public interface AmmoClip<P extends Projectile>{

    @FunctionalInterface
    public interface ProjectileLoader<P> {
        void prepare(P projectile);
    }

    int packSize();
    int currentSize();

    AmmoClip<P> setDefaultProjectileLoader(ProjectileLoader<P> defaultrojectileLoader);
    ProjectileDaemon load(ProjectileLoader<P> projectileLoader);
    ProjectileDaemon load();
}
