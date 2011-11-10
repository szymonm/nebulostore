package pl.edu.uw.mimuw.nebulostore.appcore;

/**
 * Base class for messages.
 *
 */
public abstract class Message extends net.jxta.endpoint.Message {

  public Message() {
    msgId_ = "temp";
  }

  public Message(String msgID) {
    msgId_ = msgID;
  }

  public void accept(MessageVisitor visitor) {
    visitor.visit(this);
  }

  public Module getHandler() throws Exception {
    // TODO(bolek): Change it into a more specific exception type.
    throw new Exception("This is not an initializing message!");
  }

  public String getId() {
    return msgId_;
  }

  private static final long serialVersionUID = 110048388908555817L;

  /*
   * To be used in merging queues mechanism
   */
  public String queueURI_;
  protected String msgId_;
}
