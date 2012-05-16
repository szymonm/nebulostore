package org.nebulostore.query.filestorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nebulostore.query.executor.ExecutorContext;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

public class FileStorage {

  private static Log log = LogFactory.getLog(FileStorage.class);

  private static HashMap<ExecutorContext, FileStorage> instances_ = new HashMap<ExecutorContext, FileStorage>();

  public static FileStorage getInstance(ExecutorContext context) {
    if (!instances_.containsKey(context)) {
      instances_.put(context, new FileStorage(context));
    }
    return instances_.get(context);
  }

  private final ExecutorContext context_;

  private FileStorage(ExecutorContext context) {
    context_ = context;
  }

  private static String readFileInternal(String path) throws IOException {
    // TODO: Fetching file from the Nebulostore
    //    file = (NebuloFile) NebuloObject.fromAddress(new NebuloAddress(new AppKey(
    //        new BigInteger(tokens[1])), new ObjectId(new BigInteger(tokens[2]))));

    FileInputStream stream = new FileInputStream(new File(path));
    try {
      FileChannel fc = stream.getChannel();
      MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
      /* Instead of using default, pass in a decoder. */
      return Charset.defaultCharset().decode(bb).toString();
    } finally {
      stream.close();
    }
  }

  public String readFile(String path) throws InterpreterException {
    try {
      return FileStorage.readFileInternal(context_.getDataPath() + path);
    } catch (IOException e) {
      log.error(e);
      throw new InterpreterException(e);
    }
  }
}
