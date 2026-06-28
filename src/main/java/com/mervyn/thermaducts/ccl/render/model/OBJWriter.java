package com.mervyn.dynamiducts.ccl.render.model;

import com.mervyn.dynamiducts.ccl.render.CCModel;
import com.mervyn.dynamiducts.ccl.vec.Vector3;
import com.mervyn.dynamiducts.ccl.vec.uv.UV;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Exporter class for outputting CCModel instances to Wavefront OBJ and MTL file formats.
 *
 * <p>Created by covers1624 on 16/4/22.
 */
public class OBJWriter {

  /**
   * Exports a Map of CCModels to a File.
   *
   * @param models Map of models to export.
   * @param p PrintWriter to write the model to.
   */
  public static void exportObj(Map<String, CCModel> models, PrintWriter p) {
    exportObj(models, p, null);
  }

  /**
   * Exports a Map of CCModels to an OBJ File, referencing a Material Library.
   *
   * @param models Map of models to export.
   * @param p PrintWriter to write the model to.
   * @param mtlLibName Optional filename of the MTL library to reference.
   */
  public static void exportObj(Map<String, CCModel> models, PrintWriter p, String mtlLibName) {
    if (mtlLibName != null) {
      p.println("mtllib " + mtlLibName);
      p.println();
    }
    List<Vector3> verts = new ArrayList<>();
    List<UV> uvs = new ArrayList<>();
    List<Vector3> normals = new ArrayList<>();
    List<int[]> polys = new ArrayList<>();
    for (Map.Entry<String, CCModel> e : models.entrySet()) {
      p.println("g " + e.getKey());
      if (mtlLibName != null) {
        p.println("usemtl " + e.getKey());
      }
      CCModel m = e.getValue();

      int vStart = verts.size();
      int uStart = uvs.size();
      int nStart = normals.size();
      polys.clear();

      Vector3[] mNormals = m.normals();
      boolean hasNormals = mNormals != null;
      for (int i = 0; i < m.verts.length; i++) {
        int[] ia = new int[hasNormals ? 3 : 2];
        ia[0] = addIndex(verts, m.verts[i].vec);
        ia[1] = addIndex(uvs, m.verts[i].uv);
        if (hasNormals) {
          ia[2] = addIndex(normals, mNormals[i]);
        }
        polys.add(ia);
      }

      if (vStart < verts.size()) {
        p.println();
        for (int i = vStart; i < verts.size(); i++) {
          Vector3 v = verts.get(i);
          p.format("v %s %s %s\n", clean(v.x), clean(v.y), clean(v.z));
        }
      }
      if (uStart < uvs.size()) {
        p.println();
        for (int i = uStart; i < uvs.size(); i++) {
          UV uv = uvs.get(i);
          p.format("vt %s %s\n", clean(uv.u), clean(uv.v));
        }
      }
      if (nStart < normals.size()) {
        p.println();
        for (int i = nStart; i < normals.size(); i++) {
          Vector3 n = normals.get(i);
          p.format("vn %s %s %s\n", clean(n.x), clean(n.y), clean(n.z));
        }
      }

      p.println();
      for (int i = 0; i < polys.size(); i++) {
        if (i % m.vp == 0) {
          p.format("f");
        }
        int[] ia = polys.get(i);
        if (hasNormals) {
          p.format(" %d/%d/%d", ia[0], ia[1], ia[2]);
        } else {
          p.format(" %d/%d", ia[0], ia[1]);
        }
        if (i % m.vp == m.vp - 1) {
          p.println();
        }
      }
    }
  }

  /**
   * Exports a Material Template Library (MTL) file for the specified models.
   *
   * @param modelNames The names of the models to generate materials for.
   * @param p PrintWriter to write the MTL definitions to.
   */
  public static void exportMtl(Iterable<String> modelNames, PrintWriter p) {
    for (String name : modelNames) {
      p.println("newmtl " + name);
      p.println("Ka 1.000 1.000 1.000"); // Ambient color
      p.println("Kd 1.000 1.000 1.000"); // Diffuse color
      p.println("Ks 0.000 0.000 0.000"); // Specular color
      p.println("d 1.0"); // Dissolve (opacity)
      p.println("illum 2"); // Illumination model
      p.println();
    }
  }

  private static <T> int addIndex(List<T> list, T elem) {
    int i = list.indexOf(elem) + 1;
    if (i == 0) {
      list.add(elem);
      i = list.size();
    }
    return i;
  }

  private static String clean(double d) {
    return d == (int) d ? Integer.toString((int) d) : Double.toString(d);
  }
}
