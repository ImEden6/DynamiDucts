package com.mervyn.thermaducts.ccl.vec.uv;

import com.mervyn.thermaducts.ccl.render.CCRenderState;
import com.mervyn.thermaducts.ccl.vec.IrreversibleTransformationException;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jetbrains.annotations.Nullable;

public class MultiIconTransformation extends UVTransformation {

  public TextureAtlasSprite[] icons;
  @Nullable private TextureAtlasSprite icon;

  public MultiIconTransformation(TextureAtlasSprite... icons) {
    this.icons = icons;
  }

  public MultiIconTransformation(MultiIconTransformation other) {
    this(other.icons.clone());
    icon = null; // Redundant but shuts Intellij up.
  }

  @Override
  public void operate(CCRenderState ccrs) {
    super.operate(ccrs);
    ccrs.sprite = icon;
  }

  @Override
  public void apply(UV uv) {
    icon = icons[uv.tex % icons.length];
    uv.u = icon.getU((float) uv.u);
    uv.v = icon.getV((float) uv.v);
  }

  @Override
  public UVTransformation inverse() {
    throw new IrreversibleTransformationException(this);
  }

  @Override
  public MultiIconTransformation copy() {
    return new MultiIconTransformation(this);
  }
}
