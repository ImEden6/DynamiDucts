package com.mervyn.dynamiducts.ccl.render.model;

import com.mervyn.dynamiducts.ccl.render.CCModel;
import com.mervyn.dynamiducts.ccl.vec.Vertex5;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/** Test utility for verifying the Wavefront OBJ and MTL export functionality. */
public class OBJWriterTest {

  public static void main(String[] args) {
    runTest();
  }

  public static void runTest() {
    System.out.println("Running OBJWriterTest...");

    // Create a simple quad model
    CCModel model = CCModel.newModel(VertexFormat.Mode.QUADS, 4);
    model.verts[0] = new Vertex5(0, 0, 0, 0, 0);
    model.verts[1] = new Vertex5(1, 0, 0, 1, 0);
    model.verts[2] = new Vertex5(1, 1, 0, 1, 1);
    model.verts[3] = new Vertex5(0, 1, 0, 0, 1);

    Map<String, CCModel> models = new HashMap<>();
    models.put("test_quad", model);

    // Test OBJ export with MTL reference
    StringWriter objOut = new StringWriter();
    PrintWriter objWriter = new PrintWriter(objOut);
    OBJWriter.exportObj(models, objWriter, "test.mtl");
    objWriter.flush();
    String objContent = objOut.toString();

    // Test MTL export
    StringWriter mtlOut = new StringWriter();
    PrintWriter mtlWriter = new PrintWriter(mtlOut);
    OBJWriter.exportMtl(models.keySet(), mtlWriter);
    mtlWriter.flush();
    String mtlContent = mtlOut.toString();

    System.out.println("Generated OBJ Content:\n" + objContent);
    System.out.println("Generated MTL Content:\n" + mtlContent);

    // Verification checks
    if (!objContent.contains("mtllib test.mtl")) {
      throw new AssertionError("OBJ content does not reference the material library.");
    }
    if (!objContent.contains("usemtl test_quad")) {
      throw new AssertionError("OBJ content does not reference the correct material.");
    }
    if (!objContent.contains("v 0 0 0")) {
      throw new AssertionError("OBJ content is missing expected vertex.");
    }
    if (!mtlContent.contains("newmtl test_quad")) {
      throw new AssertionError("MTL content does not define the correct material.");
    }

    System.out.println("OBJWriterTest completed successfully!");
  }
}
