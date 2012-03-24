package org.nebulostore.communication.streambinding;

import java.io.IOException;
import java.io.OutputStream;

import org.nebulostore.communication.address.CommAddress;

/**
 * @author Marcin Walas
 *
 */
public interface IStreamBindingDriver {


  /**
   * @param address -- destination address
   * @param jobId -- less than 255 bytes long string
   * @param streamId -- less than 255 bytes long string
   * @param timeout
   * @return
   * @throws IOException
   */
  OutputStream bindStream(CommAddress address, String jobId, String streamId, long timeout)
      throws IOException;
}
