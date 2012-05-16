package org.nebulostore.query.functions.ql.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nebulostore.query.executor.ExecutorContext;
import org.nebulostore.query.functions.CallParametersConditions;
import org.nebulostore.query.functions.DQLFunction;
import org.nebulostore.query.functions.exceptions.FunctionCallException;
import org.nebulostore.query.language.interpreter.datasources.DataSourcesSet;
import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType;
import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType.DQLPrimitiveTypeEnum;
import org.nebulostore.query.language.interpreter.datatypes.DQLType;
import org.nebulostore.query.language.interpreter.datatypes.values.BooleanValue;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.datatypes.values.IntegerValue;
import org.nebulostore.query.language.interpreter.datatypes.values.ListValue;
import org.nebulostore.query.language.interpreter.datatypes.values.StringValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;
import org.nebulostore.query.privacy.PrivacyLevel;
import org.nebulostore.query.privacy.level.PrivateConditionalMy;
import org.nebulostore.query.privacy.level.PrivateMy;
import org.nebulostore.query.privacy.level.PublicConditionalMy;
import org.nebulostore.query.privacy.level.PublicMy;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XPath extends DQLFunction {

  private static Log logger_ = LogFactory.getLog(XPath.class);

  private static CallParametersConditions conditions_ = CallParametersConditions
      .newBuilder()
      .parameter(0, new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLString))
      .parameter(1, new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLString))
      .parameter(2, new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLBoolean))
      .parametersNumber(3).build();

  private final XPathFactory xPathFactory_;

  private final DocumentBuilderFactory documentFactory_;

  public XPath(ExecutorContext context) {
    super("xpath", conditions_, context);
    xPathFactory_ = XPathFactory.newInstance();
    documentFactory_ = DocumentBuilderFactory.newInstance();
    documentFactory_.setNamespaceAware(true);
  }

  @Override
  public IDQLValue call(List<IDQLValue> params) throws FunctionCallException {
    checkParams(params);

    String xPathString = ((StringValue) params.get(0)).getValue();
    StringValue fileValue = ((StringValue) params.get(1));

    Document doc = deserializeDocument(fileValue.getValue());
    NodeList results = executeQueryOnDocument(doc, xPathString);

    // TODO: A jak powinno być z levelami w funkcji z NOISE?
    PrivacyLevel level = getLevel(doc, xPathString, fileValue.getPrivacyLevel()
        .getDataSources().freshCopy());

    boolean forceList = ((BooleanValue) params.get(2)).getValue();
    try {
      return deserializeNodeList(results, forceList, level);
    } catch (InterpreterException e) {
      throw new FunctionCallException(e);
    }

  }

  // TODO: Extract to superclass
  private PrivacyLevel getLevel(Document doc, String xPathString,
      DataSourcesSet fileValueSources) throws FunctionCallException {
    String publicMyQuery = xPathString + "/parent::*/privacy/public-my";
    String privateMyQuery = xPathString + "/parent::*/privacy/private-my";
    String privateConditionalQuery = xPathString +
        "/parent::*/privacy/private-cond-my";
    String publicConditionalQuery = xPathString +
        "/parent::*/privacy/public-cond-my";
    if (executeQueryOnDocument(doc, privateMyQuery).getLength() > 0) {
      return new PrivateMy(fileValueSources);
    }
    if (executeQueryOnDocument(doc, privateConditionalQuery).getLength() > 0) {
      return new PrivateConditionalMy(fileValueSources);
    }
    if (executeQueryOnDocument(doc, publicConditionalQuery).getLength() > 0) {
      return new PublicConditionalMy(fileValueSources);
    }
    if (executeQueryOnDocument(doc, publicMyQuery).getLength() > 0) {
      return new PublicMy(fileValueSources);
    }
    return new PrivateMy(fileValueSources);
  }

  // TODO: Extract to superclass
  private NodeList executeQueryOnDocument(Document doc, String query)
      throws FunctionCallException {

    javax.xml.xpath.XPath xpath = xPathFactory_.newXPath();
    XPathExpression expression;
    try {
      expression = xpath.compile(query);
    } catch (XPathExpressionException e) {
      logger_.error(e);
      throw new FunctionCallException(e);
    }
    NodeList results = null;
    try {
      results = (NodeList) expression.evaluate(doc, XPathConstants.NODESET);
    } catch (XPathExpressionException e) {
      logger_.error(e);
      throw new FunctionCallException(e);
    }

    return results;
  }

  // TODO: Extract to superclass
  private Document deserializeDocument(String contents)
      throws FunctionCallException {
    DocumentBuilder builder;
    Document doc;
    try {
      builder = documentFactory_.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      logger_.error(e);
      throw new FunctionCallException(e);
    }
    try {
      doc = builder.parse(new ByteArrayInputStream(contents.getBytes()));
    } catch (SAXException e) {
      logger_.error(e);
      throw new FunctionCallException(e);
    } catch (IOException e) {
      logger_.error(e);
      throw new FunctionCallException(e);
    }

    return doc;
  }

  private static IDQLValue deserializeNodeList(NodeList list, boolean forceList,
      PrivacyLevel level) throws InterpreterException {
    if ((!forceList) && list.getLength() == 1) {
      return deserializePrimitive(list.item(0).getTextContent(), level);
    } else {

      if (list.getLength() == 0) {
        return new ListValue(new DQLPrimitiveType(
            DQLPrimitiveTypeEnum.DQLDouble), level);
      }

      DQLType firstType = deserializePrimitive(list.item(0).getTextContent(),
          level).getType();

      ListValue ret = new ListValue(firstType, level);
      for (int i = 0; i < list.getLength(); i++) {
        ret.add(deserializePrimitive(list.item(i).getTextContent(), level));
      }

      return ret;
    }

  }

  private static IDQLValue deserializePrimitive(String textContent,
      PrivacyLevel level) {
    // TODO: Double, Boolean??
    try {
      return new IntegerValue(Integer.parseInt(textContent), level);
    } catch (Exception e) {
    }
    // TODO: Proper support of privacy levels here
    return new StringValue(textContent, level);
  }

  @Override
  public String help() {
    throw new NotImplementedException();
  }

}
