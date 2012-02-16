package org.nebulostore.query.functions.ql.xml;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nebulostore.query.executor.ExecutorContext;
import org.nebulostore.query.filestorage.FileStorage;
import org.nebulostore.query.functions.CallParametersConditions;
import org.nebulostore.query.functions.DQLFunction;
import org.nebulostore.query.functions.exceptions.FunctionCallException;
import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType;
import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType.DQLPrimitiveTypeEnum;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.datatypes.values.StringValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;
import org.nebulostore.query.privacy.level.PrivateMy;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Load extends DQLFunction {

  private static Log log = LogFactory.getLog(Load.class);

  private static CallParametersConditions conditions_ = CallParametersConditions
      .newBuilder()
      .parameter(0, new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLFile))
      .parametersNumber(1).build();

  public Load(ExecutorContext context) {
    super("Load", conditions_, context);
  }

  @Override
  public IDQLValue call(List<IDQLValue> params) throws FunctionCallException {
    checkParams(params);
    log.debug("Called");
    try {
      String fileContents = FileStorage.getInstance(getContext()).readFile(
          ((StringValue) params.get(0)).getValue());
      // TODO: Proper support of privacy levels here
      return new StringValue(fileContents, PrivateMy.getInstance());
    } catch (InterpreterException e) {
      throw new FunctionCallException(e);
    }

  }

  @Override
  public String help() {
    throw new NotImplementedException();
  }
}
