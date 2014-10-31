package com.censoredsoftware.library.schematic;

public interface World {
    PotentialMaterial getMaterialAt(int X, int Y, int Z);

    void setPoint(Point point, PotentialMaterial material);
}
