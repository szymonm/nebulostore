package org.planx.xmlstore.routing.operation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.planx.xmlstore.routing.Node;
import org.planx.xmlstore.routing.messaging.KademliaInternalMessage;

/**
 * A message containing the origin node. This class can be subclassed to
 * implement messages that contain more information.
 **/
public abstract class OriginMessage implements KademliaInternalMessage {
  protected Node origin;

  protected OriginMessage() {
  }

  public OriginMessage(Node origin) {
    this.origin = origin;
  }

  public OriginMessage(DataInput in) throws IOException {
    fromStream(in);
  }

  @Override
  public void fromStream(DataInput in) throws IOException {
    origin = new Node(in);
  }

  @Override
  public void toStream(DataOutput out) throws IOException {
    origin.toStream(out);
  }

  public Node getOrigin() {
    return origin;
  }

  @Override
  public abstract byte code();

  @Override
  public abstract String toString();
}
