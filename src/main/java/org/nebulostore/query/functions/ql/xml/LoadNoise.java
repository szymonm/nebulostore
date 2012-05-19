package org.nebulostore.query.functions.ql.xml;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.nebulostore.query.executor.ExecutorContext;
import org.nebulostore.query.filestorage.FileStorage;
import org.nebulostore.query.functions.CallParametersConditions;
import org.nebulostore.query.functions.exceptions.FunctionCallException;
import org.nebulostore.query.language.interpreter.datasources.DataSourcesSet;
import org.nebulostore.query.language.interpreter.datasources.FileDataSource;
import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType;
import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType.DQLPrimitiveTypeEnum;
import org.nebulostore.query.language.interpreter.datatypes.values.BooleanValue;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.datatypes.values.StringValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

public class LoadNoise extends AbstractLoad {

  private static Logger logger_ = Logger.getLogger(LoadNoise.class);

  private static CallParametersConditions conditions_ = CallParametersConditions
      .newBuilder()
      .parameter(0, new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLString))
      .parameter(1, new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLString))
      .parameter(2, new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLBoolean))
      .parametersNumber(3).build();

  public LoadNoise(ExecutorContext context) {
    super("Load_Noise", conditions_, context);
  }

  @Override
  public IDQLValue call(List<IDQLValue> params) throws FunctionCallException {
    checkParams(params);

    logger_.debug("Called");
    try {

      String fileName = ((StringValue) params.get(0)).getValue();
      String fileContents = FileStorage.getInstance(getContext()).readFile(
          fileName);

      String queryPath = ((StringValue) params.get(1)).getValue();
      boolean deserializeAsList = ((BooleanValue) params.get(2)).getValue();
      return perform(fileContents, fileName, queryPath, deserializeAsList);

    } catch (InterpreterException e) {
      throw new FunctionCallException(e);
    }

  }

  public IDQLValue perform(String fileContents, String fileName,
      String queryPath, boolean deserializeAsList) throws InterpreterException {

    LoadQuery query = buildQuery(queryPath);
    DataSourcesSet dataSources = new DataSourcesSet();
    dataSources.add(FileDataSource.getInstance(fileName, queryPath));

    return executeQuery(fileContents, query.toXPath(), deserializeAsList,
        dataSources, true);
  }

  @Override
  public String help() {
    throw new NotImplementedException();
  }
}
