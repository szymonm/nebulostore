tree grammar TreeWalker;

options {
  language = Java;
  tokenVocab = DQLGrammar;
  ASTLabelType = CommonTree;
}

@header {
  package org.nebulostore.query.language.interpreter.antlr;
  
  import java.util.Map;
  import java.util.TreeMap;
  import java.util.List;
  import java.util.LinkedList;
  import org.nebulostore.query.functions.DQLFunction;
  
  import org.nebulostore.query.language.interpreter.Location;
  import org.nebulostore.query.language.interpreter.datatypes.IDQLValue;
  import org.nebulostore.query.language.interpreter.datatypes.DQLValue.DQLType;
  
  import org.nebulostore.query.language.interpreter.datatypes.DoubleValue;
  import org.nebulostore.query.language.interpreter.datatypes.BooleanValue;
  import org.nebulostore.query.language.interpreter.datatypes.IntegerValue;
  import org.nebulostore.query.language.interpreter.datatypes.StringValue;
  import org.nebulostore.query.language.interpreter.datatypes.LambdaValue;
  import org.nebulostore.query.language.interpreter.datatypes.JavaValuesGlue;
  
  import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;
  import org.nebulostore.query.language.interpreter.exceptions.TypeException;
  
  import org.nebulostore.query.privacy.PrivacyLevel;
  
}

@members {
  
  private Map<String, Location> environment =  new TreeMap<String, Location>();
  private Map<Location, IDQLValue> store = new TreeMap<Location, IDQLValue>();
  
  private Map<String, DQLFunction> functions = new TreeMap<String, DQLFunction>();
  
  public void insertFunction(DQLFunction function) throws InterpreterException {
    if (functions.containsKey(function.getName())) {
      throw new InterpreterException("Function " + function.getName() + " already defined");
    }
    functions.put(function.getName(), function);  
  }
  
  // TODO: wrap it in class?
  private IDQLValue envGet(String ident) throws InterpreterException {
    if (!environment.containsKey(ident.toLowerCase())) {  
      throw new InterpreterException("Undefined variable " + ident);
    }
    
    if (!store.containsKey(environment.get(ident.toLowerCase()))) {  
      throw new InterpreterException("Environment corruption occured. Undefined store for variable " + ident);
    }   
    
    return store.get(environment.get(ident.toLowerCase()));
  }
  
  private void envPut(String ident, IDQLValue value) {
    Location location = new Location();
    environment.put(ident.toLowerCase(), location);    
    store.put(location, value);
  }
  
  private IDQLValue call(String ident, List<IDQLValue> params) throws InterpreterException
  {
    if (!functions.containsKey(ident.toLowerCase())) {
     throw new InterpreterException("Function " + ident + " not available");
    }    
    return functions.get(ident.toLowerCase()).call(params); 
  }
}


let 
  : ^(ID e=expression) { envPut($ID.text, e); }
  ;


gather_statement 
  : ^('GATHER' let+) 
  ;

forward_statement 
  : ^('FORWARD' INT expression) { envPut("DQL_RESULTS", new IntegerValue(1)); } 
  // TODO: proper handling of queries 
  ;
  
reduce_statement 
  : ^('REDUCE' expression) 
  ;


query :
   gather_statement
   forward_statement
   reduce_statement
  ;
  catch[Throwable t] {    
    System.out.println("Error catch at query level"); 
    t.printStackTrace(); 
  }
  
  
function_call_parameters returns [List<IDQLValue> result] 
  : v=expression rest=function_call_parameters { rest.add(v); result = rest; }
  | { result = new LinkedList<IDQLValue>();}
  ;
  
function_decl_parameters returns [List<String> result]
  : ID rest=function_decl_parameters { rest.add($ID.text); result = rest; }
  | { result = new LinkedList<String>(); }
  ;

type_rule returns [DQLType result]
  : 'INTEGER'              { result=null; }
  | ^('LIST' type_rule)    { result=null; }  
  ;


privacy_decl returns [PrivacyLevel result]
  : 'PRIVATE_MY' { result=null; }
  | 'PUBLIC_MY' { result=null; }
  ;

expression returns [IDQLValue result]
  // arythmetic  
  : ^('+' op1=expression op2=expression)       { result = op1.add(op2); }
  | ^('-' op1=expression op2=expression)       { result = op1.sub(op2); }
  | ^('*' op1=expression op2=expression)       { result = op1.mult(op2); }
  | ^('/' op1=expression op2=expression)       { result = op1.div(op2); }
  | ^('%' op1=expression op2=expression)       { result = op1.mod(op2); }
  | ^(NEGATION e=expression)                   { result = e.numNegation(); }
  
  // comparison
  | ^('='   op1=expression op2=expression)     { result = op1.equals(op2); }
  | ^('!='  op1=expression op2=expression)     { result = op1.notEquals(op2); }
  | ^('<'   op1=expression op2=expression)     { result = op1.less(op2); }
  | ^('<='  op1=expression op2=expression)     { result = op1.lessEquals(op2); }
  | ^('>'   op1=expression op2=expression)     { result = op1.greater(op2); }
  | ^('>='  op1=expression op2=expression)     { result = op1.greaterEquals(op2); }
  
  // boolean operations
  | ^('&&'  op1=expression op2=expression)     { result = op1.and(op2); }
  | ^('||'  op1=expression op2=expression)     { result = op1.or(op2); }
  | ^('not' op1=expression)                    { result = op1.not(); }  
   
  // vars and func_calls  
  | ID                                         { result = envGet($ID.text); }  
  | ^(ID call_params=function_call_parameters) { result = call($ID.text, call_params); }
  | ^('LAMBDA' params=function_decl_parameters ':' EXPRESSION=.*) { result = new LambdaValue(params, $EXPRESSION); }  
  
  // literals support
  | INT                                        { result = new IntegerValue(Integer.parseInt($INT.text)); }
  | DOUBLE                                     { result = new DoubleValue(Double.parseDouble($DOUBLE.text)); }
  | STRING_LITERAL                             { result = new StringValue($STRING_LITERAL.text); }
  // TODO: True/false literals
  
  // privacy handling
  | ^('AS' op=expression type=type_rule) { result = op; }
  | ^('IS' op=expression level=privacy_decl) { result = op; }
  ;
  catch[InterpreterException exc] {    
    System.out.println("Error catch at query level"); 
    exc.printStackTrace(); 
    // TODO: Going into error state with interpreter...
    throw new RuntimeException(exc);
  }
  