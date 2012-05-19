package org.nebulostore.query.functions.ql.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.nebulostore.query.executor.ExecutorContext;
import org.nebulostore.query.functions.CallParametersConditions;
import org.nebulostore.query.functions.DQLFunction;
import org.nebulostore.query.functions.exceptions.FunctionCallException;
import org.nebulostore.query.language.interpreter.datasources.DataSourcesSet;
import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType;
import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType.DQLPrimitiveTypeEnum;
import org.nebulostore.query.language.interpreter.datatypes.DQLType;
import org.nebulostore.query.language.interpreter.datatypes.values.BooleanValue;
import org.nebulostore.query.language.interpreter.datatypes.values.DoubleValue;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

abstract public class AbstractLoad extends DQLFunction {

  private static Logger logger_ = Logger.getLogger(AbstractLoad.class);

  private final XPathFactory xPathFactory_;
  private final DocumentBuilderFactory documentFactory_;

  public AbstractLoad(String name, CallParametersConditions conditions,
      ExecutorContext context) {
    super(name, conditions, context);

    xPathFactory_ = XPathFactory.newInstance();
    documentFactory_ = DocumentBuilderFactory.newInstance();
    // documentFactory_.setNamespaceAware(true);
  }

  protected Document deserializeDocument(String contents)
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

  protected PrivacyLevel getLevel(Document doc, String xPathString,
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

  protected NodeList executeQueryOnDocument(Document doc, String query)
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

  protected LoadQuery buildQuery(String queryPath) throws FunctionCallException {
    return new LoadQuery(queryPath);
  }

  protected IDQLValue executeQuery(String fileContents, String xPath,
      boolean deserializeAsList, DataSourcesSet dataSources, boolean addNoise)
          throws FunctionCallException {

    Document doc = deserializeDocument(fileContents);
    NodeList nodes = executeQueryOnDocument(doc, xPath);

    try {
      return deserializeNodeList(nodes, deserializeAsList, dataSources,
          addNoise);
    } catch (InterpreterException e) {
      throw new FunctionCallException(e);
    }
  }

  private IDQLValue deserializeNodeList(NodeList list, boolean asList,
      DataSourcesSet dataSources, boolean addNoise) throws InterpreterException {

    if ((!asList)) {
      if (list.getLength() == 0 || !isPrimitive(list.item(0))) {
        throw new InterpreterException(
            "Unable to deserialize node. Not primitive, as requested or no node obtained from query." +
                list.item(0));
      } else {
        return deserializePrimitive(list.item(0), dataSources, addNoise);
      }
    } else {

      List<IDQLValue> readed = new LinkedList<IDQLValue>();
      for (int i = 0; i < list.getLength(); i++) {
        if (isPrimitive(list.item(i))) {
          readed.add(deserializePrimitive(list.item(i), dataSources, addNoise));
        } else {
          readed.add(deserializeNodeList(list.item(i).getChildNodes(), true,
              dataSources, addNoise));
        }
      }

      if (readed.size() == 0) {
        // PrivateMy due to the lack of information may be a private information
        return new ListValue(new DQLPrimitiveType(
            DQLPrimitiveTypeEnum.DQLInteger), new PrivateMy());
      }

      DQLType firstType = readed.get(0).getType();
      ListValue ret = new ListValue(firstType, new PublicMy(dataSources));
      for (IDQLValue toAdd : readed)
        ret.add(toAdd);
      return ret;
    }

  }

  private static boolean isPrimitive(Node node) {
    logger_.debug("isPrimitive called  on" + node.toString());
    if (node.hasChildNodes()) {
      NodeList childNodes = node.getChildNodes();
      for (int i = 0; i < childNodes.getLength(); i++) {
        logger_.debug("checking node: " + node + " of name: " +
            childNodes.item(0).getNodeName());
        if (childNodes.item(i).getNodeName().equals("value")) {
          return true;
        }
      }
    }
    return false;
  }

  private IDQLValue deserializePrimitive(Node node, DataSourcesSet dataSources,
      boolean addNoise) throws InterpreterException {

    IDQLValue retValue = null;
    logger_.debug("deserializePrimitive called on node " + node);

    NodeList childNodes = node.getChildNodes();
    Node value = null;
    DQLPrimitiveTypeEnum type = null;
    PrivacyLevel level = new PrivateMy(dataSources);
    PrivacyLevel levelNoise = new PublicMy(dataSources);
    String noise = null;
    for (int i = 0; i < childNodes.getLength(); i++) {
      if (childNodes.item(i).getNodeName().equals("value")) {
        value = childNodes.item(i);
      }
      if (childNodes.item(i).getNodeName().equals("private-conditional-my")) {
        level = new PrivateConditionalMy(dataSources);
      }
      if (childNodes.item(i).getNodeName().equals("public-my")) {
        level = new PublicMy(dataSources);
      }
      if (childNodes.item(i).getNodeName().equals("integer")) {
        type = DQLPrimitiveTypeEnum.DQLInteger;
      }
      if (childNodes.item(i).getNodeName().equals("double")) {
        type = DQLPrimitiveTypeEnum.DQLDouble;
      }
      if (childNodes.item(i).getNodeName().equals("boolean")) {
        type = DQLPrimitiveTypeEnum.DQLBoolean;
      }
      if (childNodes.item(i).getNodeName().equals("string")) {
        type = DQLPrimitiveTypeEnum.DQLString;
      }
      if (childNodes.item(i).getNodeName().equals("noise")) {
        noise = childNodes.item(i).getTextContent().trim();
      }
    }

    if (type == null || value == null) {
      throw new InterpreterException(
          "Unable to deserialize. Value or type not specified.");
    }

    if (addNoise && noise == null) {
      throw new InterpreterException(
          "Noise level not set and requested execution with noise.");
    }

    String upPath = getUpPath(node);
    switch (type) {
    case DQLBoolean:
      if (!addNoise) {
        retValue = new BooleanValue(value.getTextContent().trim()
            .equalsIgnoreCase("true") ? true : false, level);
      } else {
        if (getContext().getNoiseValue(upPath) == null) {
          int rand = getContext().getRandom().nextInt(2);
          retValue = new BooleanValue(rand == 1 ? true : false, levelNoise);
          getContext().setNoiseValue(upPath, retValue);
        }
        retValue = getContext().getNoiseValue(upPath);
      }
      break;
    case DQLDouble:
      double doubleValue = Double.valueOf(value.getTextContent().trim());
      if (!addNoise) {
        retValue = new DoubleValue(doubleValue, level);
      } else {
        if (getContext().getNoiseValue(upPath) == null) {
          double noiseDouble = Double.valueOf(noise);
          double rand = getContext().getRandom().nextDouble() * noiseDouble *
              2 - noiseDouble;
          retValue = new DoubleValue(doubleValue + rand, levelNoise);
          getContext().setNoiseValue(upPath, retValue);
        }
        retValue = getContext().getNoiseValue(upPath);
      }
      break;
    case DQLInteger:
      int intValue = Integer.valueOf(value.getTextContent().trim());
      if (!addNoise) {
        retValue = new IntegerValue(intValue, level);
      } else {
        if (getContext().getNoiseValue(upPath) == null) {
          int noiseInt = Integer.valueOf(noise);
          int rand = getContext().getRandom().nextInt(noiseInt * 2) - noiseInt;
          retValue = new IntegerValue(intValue + rand, levelNoise);
          getContext().setNoiseValue(upPath, retValue);
        }
        retValue = getContext().getNoiseValue(upPath);
      }
      break;
    case DQLString:
      if (!addNoise) {
        retValue = new StringValue(value.getTextContent().trim(), level);
      } else {
        if (getContext().getNoiseValue(upPath) == null) {
          retValue = new StringValue(noise, levelNoise);
          getContext().setNoiseValue(upPath, retValue);
        }
        retValue = getContext().getNoiseValue(upPath);
      }
      break;
    }

    return retValue;

  }

  private static String getUpPath(Node node) {
    String ret = "";
    Node curr = node;
    while (curr != null) {
      ret += curr.getNodeName() + "$%$";
      curr = curr.getParentNode();
    }
    return ret;

  }
}
