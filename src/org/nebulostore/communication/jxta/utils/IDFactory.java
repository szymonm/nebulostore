package org.nebulostore.communication.jxta.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.PipeID;

/**
 * @author Marcin Walas
 *
 */
public abstract class IDFactory {

  private static final String SEED = "nebuloSeed";

  private static byte[] hash(final String expression) {
    byte[] result;
    MessageDigest digest;
    if (expression == null) {
      throw new IllegalArgumentException("Invalid null expression");
    }
    try {
      digest = MessageDigest.getInstance("SHA1");
    } catch (NoSuchAlgorithmException failed) {
      failed.printStackTrace(System.err);
      RuntimeException failure = new IllegalStateException("Could not get SHA-1 Message");
      failure.initCause(failed);
      throw failure;
    }
    try {
      byte[] expressionBytes = expression.getBytes("UTF-8");
      result = digest.digest(expressionBytes);
    } catch (UnsupportedEncodingException impossible) {
      RuntimeException failure =
          new IllegalStateException("Could not encode expression as UTF8");
      failure.initCause(impossible);
      throw failure;
    }
    return result;
  }

  public static PipeID createPipeID(PeerGroupID pgID, String pipeName) {
    String seed = pipeName + SEED;
    return net.jxta.id.IDFactory.newPipeID(pgID, hash(seed.toLowerCase()));
  }

}
