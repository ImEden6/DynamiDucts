package com.mervyn.thermaducts.stub;

import com.mervyn.thermaducts.duct.transport.TransportDuctUnit;
import com.mervyn.thermaducts.duct.transport.TransportGrid;
import com.mervyn.thermaducts.duct.transport.TransportRoute;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerLevel;

public class StubTransportGrid extends TransportGrid {

  private List<TransportRoute> routes = new ArrayList<>();

  public StubTransportGrid(ServerLevel level) {
    super(level);
  }

  public void setRoutes(List<TransportRoute> routes) {
    this.routes = routes;
  }

  @Override
  public List<TransportRoute> getRoutesFrom(TransportDuctUnit origin) {
    return routes;
  }

  @Override
  public void onMajorGridChange() {}

  public void reset() {
    routes.clear();
  }
}
