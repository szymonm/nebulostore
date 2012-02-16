// $ANTLR 3.3 Nov 30, 2010 12:50:56 /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g 2012-02-09 23:15:32

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
    
  
  import org.nebulostore.query.language.interpreter.datatypes.DQLComplexType;
  import org.nebulostore.query.language.interpreter.datatypes.DQLComplexType.DQLComplexTypeEnum;
  import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType;
  import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType.DQLPrimitiveTypeEnum;
  import org.nebulostore.query.language.interpreter.datatypes.DQLType;  
  
  import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
  import org.nebulostore.query.language.interpreter.datatypes.values.DoubleValue;
  import org.nebulostore.query.language.interpreter.datatypes.values.BooleanValue;
  import org.nebulostore.query.language.interpreter.datatypes.values.IntegerValue;
  import org.nebulostore.query.language.interpreter.datatypes.values.StringValue;
  import org.nebulostore.query.language.interpreter.datatypes.values.LambdaValue;
  import org.nebulostore.query.language.interpreter.datatypes.values.JavaValuesGlue;
  
  import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;
  import org.nebulostore.query.language.interpreter.exceptions.TypeException;
  
  import org.nebulostore.query.privacy.PrivacyLevel;
  import org.nebulostore.query.privacy.level.PublicMy;
  import org.nebulostore.query.privacy.level.PublicOthers;
  import org.nebulostore.query.privacy.level.PrivateMy;  
  import org.nebulostore.query.privacy.level.PrivateConditionalMy;
  import org.nebulostore.query.privacy.level.PrivateConditionalOthers;
  


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class TreeWalker extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "NEGATION", "ID", "INT", "EXPONENT", "DOUBLE", "COMMENT", "WS", "ESC_SEQ", "STRING", "CHAR", "HEX_DIGIT", "UNICODE_ESC", "OCTAL_ESC", "STRING_LITERAL", "','", "'LAMBDA'", "':'", "'('", "')'", "'PRIVATE_MY'", "'PUBLIC_MY'", "'PRIVATE_COND_MY'", "'<'", "'>'", "'PUBLIC_OTHER'", "'PRIVATE_COND_OTHER'", "'INTEGER'", "'DOUBLE'", "'STRING'", "'TUPLE'", "'LIST'", "'FILE'", "'TRUE'", "'FALSE'", "'not'", "'+'", "'-'", "'*'", "'/'", "'%'", "'='", "'!='", "'<='", "'>='", "'&&'", "'||'", "'IS'", "'AS'", "'LET'", "'GATHER'", "'FORWARD'", "'MAX'", "'DEPTH'", "'TO'", "'REDUCE'"
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
    public static final int T__56=56;
    public static final int T__57=57;
    public static final int T__58=58;
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
    public String getGrammarFileName() { return "/home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g"; }



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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:137:1: let : ^( ID e= expression ) ;
    public final void let() throws RecognitionException {
        CommonTree ID1=null;
        IDQLValue e = null;


        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:138:3: ( ^( ID e= expression ) )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:138:5: ^( ID e= expression )
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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:142:1: gather_statement : ^( 'GATHER' ( let )+ ) ;
    public final void gather_statement() throws RecognitionException {
        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:143:3: ( ^( 'GATHER' ( let )+ ) )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:143:5: ^( 'GATHER' ( let )+ )
            {
            match(input,53,FOLLOW_53_in_gather_statement84); 

            match(input, Token.DOWN, null); 
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:143:16: ( let )+
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
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:143:16: let
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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:146:1: forward_statement returns [IDQLValue result] : ^( 'FORWARD' INT ex= expression ) ;
    public final IDQLValue forward_statement() throws RecognitionException {
        IDQLValue result = null;

        IDQLValue ex = null;


        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:147:3: ( ^( 'FORWARD' INT ex= expression ) )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:147:5: ^( 'FORWARD' INT ex= expression )
            {
            match(input,54,FOLLOW_54_in_forward_statement108); 

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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:150:1: reduce_statement returns [IDQLValue result] : ^( 'REDUCE' ex= expression EOF ) ;
    public final IDQLValue reduce_statement() throws RecognitionException {
        IDQLValue result = null;

        IDQLValue ex = null;


        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:151:3: ( ^( 'REDUCE' ex= expression EOF ) )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:151:5: ^( 'REDUCE' ex= expression EOF )
            {
            match(input,58,FOLLOW_58_in_reduce_statement138); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_expression_in_reduce_statement142);
            ex=expression();

            state._fsp--;

            match(input,EOF,FOLLOW_EOF_in_reduce_statement144); 

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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:155:1: query : gather_statement forward_statement reduce_statement ;
    public final void query() throws RecognitionException {
        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:155:7: ( gather_statement forward_statement reduce_statement )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:156:4: gather_statement forward_statement reduce_statement
            {
            pushFollow(FOLLOW_gather_statement_in_query163);
            gather_statement();

            state._fsp--;

            pushFollow(FOLLOW_forward_statement_in_query168);
            forward_statement();

            state._fsp--;

            pushFollow(FOLLOW_reduce_statement_in_query173);
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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:166:1: function_call_parameters returns [List<IDQLValue> result] : (v= expression rest= function_call_parameters | );
    public final List<IDQLValue> function_call_parameters() throws RecognitionException {
        List<IDQLValue> result = null;

        IDQLValue v = null;

        List<IDQLValue> rest = null;


        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:167:3: (v= expression rest= function_call_parameters | )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( ((LA2_0>=NEGATION && LA2_0<=INT)||LA2_0==DOUBLE||LA2_0==STRING_LITERAL||LA2_0==19||(LA2_0>=26 && LA2_0<=27)||(LA2_0>=36 && LA2_0<=51)) ) {
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
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:167:5: v= expression rest= function_call_parameters
                    {
                    pushFollow(FOLLOW_expression_in_function_call_parameters208);
                    v=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_function_call_parameters_in_function_call_parameters212);
                    rest=function_call_parameters();

                    state._fsp--;

                     rest.add(v); result = rest; 

                    }
                    break;
                case 2 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:168:5: 
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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:171:1: function_decl_parameters returns [List<String> result] : ( ID rest= function_decl_parameters | );
    public final List<String> function_decl_parameters() throws RecognitionException {
        List<String> result = null;

        CommonTree ID2=null;
        List<String> rest = null;


        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:172:3: ( ID rest= function_decl_parameters | )
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
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:172:5: ID rest= function_decl_parameters
                    {
                    ID2=(CommonTree)match(input,ID,FOLLOW_ID_in_function_decl_parameters239); 
                    pushFollow(FOLLOW_function_decl_parameters_in_function_decl_parameters243);
                    rest=function_decl_parameters();

                    state._fsp--;

                     rest.add((ID2!=null?ID2.getText():null)); result = rest; 

                    }
                    break;
                case 2 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:173:5: 
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


    // $ANTLR start "type_list"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:176:1: type_list returns [List<DQLType> result] : (type= type_rule rest= type_list | );
    public final List<DQLType> type_list() throws RecognitionException {
        List<DQLType> result = null;

        DQLType type = null;

        List<DQLType> rest = null;


        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:177:3: (type= type_rule rest= type_list | )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( ((LA4_0>=30 && LA4_0<=35)) ) {
                alt4=1;
            }
            else if ( (LA4_0==UP) ) {
                alt4=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:177:5: type= type_rule rest= type_list
                    {
                    pushFollow(FOLLOW_type_rule_in_type_list272);
                    type=type_rule();

                    state._fsp--;

                    pushFollow(FOLLOW_type_list_in_type_list276);
                    rest=type_list();

                    state._fsp--;

                     rest.add(type); result = rest; 

                    }
                    break;
                case 2 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:178:5: 
                    {
                     result = new LinkedList<DQLType>(); 

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
    // $ANTLR end "type_list"


    // $ANTLR start "type_rule"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:181:1: type_rule returns [DQLType result] : ( 'INTEGER' | ^( 'LIST' list_type= type_rule ) | 'DOUBLE' | 'STRING' | 'FILE' | ^( 'TUPLE' list= type_list ) );
    public final DQLType type_rule() throws RecognitionException {
        DQLType result = null;

        DQLType list_type = null;

        List<DQLType> list = null;


        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:182:3: ( 'INTEGER' | ^( 'LIST' list_type= type_rule ) | 'DOUBLE' | 'STRING' | 'FILE' | ^( 'TUPLE' list= type_list ) )
            int alt5=6;
            switch ( input.LA(1) ) {
            case 30:
                {
                alt5=1;
                }
                break;
            case 34:
                {
                alt5=2;
                }
                break;
            case 31:
                {
                alt5=3;
                }
                break;
            case 32:
                {
                alt5=4;
                }
                break;
            case 35:
                {
                alt5=5;
                }
                break;
            case 33:
                {
                alt5=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }

            switch (alt5) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:182:5: 'INTEGER'
                    {
                    match(input,30,FOLLOW_30_in_type_rule302); 
                     result=new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLInteger); 

                    }
                    break;
                case 2 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:183:5: ^( 'LIST' list_type= type_rule )
                    {
                    match(input,34,FOLLOW_34_in_type_rule341); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_type_rule_in_type_rule345);
                    list_type=type_rule();

                    state._fsp--;


                    match(input, Token.UP, null); 

                          List<DQLType> list_of_types = new LinkedList<DQLType>(); 
                          list_of_types.add(list_type); 
                          result = new DQLComplexType(DQLComplexTypeEnum.DQLList, list_of_types); 
                        

                    }
                    break;
                case 3 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:189:5: 'DOUBLE'
                    {
                    match(input,31,FOLLOW_31_in_type_rule369); 
                     result = new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLDouble); 

                    }
                    break;
                case 4 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:190:5: 'STRING'
                    {
                    match(input,32,FOLLOW_32_in_type_rule408); 
                     result = new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLString); 

                    }
                    break;
                case 5 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:191:5: 'FILE'
                    {
                    match(input,35,FOLLOW_35_in_type_rule447); 
                     result = new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLFile); 

                    }
                    break;
                case 6 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:192:5: ^( 'TUPLE' list= type_list )
                    {
                    match(input,33,FOLLOW_33_in_type_rule489); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        pushFollow(FOLLOW_type_list_in_type_rule494);
                        list=type_list();

                        state._fsp--;


                        match(input, Token.UP, null); 
                    }
                     Collections.reverse(list); result = new DQLComplexType(DQLComplexTypeEnum.DQLTuple, list); 

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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:196:1: privacy_decl returns [PrivacyLevel result] : ( 'PRIVATE_MY' | 'PUBLIC_MY' | 'PUBLIC_OTHER' | 'PRIVATE_COND_MY' (EXPRESSION= . )* | 'PRIVATE_COND_OTHER' (EXPRESSION= . )* );
    public final PrivacyLevel privacy_decl() throws RecognitionException {
        PrivacyLevel result = null;

        CommonTree EXPRESSION=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:197:3: ( 'PRIVATE_MY' | 'PUBLIC_MY' | 'PUBLIC_OTHER' | 'PRIVATE_COND_MY' (EXPRESSION= . )* | 'PRIVATE_COND_OTHER' (EXPRESSION= . )* )
            int alt8=5;
            switch ( input.LA(1) ) {
            case 23:
                {
                alt8=1;
                }
                break;
            case 24:
                {
                alt8=2;
                }
                break;
            case 28:
                {
                alt8=3;
                }
                break;
            case 25:
                {
                alt8=4;
                }
                break;
            case 29:
                {
                alt8=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }

            switch (alt8) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:197:5: 'PRIVATE_MY'
                    {
                    match(input,23,FOLLOW_23_in_privacy_decl528); 
                     result=PrivateMy.getInstance(); 

                    }
                    break;
                case 2 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:198:5: 'PUBLIC_MY'
                    {
                    match(input,24,FOLLOW_24_in_privacy_decl565); 
                     result=PublicMy.getInstance(); 

                    }
                    break;
                case 3 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:199:5: 'PUBLIC_OTHER'
                    {
                    match(input,28,FOLLOW_28_in_privacy_decl603); 
                     result=PublicOthers.getInstance(); 

                    }
                    break;
                case 4 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:200:5: 'PRIVATE_COND_MY' (EXPRESSION= . )*
                    {
                    match(input,25,FOLLOW_25_in_privacy_decl638); 
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:200:33: (EXPRESSION= . )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( ((LA6_0>=NEGATION && LA6_0<=58)) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:200:33: EXPRESSION= .
                    	    {
                    	    EXPRESSION=(CommonTree)input.LT(1);
                    	    matchAny(input); 

                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);

                     result=new PrivateConditionalMy(EXPRESSION); 

                    }
                    break;
                case 5 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:201:5: 'PRIVATE_COND_OTHER' (EXPRESSION= . )*
                    {
                    match(input,29,FOLLOW_29_in_privacy_decl661); 
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:201:36: (EXPRESSION= . )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( ((LA7_0>=NEGATION && LA7_0<=58)) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:201:36: EXPRESSION= .
                    	    {
                    	    EXPRESSION=(CommonTree)input.LT(1);
                    	    matchAny(input); 

                    	    }
                    	    break;

                    	default :
                    	    break loop7;
                        }
                    } while (true);

                     result=new PrivateConditionalMy(EXPRESSION); 

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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:204:1: expression returns [IDQLValue result] : ( ^( '+' op1= expression op2= expression ) | ^( '-' op1= expression op2= expression ) | ^( '*' op1= expression op2= expression ) | ^( '/' op1= expression op2= expression ) | ^( '%' op1= expression op2= expression ) | ^( NEGATION e= expression ) | ^( '=' op1= expression op2= expression ) | ^( '!=' op1= expression op2= expression ) | ^( '<' op1= expression op2= expression ) | ^( '<=' op1= expression op2= expression ) | ^( '>' op1= expression op2= expression ) | ^( '>=' op1= expression op2= expression ) | ^( '&&' op1= expression op2= expression ) | ^( '||' op1= expression op2= expression ) | ^( 'not' op1= expression ) | ID | ^( ID call_params= function_call_parameters ) | ^( 'LAMBDA' params= function_decl_parameters ':' (EXPRESSION= . )* ) | INT | DOUBLE | STRING_LITERAL | 'TRUE' | 'FALSE' | ^( 'AS' e= expression type= type_rule ) | ^( 'IS' e= expression level= privacy_decl ) );
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

        DQLType type = null;

        PrivacyLevel level = null;


        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:206:3: ( ^( '+' op1= expression op2= expression ) | ^( '-' op1= expression op2= expression ) | ^( '*' op1= expression op2= expression ) | ^( '/' op1= expression op2= expression ) | ^( '%' op1= expression op2= expression ) | ^( NEGATION e= expression ) | ^( '=' op1= expression op2= expression ) | ^( '!=' op1= expression op2= expression ) | ^( '<' op1= expression op2= expression ) | ^( '<=' op1= expression op2= expression ) | ^( '>' op1= expression op2= expression ) | ^( '>=' op1= expression op2= expression ) | ^( '&&' op1= expression op2= expression ) | ^( '||' op1= expression op2= expression ) | ^( 'not' op1= expression ) | ID | ^( ID call_params= function_call_parameters ) | ^( 'LAMBDA' params= function_decl_parameters ':' (EXPRESSION= . )* ) | INT | DOUBLE | STRING_LITERAL | 'TRUE' | 'FALSE' | ^( 'AS' e= expression type= type_rule ) | ^( 'IS' e= expression level= privacy_decl ) )
            int alt10=25;
            alt10 = dfa10.predict(input);
            switch (alt10) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:206:5: ^( '+' op1= expression op2= expression )
                    {
                    match(input,39,FOLLOW_39_in_expression696); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression700);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression704);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.addNum(op2); 

                    }
                    break;
                case 2 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:207:5: ^( '-' op1= expression op2= expression )
                    {
                    match(input,40,FOLLOW_40_in_expression720); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression724);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression728);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.subNum(op2); 

                    }
                    break;
                case 3 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:208:5: ^( '*' op1= expression op2= expression )
                    {
                    match(input,41,FOLLOW_41_in_expression744); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression748);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression752);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.multNum(op2); 

                    }
                    break;
                case 4 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:209:5: ^( '/' op1= expression op2= expression )
                    {
                    match(input,42,FOLLOW_42_in_expression768); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression772);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression776);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.divNum(op2); 

                    }
                    break;
                case 5 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:210:5: ^( '%' op1= expression op2= expression )
                    {
                    match(input,43,FOLLOW_43_in_expression792); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression796);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression800);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.modNum(op2); 

                    }
                    break;
                case 6 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:211:5: ^( NEGATION e= expression )
                    {
                    match(input,NEGATION,FOLLOW_NEGATION_in_expression816); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression820);
                    e=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = e.numNegation(); 

                    }
                    break;
                case 7 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:214:5: ^( '=' op1= expression op2= expression )
                    {
                    match(input,44,FOLLOW_44_in_expression854); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression860);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression864);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.equals(op2); 

                    }
                    break;
                case 8 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:215:5: ^( '!=' op1= expression op2= expression )
                    {
                    match(input,45,FOLLOW_45_in_expression878); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression883);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression887);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.notEquals(op2); 

                    }
                    break;
                case 9 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:216:5: ^( '<' op1= expression op2= expression )
                    {
                    match(input,26,FOLLOW_26_in_expression901); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression907);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression911);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.less(op2); 

                    }
                    break;
                case 10 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:217:5: ^( '<=' op1= expression op2= expression )
                    {
                    match(input,46,FOLLOW_46_in_expression925); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression930);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression934);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.lessEquals(op2); 

                    }
                    break;
                case 11 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:218:5: ^( '>' op1= expression op2= expression )
                    {
                    match(input,27,FOLLOW_27_in_expression948); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression954);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression958);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.greater(op2); 

                    }
                    break;
                case 12 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:219:5: ^( '>=' op1= expression op2= expression )
                    {
                    match(input,47,FOLLOW_47_in_expression972); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression977);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression981);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.greaterEquals(op2); 

                    }
                    break;
                case 13 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:222:5: ^( '&&' op1= expression op2= expression )
                    {
                    match(input,48,FOLLOW_48_in_expression1001); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression1006);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression1010);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.and(op2); 

                    }
                    break;
                case 14 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:223:5: ^( '||' op1= expression op2= expression )
                    {
                    match(input,49,FOLLOW_49_in_expression1024); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression1029);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression1033);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.or(op2); 

                    }
                    break;
                case 15 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:224:5: ^( 'not' op1= expression )
                    {
                    match(input,38,FOLLOW_38_in_expression1047); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression1051);
                    op1=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.not(); 

                    }
                    break;
                case 16 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:227:5: ID
                    {
                    ID3=(CommonTree)match(input,ID,FOLLOW_ID_in_expression1088); 
                     result = envGet((ID3!=null?ID3.getText():null)); 

                    }
                    break;
                case 17 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:228:5: ^( ID call_params= function_call_parameters )
                    {
                    ID4=(CommonTree)match(input,ID,FOLLOW_ID_in_expression1139); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        pushFollow(FOLLOW_function_call_parameters_in_expression1143);
                        call_params=function_call_parameters();

                        state._fsp--;


                        match(input, Token.UP, null); 
                    }
                     Collections.reverse(call_params); result = call((ID4!=null?ID4.getText():null), call_params); 

                    }
                    break;
                case 18 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:229:5: ^( 'LAMBDA' params= function_decl_parameters ':' (EXPRESSION= . )* )
                    {
                    match(input,19,FOLLOW_19_in_expression1153); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_function_decl_parameters_in_expression1157);
                    params=function_decl_parameters();

                    state._fsp--;

                    match(input,20,FOLLOW_20_in_expression1159); 
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:229:62: (EXPRESSION= . )*
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( ((LA9_0>=NEGATION && LA9_0<=58)) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:229:62: EXPRESSION= .
                    	    {
                    	    EXPRESSION=(CommonTree)input.LT(1);
                    	    matchAny(input); 

                    	    }
                    	    break;

                    	default :
                    	    break loop9;
                        }
                    } while (true);


                    match(input, Token.UP, null); 
                     Collections.reverse(params); result = new LambdaValue(params, EXPRESSION, this.getEnvironmentContents(), this.getFunctions()); 

                    }
                    break;
                case 19 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:232:5: INT
                    {
                    INT5=(CommonTree)match(input,INT,FOLLOW_INT_in_expression1181); 
                     result = new IntegerValue(Integer.parseInt((INT5!=null?INT5.getText():null)), PublicMy.getInstance()); 

                    }
                    break;
                case 20 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:233:5: DOUBLE
                    {
                    DOUBLE6=(CommonTree)match(input,DOUBLE,FOLLOW_DOUBLE_in_expression1228); 
                     result = new DoubleValue(Double.parseDouble((DOUBLE6!=null?DOUBLE6.getText():null)), PublicMy.getInstance()); 

                    }
                    break;
                case 21 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:234:5: STRING_LITERAL
                    {
                    STRING_LITERAL7=(CommonTree)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_expression1272); 
                     result = new StringValue(prepareString((STRING_LITERAL7!=null?STRING_LITERAL7.getText():null)), PublicMy.getInstance()); 

                    }
                    break;
                case 22 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:235:5: 'TRUE'
                    {
                    match(input,36,FOLLOW_36_in_expression1308); 
                     result = new BooleanValue(true, PublicMy.getInstance()); 

                    }
                    break;
                case 23 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:236:5: 'FALSE'
                    {
                    match(input,37,FOLLOW_37_in_expression1352); 
                     result = new BooleanValue(false, PublicMy.getInstance()); 

                    }
                    break;
                case 24 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:239:5: ^( 'AS' e= expression type= type_rule )
                    {
                    match(input,51,FOLLOW_51_in_expression1404); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression1408);
                    e=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_type_rule_in_expression1412);
                    type=type_rule();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     e.checkType(type); result = e; 

                    }
                    break;
                case 25 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:240:5: ^( 'IS' e= expression level= privacy_decl )
                    {
                    match(input,50,FOLLOW_50_in_expression1429); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression1433);
                    e=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_privacy_decl_in_expression1437);
                    level=privacy_decl();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     e.setPrivacyLevel(level); result = e; 

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


    protected DFA10 dfa10 = new DFA10(this);
    static final String DFA10_eotS =
        "\33\uffff";
    static final String DFA10_eofS =
        "\20\uffff\1\32\12\uffff";
    static final String DFA10_minS =
        "\1\4\17\uffff\1\2\12\uffff";
    static final String DFA10_maxS =
        "\1\63\17\uffff\1\63\12\uffff";
    static final String DFA10_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1"+
        "\15\1\16\1\17\1\uffff\1\22\1\23\1\24\1\25\1\26\1\27\1\30\1\31\1"+
        "\21\1\20";
    static final String DFA10_specialS =
        "\33\uffff}>";
    static final String[] DFA10_transitionS = {
            "\1\6\1\20\1\22\1\uffff\1\23\10\uffff\1\24\1\uffff\1\21\6\uffff"+
            "\1\11\1\13\10\uffff\1\25\1\26\1\17\1\1\1\2\1\3\1\4\1\5\1\7\1"+
            "\10\1\12\1\14\1\15\1\16\1\30\1\27",
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
            "\1\31\4\32\1\uffff\1\32\10\uffff\1\32\1\uffff\1\32\3\uffff"+
            "\35\32",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA10_eot = DFA.unpackEncodedString(DFA10_eotS);
    static final short[] DFA10_eof = DFA.unpackEncodedString(DFA10_eofS);
    static final char[] DFA10_min = DFA.unpackEncodedStringToUnsignedChars(DFA10_minS);
    static final char[] DFA10_max = DFA.unpackEncodedStringToUnsignedChars(DFA10_maxS);
    static final short[] DFA10_accept = DFA.unpackEncodedString(DFA10_acceptS);
    static final short[] DFA10_special = DFA.unpackEncodedString(DFA10_specialS);
    static final short[][] DFA10_transition;

    static {
        int numStates = DFA10_transitionS.length;
        DFA10_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA10_transition[i] = DFA.unpackEncodedString(DFA10_transitionS[i]);
        }
    }

    class DFA10 extends DFA {

        public DFA10(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 10;
            this.eot = DFA10_eot;
            this.eof = DFA10_eof;
            this.min = DFA10_min;
            this.max = DFA10_max;
            this.accept = DFA10_accept;
            this.special = DFA10_special;
            this.transition = DFA10_transition;
        }
        public String getDescription() {
            return "204:1: expression returns [IDQLValue result] : ( ^( '+' op1= expression op2= expression ) | ^( '-' op1= expression op2= expression ) | ^( '*' op1= expression op2= expression ) | ^( '/' op1= expression op2= expression ) | ^( '%' op1= expression op2= expression ) | ^( NEGATION e= expression ) | ^( '=' op1= expression op2= expression ) | ^( '!=' op1= expression op2= expression ) | ^( '<' op1= expression op2= expression ) | ^( '<=' op1= expression op2= expression ) | ^( '>' op1= expression op2= expression ) | ^( '>=' op1= expression op2= expression ) | ^( '&&' op1= expression op2= expression ) | ^( '||' op1= expression op2= expression ) | ^( 'not' op1= expression ) | ID | ^( ID call_params= function_call_parameters ) | ^( 'LAMBDA' params= function_decl_parameters ':' (EXPRESSION= . )* ) | INT | DOUBLE | STRING_LITERAL | 'TRUE' | 'FALSE' | ^( 'AS' e= expression type= type_rule ) | ^( 'IS' e= expression level= privacy_decl ) );";
        }
    }
 

    public static final BitSet FOLLOW_ID_in_let61 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_let65 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_53_in_gather_statement84 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_let_in_gather_statement86 = new BitSet(new long[]{0x0000000000000028L});
    public static final BitSet FOLLOW_54_in_forward_statement108 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INT_in_forward_statement110 = new BitSet(new long[]{0x000FFFF00C0A0170L});
    public static final BitSet FOLLOW_expression_in_forward_statement114 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_58_in_reduce_statement138 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_reduce_statement142 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_reduce_statement144 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_gather_statement_in_query163 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_forward_statement_in_query168 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_reduce_statement_in_query173 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_function_call_parameters208 = new BitSet(new long[]{0x000FFFF00C0A0170L});
    public static final BitSet FOLLOW_function_call_parameters_in_function_call_parameters212 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_function_decl_parameters239 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_function_decl_parameters_in_function_decl_parameters243 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_rule_in_type_list272 = new BitSet(new long[]{0x0000000FC0000000L});
    public static final BitSet FOLLOW_type_list_in_type_list276 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_type_rule302 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_type_rule341 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_type_rule_in_type_rule345 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_31_in_type_rule369 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_type_rule408 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_type_rule447 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_type_rule489 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_type_list_in_type_rule494 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_23_in_privacy_decl528 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_24_in_privacy_decl565 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_privacy_decl603 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_privacy_decl638 = new BitSet(new long[]{0x07FFFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_29_in_privacy_decl661 = new BitSet(new long[]{0x07FFFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_39_in_expression696 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression700 = new BitSet(new long[]{0x000FFFF00C0A0170L});
    public static final BitSet FOLLOW_expression_in_expression704 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_40_in_expression720 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression724 = new BitSet(new long[]{0x000FFFF00C0A0170L});
    public static final BitSet FOLLOW_expression_in_expression728 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_41_in_expression744 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression748 = new BitSet(new long[]{0x000FFFF00C0A0170L});
    public static final BitSet FOLLOW_expression_in_expression752 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_42_in_expression768 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression772 = new BitSet(new long[]{0x000FFFF00C0A0170L});
    public static final BitSet FOLLOW_expression_in_expression776 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_43_in_expression792 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression796 = new BitSet(new long[]{0x000FFFF00C0A0170L});
    public static final BitSet FOLLOW_expression_in_expression800 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NEGATION_in_expression816 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression820 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_44_in_expression854 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression860 = new BitSet(new long[]{0x000FFFF00C0A0170L});
    public static final BitSet FOLLOW_expression_in_expression864 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_45_in_expression878 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression883 = new BitSet(new long[]{0x000FFFF00C0A0170L});
    public static final BitSet FOLLOW_expression_in_expression887 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_26_in_expression901 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression907 = new BitSet(new long[]{0x000FFFF00C0A0170L});
    public static final BitSet FOLLOW_expression_in_expression911 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_46_in_expression925 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression930 = new BitSet(new long[]{0x000FFFF00C0A0170L});
    public static final BitSet FOLLOW_expression_in_expression934 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_27_in_expression948 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression954 = new BitSet(new long[]{0x000FFFF00C0A0170L});
    public static final BitSet FOLLOW_expression_in_expression958 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_47_in_expression972 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression977 = new BitSet(new long[]{0x000FFFF00C0A0170L});
    public static final BitSet FOLLOW_expression_in_expression981 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_48_in_expression1001 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression1006 = new BitSet(new long[]{0x000FFFF00C0A0170L});
    public static final BitSet FOLLOW_expression_in_expression1010 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_49_in_expression1024 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression1029 = new BitSet(new long[]{0x000FFFF00C0A0170L});
    public static final BitSet FOLLOW_expression_in_expression1033 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_38_in_expression1047 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression1051 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_expression1088 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_expression1139 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_function_call_parameters_in_expression1143 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_19_in_expression1153 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_function_decl_parameters_in_expression1157 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_expression1159 = new BitSet(new long[]{0x07FFFFFFFFFFFFF8L});
    public static final BitSet FOLLOW_INT_in_expression1181 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_in_expression1228 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_expression1272 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_expression1308 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_expression1352 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_expression1404 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression1408 = new BitSet(new long[]{0x0000000FC0000000L});
    public static final BitSet FOLLOW_type_rule_in_expression1412 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_50_in_expression1429 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression1433 = new BitSet(new long[]{0x0000000033800000L});
    public static final BitSet FOLLOW_privacy_decl_in_expression1437 = new BitSet(new long[]{0x0000000000000008L});

}