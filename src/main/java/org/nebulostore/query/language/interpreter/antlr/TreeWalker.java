// $ANTLR 3.3 Nov 30, 2010 12:50:56 /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g 2012-05-19 12:10:29

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
    
  import org.nebulostore.query.language.interpreter.datasources.ConstantDataSource;
  
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
  import org.nebulostore.query.privacy.level.PublicConditionalMy;
  


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class TreeWalker extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "NEGATION", "ID", "INT", "EXPONENT", "DOUBLE", "COMMENT", "WS", "ESC_SEQ", "STRING", "CHAR", "HEX_DIGIT", "UNICODE_ESC", "OCTAL_ESC", "STRING_LITERAL", "','", "'LAMBDA'", "':'", "'('", "')'", "'PRIVATE_MY'", "'PUBLIC_MY'", "'PRIVATE_COND'", "'PUBLIC_OTHER'", "'PUBLIC_COND'", "'INTEGER'", "'DOUBLE'", "'STRING'", "'TUPLE'", "'<'", "'>'", "'LIST'", "'FILE'", "'TRUE'", "'FALSE'", "'not'", "'+'", "'-'", "'*'", "'/'", "'%'", "'='", "'!='", "'<='", "'>='", "'&&'", "'||'", "'IS'", "'AS'", "'LET'", "'GATHER'", "'FORWARD'", "'TO'", "'REDUCE'"
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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:138:1: let : ^( ID e= expression ) ;
    public final void let() throws RecognitionException {
        CommonTree ID1=null;
        IDQLValue e = null;


        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:139:3: ( ^( ID e= expression ) )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:139:5: ^( ID e= expression )
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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:143:1: gather_statement : ^( 'GATHER' ( let )+ ) ;
    public final void gather_statement() throws RecognitionException {
        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:144:3: ( ^( 'GATHER' ( let )+ ) )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:144:5: ^( 'GATHER' ( let )+ )
            {
            match(input,53,FOLLOW_53_in_gather_statement84); 

            match(input, Token.DOWN, null); 
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:144:16: ( let )+
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
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:144:16: let
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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:147:1: forward_statement returns [IDQLValue result] : ^( 'FORWARD' ex= expression ) ;
    public final IDQLValue forward_statement() throws RecognitionException {
        IDQLValue result = null;

        IDQLValue ex = null;


        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:148:3: ( ^( 'FORWARD' ex= expression ) )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:148:5: ^( 'FORWARD' ex= expression )
            {
            match(input,54,FOLLOW_54_in_forward_statement108); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_expression_in_forward_statement112);
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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:151:1: reduce_statement returns [IDQLValue result] : ^( 'REDUCE' ex= expression EOF ) ;
    public final IDQLValue reduce_statement() throws RecognitionException {
        IDQLValue result = null;

        IDQLValue ex = null;


        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:152:3: ( ^( 'REDUCE' ex= expression EOF ) )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:152:5: ^( 'REDUCE' ex= expression EOF )
            {
            match(input,56,FOLLOW_56_in_reduce_statement136); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_expression_in_reduce_statement140);
            ex=expression();

            state._fsp--;

            match(input,EOF,FOLLOW_EOF_in_reduce_statement142); 

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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:156:1: query : gather_statement forward_statement reduce_statement ;
    public final void query() throws RecognitionException {
        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:156:7: ( gather_statement forward_statement reduce_statement )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:157:4: gather_statement forward_statement reduce_statement
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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:167:1: function_call_parameters returns [List<IDQLValue> result] : (v= expression rest= function_call_parameters | );
    public final List<IDQLValue> function_call_parameters() throws RecognitionException {
        List<IDQLValue> result = null;

        IDQLValue v = null;

        List<IDQLValue> rest = null;


        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:168:3: (v= expression rest= function_call_parameters | )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( ((LA2_0>=NEGATION && LA2_0<=INT)||LA2_0==DOUBLE||LA2_0==STRING_LITERAL||LA2_0==19||(LA2_0>=32 && LA2_0<=33)||(LA2_0>=36 && LA2_0<=51)) ) {
                alt2=1;
            }
            else if ( (LA2_0==22) ) {
                alt2=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:168:5: v= expression rest= function_call_parameters
                    {
                    pushFollow(FOLLOW_expression_in_function_call_parameters206);
                    v=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_function_call_parameters_in_function_call_parameters210);
                    rest=function_call_parameters();

                    state._fsp--;

                     rest.add(v); result = rest; 

                    }
                    break;
                case 2 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:169:5: 
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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:172:1: function_decl_parameters returns [List<String> result] : ( ID rest= function_decl_parameters | );
    public final List<String> function_decl_parameters() throws RecognitionException {
        List<String> result = null;

        CommonTree ID2=null;
        List<String> rest = null;


        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:173:3: ( ID rest= function_decl_parameters | )
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
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:173:5: ID rest= function_decl_parameters
                    {
                    ID2=(CommonTree)match(input,ID,FOLLOW_ID_in_function_decl_parameters237); 
                    pushFollow(FOLLOW_function_decl_parameters_in_function_decl_parameters241);
                    rest=function_decl_parameters();

                    state._fsp--;

                     rest.add((ID2!=null?ID2.getText():null)); result = rest; 

                    }
                    break;
                case 2 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:174:5: 
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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:177:1: type_list returns [List<DQLType> result] : (type= type_rule rest= type_list | );
    public final List<DQLType> type_list() throws RecognitionException {
        List<DQLType> result = null;

        DQLType type = null;

        List<DQLType> rest = null;


        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:178:3: (type= type_rule rest= type_list | )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( ((LA4_0>=28 && LA4_0<=31)||(LA4_0>=34 && LA4_0<=35)) ) {
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
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:178:5: type= type_rule rest= type_list
                    {
                    pushFollow(FOLLOW_type_rule_in_type_list270);
                    type=type_rule();

                    state._fsp--;

                    pushFollow(FOLLOW_type_list_in_type_list274);
                    rest=type_list();

                    state._fsp--;

                     rest.add(type); result = rest; 

                    }
                    break;
                case 2 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:179:5: 
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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:182:1: type_rule returns [DQLType result] : ( 'INTEGER' | ^( 'LIST' list_type= type_rule ) | 'DOUBLE' | 'STRING' | 'FILE' | ^( 'TUPLE' list= type_list ) );
    public final DQLType type_rule() throws RecognitionException {
        DQLType result = null;

        DQLType list_type = null;

        List<DQLType> list = null;


        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:183:3: ( 'INTEGER' | ^( 'LIST' list_type= type_rule ) | 'DOUBLE' | 'STRING' | 'FILE' | ^( 'TUPLE' list= type_list ) )
            int alt5=6;
            switch ( input.LA(1) ) {
            case 28:
                {
                alt5=1;
                }
                break;
            case 34:
                {
                alt5=2;
                }
                break;
            case 29:
                {
                alt5=3;
                }
                break;
            case 30:
                {
                alt5=4;
                }
                break;
            case 35:
                {
                alt5=5;
                }
                break;
            case 31:
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
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:183:5: 'INTEGER'
                    {
                    match(input,28,FOLLOW_28_in_type_rule300); 
                     result=new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLInteger); 

                    }
                    break;
                case 2 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:184:5: ^( 'LIST' list_type= type_rule )
                    {
                    match(input,34,FOLLOW_34_in_type_rule339); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_type_rule_in_type_rule343);
                    list_type=type_rule();

                    state._fsp--;


                    match(input, Token.UP, null); 

                          List<DQLType> list_of_types = new LinkedList<DQLType>(); 
                          list_of_types.add(list_type); 
                          result = new DQLComplexType(DQLComplexTypeEnum.DQLList, list_of_types); 
                        

                    }
                    break;
                case 3 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:190:5: 'DOUBLE'
                    {
                    match(input,29,FOLLOW_29_in_type_rule367); 
                     result = new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLDouble); 

                    }
                    break;
                case 4 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:191:5: 'STRING'
                    {
                    match(input,30,FOLLOW_30_in_type_rule406); 
                     result = new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLString); 

                    }
                    break;
                case 5 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:192:5: 'FILE'
                    {
                    match(input,35,FOLLOW_35_in_type_rule445); 
                     result = new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLFile); 

                    }
                    break;
                case 6 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:193:5: ^( 'TUPLE' list= type_list )
                    {
                    match(input,31,FOLLOW_31_in_type_rule487); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        pushFollow(FOLLOW_type_list_in_type_rule492);
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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:197:1: privacy_decl returns [PrivacyLevel result] : ( 'PRIVATE_MY' | 'PUBLIC_MY' | 'PUBLIC_OTHER' | 'PRIVATE_COND' | 'PUBLIC_COND' );
    public final PrivacyLevel privacy_decl() throws RecognitionException {
        PrivacyLevel result = null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:198:3: ( 'PRIVATE_MY' | 'PUBLIC_MY' | 'PUBLIC_OTHER' | 'PRIVATE_COND' | 'PUBLIC_COND' )
            int alt6=5;
            switch ( input.LA(1) ) {
            case 23:
                {
                alt6=1;
                }
                break;
            case 24:
                {
                alt6=2;
                }
                break;
            case 26:
                {
                alt6=3;
                }
                break;
            case 25:
                {
                alt6=4;
                }
                break;
            case 27:
                {
                alt6=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:198:5: 'PRIVATE_MY'
                    {
                    match(input,23,FOLLOW_23_in_privacy_decl526); 
                     result = new PrivateMy(); 

                    }
                    break;
                case 2 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:199:5: 'PUBLIC_MY'
                    {
                    match(input,24,FOLLOW_24_in_privacy_decl563); 
                     result = new PublicMy(); 

                    }
                    break;
                case 3 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:200:5: 'PUBLIC_OTHER'
                    {
                    match(input,26,FOLLOW_26_in_privacy_decl601); 
                     result = new PublicOthers(); 

                    }
                    break;
                case 4 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:201:5: 'PRIVATE_COND'
                    {
                    match(input,25,FOLLOW_25_in_privacy_decl636); 
                     result = new PrivateConditionalMy(); 

                    }
                    break;
                case 5 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:202:5: 'PUBLIC_COND'
                    {
                    match(input,27,FOLLOW_27_in_privacy_decl671); 
                     result = new PublicConditionalMy(); 

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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:205:1: expression returns [IDQLValue result] : ( ^( '+' op1= expression op2= expression ) | ^( '-' op1= expression op2= expression ) | ^( '*' op1= expression op2= expression ) | ^( '/' op1= expression op2= expression ) | ^( '%' op1= expression op2= expression ) | ^( NEGATION e= expression ) | ^( '=' op1= expression op2= expression ) | ^( '!=' op1= expression op2= expression ) | ^( '<' op1= expression op2= expression ) | ^( '<=' op1= expression op2= expression ) | ^( '>' op1= expression op2= expression ) | ^( '>=' op1= expression op2= expression ) | ^( '&&' op1= expression op2= expression ) | ^( '||' op1= expression op2= expression ) | ^( 'not' op1= expression ) | ID | ^( ID '(' call_params= function_call_parameters ')' ) | ^( 'LAMBDA' params= function_decl_parameters ':' (EXPRESSION= . )* ) | INT | DOUBLE | STRING_LITERAL | 'TRUE' | 'FALSE' | ^( 'AS' e= expression type= type_rule ) | ^( 'IS' e= expression level= privacy_decl ) );
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
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:207:3: ( ^( '+' op1= expression op2= expression ) | ^( '-' op1= expression op2= expression ) | ^( '*' op1= expression op2= expression ) | ^( '/' op1= expression op2= expression ) | ^( '%' op1= expression op2= expression ) | ^( NEGATION e= expression ) | ^( '=' op1= expression op2= expression ) | ^( '!=' op1= expression op2= expression ) | ^( '<' op1= expression op2= expression ) | ^( '<=' op1= expression op2= expression ) | ^( '>' op1= expression op2= expression ) | ^( '>=' op1= expression op2= expression ) | ^( '&&' op1= expression op2= expression ) | ^( '||' op1= expression op2= expression ) | ^( 'not' op1= expression ) | ID | ^( ID '(' call_params= function_call_parameters ')' ) | ^( 'LAMBDA' params= function_decl_parameters ':' (EXPRESSION= . )* ) | INT | DOUBLE | STRING_LITERAL | 'TRUE' | 'FALSE' | ^( 'AS' e= expression type= type_rule ) | ^( 'IS' e= expression level= privacy_decl ) )
            int alt8=25;
            alt8 = dfa8.predict(input);
            switch (alt8) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:207:5: ^( '+' op1= expression op2= expression )
                    {
                    match(input,39,FOLLOW_39_in_expression722); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression726);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression730);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.addNum(op2); 

                    }
                    break;
                case 2 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:208:5: ^( '-' op1= expression op2= expression )
                    {
                    match(input,40,FOLLOW_40_in_expression746); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression750);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression754);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.subNum(op2); 

                    }
                    break;
                case 3 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:209:5: ^( '*' op1= expression op2= expression )
                    {
                    match(input,41,FOLLOW_41_in_expression770); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression774);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression778);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.multNum(op2); 

                    }
                    break;
                case 4 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:210:5: ^( '/' op1= expression op2= expression )
                    {
                    match(input,42,FOLLOW_42_in_expression794); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression798);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression802);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.divNum(op2); 

                    }
                    break;
                case 5 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:211:5: ^( '%' op1= expression op2= expression )
                    {
                    match(input,43,FOLLOW_43_in_expression818); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression822);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression826);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.modNum(op2); 

                    }
                    break;
                case 6 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:212:5: ^( NEGATION e= expression )
                    {
                    match(input,NEGATION,FOLLOW_NEGATION_in_expression842); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression846);
                    e=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = e.numNegation(); 

                    }
                    break;
                case 7 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:215:5: ^( '=' op1= expression op2= expression )
                    {
                    match(input,44,FOLLOW_44_in_expression880); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression886);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression890);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.equals(op2); 

                    }
                    break;
                case 8 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:216:5: ^( '!=' op1= expression op2= expression )
                    {
                    match(input,45,FOLLOW_45_in_expression904); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression909);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression913);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.notEquals(op2); 

                    }
                    break;
                case 9 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:217:5: ^( '<' op1= expression op2= expression )
                    {
                    match(input,32,FOLLOW_32_in_expression927); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression933);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression937);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.less(op2); 

                    }
                    break;
                case 10 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:218:5: ^( '<=' op1= expression op2= expression )
                    {
                    match(input,46,FOLLOW_46_in_expression951); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression956);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression960);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.lessEquals(op2); 

                    }
                    break;
                case 11 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:219:5: ^( '>' op1= expression op2= expression )
                    {
                    match(input,33,FOLLOW_33_in_expression974); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression980);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression984);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.greater(op2); 

                    }
                    break;
                case 12 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:220:5: ^( '>=' op1= expression op2= expression )
                    {
                    match(input,47,FOLLOW_47_in_expression998); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression1003);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression1007);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.greaterEquals(op2); 

                    }
                    break;
                case 13 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:223:5: ^( '&&' op1= expression op2= expression )
                    {
                    match(input,48,FOLLOW_48_in_expression1027); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression1032);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression1036);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.and(op2); 

                    }
                    break;
                case 14 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:224:5: ^( '||' op1= expression op2= expression )
                    {
                    match(input,49,FOLLOW_49_in_expression1050); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression1055);
                    op1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression1059);
                    op2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.or(op2); 

                    }
                    break;
                case 15 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:225:5: ^( 'not' op1= expression )
                    {
                    match(input,38,FOLLOW_38_in_expression1073); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression1077);
                    op1=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     result = op1.not(); 

                    }
                    break;
                case 16 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:228:5: ID
                    {
                    ID3=(CommonTree)match(input,ID,FOLLOW_ID_in_expression1114); 
                     result = envGet((ID3!=null?ID3.getText():null)); 

                    }
                    break;
                case 17 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:229:5: ^( ID '(' call_params= function_call_parameters ')' )
                    {
                    ID4=(CommonTree)match(input,ID,FOLLOW_ID_in_expression1165); 

                    match(input, Token.DOWN, null); 
                    match(input,21,FOLLOW_21_in_expression1167); 
                    pushFollow(FOLLOW_function_call_parameters_in_expression1171);
                    call_params=function_call_parameters();

                    state._fsp--;

                    match(input,22,FOLLOW_22_in_expression1173); 

                    match(input, Token.UP, null); 
                     Collections.reverse(call_params); result = call((ID4!=null?ID4.getText():null), call_params); 

                    }
                    break;
                case 18 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:230:5: ^( 'LAMBDA' params= function_decl_parameters ':' (EXPRESSION= . )* )
                    {
                    match(input,19,FOLLOW_19_in_expression1184); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_function_decl_parameters_in_expression1188);
                    params=function_decl_parameters();

                    state._fsp--;

                    match(input,20,FOLLOW_20_in_expression1190); 
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:230:62: (EXPRESSION= . )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( ((LA7_0>=NEGATION && LA7_0<=56)) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:230:62: EXPRESSION= .
                    	    {
                    	    EXPRESSION=(CommonTree)input.LT(1);
                    	    matchAny(input); 

                    	    }
                    	    break;

                    	default :
                    	    break loop7;
                        }
                    } while (true);


                    match(input, Token.UP, null); 
                     Collections.reverse(params); result = new LambdaValue(params, EXPRESSION, this.getEnvironmentContents(), this.getFunctions()); 

                    }
                    break;
                case 19 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:233:5: INT
                    {
                    INT5=(CommonTree)match(input,INT,FOLLOW_INT_in_expression1212); 
                     result = new IntegerValue(Integer.parseInt((INT5!=null?INT5.getText():null)), new PublicMy(ConstantDataSource.getInstance().toDataSourcesSet())); 

                    }
                    break;
                case 20 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:234:5: DOUBLE
                    {
                    DOUBLE6=(CommonTree)match(input,DOUBLE,FOLLOW_DOUBLE_in_expression1259); 
                     result = new DoubleValue(Double.parseDouble((DOUBLE6!=null?DOUBLE6.getText():null)), new PublicMy(ConstantDataSource.getInstance().toDataSourcesSet())); 

                    }
                    break;
                case 21 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:235:5: STRING_LITERAL
                    {
                    STRING_LITERAL7=(CommonTree)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_expression1303); 
                     result = new StringValue(prepareString((STRING_LITERAL7!=null?STRING_LITERAL7.getText():null)), new PublicMy(ConstantDataSource.getInstance().toDataSourcesSet())); 

                    }
                    break;
                case 22 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:236:5: 'TRUE'
                    {
                    match(input,36,FOLLOW_36_in_expression1339); 
                     result = new BooleanValue(true, new PublicMy(ConstantDataSource.getInstance().toDataSourcesSet())); 

                    }
                    break;
                case 23 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:237:5: 'FALSE'
                    {
                    match(input,37,FOLLOW_37_in_expression1383); 
                     result = new BooleanValue(false, new PublicMy(ConstantDataSource.getInstance().toDataSourcesSet())); 

                    }
                    break;
                case 24 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:240:5: ^( 'AS' e= expression type= type_rule )
                    {
                    match(input,51,FOLLOW_51_in_expression1435); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression1439);
                    e=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_type_rule_in_expression1443);
                    type=type_rule();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     e.checkType(type); result = e; 

                    }
                    break;
                case 25 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/TreeWalker.g:241:5: ^( 'IS' e= expression level= privacy_decl )
                    {
                    match(input,50,FOLLOW_50_in_expression1460); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression1464);
                    e=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_privacy_decl_in_expression1468);
                    level=privacy_decl();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     e.setToHigherOrEqualsPrivacyLevel(level.mergeSources(e.getPrivacyLevel())); result = e; 

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


    protected DFA8 dfa8 = new DFA8(this);
    static final String DFA8_eotS =
        "\33\uffff";
    static final String DFA8_eofS =
        "\20\uffff\1\32\12\uffff";
    static final String DFA8_minS =
        "\1\4\17\uffff\1\2\12\uffff";
    static final String DFA8_maxS =
        "\1\63\17\uffff\1\63\12\uffff";
    static final String DFA8_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1"+
        "\15\1\16\1\17\1\uffff\1\22\1\23\1\24\1\25\1\26\1\27\1\30\1\31\1"+
        "\21\1\20";
    static final String DFA8_specialS =
        "\33\uffff}>";
    static final String[] DFA8_transitionS = {
            "\1\6\1\20\1\22\1\uffff\1\23\10\uffff\1\24\1\uffff\1\21\14\uffff"+
            "\1\11\1\13\2\uffff\1\25\1\26\1\17\1\1\1\2\1\3\1\4\1\5\1\7\1"+
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
            "\1\31\4\32\1\uffff\1\32\10\uffff\1\32\1\uffff\1\32\2\uffff"+
            "\36\32",
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

    static final short[] DFA8_eot = DFA.unpackEncodedString(DFA8_eotS);
    static final short[] DFA8_eof = DFA.unpackEncodedString(DFA8_eofS);
    static final char[] DFA8_min = DFA.unpackEncodedStringToUnsignedChars(DFA8_minS);
    static final char[] DFA8_max = DFA.unpackEncodedStringToUnsignedChars(DFA8_maxS);
    static final short[] DFA8_accept = DFA.unpackEncodedString(DFA8_acceptS);
    static final short[] DFA8_special = DFA.unpackEncodedString(DFA8_specialS);
    static final short[][] DFA8_transition;

    static {
        int numStates = DFA8_transitionS.length;
        DFA8_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA8_transition[i] = DFA.unpackEncodedString(DFA8_transitionS[i]);
        }
    }

    class DFA8 extends DFA {

        public DFA8(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 8;
            this.eot = DFA8_eot;
            this.eof = DFA8_eof;
            this.min = DFA8_min;
            this.max = DFA8_max;
            this.accept = DFA8_accept;
            this.special = DFA8_special;
            this.transition = DFA8_transition;
        }
        public String getDescription() {
            return "205:1: expression returns [IDQLValue result] : ( ^( '+' op1= expression op2= expression ) | ^( '-' op1= expression op2= expression ) | ^( '*' op1= expression op2= expression ) | ^( '/' op1= expression op2= expression ) | ^( '%' op1= expression op2= expression ) | ^( NEGATION e= expression ) | ^( '=' op1= expression op2= expression ) | ^( '!=' op1= expression op2= expression ) | ^( '<' op1= expression op2= expression ) | ^( '<=' op1= expression op2= expression ) | ^( '>' op1= expression op2= expression ) | ^( '>=' op1= expression op2= expression ) | ^( '&&' op1= expression op2= expression ) | ^( '||' op1= expression op2= expression ) | ^( 'not' op1= expression ) | ID | ^( ID '(' call_params= function_call_parameters ')' ) | ^( 'LAMBDA' params= function_decl_parameters ':' (EXPRESSION= . )* ) | INT | DOUBLE | STRING_LITERAL | 'TRUE' | 'FALSE' | ^( 'AS' e= expression type= type_rule ) | ^( 'IS' e= expression level= privacy_decl ) );";
        }
    }
 

    public static final BitSet FOLLOW_ID_in_let61 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_let65 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_53_in_gather_statement84 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_let_in_gather_statement86 = new BitSet(new long[]{0x0000000000000028L});
    public static final BitSet FOLLOW_54_in_forward_statement108 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_forward_statement112 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_56_in_reduce_statement136 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_reduce_statement140 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_reduce_statement142 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_gather_statement_in_query161 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_forward_statement_in_query166 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_reduce_statement_in_query171 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_function_call_parameters206 = new BitSet(new long[]{0x000FFFF3000A0170L});
    public static final BitSet FOLLOW_function_call_parameters_in_function_call_parameters210 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_function_decl_parameters237 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_function_decl_parameters_in_function_decl_parameters241 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_rule_in_type_list270 = new BitSet(new long[]{0x0000000CF0000000L});
    public static final BitSet FOLLOW_type_list_in_type_list274 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_type_rule300 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_type_rule339 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_type_rule_in_type_rule343 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_29_in_type_rule367 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_type_rule406 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_type_rule445 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_type_rule487 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_type_list_in_type_rule492 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_23_in_privacy_decl526 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_24_in_privacy_decl563 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_privacy_decl601 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_privacy_decl636 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_privacy_decl671 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_expression722 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression726 = new BitSet(new long[]{0x000FFFF3000A0170L});
    public static final BitSet FOLLOW_expression_in_expression730 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_40_in_expression746 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression750 = new BitSet(new long[]{0x000FFFF3000A0170L});
    public static final BitSet FOLLOW_expression_in_expression754 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_41_in_expression770 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression774 = new BitSet(new long[]{0x000FFFF3000A0170L});
    public static final BitSet FOLLOW_expression_in_expression778 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_42_in_expression794 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression798 = new BitSet(new long[]{0x000FFFF3000A0170L});
    public static final BitSet FOLLOW_expression_in_expression802 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_43_in_expression818 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression822 = new BitSet(new long[]{0x000FFFF3000A0170L});
    public static final BitSet FOLLOW_expression_in_expression826 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NEGATION_in_expression842 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression846 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_44_in_expression880 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression886 = new BitSet(new long[]{0x000FFFF3000A0170L});
    public static final BitSet FOLLOW_expression_in_expression890 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_45_in_expression904 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression909 = new BitSet(new long[]{0x000FFFF3000A0170L});
    public static final BitSet FOLLOW_expression_in_expression913 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_32_in_expression927 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression933 = new BitSet(new long[]{0x000FFFF3000A0170L});
    public static final BitSet FOLLOW_expression_in_expression937 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_46_in_expression951 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression956 = new BitSet(new long[]{0x000FFFF3000A0170L});
    public static final BitSet FOLLOW_expression_in_expression960 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_33_in_expression974 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression980 = new BitSet(new long[]{0x000FFFF3000A0170L});
    public static final BitSet FOLLOW_expression_in_expression984 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_47_in_expression998 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression1003 = new BitSet(new long[]{0x000FFFF3000A0170L});
    public static final BitSet FOLLOW_expression_in_expression1007 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_48_in_expression1027 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression1032 = new BitSet(new long[]{0x000FFFF3000A0170L});
    public static final BitSet FOLLOW_expression_in_expression1036 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_49_in_expression1050 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression1055 = new BitSet(new long[]{0x000FFFF3000A0170L});
    public static final BitSet FOLLOW_expression_in_expression1059 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_38_in_expression1073 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression1077 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_expression1114 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_expression1165 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_21_in_expression1167 = new BitSet(new long[]{0x000FFFF3004A0170L});
    public static final BitSet FOLLOW_function_call_parameters_in_expression1171 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_expression1173 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_19_in_expression1184 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_function_decl_parameters_in_expression1188 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_expression1190 = new BitSet(new long[]{0x01FFFFFFFFFFFFF8L});
    public static final BitSet FOLLOW_INT_in_expression1212 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_in_expression1259 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_expression1303 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_expression1339 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_expression1383 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_expression1435 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression1439 = new BitSet(new long[]{0x0000000CF0000000L});
    public static final BitSet FOLLOW_type_rule_in_expression1443 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_50_in_expression1460 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression1464 = new BitSet(new long[]{0x000000000F800000L});
    public static final BitSet FOLLOW_privacy_decl_in_expression1468 = new BitSet(new long[]{0x0000000000000008L});

}