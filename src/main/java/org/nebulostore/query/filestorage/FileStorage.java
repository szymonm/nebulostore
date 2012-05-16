package org.nebulostore.query.filestorage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nebulostore.addressing.NebuloAddress;
import org.nebulostore.addressing.ObjectId;
import org.nebulostore.appcore.NebuloFile;
import org.nebulostore.appcore.NebuloObject;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.query.executor.ExecutorContext;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

public class FileStorage {

  private static Logger logger_ = Logger.getLogger(FileStorage.class);

  private static HashMap<ExecutorContext, FileStorage> instances_ = new HashMap<ExecutorContext, FileStorage>();

  public static FileStorage getInstance(ExecutorContext context) {
    if (!instances_.containsKey(context)) {
      instances_.put(context, new FileStorage(context));
    }
    return instances_.get(context);
  }

  private final ExecutorContext context_;
  private Map<String, ObjectId> filesMapping_;

  private FileStorage(ExecutorContext context) {
    context_ = context;
    filesMapping_ = null;
  }

  private void checkFilesMapping() throws InterpreterException {
    if (filesMapping_ == null) {
      try {
        NebuloFile mappingFile = (NebuloFile) NebuloObject
            .fromAddress(new NebuloAddress(context_.getAppKey(), context_
                .getFilesMapObjectId()));

        // TODO: getSize() w pliku?
        int fileSize = 1000000;
        byte[] data = mappingFile.read(0, fileSize);

        ByteArrayInputStream baos = new ByteArrayInputStream(data);
        ObjectInputStream ois = null;
        ois = new ObjectInputStream(baos);
        filesMapping_ = (HashMap<String, ObjectId>) ois.readObject();

        logger_.debug("readed mapping: " + filesMapping_);

      } catch (NebuloException e) {
        logger_.error(
            "Unable to fetch mapping file. Returning InterpreterException.", e);
        throw new InterpreterException(e);
      } catch (IOException e) {
        logger_.error("Unable to to read serialized mapping file. ", e);
        throw new InterpreterException(e);
      } catch (ClassNotFoundException e) {
        logger_.error("Class stored as a mapping file not recognized. ", e);
        throw new InterpreterException(e);
      } catch (Throwable e) {
        logger_.error("Other throwable thrown. ", e);
        throw new InterpreterException(e);
      }

    }
  }

  public String readFile(String path) throws InterpreterException {

    checkFilesMapping();

    if (!filesMapping_.containsKey(path)) {
      String exception = "Files mapping not contains the requested in query path: \"" + path + "\"";
      logger_.error(exception);
      throw new InterpreterException(exception);
    }

    ObjectId objectId = filesMapping_.get(path);
    try {
      NebuloFile dataFile = (NebuloFile) NebuloObject
          .fromAddress(new NebuloAddress(context_.getAppKey(), objectId));
      //TODO: Here also get size
      byte[] data = dataFile.read(0, 100000);
      return new String(data);

    } catch (NebuloException e) {
      logger_.error("Unable to fetch data file due to NebuloException", e);
      throw new InterpreterException(e);
    }
  }
}
