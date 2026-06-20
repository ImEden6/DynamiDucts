package com.mervyn.dynamiducts.ccl.render.pipeline.attribute;

import com.mervyn.dynamiducts.ccl.colour.ColourRGBA;
import com.mervyn.dynamiducts.ccl.render.CCRenderState;
import com.mervyn.dynamiducts.ccl.render.pipeline.VertexAttribute;
import org.jetbrains.annotations.Nullable;

/** Sets colour in CCRS to the specified colour in the model. */
public class ColourAttribute extends VertexAttribute<int[]> {

  public static final AttributeKey<int[]> attributeKey = AttributeKey.create("colour", int[]::new);

  private int @Nullable [] colourRef;

  public ColourAttribute() {
    super(attributeKey);
  }

  @Override
  public boolean load(CCRenderState ccrs) {
    assert ccrs.model != null;

    colourRef = ccrs.model.getAttribute(attributeKey);
    return colourRef != null || !ccrs.model.hasAttribute(attributeKey);
  }

  @Override
  public void operate(CCRenderState ccrs) {
    if (colourRef != null) {
      ccrs.colour = ColourRGBA.multiply(ccrs.baseColour, colourRef[ccrs.vertexIndex]);
    } else {
      ccrs.colour = ccrs.baseColour;
    }
  }
}
