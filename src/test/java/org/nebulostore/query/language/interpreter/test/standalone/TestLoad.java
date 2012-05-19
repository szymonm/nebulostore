package org.nebulostore.query.language.interpreter.test.standalone;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.nebulostore.query.executor.ExecutorContext;
import org.nebulostore.query.functions.ql.xml.Load;
import org.nebulostore.query.functions.ql.xml.LoadNoise;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

public class TestLoad {

  /**
   * @param args
   */
  private static String testFilename = "resources/test/query/trivialTest/1/peerData.xml";
  private static ExecutorContext exectuorContext_ = new ExecutorContext();

  public static void main(String[] args) {

    try {
      test("/peerData/age", true);
      test("/peerData/age", false);
      test("/peerData/friends/friend", true);
      test("/peerData/friends/friend", false);

    } catch (InterpreterException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  private static void test(String query, boolean b) throws InterpreterException {
    Load load = new Load(null);
    IDQLValue perform = load.perform(readFile(testFilename, ""),
        "peerData.xml", query, b);
    System.out.println(perform);

    try {
      LoadNoise loadNoise = new LoadNoise(exectuorContext_);
      perform = loadNoise.perform(readFile(testFilename, ""), "peerData.xml",
          query, b);
      System.out.println(perform);
    } catch (InterpreterException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private static String readFileInternal(String path) throws IOException {
    // TODO: Fetching file from the Nebulostore
    // file = (NebuloFile) NebuloObject.fromAddress(new NebuloAddress(new
    // AppKey(
    // new BigInteger(tokens[1])), new ObjectId(new BigInteger(tokens[2]))));

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

  public static String readFile(String dataPath, String path)
      throws InterpreterException {
    try {
      return readFileInternal(dataPath + path);
    } catch (IOException e) {
      throw new InterpreterException(e);
    }
  }

}
