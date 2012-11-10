// $ANTLR 3.3 Nov 30, 2010 12:50:56 /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g 2012-07-08 19:12:38

  package org.nebulostore.query.language.interpreter.antlr;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;

public class DQLGrammarParser extends Parser {
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
    public String getGrammarFileName() { return "/home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g"; }


    public static class function_decl_parameters_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "function_decl_parameters"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:80:1: function_decl_parameters : ID ( ',' ID )* ;
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
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:80:26: ( ID ( ',' ID )* )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:81:3: ID ( ',' ID )*
            {
            root_0 = (CommonTree)adaptor.nil();

            ID1=(Token)match(input,ID,FOLLOW_ID_in_function_decl_parameters620); 
            ID1_tree = (CommonTree)adaptor.create(ID1);
            adaptor.addChild(root_0, ID1_tree);

            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:81:6: ( ',' ID )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==18) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:81:8: ',' ID
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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:83:1: lambda_function : 'LAMBDA' function_decl_parameters ':' '(' expression ')' ;
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
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:83:17: ( 'LAMBDA' function_decl_parameters ':' '(' expression ')' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:84:3: 'LAMBDA' function_decl_parameters ':' '(' expression ')'
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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:86:1: call_parameter : expression ;
    public final DQLGrammarParser.call_parameter_return call_parameter() throws RecognitionException {
        DQLGrammarParser.call_parameter_return retval = new DQLGrammarParser.call_parameter_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        DQLGrammarParser.expression_return expression10 = null;



        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:86:15: ( expression )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:87:3: expression
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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:89:1: function_call_parameters : call_parameter ( ',' call_parameter )* ;
    public final DQLGrammarParser.function_call_parameters_return function_call_parameters() throws RecognitionException {
        DQLGrammarParser.function_call_parameters_return retval = new DQLGrammarParser.function_call_parameters_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal12=null;
        DQLGrammarParser.call_parameter_return call_parameter11 = null;

        DQLGrammarParser.call_parameter_return call_parameter13 = null;


        CommonTree char_literal12_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:89:26: ( call_parameter ( ',' call_parameter )* )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:90:3: call_parameter ( ',' call_parameter )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_call_parameter_in_function_call_parameters675);
            call_parameter11=call_parameter();

            state._fsp--;

            adaptor.addChild(root_0, call_parameter11.getTree());
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:90:19: ( ',' call_parameter )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==18) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:90:20: ',' call_parameter
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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:94:1: function_call : ID '(' ( function_call_parameters )? ')' ;
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
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:94:15: ( ID '(' ( function_call_parameters )? ')' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:95:3: ID '(' ( function_call_parameters )? ')'
            {
            root_0 = (CommonTree)adaptor.nil();

            ID14=(Token)match(input,ID,FOLLOW_ID_in_function_call697); 
            ID14_tree = (CommonTree)adaptor.create(ID14);
            root_0 = (CommonTree)adaptor.becomeRoot(ID14_tree, root_0);

            char_literal15=(Token)match(input,21,FOLLOW_21_in_function_call700); 
            char_literal15_tree = (CommonTree)adaptor.create(char_literal15);
            adaptor.addChild(root_0, char_literal15_tree);

            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:95:11: ( function_call_parameters )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( ((LA3_0>=ID && LA3_0<=INT)||LA3_0==DOUBLE||LA3_0==STRING_LITERAL||LA3_0==19||LA3_0==21||(LA3_0>=36 && LA3_0<=40)) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:95:11: function_call_parameters
                    {
                    pushFollow(FOLLOW_function_call_parameters_in_function_call702);
                    function_call_parameters16=function_call_parameters();

                    state._fsp--;

                    adaptor.addChild(root_0, function_call_parameters16.getTree());

                    }
                    break;

            }

            char_literal17=(Token)match(input,22,FOLLOW_22_in_function_call705); 
            char_literal17_tree = (CommonTree)adaptor.create(char_literal17);
            adaptor.addChild(root_0, char_literal17_tree);


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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:103:1: privacy_decl : ( 'PRIVATE_MY' | 'PUBLIC_MY' | 'PRIVATE_COND' | 'PUBLIC_OTHER' | 'PUBLIC_COND' );
    public final DQLGrammarParser.privacy_decl_return privacy_decl() throws RecognitionException {
        DQLGrammarParser.privacy_decl_return retval = new DQLGrammarParser.privacy_decl_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token set18=null;

        CommonTree set18_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:104:3: ( 'PRIVATE_MY' | 'PUBLIC_MY' | 'PRIVATE_COND' | 'PUBLIC_OTHER' | 'PUBLIC_COND' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:
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

    public static class type_list_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "type_list"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:112:1: type_list : type ( ',' type )* ;
    public final DQLGrammarParser.type_list_return type_list() throws RecognitionException {
        DQLGrammarParser.type_list_return retval = new DQLGrammarParser.type_list_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal20=null;
        DQLGrammarParser.type_return type19 = null;

        DQLGrammarParser.type_return type21 = null;


        CommonTree char_literal20_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:112:11: ( type ( ',' type )* )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:113:3: type ( ',' type )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_type_in_type_list790);
            type19=type();

            state._fsp--;

            adaptor.addChild(root_0, type19.getTree());
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:113:9: ( ',' type )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==18) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:113:10: ',' type
            	    {
            	    char_literal20=(Token)match(input,18,FOLLOW_18_in_type_list794); 
            	    pushFollow(FOLLOW_type_in_type_list797);
            	    type21=type();

            	    state._fsp--;

            	    adaptor.addChild(root_0, type21.getTree());

            	    }
            	    break;

            	default :
            	    break loop4;
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
    // $ANTLR end "type_list"

    public static class type_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "type"
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:115:1: type : ( 'INTEGER' | 'DOUBLE' | 'STRING' | 'TUPLE' '<' type_list '>' | 'LIST' '<' type '>' | 'FILE' );
    public final DQLGrammarParser.type_return type() throws RecognitionException {
        DQLGrammarParser.type_return retval = new DQLGrammarParser.type_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal22=null;
        Token string_literal23=null;
        Token string_literal24=null;
        Token string_literal25=null;
        Token char_literal26=null;
        Token char_literal28=null;
        Token string_literal29=null;
        Token char_literal30=null;
        Token char_literal32=null;
        Token string_literal33=null;
        DQLGrammarParser.type_list_return type_list27 = null;

        DQLGrammarParser.type_return type31 = null;


        CommonTree string_literal22_tree=null;
        CommonTree string_literal23_tree=null;
        CommonTree string_literal24_tree=null;
        CommonTree string_literal25_tree=null;
        CommonTree char_literal26_tree=null;
        CommonTree char_literal28_tree=null;
        CommonTree string_literal29_tree=null;
        CommonTree char_literal30_tree=null;
        CommonTree char_literal32_tree=null;
        CommonTree string_literal33_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:116:3: ( 'INTEGER' | 'DOUBLE' | 'STRING' | 'TUPLE' '<' type_list '>' | 'LIST' '<' type '>' | 'FILE' )
            int alt5=6;
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
            case 35:
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
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:116:5: 'INTEGER'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal22=(Token)match(input,28,FOLLOW_28_in_type811); 
                    string_literal22_tree = (CommonTree)adaptor.create(string_literal22);
                    adaptor.addChild(root_0, string_literal22_tree);


                    }
                    break;
                case 2 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:117:5: 'DOUBLE'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal23=(Token)match(input,29,FOLLOW_29_in_type817); 
                    string_literal23_tree = (CommonTree)adaptor.create(string_literal23);
                    adaptor.addChild(root_0, string_literal23_tree);


                    }
                    break;
                case 3 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:118:5: 'STRING'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal24=(Token)match(input,30,FOLLOW_30_in_type823); 
                    string_literal24_tree = (CommonTree)adaptor.create(string_literal24);
                    adaptor.addChild(root_0, string_literal24_tree);


                    }
                    break;
                case 4 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:119:5: 'TUPLE' '<' type_list '>'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal25=(Token)match(input,31,FOLLOW_31_in_type829); 
                    string_literal25_tree = (CommonTree)adaptor.create(string_literal25);
                    root_0 = (CommonTree)adaptor.becomeRoot(string_literal25_tree, root_0);

                    char_literal26=(Token)match(input,32,FOLLOW_32_in_type832); 
                    pushFollow(FOLLOW_type_list_in_type835);
                    type_list27=type_list();

                    state._fsp--;

                    adaptor.addChild(root_0, type_list27.getTree());
                    char_literal28=(Token)match(input,33,FOLLOW_33_in_type837); 

                    }
                    break;
                case 5 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:120:5: 'LIST' '<' type '>'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal29=(Token)match(input,34,FOLLOW_34_in_type844); 
                    string_literal29_tree = (CommonTree)adaptor.create(string_literal29);
                    root_0 = (CommonTree)adaptor.becomeRoot(string_literal29_tree, root_0);

                    char_literal30=(Token)match(input,32,FOLLOW_32_in_type847); 
                    pushFollow(FOLLOW_type_in_type850);
                    type31=type();

                    state._fsp--;

                    adaptor.addChild(root_0, type31.getTree());
                    char_literal32=(Token)match(input,33,FOLLOW_33_in_type852); 

                    }
                    break;
                case 6 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:121:5: 'FILE'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal33=(Token)match(input,35,FOLLOW_35_in_type860); 
                    string_literal33_tree = (CommonTree)adaptor.create(string_literal33);
                    adaptor.addChild(root_0, string_literal33_tree);


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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:125:1: term : ( ID | '(' expression ')' | INT | DOUBLE | STRING_LITERAL | function_call | lambda_function | 'TRUE' | 'FALSE' );
    public final DQLGrammarParser.term_return term() throws RecognitionException {
        DQLGrammarParser.term_return retval = new DQLGrammarParser.term_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token ID34=null;
        Token char_literal35=null;
        Token char_literal37=null;
        Token INT38=null;
        Token DOUBLE39=null;
        Token STRING_LITERAL40=null;
        Token string_literal43=null;
        Token string_literal44=null;
        DQLGrammarParser.expression_return expression36 = null;

        DQLGrammarParser.function_call_return function_call41 = null;

        DQLGrammarParser.lambda_function_return lambda_function42 = null;


        CommonTree ID34_tree=null;
        CommonTree char_literal35_tree=null;
        CommonTree char_literal37_tree=null;
        CommonTree INT38_tree=null;
        CommonTree DOUBLE39_tree=null;
        CommonTree STRING_LITERAL40_tree=null;
        CommonTree string_literal43_tree=null;
        CommonTree string_literal44_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:126:3: ( ID | '(' expression ')' | INT | DOUBLE | STRING_LITERAL | function_call | lambda_function | 'TRUE' | 'FALSE' )
            int alt6=9;
            alt6 = dfa6.predict(input);
            switch (alt6) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:126:5: ID
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    ID34=(Token)match(input,ID,FOLLOW_ID_in_term874); 
                    ID34_tree = (CommonTree)adaptor.create(ID34);
                    adaptor.addChild(root_0, ID34_tree);


                    }
                    break;
                case 2 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:127:5: '(' expression ')'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    char_literal35=(Token)match(input,21,FOLLOW_21_in_term880); 
                    pushFollow(FOLLOW_expression_in_term883);
                    expression36=expression();

                    state._fsp--;

                    adaptor.addChild(root_0, expression36.getTree());
                    char_literal37=(Token)match(input,22,FOLLOW_22_in_term885); 

                    }
                    break;
                case 3 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:128:5: INT
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    INT38=(Token)match(input,INT,FOLLOW_INT_in_term892); 
                    INT38_tree = (CommonTree)adaptor.create(INT38);
                    adaptor.addChild(root_0, INT38_tree);


                    }
                    break;
                case 4 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:129:5: DOUBLE
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    DOUBLE39=(Token)match(input,DOUBLE,FOLLOW_DOUBLE_in_term898); 
                    DOUBLE39_tree = (CommonTree)adaptor.create(DOUBLE39);
                    adaptor.addChild(root_0, DOUBLE39_tree);


                    }
                    break;
                case 5 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:130:5: STRING_LITERAL
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    STRING_LITERAL40=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_term905); 
                    STRING_LITERAL40_tree = (CommonTree)adaptor.create(STRING_LITERAL40);
                    adaptor.addChild(root_0, STRING_LITERAL40_tree);


                    }
                    break;
                case 6 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:131:5: function_call
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_function_call_in_term911);
                    function_call41=function_call();

                    state._fsp--;

                    adaptor.addChild(root_0, function_call41.getTree());

                    }
                    break;
                case 7 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:132:5: lambda_function
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_lambda_function_in_term917);
                    lambda_function42=lambda_function();

                    state._fsp--;

                    adaptor.addChild(root_0, lambda_function42.getTree());

                    }
                    break;
                case 8 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:133:5: 'TRUE'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal43=(Token)match(input,36,FOLLOW_36_in_term924); 
                    string_literal43_tree = (CommonTree)adaptor.create(string_literal43);
                    adaptor.addChild(root_0, string_literal43_tree);


                    }
                    break;
                case 9 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:134:5: 'FALSE'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal44=(Token)match(input,37,FOLLOW_37_in_term930); 
                    string_literal44_tree = (CommonTree)adaptor.create(string_literal44);
                    adaptor.addChild(root_0, string_literal44_tree);


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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:137:1: negation : ( 'not' )* term ;
    public final DQLGrammarParser.negation_return negation() throws RecognitionException {
        DQLGrammarParser.negation_return retval = new DQLGrammarParser.negation_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal45=null;
        DQLGrammarParser.term_return term46 = null;


        CommonTree string_literal45_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:138:3: ( ( 'not' )* term )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:138:5: ( 'not' )* term
            {
            root_0 = (CommonTree)adaptor.nil();

            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:138:5: ( 'not' )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==38) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:138:6: 'not'
            	    {
            	    string_literal45=(Token)match(input,38,FOLLOW_38_in_negation946); 
            	    string_literal45_tree = (CommonTree)adaptor.create(string_literal45);
            	    root_0 = (CommonTree)adaptor.becomeRoot(string_literal45_tree, root_0);


            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);

            pushFollow(FOLLOW_term_in_negation951);
            term46=term();

            state._fsp--;

            adaptor.addChild(root_0, term46.getTree());

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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:141:1: unary : ( '+' | unary_negation_rewrite )* negation ;
    public final DQLGrammarParser.unary_return unary() throws RecognitionException {
        DQLGrammarParser.unary_return retval = new DQLGrammarParser.unary_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal47=null;
        DQLGrammarParser.unary_negation_rewrite_return unary_negation_rewrite48 = null;

        DQLGrammarParser.negation_return negation49 = null;


        CommonTree char_literal47_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:142:3: ( ( '+' | unary_negation_rewrite )* negation )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:142:5: ( '+' | unary_negation_rewrite )* negation
            {
            root_0 = (CommonTree)adaptor.nil();

            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:142:5: ( '+' | unary_negation_rewrite )*
            loop8:
            do {
                int alt8=3;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==39) ) {
                    alt8=1;
                }
                else if ( (LA8_0==40) ) {
                    alt8=2;
                }


                switch (alt8) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:142:6: '+'
            	    {
            	    char_literal47=(Token)match(input,39,FOLLOW_39_in_unary967); 

            	    }
            	    break;
            	case 2 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:142:13: unary_negation_rewrite
            	    {
            	    pushFollow(FOLLOW_unary_negation_rewrite_in_unary972);
            	    unary_negation_rewrite48=unary_negation_rewrite();

            	    state._fsp--;

            	    root_0 = (CommonTree)adaptor.becomeRoot(unary_negation_rewrite48.getTree(), root_0);

            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            pushFollow(FOLLOW_negation_in_unary977);
            negation49=negation();

            state._fsp--;

            adaptor.addChild(root_0, negation49.getTree());

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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:145:1: unary_negation_rewrite : '-' -> NEGATION ;
    public final DQLGrammarParser.unary_negation_rewrite_return unary_negation_rewrite() throws RecognitionException {
        DQLGrammarParser.unary_negation_rewrite_return retval = new DQLGrammarParser.unary_negation_rewrite_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal50=null;

        CommonTree char_literal50_tree=null;
        RewriteRuleTokenStream stream_40=new RewriteRuleTokenStream(adaptor,"token 40");

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:146:3: ( '-' -> NEGATION )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:146:5: '-'
            {
            char_literal50=(Token)match(input,40,FOLLOW_40_in_unary_negation_rewrite992);  
            stream_40.add(char_literal50);



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
            // 146:9: -> NEGATION
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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:149:1: mult : unary ( ( '*' | '/' | '%' ) unary )* ;
    public final DQLGrammarParser.mult_return mult() throws RecognitionException {
        DQLGrammarParser.mult_return retval = new DQLGrammarParser.mult_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal52=null;
        Token char_literal53=null;
        Token char_literal54=null;
        DQLGrammarParser.unary_return unary51 = null;

        DQLGrammarParser.unary_return unary55 = null;


        CommonTree char_literal52_tree=null;
        CommonTree char_literal53_tree=null;
        CommonTree char_literal54_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:150:3: ( unary ( ( '*' | '/' | '%' ) unary )* )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:150:5: unary ( ( '*' | '/' | '%' ) unary )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_unary_in_mult1011);
            unary51=unary();

            state._fsp--;

            adaptor.addChild(root_0, unary51.getTree());
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:150:11: ( ( '*' | '/' | '%' ) unary )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( ((LA10_0>=41 && LA10_0<=43)) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:150:12: ( '*' | '/' | '%' ) unary
            	    {
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:150:12: ( '*' | '/' | '%' )
            	    int alt9=3;
            	    switch ( input.LA(1) ) {
            	    case 41:
            	        {
            	        alt9=1;
            	        }
            	        break;
            	    case 42:
            	        {
            	        alt9=2;
            	        }
            	        break;
            	    case 43:
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
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:150:13: '*'
            	            {
            	            char_literal52=(Token)match(input,41,FOLLOW_41_in_mult1015); 
            	            char_literal52_tree = (CommonTree)adaptor.create(char_literal52);
            	            root_0 = (CommonTree)adaptor.becomeRoot(char_literal52_tree, root_0);


            	            }
            	            break;
            	        case 2 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:150:20: '/'
            	            {
            	            char_literal53=(Token)match(input,42,FOLLOW_42_in_mult1020); 
            	            char_literal53_tree = (CommonTree)adaptor.create(char_literal53);
            	            root_0 = (CommonTree)adaptor.becomeRoot(char_literal53_tree, root_0);


            	            }
            	            break;
            	        case 3 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:150:27: '%'
            	            {
            	            char_literal54=(Token)match(input,43,FOLLOW_43_in_mult1025); 
            	            char_literal54_tree = (CommonTree)adaptor.create(char_literal54);
            	            root_0 = (CommonTree)adaptor.becomeRoot(char_literal54_tree, root_0);


            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_unary_in_mult1029);
            	    unary55=unary();

            	    state._fsp--;

            	    adaptor.addChild(root_0, unary55.getTree());

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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:154:1: add : mult ( ( '+' | '-' ) mult )* ;
    public final DQLGrammarParser.add_return add() throws RecognitionException {
        DQLGrammarParser.add_return retval = new DQLGrammarParser.add_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal57=null;
        Token char_literal58=null;
        DQLGrammarParser.mult_return mult56 = null;

        DQLGrammarParser.mult_return mult59 = null;


        CommonTree char_literal57_tree=null;
        CommonTree char_literal58_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:155:3: ( mult ( ( '+' | '-' ) mult )* )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:155:5: mult ( ( '+' | '-' ) mult )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_mult_in_add1045);
            mult56=mult();

            state._fsp--;

            adaptor.addChild(root_0, mult56.getTree());
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:155:10: ( ( '+' | '-' ) mult )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( ((LA12_0>=39 && LA12_0<=40)) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:155:11: ( '+' | '-' ) mult
            	    {
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:155:11: ( '+' | '-' )
            	    int alt11=2;
            	    int LA11_0 = input.LA(1);

            	    if ( (LA11_0==39) ) {
            	        alt11=1;
            	    }
            	    else if ( (LA11_0==40) ) {
            	        alt11=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 11, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt11) {
            	        case 1 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:155:12: '+'
            	            {
            	            char_literal57=(Token)match(input,39,FOLLOW_39_in_add1049); 
            	            char_literal57_tree = (CommonTree)adaptor.create(char_literal57);
            	            root_0 = (CommonTree)adaptor.becomeRoot(char_literal57_tree, root_0);


            	            }
            	            break;
            	        case 2 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:155:19: '-'
            	            {
            	            char_literal58=(Token)match(input,40,FOLLOW_40_in_add1054); 
            	            char_literal58_tree = (CommonTree)adaptor.create(char_literal58);
            	            root_0 = (CommonTree)adaptor.becomeRoot(char_literal58_tree, root_0);


            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_mult_in_add1058);
            	    mult59=mult();

            	    state._fsp--;

            	    adaptor.addChild(root_0, mult59.getTree());

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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:158:1: relation : add ( ( '=' | '!=' | '<' | '<=' | '>=' | '>' ) add )* ;
    public final DQLGrammarParser.relation_return relation() throws RecognitionException {
        DQLGrammarParser.relation_return retval = new DQLGrammarParser.relation_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal61=null;
        Token string_literal62=null;
        Token char_literal63=null;
        Token string_literal64=null;
        Token string_literal65=null;
        Token char_literal66=null;
        DQLGrammarParser.add_return add60 = null;

        DQLGrammarParser.add_return add67 = null;


        CommonTree char_literal61_tree=null;
        CommonTree string_literal62_tree=null;
        CommonTree char_literal63_tree=null;
        CommonTree string_literal64_tree=null;
        CommonTree string_literal65_tree=null;
        CommonTree char_literal66_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:159:3: ( add ( ( '=' | '!=' | '<' | '<=' | '>=' | '>' ) add )* )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:159:5: add ( ( '=' | '!=' | '<' | '<=' | '>=' | '>' ) add )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_add_in_relation1073);
            add60=add();

            state._fsp--;

            adaptor.addChild(root_0, add60.getTree());
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:159:9: ( ( '=' | '!=' | '<' | '<=' | '>=' | '>' ) add )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( ((LA14_0>=32 && LA14_0<=33)||(LA14_0>=44 && LA14_0<=47)) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:159:10: ( '=' | '!=' | '<' | '<=' | '>=' | '>' ) add
            	    {
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:159:10: ( '=' | '!=' | '<' | '<=' | '>=' | '>' )
            	    int alt13=6;
            	    switch ( input.LA(1) ) {
            	    case 44:
            	        {
            	        alt13=1;
            	        }
            	        break;
            	    case 45:
            	        {
            	        alt13=2;
            	        }
            	        break;
            	    case 32:
            	        {
            	        alt13=3;
            	        }
            	        break;
            	    case 46:
            	        {
            	        alt13=4;
            	        }
            	        break;
            	    case 47:
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
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:159:11: '='
            	            {
            	            char_literal61=(Token)match(input,44,FOLLOW_44_in_relation1077); 
            	            char_literal61_tree = (CommonTree)adaptor.create(char_literal61);
            	            root_0 = (CommonTree)adaptor.becomeRoot(char_literal61_tree, root_0);


            	            }
            	            break;
            	        case 2 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:159:18: '!='
            	            {
            	            string_literal62=(Token)match(input,45,FOLLOW_45_in_relation1082); 
            	            string_literal62_tree = (CommonTree)adaptor.create(string_literal62);
            	            root_0 = (CommonTree)adaptor.becomeRoot(string_literal62_tree, root_0);


            	            }
            	            break;
            	        case 3 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:159:26: '<'
            	            {
            	            char_literal63=(Token)match(input,32,FOLLOW_32_in_relation1087); 
            	            char_literal63_tree = (CommonTree)adaptor.create(char_literal63);
            	            root_0 = (CommonTree)adaptor.becomeRoot(char_literal63_tree, root_0);


            	            }
            	            break;
            	        case 4 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:159:33: '<='
            	            {
            	            string_literal64=(Token)match(input,46,FOLLOW_46_in_relation1092); 
            	            string_literal64_tree = (CommonTree)adaptor.create(string_literal64);
            	            root_0 = (CommonTree)adaptor.becomeRoot(string_literal64_tree, root_0);


            	            }
            	            break;
            	        case 5 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:159:41: '>='
            	            {
            	            string_literal65=(Token)match(input,47,FOLLOW_47_in_relation1097); 
            	            string_literal65_tree = (CommonTree)adaptor.create(string_literal65);
            	            root_0 = (CommonTree)adaptor.becomeRoot(string_literal65_tree, root_0);


            	            }
            	            break;
            	        case 6 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:159:49: '>'
            	            {
            	            char_literal66=(Token)match(input,33,FOLLOW_33_in_relation1102); 
            	            char_literal66_tree = (CommonTree)adaptor.create(char_literal66);
            	            root_0 = (CommonTree)adaptor.becomeRoot(char_literal66_tree, root_0);


            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_add_in_relation1106);
            	    add67=add();

            	    state._fsp--;

            	    adaptor.addChild(root_0, add67.getTree());

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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:162:1: expression : relation ( ( '&&' | '||' ) relation )* ( 'IS' privacy_decl ( 'AS' type )? )? ;
    public final DQLGrammarParser.expression_return expression() throws RecognitionException {
        DQLGrammarParser.expression_return retval = new DQLGrammarParser.expression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal69=null;
        Token string_literal70=null;
        Token string_literal72=null;
        Token string_literal74=null;
        DQLGrammarParser.relation_return relation68 = null;

        DQLGrammarParser.relation_return relation71 = null;

        DQLGrammarParser.privacy_decl_return privacy_decl73 = null;

        DQLGrammarParser.type_return type75 = null;


        CommonTree string_literal69_tree=null;
        CommonTree string_literal70_tree=null;
        CommonTree string_literal72_tree=null;
        CommonTree string_literal74_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:163:3: ( relation ( ( '&&' | '||' ) relation )* ( 'IS' privacy_decl ( 'AS' type )? )? )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:163:5: relation ( ( '&&' | '||' ) relation )* ( 'IS' privacy_decl ( 'AS' type )? )?
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_relation_in_expression1123);
            relation68=relation();

            state._fsp--;

            adaptor.addChild(root_0, relation68.getTree());
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:163:14: ( ( '&&' | '||' ) relation )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( ((LA16_0>=48 && LA16_0<=49)) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:163:15: ( '&&' | '||' ) relation
            	    {
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:163:15: ( '&&' | '||' )
            	    int alt15=2;
            	    int LA15_0 = input.LA(1);

            	    if ( (LA15_0==48) ) {
            	        alt15=1;
            	    }
            	    else if ( (LA15_0==49) ) {
            	        alt15=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 15, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt15) {
            	        case 1 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:163:16: '&&'
            	            {
            	            string_literal69=(Token)match(input,48,FOLLOW_48_in_expression1127); 
            	            string_literal69_tree = (CommonTree)adaptor.create(string_literal69);
            	            root_0 = (CommonTree)adaptor.becomeRoot(string_literal69_tree, root_0);


            	            }
            	            break;
            	        case 2 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:163:24: '||'
            	            {
            	            string_literal70=(Token)match(input,49,FOLLOW_49_in_expression1132); 
            	            string_literal70_tree = (CommonTree)adaptor.create(string_literal70);
            	            root_0 = (CommonTree)adaptor.becomeRoot(string_literal70_tree, root_0);


            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_relation_in_expression1136);
            	    relation71=relation();

            	    state._fsp--;

            	    adaptor.addChild(root_0, relation71.getTree());

            	    }
            	    break;

            	default :
            	    break loop16;
                }
            } while (true);

            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:163:42: ( 'IS' privacy_decl ( 'AS' type )? )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==50) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:163:44: 'IS' privacy_decl ( 'AS' type )?
                    {
                    string_literal72=(Token)match(input,50,FOLLOW_50_in_expression1142); 
                    string_literal72_tree = (CommonTree)adaptor.create(string_literal72);
                    root_0 = (CommonTree)adaptor.becomeRoot(string_literal72_tree, root_0);

                    pushFollow(FOLLOW_privacy_decl_in_expression1145);
                    privacy_decl73=privacy_decl();

                    state._fsp--;

                    adaptor.addChild(root_0, privacy_decl73.getTree());
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:163:63: ( 'AS' type )?
                    int alt17=2;
                    int LA17_0 = input.LA(1);

                    if ( (LA17_0==51) ) {
                        alt17=1;
                    }
                    switch (alt17) {
                        case 1 :
                            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:163:65: 'AS' type
                            {
                            string_literal74=(Token)match(input,51,FOLLOW_51_in_expression1149); 
                            string_literal74_tree = (CommonTree)adaptor.create(string_literal74);
                            root_0 = (CommonTree)adaptor.becomeRoot(string_literal74_tree, root_0);

                            pushFollow(FOLLOW_type_in_expression1152);
                            type75=type();

                            state._fsp--;

                            adaptor.addChild(root_0, type75.getTree());

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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:167:1: let : 'LET' ID '=' expression ;
    public final DQLGrammarParser.let_return let() throws RecognitionException {
        DQLGrammarParser.let_return retval = new DQLGrammarParser.let_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal76=null;
        Token ID77=null;
        Token char_literal78=null;
        DQLGrammarParser.expression_return expression79 = null;


        CommonTree string_literal76_tree=null;
        CommonTree ID77_tree=null;
        CommonTree char_literal78_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:167:5: ( 'LET' ID '=' expression )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:167:7: 'LET' ID '=' expression
            {
            root_0 = (CommonTree)adaptor.nil();

            string_literal76=(Token)match(input,52,FOLLOW_52_in_let1170); 
            ID77=(Token)match(input,ID,FOLLOW_ID_in_let1173); 
            ID77_tree = (CommonTree)adaptor.create(ID77);
            root_0 = (CommonTree)adaptor.becomeRoot(ID77_tree, root_0);

            char_literal78=(Token)match(input,44,FOLLOW_44_in_let1176); 
            pushFollow(FOLLOW_expression_in_let1179);
            expression79=expression();

            state._fsp--;

            adaptor.addChild(root_0, expression79.getTree());

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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:169:1: gather_statement : 'GATHER' ( let )+ ;
    public final DQLGrammarParser.gather_statement_return gather_statement() throws RecognitionException {
        DQLGrammarParser.gather_statement_return retval = new DQLGrammarParser.gather_statement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal80=null;
        DQLGrammarParser.let_return let81 = null;


        CommonTree string_literal80_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:170:3: ( 'GATHER' ( let )+ )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:170:7: 'GATHER' ( let )+
            {
            root_0 = (CommonTree)adaptor.nil();

            string_literal80=(Token)match(input,53,FOLLOW_53_in_gather_statement1193); 
            string_literal80_tree = (CommonTree)adaptor.create(string_literal80);
            root_0 = (CommonTree)adaptor.becomeRoot(string_literal80_tree, root_0);

            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:170:17: ( let )+
            int cnt19=0;
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==52) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:170:17: let
            	    {
            	    pushFollow(FOLLOW_let_in_gather_statement1196);
            	    let81=let();

            	    state._fsp--;

            	    adaptor.addChild(root_0, let81.getTree());

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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:172:1: forward_statement : 'FORWARD' 'TO' expression ;
    public final DQLGrammarParser.forward_statement_return forward_statement() throws RecognitionException {
        DQLGrammarParser.forward_statement_return retval = new DQLGrammarParser.forward_statement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal82=null;
        Token string_literal83=null;
        DQLGrammarParser.expression_return expression84 = null;


        CommonTree string_literal82_tree=null;
        CommonTree string_literal83_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:173:3: ( 'FORWARD' 'TO' expression )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:173:5: 'FORWARD' 'TO' expression
            {
            root_0 = (CommonTree)adaptor.nil();

            string_literal82=(Token)match(input,54,FOLLOW_54_in_forward_statement1207); 
            string_literal82_tree = (CommonTree)adaptor.create(string_literal82);
            root_0 = (CommonTree)adaptor.becomeRoot(string_literal82_tree, root_0);

            string_literal83=(Token)match(input,55,FOLLOW_55_in_forward_statement1210); 
            pushFollow(FOLLOW_expression_in_forward_statement1213);
            expression84=expression();

            state._fsp--;

            adaptor.addChild(root_0, expression84.getTree());

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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:175:1: reduce_statement : 'REDUCE' expression EOF ;
    public final DQLGrammarParser.reduce_statement_return reduce_statement() throws RecognitionException {
        DQLGrammarParser.reduce_statement_return retval = new DQLGrammarParser.reduce_statement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal85=null;
        Token EOF87=null;
        DQLGrammarParser.expression_return expression86 = null;


        CommonTree string_literal85_tree=null;
        CommonTree EOF87_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:176:3: ( 'REDUCE' expression EOF )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:176:5: 'REDUCE' expression EOF
            {
            root_0 = (CommonTree)adaptor.nil();

            string_literal85=(Token)match(input,56,FOLLOW_56_in_reduce_statement1223); 
            string_literal85_tree = (CommonTree)adaptor.create(string_literal85);
            root_0 = (CommonTree)adaptor.becomeRoot(string_literal85_tree, root_0);

            pushFollow(FOLLOW_expression_in_reduce_statement1226);
            expression86=expression();

            state._fsp--;

            adaptor.addChild(root_0, expression86.getTree());
            EOF87=(Token)match(input,EOF,FOLLOW_EOF_in_reduce_statement1228); 
            EOF87_tree = (CommonTree)adaptor.create(EOF87);
            adaptor.addChild(root_0, EOF87_tree);


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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:178:1: query : forward_statement gather_statement reduce_statement ;
    public final DQLGrammarParser.query_return query() throws RecognitionException {
        DQLGrammarParser.query_return retval = new DQLGrammarParser.query_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        DQLGrammarParser.forward_statement_return forward_statement88 = null;

        DQLGrammarParser.gather_statement_return gather_statement89 = null;

        DQLGrammarParser.reduce_statement_return reduce_statement90 = null;



        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:178:7: ( forward_statement gather_statement reduce_statement )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:179:4: forward_statement gather_statement reduce_statement
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_forward_statement_in_query1239);
            forward_statement88=forward_statement();

            state._fsp--;

            adaptor.addChild(root_0, forward_statement88.getTree());
            pushFollow(FOLLOW_gather_statement_in_query1244);
            gather_statement89=gather_statement();

            state._fsp--;

            adaptor.addChild(root_0, gather_statement89.getTree());
            pushFollow(FOLLOW_reduce_statement_in_query1252);
            reduce_statement90=reduce_statement();

            state._fsp--;

            adaptor.addChild(root_0, reduce_statement90.getTree());

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


    protected DFA6 dfa6 = new DFA6(this);
    static final String DFA6_eotS =
        "\13\uffff";
    static final String DFA6_eofS =
        "\1\uffff\1\12\11\uffff";
    static final String DFA6_minS =
        "\1\5\1\22\11\uffff";
    static final String DFA6_maxS =
        "\1\45\1\70\11\uffff";
    static final String DFA6_acceptS =
        "\2\uffff\1\2\1\3\1\4\1\5\1\7\1\10\1\11\1\6\1\1";
    static final String DFA6_specialS =
        "\13\uffff}>";
    static final String[] DFA6_transitionS = {
            "\1\1\1\3\1\uffff\1\4\10\uffff\1\5\1\uffff\1\6\1\uffff\1\2\16"+
            "\uffff\1\7\1\10",
            "\1\12\2\uffff\1\11\1\12\11\uffff\2\12\5\uffff\14\12\1\uffff"+
            "\2\12\2\uffff\1\12",
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

    static final short[] DFA6_eot = DFA.unpackEncodedString(DFA6_eotS);
    static final short[] DFA6_eof = DFA.unpackEncodedString(DFA6_eofS);
    static final char[] DFA6_min = DFA.unpackEncodedStringToUnsignedChars(DFA6_minS);
    static final char[] DFA6_max = DFA.unpackEncodedStringToUnsignedChars(DFA6_maxS);
    static final short[] DFA6_accept = DFA.unpackEncodedString(DFA6_acceptS);
    static final short[] DFA6_special = DFA.unpackEncodedString(DFA6_specialS);
    static final short[][] DFA6_transition;

    static {
        int numStates = DFA6_transitionS.length;
        DFA6_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA6_transition[i] = DFA.unpackEncodedString(DFA6_transitionS[i]);
        }
    }

    class DFA6 extends DFA {

        public DFA6(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 6;
            this.eot = DFA6_eot;
            this.eof = DFA6_eof;
            this.min = DFA6_min;
            this.max = DFA6_max;
            this.accept = DFA6_accept;
            this.special = DFA6_special;
            this.transition = DFA6_transition;
        }
        public String getDescription() {
            return "125:1: term : ( ID | '(' expression ')' | INT | DOUBLE | STRING_LITERAL | function_call | lambda_function | 'TRUE' | 'FALSE' );";
        }
    }
 

    public static final BitSet FOLLOW_ID_in_function_decl_parameters620 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_18_in_function_decl_parameters624 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_function_decl_parameters628 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_19_in_lambda_function642 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_function_decl_parameters_in_lambda_function645 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_lambda_function647 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_lambda_function649 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_expression_in_lambda_function652 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_lambda_function654 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_call_parameter664 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_parameter_in_function_call_parameters675 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_18_in_function_call_parameters679 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_call_parameter_in_function_call_parameters682 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_ID_in_function_call697 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_function_call700 = new BitSet(new long[]{0x000001F0006A0160L});
    public static final BitSet FOLLOW_function_call_parameters_in_function_call702 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_function_call705 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_privacy_decl0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_type_list790 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_18_in_type_list794 = new BitSet(new long[]{0x0000000CF0000000L});
    public static final BitSet FOLLOW_type_in_type_list797 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_28_in_type811 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_type817 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_type823 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_type829 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_32_in_type832 = new BitSet(new long[]{0x0000000CF0000000L});
    public static final BitSet FOLLOW_type_list_in_type835 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_type837 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_type844 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_32_in_type847 = new BitSet(new long[]{0x0000000CF0000000L});
    public static final BitSet FOLLOW_type_in_type850 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_type852 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_type860 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_term874 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_term880 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_expression_in_term883 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_term885 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_term892 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_in_term898 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_term905 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_call_in_term911 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lambda_function_in_term917 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_term924 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_term930 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_negation946 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_term_in_negation951 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_unary967 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_unary_negation_rewrite_in_unary972 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_negation_in_unary977 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_unary_negation_rewrite992 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unary_in_mult1011 = new BitSet(new long[]{0x00000E0000000002L});
    public static final BitSet FOLLOW_41_in_mult1015 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_42_in_mult1020 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_43_in_mult1025 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_unary_in_mult1029 = new BitSet(new long[]{0x00000E0000000002L});
    public static final BitSet FOLLOW_mult_in_add1045 = new BitSet(new long[]{0x0000018000000002L});
    public static final BitSet FOLLOW_39_in_add1049 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_40_in_add1054 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_mult_in_add1058 = new BitSet(new long[]{0x0000018000000002L});
    public static final BitSet FOLLOW_add_in_relation1073 = new BitSet(new long[]{0x0000F00300000002L});
    public static final BitSet FOLLOW_44_in_relation1077 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_45_in_relation1082 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_32_in_relation1087 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_46_in_relation1092 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_47_in_relation1097 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_33_in_relation1102 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_add_in_relation1106 = new BitSet(new long[]{0x0000F00300000002L});
    public static final BitSet FOLLOW_relation_in_expression1123 = new BitSet(new long[]{0x0007000000000002L});
    public static final BitSet FOLLOW_48_in_expression1127 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_49_in_expression1132 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_relation_in_expression1136 = new BitSet(new long[]{0x0007000000000002L});
    public static final BitSet FOLLOW_50_in_expression1142 = new BitSet(new long[]{0x000000000F800000L});
    public static final BitSet FOLLOW_privacy_decl_in_expression1145 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_51_in_expression1149 = new BitSet(new long[]{0x0000000CF0000000L});
    public static final BitSet FOLLOW_type_in_expression1152 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_52_in_let1170 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_let1173 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_let1176 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_expression_in_let1179 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_gather_statement1193 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_let_in_gather_statement1196 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_54_in_forward_statement1207 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_55_in_forward_statement1210 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_expression_in_forward_statement1213 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_56_in_reduce_statement1223 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_expression_in_reduce_statement1226 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_reduce_statement1228 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_forward_statement_in_query1239 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_gather_statement_in_query1244 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_reduce_statement_in_query1252 = new BitSet(new long[]{0x0000000000000002L});

}