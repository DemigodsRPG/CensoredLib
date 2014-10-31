package com.censoredsoftware.library.schematic;

public class Point {
    private final int X, Y, Z;
    private final World world;

    public Point(int X, int Y, int Z, World world) {
        this.X = X;
        this.Y = Y;
        this.Z = Z;
        this.world = world;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    public int getZ() {
        return Z;
    }

    public World getWorld() {
        return world;
    }

    public Point add(int X, int Y, int Z) {
        return new Point(this.X + X, this.Y + Y, this.Z + Z, world);
    }
}
