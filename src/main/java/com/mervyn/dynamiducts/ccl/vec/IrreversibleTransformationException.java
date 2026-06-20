package com.mervyn.dynamiducts.ccl.vec;

public class IrreversibleTransformationException extends RuntimeException {

  @java.io.Serial private static final long serialVersionUID = 1L;

  public transient ITransformation<?, ?> t;

  public IrreversibleTransformationException(ITransformation<?, ?> t) {
    this.t = t;
  }

  @Override
  public String getMessage() {
    return "The following transformation is irreversible:\n" + t;
  }
}
