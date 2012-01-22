// $ANTLR 3.3 Nov 30, 2010 12:50:56 /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g 2012-01-22 23:18:49

  package org.nebulostore.query.language.interpreter.antlr;

  import java.util.Collection;  
  import java.util.Collections;
  import java.util.Map;
  import java.util.TreeMap;

  import java.util.LinkedList;
  import java.util.List;
  import java.util.HashMap;
  
  import org.apache.commons.logging.Log;
  import org.apache.commons.logging.LogFactory;  
  
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
  import org.nebulostore.query.privacy.level.PublicMy;  
  


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class TreeWalker extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "NEGATION", "ID", "INT", "EXPONENT", "DOUBLE", "COMMENT", "WS", "ESC_SEQ", "STRING", "CHAR", "HEX_DIGIT", "UNICODE_ESC", "OCTAL_ESC", "STRING_LITERAL", "','", "'LAMBDA'", "':'", "'('", "')'", "'PRIVATE_MY'", "'PUBLIC_MY'", "'PRIVATE_COND_MY'", "'PUBLIC_OTHER'", "'PRIVATE_COND_OTHER'", "'INTEGER'", "'DOUBLE'", "'STRING'", "'TUPLE'", "'<'", "'>'", "'LIST'", "'not'", "'+'", "'-'", "'*'", "'/'", "'%'", "'='", "'!='", "'<='", "'>='", "'&&'", "'||'", "'IS'", "'AS'", "'LET'", "'GATHER'", "'FORWARD'", "'MAX'", "'DEPTH'", "'TO'", "'REDUCE'"
    };
    public static final int EOF=-1;
    public static final int T__18=18;
    public static final int T__19=19;
    public static final int T__20=20;
    public static final int T__21=21;
    public static final int T__22=22;
    public static final int T__23=23;
    public static final int T__24=24;
    public static final int T__25=25;
    public static final int T__26=26;
    public static final int T__27=27;
    public static final int T__28=28;
    public static final int T__29=29;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int T__33=33;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int T__36=36;
    public static final int T__37=37;
    public static final int T__38=38;
    public static final int T__39=39;
    public static final int T__40=40;
    public static final int T__41=41;
    public static final int T__42=42;
    public static final int T__43=43;
    public static final int T__44=44;
    public static final int T__45=45;
    public static final int T__46=46;
    public static final int T__47=47;
    public static final int T__48=48;
    public static final int T__49=49;
    public static final int T__50=50;
    public static final int T__51=51;
    public static final int T__52=52;
    public static final int T__53=53;
    public static final int T__54=54;
    public static final int T__55=55;
    public static final int NEGATION=4;
    public static final int ID=5;
    public static final int INT=6;
    public static final int EXPONENT=7;
    public static final int DOUBLE=8;
    public static final int COMMENT=9;
    public static final int WS=10;
    public static final int ESC_SEQ=11;
    public static final int STRING=12;
    public static final int CHAR=13;
    public static final int HEX_DIGIT=14;
    public static final int UNICODE_ESC=15;
    public static final int OCTAL_ESC=16;
    public static final int STRING_LITERAL=17;

    // delegates
    // delegators


        public TreeWalker(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public TreeWalker(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return TreeWalker.tokenNames; }
    public String getGrammarFileName() { return "/home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g"; }



      private static Log log = LogFactory.getLog(TreeWalker.class);
      
      private Map<String, Location> environment =  new TreeMap<String, Location>();
      private Map<Location, IDQLValue> store = new TreeMap<Location, IDQLValue>();
      
      private Map<String, DQLFunction> functions = new TreeMap<String, DQLFunction>();
      
      public void insertFunction(DQLFunction function) throws InterpreterException {
        if (functions.containsKey(function.getName())) {
          throw new InterpreterException("Function " + function.getName() + " already defined");
        }
        functions.put(function.getName(), function);  
      }
      
      public void insertFunctions(Iterable <DQLFunction> functions) throws InterpreterException {
        for (DQLFunction function : functions) {
          insertFunction(function);
        }
      }
      
      public Collection<DQLFunction> getFunctions() {
        return new LinkedList<DQLFunction> (functions.values());
      }
      
      public void setInEnvironment(Map<String, IDQLValue> values) {
        log.info("Setting environment");
        for (String key : values.keySet()) {
          envPut(key, values.get(key));      
        }  
      }
      
      // TODO: wrap it in class?
      private IDQLValue envGet(String ident) throws InterpreterException {
        
        
        if (!environment.containsKey(ident.toLowerCase())) {      
          throw new InterpreterException("Undefined variable " + ident);
        }
        
        if (!store.containsKey(environment.get(ident.toLowerCase()))) {  
          throw new InterpreterException("Environment corruption occured. Undefined store for variable " + ident);
        }
        log.info("envGet of " + ident + " of value " + store.get(environment.get(ident.toLowerCase())));
        return store.get(environment.get(ident.toLowerCase()));
      }
      
      public Map<String, IDQLValue> getEnvironmentContents() {
        Map<String, IDQLValue> ret = new HashMap<String, IDQLValue>(32);
        for (String key : environment.keySet()) {
          ret.put(key, store.get(environment.get(key)));
        }
        return ret;
      }
      
      private void envPut(String ident, IDQLValue value) {
        log.info("envPut " + ident + " : " + value);
        Location location = new Location();
        environment.put(ident.toLowerCase(), location);    
        store.put(location, value);
      }
      
      private IDQLValue call(String ident, List<IDQLValue> params) throws InterpreterException, RecognitionException
      {
        log.info("call of function " + ident + " started");
      
        if (!functions.containsKey(ident.toLowerCase())) {
         throw new InterpreterException("Function " + ident + " not available");
        }    
        return functions.get(ident.toLowerCase()).call(params); 
      }
      
      private String prepareString(String v) {
        v = v.trim();
        v = v.replaceAll("^\"", "");
        v = v.replaceAll("\"$", "");
        return v;
      }



    // $ANTLR start "let"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:127:1: let : ^( ID e= expression ) ;
    public final void let() throws RecognitionException {
        CommonTree ID1=null;
        IDQLValue e = null;


        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:128:3: ( ^( ID e= expression ) )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:128:5: ^( ID e= expression )
            {
            ID1=(CommonTree)match(input,ID,FOLLOW_ID_in_let61); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_expression_in_let65);
            e=expression();

            state._fsp--;


            match(input, Token.UP, null); 
             envPut((ID1!=null?ID1.getText():null), e); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "let"


    // $ANTLR start "gather_statement"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:132:1: gather_statement : ^( 'GATHER' ( let )+ ) ;
    public final void gather_statement() throws RecognitionException {
        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:133:3: ( ^( 'GATHER' ( let )+ ) )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:133:5: ^( 'GATHER' ( let )+ )
            {
            match(input,50,FOLLOW_50_in_gather_statement84); 

            match(input, Token.DOWN, null); 
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:133:16: ( let )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==ID) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:133:16: let
            	    {
            	    pushFollow(FOLLOW_let_in_gather_statement86);
            	    let();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);


            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "gather_statement"


    // $ANTLR start "forward_statement"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:136:1: forward_statement returns [IDQLValue result] : ^( 'FORWARD' INT ex= expression ) ;
    public final IDQLValue forward_statement() throws RecognitionException {
        IDQLValue result = null;

        IDQLValue ex = null;


        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:137:3: ( ^( 'FORWARD' INT ex= expression ) )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:137:5: ^( 'FORWARD' INT ex= expression )
            {
            match(input,51,FOLLOW_51_in_forward_statement108); 

            match(input, Token.DOWN, null); 
            match(input,INT,FOLLOW_INT_in_forward_statement110); 
            pushFollow(FOLLOW_expression_in_forward_statement114);
            ex=expression();

            state._fsp--;


            match(input, Token.UP, null); 
             result = ex; 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return result;
    }
    // $ANTLR end "forward_statement"


    // $ANTLR start "reduce_statement"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:140:1: reduce_statement returns [IDQLValue result] : ^( 'REDUCE' ex= expression ) ;
    public final IDQLValue reduce_statement() throws RecognitionException {
        IDQLValue result = null;

        IDQLValue ex = null;


        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:141:3: ( ^( 'REDUCE' ex= expression ) )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:141:5: ^( 'REDUCE' ex= expression )
            {
            match(input,55,FOLLOW_55_in_reduce_statement138); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_expression_in_reduce_statement142);
            ex=expression();

            state._fsp--;


            match(input, Token.UP, null); 
             result = ex; 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return result;
    }
    // $ANTLR end "reduce_statement"


    // $ANTLR start "query"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:145:1: query : gather_statement forward_statement reduce_statement ;
    public final void query() throws RecognitionException {
        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:145:7: ( gather_statement forward_statement reduce_statement )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:146:4: gather_statement forward_statement reduce_statement
            {
            pushFollow(FOLLOW_gather_statement_in_query161);
            gather_statement();

            state._fsp--;

            pushFollow(FOLLOW_forward_statement_in_query166);
            forward_statement();

            state._fsp--;

            pushFollow(FOLLOW_reduce_statement_in_query171);
            reduce_statement();

            state._fsp--;


            }

        }
        catch (Throwable t) {
                
                System.out.println("Error catch at query level"); 
                t.printStackTrace(); 
              
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "query"


    // $ANTLR start "function_call_parameters"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:156:1: function_call_parameters returns [List<IDQLValue> result] : (v= expression rest= function_call_parameters | );
    public final List<IDQLValue> function_call_parameters() throws RecognitionException {
        List<IDQLValue> result = null;

        IDQLValue v = null;

        List<IDQLValue> rest = null;


        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:157:3: (v= expression rest= function_call_parameters | )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( ((LA2_0>=NEGATION && LA2_0<=INT)||LA2_0==DOUBLE||LA2_0==STRING_LITERAL||LA2_0==19||(LA2_0>=32 && LA2_0<=33)||(LA2_0>=35 && LA2_0<=48)) ) {
                alt2=1;
            }
            else if ( (LA2_0==UP) ) {
                alt2=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:157:5: v= expression rest= function_call_parameters
                    {
                    pushFollow(FOLLOW_expression_in_function_call_parameters203);
                    v=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_function_call_parameters_in_function_call_parameters207);
                    rest=function_call_parameters();

                    state._fsp--;

                     rest.add(v); result = rest; 

                    }
                    break;
                case 2 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:158:5: 
                    {
                     result = new LinkedList<IDQLValue>();

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return result;
    }
    // $ANTLR end "function_call_parameters"


    // $ANTLR start "function_decl_parameters"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:161:1: function_decl_parameters returns [List<String> result] : ( ID rest= function_decl_parameters | );
    public final List<String> function_decl_parameters() throws RecognitionException {
        List<String> result = null;

        CommonTree ID2=null;
        List<String> rest = null;


        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:162:3: ( ID rest= function_decl_parameters | )
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==ID) ) {
                alt3=1;
            }
            else if ( (LA3_0==20) ) {
                alt3=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:162:5: ID rest= function_decl_parameters
                    {
                    ID2=(CommonTree)match(input,ID,FOLLOW_ID_in_function_decl_parameters234); 
                    pushFollow(FOLLOW_function_decl_parameters_in_function_decl_parameters238);
                    rest=function_decl_parameters();

                    state._fsp--;

                     rest.add((ID2!=null?ID2.getText():null)); result = rest; 

                    }
                    break;
                case 2 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:163:5: 
                    {
                     result = new LinkedList<String>(); 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return result;
    }
    // $ANTLR end "function_decl_parameters"


    // $ANTLR start "type_rule"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:166:1: type_rule returns [DQLType result] : ( 'INTEGER' | ^( 'LIST' type_rule ) );
    public final DQLType type_rule() throws RecognitionException {
        DQLType result = null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:167:3: ( 'INTEGER' | ^( 'LIST' type_rule ) )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==28) ) {
                alt4=1;
            }
            else if ( (LA4_0==34) ) {
                alt4=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:167:5: 'INTEGER'
                    {
                    match(input,28,FOLLOW_28_in_type_rule263); 
                     result=null; 

                    }
                    break;
                case 2 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:168:5: ^( 'LIST' type_rule )
                    {
                    match(input,34,FOLLOW_34_in_type_rule285); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_type_rule_in_type_rule287);
                    type_rule();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result=null; 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return result;
    }
    // $ANTLR end "type_rule"


    // $ANTLR start "privacy_decl"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:172:1: privacy_decl returns [PrivacyLevel result] : ( 'PRIVATE_MY' | 'PUBLIC_MY' );
    public final PrivacyLevel privacy_decl() throws RecognitionException {
        PrivacyLevel result = null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:173:3: ( 'PRIVATE_MY' | 'PUBLIC_MY' )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==23) ) {
                alt5=1;
            }
            else if ( (LA5_0==24) ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:173:5: 'PRIVATE_MY'
                    {
                    match(input,23,FOLLOW_23_in_privacy_decl313); 
                     result=null; 

                    }
                    break;
                case 2 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:174:5: 'PUBLIC_MY'
                    {
                    match(input,24,FOLLOW_24_in_privacy_decl321); 
                     result=null; 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return result;
    }
    // $ANTLR end "privacy_decl"


    // $ANTLR start "expression"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:177:1: expression returns [IDQLValue result] : ( ^( '+' op1= expression op2= expression ) | ^( '-' op1= expression op2= expression ) | ^( '*' op1= expression op2= expression ) | ^( '/' op1= expression op2= expression ) | ^( '%' op1= expression op2= expression ) | ^( NEGATION e= expression ) | ^( '=' op1= expression op2= expression ) | ^( '!=' op1= expression op2= expression ) | ^( '<' op1= expression op2= expression ) | ^( '<=' op1= expression op2= expression ) | ^( '>' op1= expression op2= expression ) | ^( '>=' op1= expression op2= expression ) | ^( '&&' op1= expression op2= expression ) | ^( '||' op1= expression op2= expression ) | ^( 'not' op1= expression ) | ID | ^( ID call_params= function_call_parameters ) | ^( 'LAMBDA' params= function_decl_parameters ':' (EXPRESSION= . )* ) | INT | DOUBLE | STRING_LITERAL | ^( 'AS' op= expression type= type_rule ) | ^( 'IS' op= expression level= privacy_decl ) );
    public final IDQLValue expression() throws RecognitionException {
        IDQLValue result = null;

        CommonTree ID3=null;
        CommonTree ID4=null;
        CommonTree INT5=null;
        CommonTree DOUBLE6=null;
        CommonTree STRING_LITERAL7=null;
        CommonTree EXPRESSION=null;
        IDQLValue op1 = null;

        IDQLValue op2 = null;

        IDQLValue e = null;

        List<IDQLValue> call_params = null;

        List<String> params = null;

        IDQLValue op = null;

        DQLType type = null;

        PrivacyLevel level = null;


        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:179:3: ( ^( '+' op1= expression op2= expression ) | ^( '-' op1= expression op2= expression ) | ^( '*' op1= expression op2= expression ) | ^( '/' op1= expression op2= expression ) | ^( '%' op1= expression op2= expression ) | ^( NEGATION e= expression ) | ^( '=' op1= expression op2= expression ) | ^( '!=' op1= expression op2= expression ) | ^( '<' op1= expression op2= expression ) | ^( '<=' op1= expression op2= expression ) | ^( '>' op1= expression op2= expression ) | ^( '>=' op1= expression op2= expression ) | ^( '&&' op1= expression op2= expression ) | ^( '||' op1= expression op2= expression ) | ^( 'not' op1= expression ) | ID | ^( ID call_params= function_call_parameters ) | ^( 'LAMBDA' params= function_decl_parameters ':' (EXPRESSION= . )* ) | INT | DOUBLE | STRING_LITERAL | ^( 'AS' op= expression type= type_rule ) | ^( 'IS' op= expression level= privacy_decl ) )
            int alt7=23;
            alt7 = dfa7.predict(input);
            switch (alt7) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:179:5: ^( '+' op1= expression op2= expression )
                    {
                    match(input,36,FOLLOW_36_in_expression344); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression348);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression352);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.addNum(op2); 

                    }
                    break;
                case 2 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:180:5: ^( '-' op1= expression op2= expression )
                    {
                    match(input,37,FOLLOW_37_in_expression368); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression372);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression376);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.subNum(op2); 

                    }
                    break;
                case 3 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:181:5: ^( '*' op1= expression op2= expression )
                    {
                    match(input,38,FOLLOW_38_in_expression392); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression396);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression400);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.multNum(op2); 

                    }
                    break;
                case 4 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:182:5: ^( '/' op1= expression op2= expression )
                    {
                    match(input,39,FOLLOW_39_in_expression416); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression420);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression424);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.divNum(op2); 

                    }
                    break;
                case 5 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:183:5: ^( '%' op1= expression op2= expression )
                    {
                    match(input,40,FOLLOW_40_in_expression440); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression444);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression448);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.modNum(op2); 

                    }
                    break;
                case 6 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:184:5: ^( NEGATION e= expression )
                    {
                    match(input,NEGATION,FOLLOW_NEGATION_in_expression464); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression468);
                    e=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = e.numNegation(); 

                    }
                    break;
                case 7 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:187:5: ^( '=' op1= expression op2= expression )
                    {
                    match(input,41,FOLLOW_41_in_expression502); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression508);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression512);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.equals(op2); 

                    }
                    break;
                case 8 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:188:5: ^( '!=' op1= expression op2= expression )
                    {
                    match(input,42,FOLLOW_42_in_expression526); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression531);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression535);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.notEquals(op2); 

                    }
                    break;
                case 9 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:189:5: ^( '<' op1= expression op2= expression )
                    {
                    match(input,32,FOLLOW_32_in_expression549); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression555);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression559);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.less(op2); 

                    }
                    break;
                case 10 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:190:5: ^( '<=' op1= expression op2= expression )
                    {
                    match(input,43,FOLLOW_43_in_expression573); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression578);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression582);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.lessEquals(op2); 

                    }
                    break;
                case 11 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:191:5: ^( '>' op1= expression op2= expression )
                    {
                    match(input,33,FOLLOW_33_in_expression596); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression602);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression606);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.greater(op2); 

                    }
                    break;
                case 12 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:192:5: ^( '>=' op1= expression op2= expression )
                    {
                    match(input,44,FOLLOW_44_in_expression620); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression625);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression629);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.greaterEquals(op2); 

                    }
                    break;
                case 13 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:195:5: ^( '&&' op1= expression op2= expression )
                    {
                    match(input,45,FOLLOW_45_in_expression649); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression654);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression658);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.and(op2); 

                    }
                    break;
                case 14 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:196:5: ^( '||' op1= expression op2= expression )
                    {
                    match(input,46,FOLLOW_46_in_expression672); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression677);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression681);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.or(op2); 

                    }
                    break;
                case 15 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:197:5: ^( 'not' op1= expression )
                    {
                    match(input,35,FOLLOW_35_in_expression695); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression699);
                    op1=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.not(); 

                    }
                    break;
                case 16 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:200:5: ID
                    {
                    ID3=(CommonTree)match(input,ID,FOLLOW_ID_in_expression736); 
                     result = envGet((ID3!=null?ID3.getText():null)); 

                    }
                    break;
                case 17 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:201:5: ^( ID call_params= function_call_parameters )
                    {
                    ID4=(CommonTree)match(input,ID,FOLLOW_ID_in_expression787); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        pushFollow(FOLLOW_function_call_parameters_in_expression791);
                        call_params=function_call_parameters();

                        state._fsp--;


                        match(input, Token.UP, null); 
                    }
                     Collections.reverse(call_params); result = call((ID4!=null?ID4.getText():null), call_params); 

                    }
                    break;
                case 18 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:202:5: ^( 'LAMBDA' params= function_decl_parameters ':' (EXPRESSION= . )* )
                    {
                    match(input,19,FOLLOW_19_in_expression801); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_function_decl_parameters_in_expression805);
                    params=function_decl_parameters();

                    state._fsp--;

                    match(input,20,FOLLOW_20_in_expression807); 
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:202:62: (EXPRESSION= . )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( ((LA6_0>=NEGATION && LA6_0<=55)) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:202:62: EXPRESSION= .
                    	    {
                    	    EXPRESSION=(CommonTree)input.LT(1);
                    	    matchAny(input); 

                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);


                    match(input, Token.UP, null); 
                     Collections.reverse(params); result = new LambdaValue(params, EXPRESSION, this.getEnvironmentContents(), this.getFunctions()); 

                    }
                    break;
                case 19 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:205:5: INT
                    {
                    INT5=(CommonTree)match(input,INT,FOLLOW_INT_in_expression829); 
                     result = new IntegerValue(Integer.parseInt((INT5!=null?INT5.getText():null)), PublicMy.getInstance()); 

                    }
                    break;
                case 20 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:206:5: DOUBLE
                    {
                    DOUBLE6=(CommonTree)match(input,DOUBLE,FOLLOW_DOUBLE_in_expression876); 
                     result = new DoubleValue(Double.parseDouble((DOUBLE6!=null?DOUBLE6.getText():null)), PublicMy.getInstance()); 

                    }
                    break;
                case 21 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:207:5: STRING_LITERAL
                    {
                    STRING_LITERAL7=(CommonTree)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_expression920); 
                     result = new StringValue(prepareString((STRING_LITERAL7!=null?STRING_LITERAL7.getText():null)), PublicMy.getInstance()); 

                    }
                    break;
                case 22 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:211:5: ^( 'AS' op= expression type= type_rule )
                    {
                    match(input,48,FOLLOW_48_in_expression966); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression970);
                    op=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_type_rule_in_expression974);
                    type=type_rule();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op; 

                    }
                    break;
                case 23 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/TreeWalker.g:212:5: ^( 'IS' op= expression level= privacy_decl )
                    {
                    match(input,47,FOLLOW_47_in_expression984); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression988);
                    op=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_privacy_decl_in_expression992);
                    level=privacy_decl();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op; 

                    }
                    break;

            }
        }
        catch (InterpreterException exc) {
                
                System.out.println("Error catch at query level"); 
                exc.printStackTrace(); 
                // TODO: Going into error state with interpreter...
                throw new RuntimeException(exc);
              
        }
        finally {
        }
        return result;
    }
    // $ANTLR end "expression"

    // Delegated rules


    protected DFA7 dfa7 = new DFA7(this);
    static final String DFA7_eotS =
        "\31\uffff";
    static final String DFA7_eofS =
        "\31\uffff";
    static final String DFA7_minS =
        "\1\4\17\uffff\1\2\10\uffff";
    static final String DFA7_maxS =
        "\1\60\17\uffff\1\60\10\uffff";
    static final String DFA7_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1"+
        "\15\1\16\1\17\1\uffff\1\22\1\23\1\24\1\25\1\26\1\27\1\21\1\20";
    static final String DFA7_specialS =
        "\31\uffff}>";
    static final String[] DFA7_transitionS = {
            "\1\6\1\20\1\22\1\uffff\1\23\10\uffff\1\24\1\uffff\1\21\14\uffff"+
            "\1\11\1\13\1\uffff\1\17\1\1\1\2\1\3\1\4\1\5\1\7\1\10\1\12\1"+
            "\14\1\15\1\16\1\26\1\25",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\27\4\30\1\uffff\1\30\10\uffff\1\30\1\uffff\1\30\3\uffff"+
            "\2\30\3\uffff\1\30\3\uffff\21\30",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA7_eot = DFA.unpackEncodedString(DFA7_eotS);
    static final short[] DFA7_eof = DFA.unpackEncodedString(DFA7_eofS);
    static final char[] DFA7_min = DFA.unpackEncodedStringToUnsignedChars(DFA7_minS);
    static final char[] DFA7_max = DFA.unpackEncodedStringToUnsignedChars(DFA7_maxS);
    static final short[] DFA7_accept = DFA.unpackEncodedString(DFA7_acceptS);
    static final short[] DFA7_special = DFA.unpackEncodedString(DFA7_specialS);
    static final short[][] DFA7_transition;

    static {
        int numStates = DFA7_transitionS.length;
        DFA7_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA7_transition[i] = DFA.unpackEncodedString(DFA7_transitionS[i]);
        }
    }

    class DFA7 extends DFA {

        public DFA7(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 7;
            this.eot = DFA7_eot;
            this.eof = DFA7_eof;
            this.min = DFA7_min;
            this.max = DFA7_max;
            this.accept = DFA7_accept;
            this.special = DFA7_special;
            this.transition = DFA7_transition;
        }
        public String getDescription() {
            return "177:1: expression returns [IDQLValue result] : ( ^( '+' op1= expression op2= expression ) | ^( '-' op1= expression op2= expression ) | ^( '*' op1= expression op2= expression ) | ^( '/' op1= expression op2= expression ) | ^( '%' op1= expression op2= expression ) | ^( NEGATION e= expression ) | ^( '=' op1= expression op2= expression ) | ^( '!=' op1= expression op2= expression ) | ^( '<' op1= expression op2= expression ) | ^( '<=' op1= expression op2= expression ) | ^( '>' op1= expression op2= expression ) | ^( '>=' op1= expression op2= expression ) | ^( '&&' op1= expression op2= expression ) | ^( '||' op1= expression op2= expression ) | ^( 'not' op1= expression ) | ID | ^( ID call_params= function_call_parameters ) | ^( 'LAMBDA' params= function_decl_parameters ':' (EXPRESSION= . )* ) | INT | DOUBLE | STRING_LITERAL | ^( 'AS' op= expression type= type_rule ) | ^( 'IS' op= expression level= privacy_decl ) );";
        }
    }
 

    public static final BitSet FOLLOW_ID_in_let61 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_let65 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_50_in_gather_statement84 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_let_in_gather_statement86 = new BitSet(new long[]{0x0000000000000028L});
    public static final BitSet FOLLOW_51_in_forward_statement108 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INT_in_forward_statement110 = new BitSet(new long[]{0x0001FFFB000A0170L});
    public static final BitSet FOLLOW_expression_in_forward_statement114 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_55_in_reduce_statement138 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_reduce_statement142 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_gather_statement_in_query161 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_forward_statement_in_query166 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_reduce_statement_in_query171 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_function_call_parameters203 = new BitSet(new long[]{0x0001FFFB000A0170L});
    public static final BitSet FOLLOW_function_call_parameters_in_function_call_parameters207 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_function_decl_parameters234 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_function_decl_parameters_in_function_decl_parameters238 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_type_rule263 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_type_rule285 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_type_rule_in_type_rule287 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_23_in_privacy_decl313 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_24_in_privacy_decl321 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_expression344 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression348 = new BitSet(new long[]{0x0001FFFB000A0170L});
    public static final BitSet FOLLOW_expression_in_expression352 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_37_in_expression368 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression372 = new BitSet(new long[]{0x0001FFFB000A0170L});
    public static final BitSet FOLLOW_expression_in_expression376 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_38_in_expression392 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression396 = new BitSet(new long[]{0x0001FFFB000A0170L});
    public static final BitSet FOLLOW_expression_in_expression400 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_39_in_expression416 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression420 = new BitSet(new long[]{0x0001FFFB000A0170L});
    public static final BitSet FOLLOW_expression_in_expression424 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_40_in_expression440 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression444 = new BitSet(new long[]{0x0001FFFB000A0170L});
    public static final BitSet FOLLOW_expression_in_expression448 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NEGATION_in_expression464 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression468 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_41_in_expression502 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression508 = new BitSet(new long[]{0x0001FFFB000A0170L});
    public static final BitSet FOLLOW_expression_in_expression512 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_42_in_expression526 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression531 = new BitSet(new long[]{0x0001FFFB000A0170L});
    public static final BitSet FOLLOW_expression_in_expression535 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_32_in_expression549 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression555 = new BitSet(new long[]{0x0001FFFB000A0170L});
    public static final BitSet FOLLOW_expression_in_expression559 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_43_in_expression573 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression578 = new BitSet(new long[]{0x0001FFFB000A0170L});
    public static final BitSet FOLLOW_expression_in_expression582 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_33_in_expression596 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression602 = new BitSet(new long[]{0x0001FFFB000A0170L});
    public static final BitSet FOLLOW_expression_in_expression606 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_44_in_expression620 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression625 = new BitSet(new long[]{0x0001FFFB000A0170L});
    public static final BitSet FOLLOW_expression_in_expression629 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_45_in_expression649 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression654 = new BitSet(new long[]{0x0001FFFB000A0170L});
    public static final BitSet FOLLOW_expression_in_expression658 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_46_in_expression672 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression677 = new BitSet(new long[]{0x0001FFFB000A0170L});
    public static final BitSet FOLLOW_expression_in_expression681 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_35_in_expression695 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression699 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_expression736 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_expression787 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_function_call_parameters_in_expression791 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_19_in_expression801 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_function_decl_parameters_in_expression805 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_expression807 = new BitSet(new long[]{0x00FFFFFFFFFFFFF8L});
    public static final BitSet FOLLOW_INT_in_expression829 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_in_expression876 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_expression920 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_expression966 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression970 = new BitSet(new long[]{0x0000000410000000L});
    public static final BitSet FOLLOW_type_rule_in_expression974 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_47_in_expression984 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression988 = new BitSet(new long[]{0x0000000001800000L});
    public static final BitSet FOLLOW_privacy_decl_in_expression992 = new BitSet(new long[]{0x0000000000000008L});

}