package com.censoredsoftware.library.schematic;

public interface World {
    public PotentialMaterial getMaterialAt(int X, int Y, int Z);

    public void setPoint(Point point, PotentialMaterial material);

    public String getName();
}
