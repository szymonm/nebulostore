// $ANTLR 3.3 Nov 30, 2010 12:50:56 /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g 2012-01-22 23:18:46

  package org.nebulostore.query.language.interpreter.antlr;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;

public class DQLGrammarParser extends Parser {
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


        public DQLGrammarParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public DQLGrammarParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return DQLGrammarParser.tokenNames; }
    public String getGrammarFileName() { return "/home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g"; }


    public static class function_decl_parameters_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "function_decl_parameters"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:80:1: function_decl_parameters : ID ( ',' ID )* ;
    public final DQLGrammarParser.function_decl_parameters_return function_decl_parameters() throws RecognitionException {
        DQLGrammarParser.function_decl_parameters_return retval = new DQLGrammarParser.function_decl_parameters_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token ID1=null;
        Token char_literal2=null;
        Token ID3=null;

        CommonTree ID1_tree=null;
        CommonTree char_literal2_tree=null;
        CommonTree ID3_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:80:26: ( ID ( ',' ID )* )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:81:3: ID ( ',' ID )*
            {
            root_0 = (CommonTree)adaptor.nil();

            ID1=(Token)match(input,ID,FOLLOW_ID_in_function_decl_parameters620); 
            ID1_tree = (CommonTree)adaptor.create(ID1);
            adaptor.addChild(root_0, ID1_tree);

            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:81:6: ( ',' ID )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==18) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:81:8: ',' ID
            	    {
            	    char_literal2=(Token)match(input,18,FOLLOW_18_in_function_decl_parameters624); 
            	    ID3=(Token)match(input,ID,FOLLOW_ID_in_function_decl_parameters628); 
            	    ID3_tree = (CommonTree)adaptor.create(ID3);
            	    adaptor.addChild(root_0, ID3_tree);


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "function_decl_parameters"

    public static class lambda_function_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "lambda_function"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:83:1: lambda_function : 'LAMBDA' function_decl_parameters ':' '(' expression ')' ;
    public final DQLGrammarParser.lambda_function_return lambda_function() throws RecognitionException {
        DQLGrammarParser.lambda_function_return retval = new DQLGrammarParser.lambda_function_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal4=null;
        Token char_literal6=null;
        Token char_literal7=null;
        Token char_literal9=null;
        DQLGrammarParser.function_decl_parameters_return function_decl_parameters5 = null;

        DQLGrammarParser.expression_return expression8 = null;


        CommonTree string_literal4_tree=null;
        CommonTree char_literal6_tree=null;
        CommonTree char_literal7_tree=null;
        CommonTree char_literal9_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:83:17: ( 'LAMBDA' function_decl_parameters ':' '(' expression ')' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:84:3: 'LAMBDA' function_decl_parameters ':' '(' expression ')'
            {
            root_0 = (CommonTree)adaptor.nil();

            string_literal4=(Token)match(input,19,FOLLOW_19_in_lambda_function642); 
            string_literal4_tree = (CommonTree)adaptor.create(string_literal4);
            root_0 = (CommonTree)adaptor.becomeRoot(string_literal4_tree, root_0);

            pushFollow(FOLLOW_function_decl_parameters_in_lambda_function645);
            function_decl_parameters5=function_decl_parameters();

            state._fsp--;

            adaptor.addChild(root_0, function_decl_parameters5.getTree());
            char_literal6=(Token)match(input,20,FOLLOW_20_in_lambda_function647); 
            char_literal6_tree = (CommonTree)adaptor.create(char_literal6);
            adaptor.addChild(root_0, char_literal6_tree);

            char_literal7=(Token)match(input,21,FOLLOW_21_in_lambda_function649); 
            pushFollow(FOLLOW_expression_in_lambda_function652);
            expression8=expression();

            state._fsp--;

            adaptor.addChild(root_0, expression8.getTree());
            char_literal9=(Token)match(input,22,FOLLOW_22_in_lambda_function654); 

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "lambda_function"

    public static class call_parameter_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "call_parameter"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:86:1: call_parameter : expression ;
    public final DQLGrammarParser.call_parameter_return call_parameter() throws RecognitionException {
        DQLGrammarParser.call_parameter_return retval = new DQLGrammarParser.call_parameter_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        DQLGrammarParser.expression_return expression10 = null;



        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:86:15: ( expression )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:87:3: expression
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_expression_in_call_parameter664);
            expression10=expression();

            state._fsp--;

            adaptor.addChild(root_0, expression10.getTree());

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "call_parameter"

    public static class function_call_parameters_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "function_call_parameters"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:89:1: function_call_parameters : call_parameter ( ',' call_parameter )* ;
    public final DQLGrammarParser.function_call_parameters_return function_call_parameters() throws RecognitionException {
        DQLGrammarParser.function_call_parameters_return retval = new DQLGrammarParser.function_call_parameters_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal12=null;
        DQLGrammarParser.call_parameter_return call_parameter11 = null;

        DQLGrammarParser.call_parameter_return call_parameter13 = null;


        CommonTree char_literal12_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:89:26: ( call_parameter ( ',' call_parameter )* )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:90:3: call_parameter ( ',' call_parameter )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_call_parameter_in_function_call_parameters675);
            call_parameter11=call_parameter();

            state._fsp--;

            adaptor.addChild(root_0, call_parameter11.getTree());
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:90:19: ( ',' call_parameter )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==18) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:90:20: ',' call_parameter
            	    {
            	    char_literal12=(Token)match(input,18,FOLLOW_18_in_function_call_parameters679); 
            	    pushFollow(FOLLOW_call_parameter_in_function_call_parameters682);
            	    call_parameter13=call_parameter();

            	    state._fsp--;

            	    adaptor.addChild(root_0, call_parameter13.getTree());

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "function_call_parameters"

    public static class function_call_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "function_call"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:94:1: function_call : ID '(' ( function_call_parameters )? ')' ;
    public final DQLGrammarParser.function_call_return function_call() throws RecognitionException {
        DQLGrammarParser.function_call_return retval = new DQLGrammarParser.function_call_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token ID14=null;
        Token char_literal15=null;
        Token char_literal17=null;
        DQLGrammarParser.function_call_parameters_return function_call_parameters16 = null;


        CommonTree ID14_tree=null;
        CommonTree char_literal15_tree=null;
        CommonTree char_literal17_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:94:15: ( ID '(' ( function_call_parameters )? ')' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:95:3: ID '(' ( function_call_parameters )? ')'
            {
            root_0 = (CommonTree)adaptor.nil();

            ID14=(Token)match(input,ID,FOLLOW_ID_in_function_call697); 
            ID14_tree = (CommonTree)adaptor.create(ID14);
            root_0 = (CommonTree)adaptor.becomeRoot(ID14_tree, root_0);

            char_literal15=(Token)match(input,21,FOLLOW_21_in_function_call700); 
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:95:12: ( function_call_parameters )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( ((LA3_0>=ID && LA3_0<=INT)||LA3_0==DOUBLE||LA3_0==STRING_LITERAL||LA3_0==19||LA3_0==21||(LA3_0>=35 && LA3_0<=37)) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:95:12: function_call_parameters
                    {
                    pushFollow(FOLLOW_function_call_parameters_in_function_call703);
                    function_call_parameters16=function_call_parameters();

                    state._fsp--;

                    adaptor.addChild(root_0, function_call_parameters16.getTree());

                    }
                    break;

            }

            char_literal17=(Token)match(input,22,FOLLOW_22_in_function_call706); 

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "function_call"

    public static class privacy_decl_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "privacy_decl"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:103:1: privacy_decl : ( 'PRIVATE_MY' | 'PUBLIC_MY' | 'PRIVATE_COND_MY' | 'PUBLIC_OTHER' | 'PRIVATE_COND_OTHER' );
    public final DQLGrammarParser.privacy_decl_return privacy_decl() throws RecognitionException {
        DQLGrammarParser.privacy_decl_return retval = new DQLGrammarParser.privacy_decl_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token set18=null;

        CommonTree set18_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:104:3: ( 'PRIVATE_MY' | 'PUBLIC_MY' | 'PRIVATE_COND_MY' | 'PUBLIC_OTHER' | 'PRIVATE_COND_OTHER' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:
            {
            root_0 = (CommonTree)adaptor.nil();

            set18=(Token)input.LT(1);
            if ( (input.LA(1)>=23 && input.LA(1)<=27) ) {
                input.consume();
                adaptor.addChild(root_0, (CommonTree)adaptor.create(set18));
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "privacy_decl"

    public static class type_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "type"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:111:1: type : ( 'INTEGER' | 'DOUBLE' | 'STRING' | 'TUPLE' '<' ( type )+ '>' | 'LIST' '<' type '>' );
    public final DQLGrammarParser.type_return type() throws RecognitionException {
        DQLGrammarParser.type_return retval = new DQLGrammarParser.type_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal19=null;
        Token string_literal20=null;
        Token string_literal21=null;
        Token string_literal22=null;
        Token char_literal23=null;
        Token char_literal25=null;
        Token string_literal26=null;
        Token char_literal27=null;
        Token char_literal29=null;
        DQLGrammarParser.type_return type24 = null;

        DQLGrammarParser.type_return type28 = null;


        CommonTree string_literal19_tree=null;
        CommonTree string_literal20_tree=null;
        CommonTree string_literal21_tree=null;
        CommonTree string_literal22_tree=null;
        CommonTree char_literal23_tree=null;
        CommonTree char_literal25_tree=null;
        CommonTree string_literal26_tree=null;
        CommonTree char_literal27_tree=null;
        CommonTree char_literal29_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:112:3: ( 'INTEGER' | 'DOUBLE' | 'STRING' | 'TUPLE' '<' ( type )+ '>' | 'LIST' '<' type '>' )
            int alt5=5;
            switch ( input.LA(1) ) {
            case 28:
                {
                alt5=1;
                }
                break;
            case 29:
                {
                alt5=2;
                }
                break;
            case 30:
                {
                alt5=3;
                }
                break;
            case 31:
                {
                alt5=4;
                }
                break;
            case 34:
                {
                alt5=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }

            switch (alt5) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:112:5: 'INTEGER'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal19=(Token)match(input,28,FOLLOW_28_in_type793); 
                    string_literal19_tree = (CommonTree)adaptor.create(string_literal19);
                    adaptor.addChild(root_0, string_literal19_tree);


                    }
                    break;
                case 2 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:113:5: 'DOUBLE'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal20=(Token)match(input,29,FOLLOW_29_in_type799); 
                    string_literal20_tree = (CommonTree)adaptor.create(string_literal20);
                    adaptor.addChild(root_0, string_literal20_tree);


                    }
                    break;
                case 3 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:114:5: 'STRING'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal21=(Token)match(input,30,FOLLOW_30_in_type805); 
                    string_literal21_tree = (CommonTree)adaptor.create(string_literal21);
                    adaptor.addChild(root_0, string_literal21_tree);


                    }
                    break;
                case 4 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:115:5: 'TUPLE' '<' ( type )+ '>'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal22=(Token)match(input,31,FOLLOW_31_in_type811); 
                    string_literal22_tree = (CommonTree)adaptor.create(string_literal22);
                    root_0 = (CommonTree)adaptor.becomeRoot(string_literal22_tree, root_0);

                    char_literal23=(Token)match(input,32,FOLLOW_32_in_type814); 
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:115:19: ( type )+
                    int cnt4=0;
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( ((LA4_0>=28 && LA4_0<=31)||LA4_0==34) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:115:19: type
                    	    {
                    	    pushFollow(FOLLOW_type_in_type817);
                    	    type24=type();

                    	    state._fsp--;

                    	    adaptor.addChild(root_0, type24.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt4 >= 1 ) break loop4;
                                EarlyExitException eee =
                                    new EarlyExitException(4, input);
                                throw eee;
                        }
                        cnt4++;
                    } while (true);

                    char_literal25=(Token)match(input,33,FOLLOW_33_in_type820); 

                    }
                    break;
                case 5 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:116:5: 'LIST' '<' type '>'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal26=(Token)match(input,34,FOLLOW_34_in_type827); 
                    string_literal26_tree = (CommonTree)adaptor.create(string_literal26);
                    root_0 = (CommonTree)adaptor.becomeRoot(string_literal26_tree, root_0);

                    char_literal27=(Token)match(input,32,FOLLOW_32_in_type830); 
                    pushFollow(FOLLOW_type_in_type833);
                    type28=type();

                    state._fsp--;

                    adaptor.addChild(root_0, type28.getTree());
                    char_literal29=(Token)match(input,33,FOLLOW_33_in_type835); 

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "type"

    public static class term_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "term"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:120:1: term : ( ID | '(' expression ')' | INT | DOUBLE | STRING_LITERAL | function_call | lambda_function );
    public final DQLGrammarParser.term_return term() throws RecognitionException {
        DQLGrammarParser.term_return retval = new DQLGrammarParser.term_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token ID30=null;
        Token char_literal31=null;
        Token char_literal33=null;
        Token INT34=null;
        Token DOUBLE35=null;
        Token STRING_LITERAL36=null;
        DQLGrammarParser.expression_return expression32 = null;

        DQLGrammarParser.function_call_return function_call37 = null;

        DQLGrammarParser.lambda_function_return lambda_function38 = null;


        CommonTree ID30_tree=null;
        CommonTree char_literal31_tree=null;
        CommonTree char_literal33_tree=null;
        CommonTree INT34_tree=null;
        CommonTree DOUBLE35_tree=null;
        CommonTree STRING_LITERAL36_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:121:3: ( ID | '(' expression ')' | INT | DOUBLE | STRING_LITERAL | function_call | lambda_function )
            int alt6=7;
            switch ( input.LA(1) ) {
            case ID:
                {
                int LA6_1 = input.LA(2);

                if ( (LA6_1==21) ) {
                    alt6=6;
                }
                else if ( (LA6_1==EOF||LA6_1==18||LA6_1==22||(LA6_1>=32 && LA6_1<=33)||(LA6_1>=36 && LA6_1<=47)||LA6_1==49||LA6_1==51||LA6_1==55) ) {
                    alt6=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 1, input);

                    throw nvae;
                }
                }
                break;
            case 21:
                {
                alt6=2;
                }
                break;
            case INT:
                {
                alt6=3;
                }
                break;
            case DOUBLE:
                {
                alt6=4;
                }
                break;
            case STRING_LITERAL:
                {
                alt6=5;
                }
                break;
            case 19:
                {
                alt6=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:121:5: ID
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    ID30=(Token)match(input,ID,FOLLOW_ID_in_term851); 
                    ID30_tree = (CommonTree)adaptor.create(ID30);
                    adaptor.addChild(root_0, ID30_tree);


                    }
                    break;
                case 2 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:122:5: '(' expression ')'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    char_literal31=(Token)match(input,21,FOLLOW_21_in_term857); 
                    pushFollow(FOLLOW_expression_in_term860);
                    expression32=expression();

                    state._fsp--;

                    adaptor.addChild(root_0, expression32.getTree());
                    char_literal33=(Token)match(input,22,FOLLOW_22_in_term862); 

                    }
                    break;
                case 3 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:123:5: INT
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    INT34=(Token)match(input,INT,FOLLOW_INT_in_term869); 
                    INT34_tree = (CommonTree)adaptor.create(INT34);
                    adaptor.addChild(root_0, INT34_tree);


                    }
                    break;
                case 4 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:124:5: DOUBLE
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    DOUBLE35=(Token)match(input,DOUBLE,FOLLOW_DOUBLE_in_term875); 
                    DOUBLE35_tree = (CommonTree)adaptor.create(DOUBLE35);
                    adaptor.addChild(root_0, DOUBLE35_tree);


                    }
                    break;
                case 5 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:125:5: STRING_LITERAL
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    STRING_LITERAL36=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_term882); 
                    STRING_LITERAL36_tree = (CommonTree)adaptor.create(STRING_LITERAL36);
                    adaptor.addChild(root_0, STRING_LITERAL36_tree);


                    }
                    break;
                case 6 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:126:5: function_call
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_function_call_in_term888);
                    function_call37=function_call();

                    state._fsp--;

                    adaptor.addChild(root_0, function_call37.getTree());

                    }
                    break;
                case 7 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:127:5: lambda_function
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_lambda_function_in_term894);
                    lambda_function38=lambda_function();

                    state._fsp--;

                    adaptor.addChild(root_0, lambda_function38.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "term"

    public static class negation_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "negation"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:130:1: negation : ( 'not' )* term ;
    public final DQLGrammarParser.negation_return negation() throws RecognitionException {
        DQLGrammarParser.negation_return retval = new DQLGrammarParser.negation_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal39=null;
        DQLGrammarParser.term_return term40 = null;


        CommonTree string_literal39_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:131:3: ( ( 'not' )* term )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:131:5: ( 'not' )* term
            {
            root_0 = (CommonTree)adaptor.nil();

            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:131:5: ( 'not' )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==35) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:131:6: 'not'
            	    {
            	    string_literal39=(Token)match(input,35,FOLLOW_35_in_negation911); 
            	    string_literal39_tree = (CommonTree)adaptor.create(string_literal39);
            	    root_0 = (CommonTree)adaptor.becomeRoot(string_literal39_tree, root_0);


            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);

            pushFollow(FOLLOW_term_in_negation916);
            term40=term();

            state._fsp--;

            adaptor.addChild(root_0, term40.getTree());

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "negation"

    public static class unary_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "unary"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:134:1: unary : ( '+' | unary_negation_rewrite )* negation ;
    public final DQLGrammarParser.unary_return unary() throws RecognitionException {
        DQLGrammarParser.unary_return retval = new DQLGrammarParser.unary_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal41=null;
        DQLGrammarParser.unary_negation_rewrite_return unary_negation_rewrite42 = null;

        DQLGrammarParser.negation_return negation43 = null;


        CommonTree char_literal41_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:135:3: ( ( '+' | unary_negation_rewrite )* negation )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:135:5: ( '+' | unary_negation_rewrite )* negation
            {
            root_0 = (CommonTree)adaptor.nil();

            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:135:5: ( '+' | unary_negation_rewrite )*
            loop8:
            do {
                int alt8=3;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==36) ) {
                    alt8=1;
                }
                else if ( (LA8_0==37) ) {
                    alt8=2;
                }


                switch (alt8) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:135:6: '+'
            	    {
            	    char_literal41=(Token)match(input,36,FOLLOW_36_in_unary932); 

            	    }
            	    break;
            	case 2 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:135:13: unary_negation_rewrite
            	    {
            	    pushFollow(FOLLOW_unary_negation_rewrite_in_unary937);
            	    unary_negation_rewrite42=unary_negation_rewrite();

            	    state._fsp--;

            	    root_0 = (CommonTree)adaptor.becomeRoot(unary_negation_rewrite42.getTree(), root_0);

            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            pushFollow(FOLLOW_negation_in_unary942);
            negation43=negation();

            state._fsp--;

            adaptor.addChild(root_0, negation43.getTree());

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "unary"

    public static class unary_negation_rewrite_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "unary_negation_rewrite"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:138:1: unary_negation_rewrite : '-' -> NEGATION ;
    public final DQLGrammarParser.unary_negation_rewrite_return unary_negation_rewrite() throws RecognitionException {
        DQLGrammarParser.unary_negation_rewrite_return retval = new DQLGrammarParser.unary_negation_rewrite_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal44=null;

        CommonTree char_literal44_tree=null;
        RewriteRuleTokenStream stream_37=new RewriteRuleTokenStream(adaptor,"token 37");

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:139:3: ( '-' -> NEGATION )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:139:5: '-'
            {
            char_literal44=(Token)match(input,37,FOLLOW_37_in_unary_negation_rewrite957);  
            stream_37.add(char_literal44);



            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 139:9: -> NEGATION
            {
                adaptor.addChild(root_0, (CommonTree)adaptor.create(NEGATION, "NEGATION"));

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "unary_negation_rewrite"

    public static class mult_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "mult"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:142:1: mult : unary ( ( '*' | '/' | '%' ) unary )* ;
    public final DQLGrammarParser.mult_return mult() throws RecognitionException {
        DQLGrammarParser.mult_return retval = new DQLGrammarParser.mult_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal46=null;
        Token char_literal47=null;
        Token char_literal48=null;
        DQLGrammarParser.unary_return unary45 = null;

        DQLGrammarParser.unary_return unary49 = null;


        CommonTree char_literal46_tree=null;
        CommonTree char_literal47_tree=null;
        CommonTree char_literal48_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:143:3: ( unary ( ( '*' | '/' | '%' ) unary )* )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:143:5: unary ( ( '*' | '/' | '%' ) unary )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_unary_in_mult976);
            unary45=unary();

            state._fsp--;

            adaptor.addChild(root_0, unary45.getTree());
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:143:11: ( ( '*' | '/' | '%' ) unary )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( ((LA10_0>=38 && LA10_0<=40)) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:143:12: ( '*' | '/' | '%' ) unary
            	    {
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:143:12: ( '*' | '/' | '%' )
            	    int alt9=3;
            	    switch ( input.LA(1) ) {
            	    case 38:
            	        {
            	        alt9=1;
            	        }
            	        break;
            	    case 39:
            	        {
            	        alt9=2;
            	        }
            	        break;
            	    case 40:
            	        {
            	        alt9=3;
            	        }
            	        break;
            	    default:
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 9, 0, input);

            	        throw nvae;
            	    }

            	    switch (alt9) {
            	        case 1 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:143:13: '*'
            	            {
            	            char_literal46=(Token)match(input,38,FOLLOW_38_in_mult980); 
            	            char_literal46_tree = (CommonTree)adaptor.create(char_literal46);
            	            root_0 = (CommonTree)adaptor.becomeRoot(char_literal46_tree, root_0);


            	            }
            	            break;
            	        case 2 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:143:20: '/'
            	            {
            	            char_literal47=(Token)match(input,39,FOLLOW_39_in_mult985); 
            	            char_literal47_tree = (CommonTree)adaptor.create(char_literal47);
            	            root_0 = (CommonTree)adaptor.becomeRoot(char_literal47_tree, root_0);


            	            }
            	            break;
            	        case 3 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:143:27: '%'
            	            {
            	            char_literal48=(Token)match(input,40,FOLLOW_40_in_mult990); 
            	            char_literal48_tree = (CommonTree)adaptor.create(char_literal48);
            	            root_0 = (CommonTree)adaptor.becomeRoot(char_literal48_tree, root_0);


            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_unary_in_mult994);
            	    unary49=unary();

            	    state._fsp--;

            	    adaptor.addChild(root_0, unary49.getTree());

            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "mult"

    public static class add_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "add"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:146:1: add : mult ( ( '+' | '-' ) mult )* ;
    public final DQLGrammarParser.add_return add() throws RecognitionException {
        DQLGrammarParser.add_return retval = new DQLGrammarParser.add_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal51=null;
        Token char_literal52=null;
        DQLGrammarParser.mult_return mult50 = null;

        DQLGrammarParser.mult_return mult53 = null;


        CommonTree char_literal51_tree=null;
        CommonTree char_literal52_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:147:3: ( mult ( ( '+' | '-' ) mult )* )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:147:5: mult ( ( '+' | '-' ) mult )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_mult_in_add1009);
            mult50=mult();

            state._fsp--;

            adaptor.addChild(root_0, mult50.getTree());
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:147:10: ( ( '+' | '-' ) mult )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( ((LA12_0>=36 && LA12_0<=37)) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:147:11: ( '+' | '-' ) mult
            	    {
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:147:11: ( '+' | '-' )
            	    int alt11=2;
            	    int LA11_0 = input.LA(1);

            	    if ( (LA11_0==36) ) {
            	        alt11=1;
            	    }
            	    else if ( (LA11_0==37) ) {
            	        alt11=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 11, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt11) {
            	        case 1 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:147:12: '+'
            	            {
            	            char_literal51=(Token)match(input,36,FOLLOW_36_in_add1013); 
            	            char_literal51_tree = (CommonTree)adaptor.create(char_literal51);
            	            root_0 = (CommonTree)adaptor.becomeRoot(char_literal51_tree, root_0);


            	            }
            	            break;
            	        case 2 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:147:19: '-'
            	            {
            	            char_literal52=(Token)match(input,37,FOLLOW_37_in_add1018); 
            	            char_literal52_tree = (CommonTree)adaptor.create(char_literal52);
            	            root_0 = (CommonTree)adaptor.becomeRoot(char_literal52_tree, root_0);


            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_mult_in_add1022);
            	    mult53=mult();

            	    state._fsp--;

            	    adaptor.addChild(root_0, mult53.getTree());

            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "add"

    public static class relation_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "relation"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:150:1: relation : add ( ( '=' | '!=' | '<' | '<=' | '>=' | '>' ) add )* ;
    public final DQLGrammarParser.relation_return relation() throws RecognitionException {
        DQLGrammarParser.relation_return retval = new DQLGrammarParser.relation_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal55=null;
        Token string_literal56=null;
        Token char_literal57=null;
        Token string_literal58=null;
        Token string_literal59=null;
        Token char_literal60=null;
        DQLGrammarParser.add_return add54 = null;

        DQLGrammarParser.add_return add61 = null;


        CommonTree char_literal55_tree=null;
        CommonTree string_literal56_tree=null;
        CommonTree char_literal57_tree=null;
        CommonTree string_literal58_tree=null;
        CommonTree string_literal59_tree=null;
        CommonTree char_literal60_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:151:3: ( add ( ( '=' | '!=' | '<' | '<=' | '>=' | '>' ) add )* )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:151:5: add ( ( '=' | '!=' | '<' | '<=' | '>=' | '>' ) add )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_add_in_relation1037);
            add54=add();

            state._fsp--;

            adaptor.addChild(root_0, add54.getTree());
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:151:9: ( ( '=' | '!=' | '<' | '<=' | '>=' | '>' ) add )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( ((LA14_0>=32 && LA14_0<=33)||(LA14_0>=41 && LA14_0<=44)) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:151:10: ( '=' | '!=' | '<' | '<=' | '>=' | '>' ) add
            	    {
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:151:10: ( '=' | '!=' | '<' | '<=' | '>=' | '>' )
            	    int alt13=6;
            	    switch ( input.LA(1) ) {
            	    case 41:
            	        {
            	        alt13=1;
            	        }
            	        break;
            	    case 42:
            	        {
            	        alt13=2;
            	        }
            	        break;
            	    case 32:
            	        {
            	        alt13=3;
            	        }
            	        break;
            	    case 43:
            	        {
            	        alt13=4;
            	        }
            	        break;
            	    case 44:
            	        {
            	        alt13=5;
            	        }
            	        break;
            	    case 33:
            	        {
            	        alt13=6;
            	        }
            	        break;
            	    default:
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 13, 0, input);

            	        throw nvae;
            	    }

            	    switch (alt13) {
            	        case 1 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:151:11: '='
            	            {
            	            char_literal55=(Token)match(input,41,FOLLOW_41_in_relation1041); 
            	            char_literal55_tree = (CommonTree)adaptor.create(char_literal55);
            	            root_0 = (CommonTree)adaptor.becomeRoot(char_literal55_tree, root_0);


            	            }
            	            break;
            	        case 2 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:151:18: '!='
            	            {
            	            string_literal56=(Token)match(input,42,FOLLOW_42_in_relation1046); 
            	            string_literal56_tree = (CommonTree)adaptor.create(string_literal56);
            	            root_0 = (CommonTree)adaptor.becomeRoot(string_literal56_tree, root_0);


            	            }
            	            break;
            	        case 3 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:151:26: '<'
            	            {
            	            char_literal57=(Token)match(input,32,FOLLOW_32_in_relation1051); 
            	            char_literal57_tree = (CommonTree)adaptor.create(char_literal57);
            	            root_0 = (CommonTree)adaptor.becomeRoot(char_literal57_tree, root_0);


            	            }
            	            break;
            	        case 4 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:151:33: '<='
            	            {
            	            string_literal58=(Token)match(input,43,FOLLOW_43_in_relation1056); 
            	            string_literal58_tree = (CommonTree)adaptor.create(string_literal58);
            	            root_0 = (CommonTree)adaptor.becomeRoot(string_literal58_tree, root_0);


            	            }
            	            break;
            	        case 5 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:151:41: '>='
            	            {
            	            string_literal59=(Token)match(input,44,FOLLOW_44_in_relation1061); 
            	            string_literal59_tree = (CommonTree)adaptor.create(string_literal59);
            	            root_0 = (CommonTree)adaptor.becomeRoot(string_literal59_tree, root_0);


            	            }
            	            break;
            	        case 6 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:151:49: '>'
            	            {
            	            char_literal60=(Token)match(input,33,FOLLOW_33_in_relation1066); 
            	            char_literal60_tree = (CommonTree)adaptor.create(char_literal60);
            	            root_0 = (CommonTree)adaptor.becomeRoot(char_literal60_tree, root_0);


            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_add_in_relation1070);
            	    add61=add();

            	    state._fsp--;

            	    adaptor.addChild(root_0, add61.getTree());

            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "relation"

    public static class expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "expression"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:154:1: expression : relation ( ( '&&' | '||' ) relation )* ( 'IS' privacy_decl ( 'AS' type )? )? ;
    public final DQLGrammarParser.expression_return expression() throws RecognitionException {
        DQLGrammarParser.expression_return retval = new DQLGrammarParser.expression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal63=null;
        Token string_literal64=null;
        Token string_literal66=null;
        Token string_literal68=null;
        DQLGrammarParser.relation_return relation62 = null;

        DQLGrammarParser.relation_return relation65 = null;

        DQLGrammarParser.privacy_decl_return privacy_decl67 = null;

        DQLGrammarParser.type_return type69 = null;


        CommonTree string_literal63_tree=null;
        CommonTree string_literal64_tree=null;
        CommonTree string_literal66_tree=null;
        CommonTree string_literal68_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:155:3: ( relation ( ( '&&' | '||' ) relation )* ( 'IS' privacy_decl ( 'AS' type )? )? )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:155:5: relation ( ( '&&' | '||' ) relation )* ( 'IS' privacy_decl ( 'AS' type )? )?
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_relation_in_expression1087);
            relation62=relation();

            state._fsp--;

            adaptor.addChild(root_0, relation62.getTree());
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:155:14: ( ( '&&' | '||' ) relation )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( ((LA16_0>=45 && LA16_0<=46)) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:155:15: ( '&&' | '||' ) relation
            	    {
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:155:15: ( '&&' | '||' )
            	    int alt15=2;
            	    int LA15_0 = input.LA(1);

            	    if ( (LA15_0==45) ) {
            	        alt15=1;
            	    }
            	    else if ( (LA15_0==46) ) {
            	        alt15=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 15, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt15) {
            	        case 1 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:155:16: '&&'
            	            {
            	            string_literal63=(Token)match(input,45,FOLLOW_45_in_expression1091); 
            	            string_literal63_tree = (CommonTree)adaptor.create(string_literal63);
            	            root_0 = (CommonTree)adaptor.becomeRoot(string_literal63_tree, root_0);


            	            }
            	            break;
            	        case 2 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:155:24: '||'
            	            {
            	            string_literal64=(Token)match(input,46,FOLLOW_46_in_expression1096); 
            	            string_literal64_tree = (CommonTree)adaptor.create(string_literal64);
            	            root_0 = (CommonTree)adaptor.becomeRoot(string_literal64_tree, root_0);


            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_relation_in_expression1100);
            	    relation65=relation();

            	    state._fsp--;

            	    adaptor.addChild(root_0, relation65.getTree());

            	    }
            	    break;

            	default :
            	    break loop16;
                }
            } while (true);

            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:155:42: ( 'IS' privacy_decl ( 'AS' type )? )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==47) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:155:43: 'IS' privacy_decl ( 'AS' type )?
                    {
                    string_literal66=(Token)match(input,47,FOLLOW_47_in_expression1105); 
                    string_literal66_tree = (CommonTree)adaptor.create(string_literal66);
                    root_0 = (CommonTree)adaptor.becomeRoot(string_literal66_tree, root_0);

                    pushFollow(FOLLOW_privacy_decl_in_expression1108);
                    privacy_decl67=privacy_decl();

                    state._fsp--;

                    adaptor.addChild(root_0, privacy_decl67.getTree());
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:155:62: ( 'AS' type )?
                    int alt17=2;
                    int LA17_0 = input.LA(1);

                    if ( (LA17_0==48) ) {
                        alt17=1;
                    }
                    switch (alt17) {
                        case 1 :
                            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:155:64: 'AS' type
                            {
                            string_literal68=(Token)match(input,48,FOLLOW_48_in_expression1112); 
                            string_literal68_tree = (CommonTree)adaptor.create(string_literal68);
                            root_0 = (CommonTree)adaptor.becomeRoot(string_literal68_tree, root_0);

                            pushFollow(FOLLOW_type_in_expression1115);
                            type69=type();

                            state._fsp--;

                            adaptor.addChild(root_0, type69.getTree());

                            }
                            break;

                    }


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "expression"

    public static class let_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "let"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:159:1: let : 'LET' ID '=' expression ;
    public final DQLGrammarParser.let_return let() throws RecognitionException {
        DQLGrammarParser.let_return retval = new DQLGrammarParser.let_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal70=null;
        Token ID71=null;
        Token char_literal72=null;
        DQLGrammarParser.expression_return expression73 = null;


        CommonTree string_literal70_tree=null;
        CommonTree ID71_tree=null;
        CommonTree char_literal72_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:159:5: ( 'LET' ID '=' expression )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:159:7: 'LET' ID '=' expression
            {
            root_0 = (CommonTree)adaptor.nil();

            string_literal70=(Token)match(input,49,FOLLOW_49_in_let1133); 
            ID71=(Token)match(input,ID,FOLLOW_ID_in_let1136); 
            ID71_tree = (CommonTree)adaptor.create(ID71);
            root_0 = (CommonTree)adaptor.becomeRoot(ID71_tree, root_0);

            char_literal72=(Token)match(input,41,FOLLOW_41_in_let1139); 
            pushFollow(FOLLOW_expression_in_let1142);
            expression73=expression();

            state._fsp--;

            adaptor.addChild(root_0, expression73.getTree());

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "let"

    public static class gather_statement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "gather_statement"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:161:1: gather_statement : 'GATHER' ( let )+ ;
    public final DQLGrammarParser.gather_statement_return gather_statement() throws RecognitionException {
        DQLGrammarParser.gather_statement_return retval = new DQLGrammarParser.gather_statement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal74=null;
        DQLGrammarParser.let_return let75 = null;


        CommonTree string_literal74_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:162:3: ( 'GATHER' ( let )+ )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:162:7: 'GATHER' ( let )+
            {
            root_0 = (CommonTree)adaptor.nil();

            string_literal74=(Token)match(input,50,FOLLOW_50_in_gather_statement1156); 
            string_literal74_tree = (CommonTree)adaptor.create(string_literal74);
            root_0 = (CommonTree)adaptor.becomeRoot(string_literal74_tree, root_0);

            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:162:17: ( let )+
            int cnt19=0;
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==49) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:162:17: let
            	    {
            	    pushFollow(FOLLOW_let_in_gather_statement1159);
            	    let75=let();

            	    state._fsp--;

            	    adaptor.addChild(root_0, let75.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt19 >= 1 ) break loop19;
                        EarlyExitException eee =
                            new EarlyExitException(19, input);
                        throw eee;
                }
                cnt19++;
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "gather_statement"

    public static class forward_statement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "forward_statement"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:164:1: forward_statement : 'FORWARD' ( 'MAX' 'DEPTH' INT )? 'TO' expression ;
    public final DQLGrammarParser.forward_statement_return forward_statement() throws RecognitionException {
        DQLGrammarParser.forward_statement_return retval = new DQLGrammarParser.forward_statement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal76=null;
        Token string_literal77=null;
        Token string_literal78=null;
        Token INT79=null;
        Token string_literal80=null;
        DQLGrammarParser.expression_return expression81 = null;


        CommonTree string_literal76_tree=null;
        CommonTree string_literal77_tree=null;
        CommonTree string_literal78_tree=null;
        CommonTree INT79_tree=null;
        CommonTree string_literal80_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:165:3: ( 'FORWARD' ( 'MAX' 'DEPTH' INT )? 'TO' expression )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:165:5: 'FORWARD' ( 'MAX' 'DEPTH' INT )? 'TO' expression
            {
            root_0 = (CommonTree)adaptor.nil();

            string_literal76=(Token)match(input,51,FOLLOW_51_in_forward_statement1170); 
            string_literal76_tree = (CommonTree)adaptor.create(string_literal76);
            root_0 = (CommonTree)adaptor.becomeRoot(string_literal76_tree, root_0);

            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:165:16: ( 'MAX' 'DEPTH' INT )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==52) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:165:17: 'MAX' 'DEPTH' INT
                    {
                    string_literal77=(Token)match(input,52,FOLLOW_52_in_forward_statement1174); 
                    string_literal78=(Token)match(input,53,FOLLOW_53_in_forward_statement1177); 
                    INT79=(Token)match(input,INT,FOLLOW_INT_in_forward_statement1180); 
                    INT79_tree = (CommonTree)adaptor.create(INT79);
                    adaptor.addChild(root_0, INT79_tree);


                    }
                    break;

            }

            string_literal80=(Token)match(input,54,FOLLOW_54_in_forward_statement1184); 
            pushFollow(FOLLOW_expression_in_forward_statement1187);
            expression81=expression();

            state._fsp--;

            adaptor.addChild(root_0, expression81.getTree());

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "forward_statement"

    public static class reduce_statement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "reduce_statement"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:167:1: reduce_statement : 'REDUCE' expression ;
    public final DQLGrammarParser.reduce_statement_return reduce_statement() throws RecognitionException {
        DQLGrammarParser.reduce_statement_return retval = new DQLGrammarParser.reduce_statement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal82=null;
        DQLGrammarParser.expression_return expression83 = null;


        CommonTree string_literal82_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:168:3: ( 'REDUCE' expression )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:168:5: 'REDUCE' expression
            {
            root_0 = (CommonTree)adaptor.nil();

            string_literal82=(Token)match(input,55,FOLLOW_55_in_reduce_statement1197); 
            string_literal82_tree = (CommonTree)adaptor.create(string_literal82);
            root_0 = (CommonTree)adaptor.becomeRoot(string_literal82_tree, root_0);

            pushFollow(FOLLOW_expression_in_reduce_statement1200);
            expression83=expression();

            state._fsp--;

            adaptor.addChild(root_0, expression83.getTree());

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "reduce_statement"

    public static class query_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "query"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:170:1: query : gather_statement forward_statement reduce_statement EOF ;
    public final DQLGrammarParser.query_return query() throws RecognitionException {
        DQLGrammarParser.query_return retval = new DQLGrammarParser.query_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token EOF87=null;
        DQLGrammarParser.gather_statement_return gather_statement84 = null;

        DQLGrammarParser.forward_statement_return forward_statement85 = null;

        DQLGrammarParser.reduce_statement_return reduce_statement86 = null;


        CommonTree EOF87_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:170:7: ( gather_statement forward_statement reduce_statement EOF )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:171:4: gather_statement forward_statement reduce_statement EOF
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_gather_statement_in_query1211);
            gather_statement84=gather_statement();

            state._fsp--;

            adaptor.addChild(root_0, gather_statement84.getTree());
            pushFollow(FOLLOW_forward_statement_in_query1216);
            forward_statement85=forward_statement();

            state._fsp--;

            adaptor.addChild(root_0, forward_statement85.getTree());
            pushFollow(FOLLOW_reduce_statement_in_query1221);
            reduce_statement86=reduce_statement();

            state._fsp--;

            adaptor.addChild(root_0, reduce_statement86.getTree());
            EOF87=(Token)match(input,EOF,FOLLOW_EOF_in_query1225); 

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "query"

    // Delegated rules


 

    public static final BitSet FOLLOW_ID_in_function_decl_parameters620 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_18_in_function_decl_parameters624 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_function_decl_parameters628 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_19_in_lambda_function642 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_function_decl_parameters_in_lambda_function645 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_lambda_function647 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_lambda_function649 = new BitSet(new long[]{0x00000038002A0160L});
    public static final BitSet FOLLOW_expression_in_lambda_function652 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_lambda_function654 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_call_parameter664 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_parameter_in_function_call_parameters675 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_18_in_function_call_parameters679 = new BitSet(new long[]{0x00000038002A0160L});
    public static final BitSet FOLLOW_call_parameter_in_function_call_parameters682 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_ID_in_function_call697 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_function_call700 = new BitSet(new long[]{0x00000038006A0160L});
    public static final BitSet FOLLOW_function_call_parameters_in_function_call703 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_function_call706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_privacy_decl0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_type793 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_type799 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_type805 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_type811 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_32_in_type814 = new BitSet(new long[]{0x00000004F0000000L});
    public static final BitSet FOLLOW_type_in_type817 = new BitSet(new long[]{0x00000006F0000000L});
    public static final BitSet FOLLOW_33_in_type820 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_type827 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_32_in_type830 = new BitSet(new long[]{0x00000004F0000000L});
    public static final BitSet FOLLOW_type_in_type833 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_type835 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_term851 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_term857 = new BitSet(new long[]{0x00000038002A0160L});
    public static final BitSet FOLLOW_expression_in_term860 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_term862 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_term869 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_in_term875 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_term882 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_call_in_term888 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lambda_function_in_term894 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_negation911 = new BitSet(new long[]{0x00000038002A0160L});
    public static final BitSet FOLLOW_term_in_negation916 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_unary932 = new BitSet(new long[]{0x00000038002A0160L});
    public static final BitSet FOLLOW_unary_negation_rewrite_in_unary937 = new BitSet(new long[]{0x00000038002A0160L});
    public static final BitSet FOLLOW_negation_in_unary942 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_unary_negation_rewrite957 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unary_in_mult976 = new BitSet(new long[]{0x000001C000000002L});
    public static final BitSet FOLLOW_38_in_mult980 = new BitSet(new long[]{0x00000038002A0160L});
    public static final BitSet FOLLOW_39_in_mult985 = new BitSet(new long[]{0x00000038002A0160L});
    public static final BitSet FOLLOW_40_in_mult990 = new BitSet(new long[]{0x00000038002A0160L});
    public static final BitSet FOLLOW_unary_in_mult994 = new BitSet(new long[]{0x000001C000000002L});
    public static final BitSet FOLLOW_mult_in_add1009 = new BitSet(new long[]{0x0000003000000002L});
    public static final BitSet FOLLOW_36_in_add1013 = new BitSet(new long[]{0x00000038002A0160L});
    public static final BitSet FOLLOW_37_in_add1018 = new BitSet(new long[]{0x00000038002A0160L});
    public static final BitSet FOLLOW_mult_in_add1022 = new BitSet(new long[]{0x0000003000000002L});
    public static final BitSet FOLLOW_add_in_relation1037 = new BitSet(new long[]{0x00001E0300000002L});
    public static final BitSet FOLLOW_41_in_relation1041 = new BitSet(new long[]{0x00000038002A0160L});
    public static final BitSet FOLLOW_42_in_relation1046 = new BitSet(new long[]{0x00000038002A0160L});
    public static final BitSet FOLLOW_32_in_relation1051 = new BitSet(new long[]{0x00000038002A0160L});
    public static final BitSet FOLLOW_43_in_relation1056 = new BitSet(new long[]{0x00000038002A0160L});
    public static final BitSet FOLLOW_44_in_relation1061 = new BitSet(new long[]{0x00000038002A0160L});
    public static final BitSet FOLLOW_33_in_relation1066 = new BitSet(new long[]{0x00000038002A0160L});
    public static final BitSet FOLLOW_add_in_relation1070 = new BitSet(new long[]{0x00001E0300000002L});
    public static final BitSet FOLLOW_relation_in_expression1087 = new BitSet(new long[]{0x0000E00000000002L});
    public static final BitSet FOLLOW_45_in_expression1091 = new BitSet(new long[]{0x00000038002A0160L});
    public static final BitSet FOLLOW_46_in_expression1096 = new BitSet(new long[]{0x00000038002A0160L});
    public static final BitSet FOLLOW_relation_in_expression1100 = new BitSet(new long[]{0x0000E00000000002L});
    public static final BitSet FOLLOW_47_in_expression1105 = new BitSet(new long[]{0x000000000F800000L});
    public static final BitSet FOLLOW_privacy_decl_in_expression1108 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_expression1112 = new BitSet(new long[]{0x00000004F0000000L});
    public static final BitSet FOLLOW_type_in_expression1115 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_let1133 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_let1136 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_41_in_let1139 = new BitSet(new long[]{0x00000038002A0160L});
    public static final BitSet FOLLOW_expression_in_let1142 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_50_in_gather_statement1156 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_let_in_gather_statement1159 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_51_in_forward_statement1170 = new BitSet(new long[]{0x0050000000000000L});
    public static final BitSet FOLLOW_52_in_forward_statement1174 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_53_in_forward_statement1177 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_INT_in_forward_statement1180 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_54_in_forward_statement1184 = new BitSet(new long[]{0x00000038002A0160L});
    public static final BitSet FOLLOW_expression_in_forward_statement1187 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_55_in_reduce_statement1197 = new BitSet(new long[]{0x00000038002A0160L});
    public static final BitSet FOLLOW_expression_in_reduce_statement1200 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_gather_statement_in_query1211 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_forward_statement_in_query1216 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_reduce_statement_in_query1221 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_query1225 = new BitSet(new long[]{0x0000000000000002L});

}