package com.censoredsoftware.library.schematic;

import java.util.Objects;

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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Point && ((Point) obj).X == X && ((Point) obj).Y == Y && ((Point) obj).Z == Z && ((Point) obj).world.getName().equals(world.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(X, Y, Z, world.getName());
    }
}
