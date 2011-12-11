/**
 */
package org.nebulostore.replicator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

import org.nebulostore.appcore.DirectoryEntry;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.Module;
import org.nebulostore.appcore.NebuloDir;
import org.nebulostore.appcore.NebuloFile;
import org.nebulostore.appcore.NebuloObject;
import org.nebulostore.appcore.ObjectId;
import org.nebulostore.appcore.Reference;

/**
 * @author szymonmatejczyk
 */
public class Replicator extends Module {

  public Replicator(BlockingQueue<Message> inQueue, BlockingQueue<Message> outQueue) {
    super(inQueue, outQueue);
  }

  @Override
  protected void processMessage(Message msg) {
    // TODO(SZM) should Replicator handle any messages?
  }

  private HashMap<ObjectId, String> filesLocations_ = new HashMap<ObjectId, String>();

  public void storeObject(ObjectId objectId, NebuloObject dataFile) throws SaveException {
    if (filesLocations_.containsKey(objectId)) {
      throw new SaveException();
    }
    String location = getLocationPrefix() + objectId.toString();
    filesLocations_.put(objectId, location);
    updateObject(objectId, dataFile);
  }

  /**
   * Stream methods are temporary and ineffective.
   */
  public void storeObject(ObjectId objectId, InputStream inputStream) throws SaveException {
    try {
      NebuloFile dataFile = (NebuloFile) ((ObjectInputStream) inputStream).readObject();
      storeObject(objectId, dataFile);
    } catch (ClassCastException exception) {
      throw new SaveException();
    } catch (IOException e) {
      throw new SaveException();
    } catch (ClassNotFoundException e) {
      throw new SaveException();
    }
  }

  public void getObject(ObjectId objectId, OutputStream outputStream) {
    NebuloObject dataFile = getObject(objectId);
    if (dataFile == null)
      return;
    try {
      ((ObjectOutputStream) outputStream).writeObject(dataFile);
    } catch (IOException e) {
      return;
    }
  }

  private void updateObject(ObjectId objectId, NebuloObject dataFile) throws SaveException {
    String location = filesLocations_.get(objectId);
    if (location == null)
      throw new SaveException();

    FileOutputStream fos = null;
    ObjectOutputStream oos = null;
    try {
      File f = new File(location);
      f.getParentFile().mkdirs();
      fos = new FileOutputStream(f);
      oos = new ObjectOutputStream(fos);
      oos.writeObject(dataFile);
      oos.close();
    } catch (IOException exception) {
      exception.printStackTrace();
      throw new SaveException();
    }
  }

  private String getLocationPrefix() {
    return "/tmp/nebulostore/store/";
  }

  /**
   * Returns object stored by replicator or null if it's not found.
   * @param objectId
   *          Object's key.
   * @return DataFile.
   */
  public NebuloObject getObject(ObjectId objectId) {
    String location = filesLocations_.get(objectId);
    if (location == null) {
      return null;
    }

    FileInputStream fis = null;
    ObjectInputStream ois = null;
    NebuloFile result = null;
    try {
      fis = new FileInputStream(location);
      ois = new ObjectInputStream(fis);
      result = (NebuloFile) ois.readObject();
      ois.close();
    } catch (IOException exception) {
      exception.printStackTrace();
      return null;
    } catch (ClassNotFoundException exception) {
      exception.printStackTrace();
      return null;
    }
    return result;
  }

  public void deleteObject(ObjectId objectId) {
    String location = filesLocations_.get(objectId);
    if (location == null)
      return;
    filesLocations_.remove(objectId);

    File f = new File(location);
    if (!f.exists())
      return;
    // TODO there should be a way to indicate failure.
    boolean success = f.delete();
    if (!success)
      return;
    // TODO there should be a way to indicate failure
  }

  public void createEmptyDirectory(ObjectId dirKey) throws SaveException {
    storeObject(dirKey, new NebuloDir());
  }

  public Collection<DirectoryEntry> listDirectory(ObjectId dirKey) {
    // TODO(szymon): Should this return encrypted entries? Construct the collection and return it.
    return null;
  }

  public void appendToDirectory(ObjectId dirKey, Reference directoryEntry)
    throws SaveException {
    NebuloDir directory = (NebuloDir) getObject(dirKey);
    // TODO(szymon): Encrypt directoryEntry and add it to map.
    //directory.getEntries().add(directoryEntry);

    updateObject(dirKey, directory);
  }
}
