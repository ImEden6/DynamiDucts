package com.mervyn.thermaducts.ccl.render;

import com.mervyn.thermaducts.ccl.render.buffer.TransformingVertexConsumer;
import com.mervyn.thermaducts.ccl.vec.*;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

/**
 * Utility functions for rendering fluids, solid cuboids, outline wireframes, hitboxes, and
 * computing vector/matrix transformations.
 */
public class RenderUtils {

  private static final Vector3[] vectors = new Vector3[8];

  static {
    for (int i = 0; i < vectors.length; i++) {
      vectors[i] = new Vector3();
    }
  }

  // region Fluid Rendering

  /**
   * Gets the standard RenderType used for drawing fluids (translucent block sheet).
   *
   * @return The translucent block sheet RenderType.
   */
  public static RenderType getFluidRenderType() {
    return net.minecraft.client.renderer.Sheets.translucentBlockSheet();
  }

  /**
   * Renders a fluid cuboid into the specified buffer source, scaling the height based on capacity.
   *
   * @param ccrs The CCRenderState.
   * @param mat The transformation matrix.
   * @param renderType The render type for the fluid.
   * @param source The buffer source.
   * @param stack The fluid stack.
   * @param bound The boundary cuboid.
   * @param capacity The fill ratio (0.0 to 1.0).
   * @param res The texture resolution detail divisor.
   */
  public static void renderFluidCuboid(
      CCRenderState ccrs,
      Matrix4 mat,
      RenderType renderType,
      MultiBufferSource source,
      FluidStack stack,
      Cuboid6 bound,
      double capacity,
      double res) {
    if (stack.isEmpty()) {
      return;
    }
    int alpha = 255;
    FluidType type = stack.getFluid().getFluidType();
    if (type.isLighterThanAir()) {
      alpha = (int) (Math.pow(capacity, 0.4) * 255);
    } else {
      bound.max.y = bound.min.y + (bound.max.y - bound.min.y) * capacity;
    }
    var fluidModel =
        Minecraft.getInstance()
            .getModelManager()
            .getFluidStateModelSet()
            .get(stack.getFluid().defaultFluidState());
    TextureAtlasSprite sprite = fluidModel.stillMaterial().sprite();
    ccrs.bind(renderType, source);
    int tint = fluidModel.fluidTintSource().colorAsStack(stack);
    ccrs.baseColour = (tint & 0xFFFFFF) << 8 | alpha;
    makeFluidModel(bound, sprite, res).render(ccrs, mat);
  }

  /**
   * Creates a CCModel representing a fluid cuboid.
   *
   * @param bound The bounding box of the fluid.
   * @param tex The fluid texture sprite.
   * @param res The resolution divisor.
   * @return A CCModel representing the fluid block.
   */
  public static CCModel makeFluidModel(Cuboid6 bound, TextureAtlasSprite tex, double res) {
    CCModel model = CCModel.newModel(VertexFormat.Mode.QUADS);
    List<Vertex5> verts = new ArrayList<>();
    makeFluidCuboid(verts, bound, tex, res);
    model.verts = verts.toArray(new Vertex5[0]);
    return model;
  }

  /**
   * Generates vertex coordinates for all 6 faces of a fluid cuboid.
   *
   * @param vertices The vertex destination list.
   * @param bound The boundary box.
   * @param tex The texture sprite.
   * @param res The resolution divisor.
   */
  public static void makeFluidCuboid(
      List<Vertex5> vertices, Cuboid6 bound, TextureAtlasSprite tex, double res) {
    makeFluidQuadVertices(
        vertices,
        new Vector3(bound.min.x, bound.min.y, bound.min.z),
        new Vector3(bound.max.x, bound.min.y, bound.min.z),
        new Vector3(bound.max.x, bound.min.y, bound.max.z),
        new Vector3(bound.min.x, bound.min.y, bound.max.z),
        tex,
        res);
    makeFluidQuadVertices(
        vertices,
        new Vector3(bound.min.x, bound.max.y, bound.min.z),
        new Vector3(bound.min.x, bound.max.y, bound.max.z),
        new Vector3(bound.max.x, bound.max.y, bound.max.z),
        new Vector3(bound.max.x, bound.max.y, bound.min.z),
        tex,
        res);
    makeFluidQuadVertices(
        vertices,
        new Vector3(bound.min.x, bound.max.y, bound.min.z),
        new Vector3(bound.min.x, bound.min.y, bound.min.z),
        new Vector3(bound.min.x, bound.min.y, bound.max.z),
        new Vector3(bound.min.x, bound.max.y, bound.max.z),
        tex,
        res);
    makeFluidQuadVertices(
        vertices,
        new Vector3(bound.max.x, bound.max.y, bound.max.z),
        new Vector3(bound.max.x, bound.min.y, bound.max.z),
        new Vector3(bound.max.x, bound.min.y, bound.min.z),
        new Vector3(bound.max.x, bound.max.y, bound.min.z),
        tex,
        res);
    makeFluidQuadVertices(
        vertices,
        new Vector3(bound.max.x, bound.max.y, bound.min.z),
        new Vector3(bound.max.x, bound.min.y, bound.min.z),
        new Vector3(bound.min.x, bound.min.y, bound.min.z),
        new Vector3(bound.min.x, bound.max.y, bound.min.z),
        tex,
        res);
    makeFluidQuadVertices(
        vertices,
        new Vector3(bound.min.x, bound.max.y, bound.max.z),
        new Vector3(bound.min.x, bound.min.y, bound.max.z),
        new Vector3(bound.max.x, bound.min.y, bound.max.z),
        new Vector3(bound.max.x, bound.max.y, bound.max.z),
        tex,
        res);
  }

  /**
   * Builds standard quad vertices for fluid faces with texture coordinate interpolation.
   *
   * @param vertices Destination list.
   * @param point1 Start corner vertex.
   * @param point2 Second corner vertex.
   * @param point3 Third corner vertex.
   * @param point4 Fourth corner vertex.
   * @param icon Texture sprite.
   * @param res Resolution.
   */
  public static void makeFluidQuadVertices(
      List<Vertex5> vertices,
      Vector3 point1,
      Vector3 point2,
      Vector3 point3,
      Vector3 point4,
      TextureAtlasSprite icon,
      double res) {
    makeFluidQuadVertices(
        vertices,
        point2,
        vectors[0].set(point4).subtract(point1),
        vectors[1].set(point1).subtract(point2),
        icon,
        res);
  }

  /**
   * Builds standard quad vertices for fluid faces with wide and high vector offsets.
   *
   * @param vertices Destination list.
   * @param base Base position vector.
   * @param wide Width vector offset.
   * @param high Height vector offset.
   * @param icon Texture sprite.
   * @param res Resolution.
   */
  public static void makeFluidQuadVertices(
      List<Vertex5> vertices,
      Vector3 base,
      Vector3 wide,
      Vector3 high,
      TextureAtlasSprite icon,
      double res) {
    Vector3 a = new Vector3();
    Vector3 b = new Vector3();
    Vector3 c = new Vector3();
    Vector3 d = new Vector3();

    double u1 = icon.getU0();
    double du = icon.getU1() - icon.getU0();
    double v2 = icon.getV1();
    double dv = icon.getV1() - icon.getV0();

    double wlen = wide.mag();
    double hlen = high.mag();

    double x = 0;
    while (x < wlen) {
      double rx = wlen - x;
      if (rx > res) {
        rx = res;
      }

      double y = 0;
      while (y < hlen) {
        double ry = hlen - y;
        if (ry > res) {
          ry = res;
        }

        Vector3 dx1 = a.set(wide).multiply(x / wlen);
        Vector3 dx2 = b.set(wide).multiply((x + rx) / wlen);
        Vector3 dy1 = c.set(high).multiply(y / hlen);
        Vector3 dy2 = d.set(high).multiply((y + ry) / hlen);

        vertices.add(
            new Vertex5(
                base.x + dx1.x + dy2.x,
                base.y + dx1.y + dy2.y,
                base.z + dx1.z + dy2.z,
                u1,
                v2 - ry / res * dv));
        vertices.add(
            new Vertex5(
                base.x + dx1.x + dy1.x, base.y + dx1.y + dy1.y, base.z + dx1.z + dy1.z, u1, v2));
        vertices.add(
            new Vertex5(
                base.x + dx2.x + dy1.x,
                base.y + dx2.y + dy1.y,
                base.z + dx2.z + dy1.z,
                (u1 + rx / res * du),
                v2));
        vertices.add(
            new Vertex5(
                base.x + dx2.x + dy2.x,
                base.y + dx2.y + dy2.y,
                base.z + dx2.z + dy2.z,
                (u1 + rx / res * du),
                v2 - ry / res * dv));

        y += ry;
      }

      x += rx;
    }
  }

  // endregion

  // region Solid Cuboid Rendering

  /**
   * Builds a solid cuboid. Expects VertexFormat of POSITION_COLOR. If you need anything more
   * specialized, Use a {@link CCModel}.
   *
   * @param builder The {@link VertexConsumer}
   * @param c The {@link Cuboid6}
   * @param r Red color.
   * @param g Green color.
   * @param b Blue Color.
   * @param a Alpha channel.
   */
  public static void bufferCuboidSolid(
      VertexConsumer builder, Cuboid6 c, float r, float g, float b, float a) {
    builder.addVertex((float) c.min.x, (float) c.max.y, (float) c.min.z).setColor(r, g, b, a);
    builder.addVertex((float) c.max.x, (float) c.max.y, (float) c.min.z).setColor(r, g, b, a);
    builder.addVertex((float) c.max.x, (float) c.min.y, (float) c.min.z).setColor(r, g, b, a);
    builder.addVertex((float) c.min.x, (float) c.min.y, (float) c.min.z).setColor(r, g, b, a);

    builder.addVertex((float) c.min.x, (float) c.min.y, (float) c.max.z).setColor(r, g, b, a);
    builder.addVertex((float) c.max.x, (float) c.min.y, (float) c.max.z).setColor(r, g, b, a);
    builder.addVertex((float) c.max.x, (float) c.max.y, (float) c.max.z).setColor(r, g, b, a);
    builder.addVertex((float) c.min.x, (float) c.max.y, (float) c.max.z).setColor(r, g, b, a);

    builder.addVertex((float) c.min.x, (float) c.min.y, (float) c.min.z).setColor(r, g, b, a);
    builder.addVertex((float) c.max.x, (float) c.min.y, (float) c.min.z).setColor(r, g, b, a);
    builder.addVertex((float) c.max.x, (float) c.min.y, (float) c.max.z).setColor(r, g, b, a);
    builder.addVertex((float) c.min.x, (float) c.min.y, (float) c.max.z).setColor(r, g, b, a);

    builder.addVertex((float) c.min.x, (float) c.max.y, (float) c.max.z).setColor(r, g, b, a);
    builder.addVertex((float) c.max.x, (float) c.max.y, (float) c.max.z).setColor(r, g, b, a);
    builder.addVertex((float) c.max.x, (float) c.max.y, (float) c.min.z).setColor(r, g, b, a);
    builder.addVertex((float) c.min.x, (float) c.max.y, (float) c.min.z).setColor(r, g, b, a);

    builder.addVertex((float) c.min.x, (float) c.min.y, (float) c.max.z).setColor(r, g, b, a);
    builder.addVertex((float) c.min.x, (float) c.max.y, (float) c.max.z).setColor(r, g, b, a);
    builder.addVertex((float) c.min.x, (float) c.max.y, (float) c.min.z).setColor(r, g, b, a);
    builder.addVertex((float) c.min.x, (float) c.min.y, (float) c.min.z).setColor(r, g, b, a);

    builder.addVertex((float) c.max.x, (float) c.min.y, (float) c.min.z).setColor(r, g, b, a);
    builder.addVertex((float) c.max.x, (float) c.max.y, (float) c.min.z).setColor(r, g, b, a);
    builder.addVertex((float) c.max.x, (float) c.max.y, (float) c.max.z).setColor(r, g, b, a);
    builder.addVertex((float) c.max.x, (float) c.min.y, (float) c.max.z).setColor(r, g, b, a);
  }

  // endregion

  // region Outline Wireframe and Hitbox Rendering

  /**
   * Buffers a bounding hitbox wireframe relative to the camera projection view.
   *
   * @param mat The transformation matrix.
   * @param getter The MultiBufferSource.
   * @param renderInfo The camera render info (for relative offset calculation).
   * @param cuboid The boundary cuboid.
   */
  public static void bufferHitbox(
      Matrix4 mat,
      MultiBufferSource getter,
      net.minecraft.client.Camera renderInfo,
      Cuboid6 cuboid) {
    Vec3 projectedView = renderInfo.position();
    bufferHitBox(
        mat.copy().translate(-projectedView.x, -projectedView.y, -projectedView.z), getter, cuboid);
  }

  /**
   * Buffers a bounding hitbox wireframe.
   *
   * @param mat The transformation matrix.
   * @param getter The MultiBufferSource.
   * @param cuboid The boundary cuboid.
   */
  public static void bufferHitBox(Matrix4 mat, MultiBufferSource getter, Cuboid6 cuboid) {
    VertexConsumer builder =
        new TransformingVertexConsumer(
            getter.getBuffer(net.minecraft.client.renderer.rendertype.RenderTypes.lines()), mat);
    bufferCuboidOutline(
        builder, cuboid.copy().expand(0.0020000000949949026D), 0.0F, 0.0F, 0.0F, 0.4F);
  }

  /**
   * Buffers the 12 wireframe edge lines of a cuboid.
   *
   * @param builder The VertexConsumer.
   * @param c The boundary cuboid.
   * @param r Red.
   * @param g Green.
   * @param b Blue.
   * @param a Alpha.
   */
  public static void bufferCuboidOutline(
      VertexConsumer builder, Cuboid6 c, float r, float g, float b, float a) {
    bufferLinePair(builder, c.min.x, c.min.y, c.min.z, c.max.x, c.min.y, c.min.z, r, g, b, a);
    bufferLinePair(builder, c.max.x, c.min.y, c.min.z, c.max.x, c.min.y, c.max.z, r, g, b, a);
    bufferLinePair(builder, c.max.x, c.min.y, c.max.z, c.min.x, c.min.y, c.max.z, r, g, b, a);
    bufferLinePair(builder, c.min.x, c.min.y, c.max.z, c.min.x, c.min.y, c.min.z, r, g, b, a);
    bufferLinePair(builder, c.min.x, c.max.y, c.min.z, c.max.x, c.max.y, c.min.z, r, g, b, a);
    bufferLinePair(builder, c.max.x, c.max.y, c.min.z, c.max.x, c.max.y, c.max.z, r, g, b, a);
    bufferLinePair(builder, c.max.x, c.max.y, c.max.z, c.min.x, c.max.y, c.max.z, r, g, b, a);
    bufferLinePair(builder, c.min.x, c.max.y, c.max.z, c.min.x, c.max.y, c.min.z, r, g, b, a);
    bufferLinePair(builder, c.min.x, c.min.y, c.min.z, c.min.x, c.max.y, c.min.z, r, g, b, a);
    bufferLinePair(builder, c.max.x, c.min.y, c.min.z, c.max.x, c.max.y, c.min.z, r, g, b, a);
    bufferLinePair(builder, c.max.x, c.min.y, c.max.z, c.max.x, c.max.y, c.max.z, r, g, b, a);
    bufferLinePair(builder, c.min.x, c.min.y, c.max.z, c.min.x, c.max.y, c.max.z, r, g, b, a);
  }

  /**
   * Buffers a VoxelShape wireframe relative to the camera projection view.
   *
   * @param mat The transformation matrix.
   * @param buffers The MultiBufferSource.
   * @param renderInfo The camera render info.
   * @param shape The voxel shape.
   */
  public static void bufferShapeHitBox(
      Matrix4 mat,
      MultiBufferSource buffers,
      net.minecraft.client.Camera renderInfo,
      VoxelShape shape) {
    Vec3 projectedView = renderInfo.position();
    bufferShapeHitBox(
        mat.copy().translate(-projectedView.x, -projectedView.y, -projectedView.z), buffers, shape);
  }

  /**
   * Buffers a VoxelShape wireframe.
   *
   * @param mat The transformation matrix.
   * @param buffers The MultiBufferSource.
   * @param shape The voxel shape.
   */
  public static void bufferShapeHitBox(Matrix4 mat, MultiBufferSource buffers, VoxelShape shape) {
    VertexConsumer builder =
        new TransformingVertexConsumer(
            buffers.getBuffer(net.minecraft.client.renderer.rendertype.RenderTypes.lines()), mat);
    bufferShapeOutline(builder, shape, 0.0F, 0.0F, 0.0F, 0.4F);
  }

  /**
   * Buffers the edges of a VoxelShape using edge outline lines.
   *
   * @param builder The VertexConsumer.
   * @param shape The voxel shape.
   * @param r Red.
   * @param g Green.
   * @param b Blue.
   * @param a Alpha.
   */
  public static void bufferShapeOutline(
      VertexConsumer builder, VoxelShape shape, float r, float g, float b, float a) {
    shape.forAllEdges(
        (x1, y1, z1, x2, y2, z2) -> {
          bufferLinePair(builder, x1, y1, z1, x2, y2, z2, r, g, b, a);
        });
  }

  /** Buffers a single line segment. */
  private static void bufferLinePair(
      VertexConsumer builder,
      double x1,
      double y1,
      double z1,
      double x2,
      double y2,
      double z2,
      float r,
      float g,
      float b,
      float a) {
    Vector3 v1 = vectors[0].set(x1, y1, z1).subtract(x2, y2, z2);
    double d = v1.mag();
    v1.divide(d);
    builder
        .addVertex((float) x1, (float) y1, (float) z1)
        .setColor(r, g, b, a)
        .setNormal((float) v1.x, (float) v1.y, (float) v1.z);
    builder
        .addVertex((float) x2, (float) y2, (float) z2)
        .setColor(r, g, b, a)
        .setNormal((float) v1.x, (float) v1.y, (float) v1.z);
  }

  // endregion

  // region Matrix and Helper Math Operations

  /**
   * Computes a combined translation, scale, and rotation matrix based on an input matrix.
   *
   * @param in Input matrix.
   * @param translation Translation vector.
   * @param rotation Rotation vector.
   * @param scale Scale factor.
   * @return The combined Matrix4.
   */
  public static Matrix4 getMatrix(
      Matrix4 in, Vector3 translation, Rotation rotation, double scale) {
    return in.translate(translation).scale(scale).rotate(rotation);
  }

  /**
   * Computes a combined translation, scale, and rotation matrix.
   *
   * @param translation Translation vector.
   * @param rotation Rotation vector.
   * @param scale Scale factor.
   * @return The combined Matrix4.
   */
  @Deprecated
  public static Matrix4 getMatrix(Vector3 translation, Rotation rotation, double scale) {
    return getMatrix(new Matrix4(), translation, rotation, scale);
  }

  /**
   * Computes the vertical bobbing offset for rendering.
   *
   * @param time Tick/frame time parameter.
   * @return The bobbing y-offset.
   */
  public static float getPearlBob(double time) {
    return (float) Math.sin(time / 25 * 3.141593) * 0.1F;
  }

  /**
   * Gets a rendering time offset based on block position coordinates.
   *
   * @param pos The block position.
   * @return The time offset.
   */
  public static int getTimeOffset(BlockPos pos) {
    return getTimeOffset(pos.getX(), pos.getY(), pos.getZ());
  }

  /**
   * Gets a rendering time offset based on raw 3D coordinates.
   *
   * @param x X coordinate.
   * @param y Y coordinate.
   * @param z Z coordinate.
   * @return The time offset.
   */
  public static int getTimeOffset(int x, int y, int z) {
    return x * 3 + y * 5 + z * 9;
  }

  // endregion
}
