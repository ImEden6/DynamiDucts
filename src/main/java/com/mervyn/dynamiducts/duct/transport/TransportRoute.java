package com.mervyn.dynamiducts.duct.transport;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class TransportRoute {

  public final BlockPos destination;
  public final byte exitSide;
  public final List<Direction> path;
  public final int pathWeight;

  public TransportRoute(BlockPos destination, byte exitSide, List<Direction> path, int pathWeight) {
    this.destination = destination;
    this.exitSide = exitSide;
    this.path = List.copyOf(path);
    this.pathWeight = pathWeight;
  }
}
