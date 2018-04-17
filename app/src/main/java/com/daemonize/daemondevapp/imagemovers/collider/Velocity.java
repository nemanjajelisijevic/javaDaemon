package com.daemonize.daemondevapp.imagemovers.collider;



public class Velocity {

    public static class Direction {

        private float coeficientX;
        private float coeficientY;

        public float getCoeficientX() {
            return coeficientX;
        }

        public float getCoeficientY() {
            return coeficientY;
        }

        public Direction(float coeficientX, float coeficientY) {
            this.coeficientX = coeficientX;
            this.coeficientY = coeficientY;
        }
    }

    private Direction direction;
    private float intensity;

    public Direction getDirection() {
        return direction;
    }

    public float getIntensity() {
        return intensity;
    }

    public Velocity(Direction direction, float intensity) {
        this.direction = direction;
        this.intensity = intensity;
    }

}
