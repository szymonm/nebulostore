// $ANTLR 3.3 Nov 30, 2010 12:50:56 /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g 2012-02-09 23:15:33

  package org.nebulostore.query.language.interpreter.antlr;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;

public class DQLGrammarParser extends Parser {
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
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:95:12: ( function_call_parameters )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( ((LA3_0>=ID && LA3_0<=INT)||LA3_0==DOUBLE||LA3_0==STRING_LITERAL||LA3_0==19||LA3_0==21||(LA3_0>=36 && LA3_0<=40)) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:95:12: function_call_parameters
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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:103:1: privacy_decl : ( 'PRIVATE_MY' | 'PUBLIC_MY' | 'PRIVATE_COND_MY' '<' expression '>' | 'PUBLIC_OTHER' | 'PRIVATE_COND_OTHER' '<' expression '>' );
    public final DQLGrammarParser.privacy_decl_return privacy_decl() throws RecognitionException {
        DQLGrammarParser.privacy_decl_return retval = new DQLGrammarParser.privacy_decl_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal18=null;
        Token string_literal19=null;
        Token string_literal20=null;
        Token char_literal21=null;
        Token char_literal23=null;
        Token string_literal24=null;
        Token string_literal25=null;
        Token char_literal26=null;
        Token char_literal28=null;
        DQLGrammarParser.expression_return expression22 = null;

        DQLGrammarParser.expression_return expression27 = null;


        CommonTree string_literal18_tree=null;
        CommonTree string_literal19_tree=null;
        CommonTree string_literal20_tree=null;
        CommonTree char_literal21_tree=null;
        CommonTree char_literal23_tree=null;
        CommonTree string_literal24_tree=null;
        CommonTree string_literal25_tree=null;
        CommonTree char_literal26_tree=null;
        CommonTree char_literal28_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:104:3: ( 'PRIVATE_MY' | 'PUBLIC_MY' | 'PRIVATE_COND_MY' '<' expression '>' | 'PUBLIC_OTHER' | 'PRIVATE_COND_OTHER' '<' expression '>' )
            int alt4=5;
            switch ( input.LA(1) ) {
            case 23:
                {
                alt4=1;
                }
                break;
            case 24:
                {
                alt4=2;
                }
                break;
            case 25:
                {
                alt4=3;
                }
                break;
            case 28:
                {
                alt4=4;
                }
                break;
            case 29:
                {
                alt4=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:104:5: 'PRIVATE_MY'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal18=(Token)match(input,23,FOLLOW_23_in_privacy_decl753); 
                    string_literal18_tree = (CommonTree)adaptor.create(string_literal18);
                    adaptor.addChild(root_0, string_literal18_tree);


                    }
                    break;
                case 2 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:105:5: 'PUBLIC_MY'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal19=(Token)match(input,24,FOLLOW_24_in_privacy_decl759); 
                    string_literal19_tree = (CommonTree)adaptor.create(string_literal19);
                    adaptor.addChild(root_0, string_literal19_tree);


                    }
                    break;
                case 3 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:106:5: 'PRIVATE_COND_MY' '<' expression '>'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal20=(Token)match(input,25,FOLLOW_25_in_privacy_decl765); 
                    string_literal20_tree = (CommonTree)adaptor.create(string_literal20);
                    root_0 = (CommonTree)adaptor.becomeRoot(string_literal20_tree, root_0);

                    char_literal21=(Token)match(input,26,FOLLOW_26_in_privacy_decl768); 
                    pushFollow(FOLLOW_expression_in_privacy_decl771);
                    expression22=expression();

                    state._fsp--;

                    adaptor.addChild(root_0, expression22.getTree());
                    char_literal23=(Token)match(input,27,FOLLOW_27_in_privacy_decl773); 

                    }
                    break;
                case 4 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:107:5: 'PUBLIC_OTHER'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal24=(Token)match(input,28,FOLLOW_28_in_privacy_decl781); 
                    string_literal24_tree = (CommonTree)adaptor.create(string_literal24);
                    adaptor.addChild(root_0, string_literal24_tree);


                    }
                    break;
                case 5 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:108:5: 'PRIVATE_COND_OTHER' '<' expression '>'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal25=(Token)match(input,29,FOLLOW_29_in_privacy_decl787); 
                    string_literal25_tree = (CommonTree)adaptor.create(string_literal25);
                    adaptor.addChild(root_0, string_literal25_tree);

                    char_literal26=(Token)match(input,26,FOLLOW_26_in_privacy_decl789); 
                    pushFollow(FOLLOW_expression_in_privacy_decl792);
                    expression27=expression();

                    state._fsp--;

                    adaptor.addChild(root_0, expression27.getTree());
                    char_literal28=(Token)match(input,27,FOLLOW_27_in_privacy_decl794); 

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

        Token char_literal30=null;
        DQLGrammarParser.type_return type29 = null;

        DQLGrammarParser.type_return type31 = null;


        CommonTree char_literal30_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:112:11: ( type ( ',' type )* )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:113:3: type ( ',' type )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_type_in_type_list809);
            type29=type();

            state._fsp--;

            adaptor.addChild(root_0, type29.getTree());
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:113:9: ( ',' type )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==18) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:113:10: ',' type
            	    {
            	    char_literal30=(Token)match(input,18,FOLLOW_18_in_type_list813); 
            	    pushFollow(FOLLOW_type_in_type_list816);
            	    type31=type();

            	    state._fsp--;

            	    adaptor.addChild(root_0, type31.getTree());

            	    }
            	    break;

            	default :
            	    break loop5;
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

        Token string_literal32=null;
        Token string_literal33=null;
        Token string_literal34=null;
        Token string_literal35=null;
        Token char_literal36=null;
        Token char_literal38=null;
        Token string_literal39=null;
        Token char_literal40=null;
        Token char_literal42=null;
        Token string_literal43=null;
        DQLGrammarParser.type_list_return type_list37 = null;

        DQLGrammarParser.type_return type41 = null;


        CommonTree string_literal32_tree=null;
        CommonTree string_literal33_tree=null;
        CommonTree string_literal34_tree=null;
        CommonTree string_literal35_tree=null;
        CommonTree char_literal36_tree=null;
        CommonTree char_literal38_tree=null;
        CommonTree string_literal39_tree=null;
        CommonTree char_literal40_tree=null;
        CommonTree char_literal42_tree=null;
        CommonTree string_literal43_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:116:3: ( 'INTEGER' | 'DOUBLE' | 'STRING' | 'TUPLE' '<' type_list '>' | 'LIST' '<' type '>' | 'FILE' )
            int alt6=6;
            switch ( input.LA(1) ) {
            case 30:
                {
                alt6=1;
                }
                break;
            case 31:
                {
                alt6=2;
                }
                break;
            case 32:
                {
                alt6=3;
                }
                break;
            case 33:
                {
                alt6=4;
                }
                break;
            case 34:
                {
                alt6=5;
                }
                break;
            case 35:
                {
                alt6=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:116:5: 'INTEGER'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal32=(Token)match(input,30,FOLLOW_30_in_type830); 
                    string_literal32_tree = (CommonTree)adaptor.create(string_literal32);
                    adaptor.addChild(root_0, string_literal32_tree);


                    }
                    break;
                case 2 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:117:5: 'DOUBLE'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal33=(Token)match(input,31,FOLLOW_31_in_type836); 
                    string_literal33_tree = (CommonTree)adaptor.create(string_literal33);
                    adaptor.addChild(root_0, string_literal33_tree);


                    }
                    break;
                case 3 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:118:5: 'STRING'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal34=(Token)match(input,32,FOLLOW_32_in_type842); 
                    string_literal34_tree = (CommonTree)adaptor.create(string_literal34);
                    adaptor.addChild(root_0, string_literal34_tree);


                    }
                    break;
                case 4 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:119:5: 'TUPLE' '<' type_list '>'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal35=(Token)match(input,33,FOLLOW_33_in_type848); 
                    string_literal35_tree = (CommonTree)adaptor.create(string_literal35);
                    root_0 = (CommonTree)adaptor.becomeRoot(string_literal35_tree, root_0);

                    char_literal36=(Token)match(input,26,FOLLOW_26_in_type851); 
                    pushFollow(FOLLOW_type_list_in_type854);
                    type_list37=type_list();

                    state._fsp--;

                    adaptor.addChild(root_0, type_list37.getTree());
                    char_literal38=(Token)match(input,27,FOLLOW_27_in_type856); 

                    }
                    break;
                case 5 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:120:5: 'LIST' '<' type '>'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal39=(Token)match(input,34,FOLLOW_34_in_type863); 
                    string_literal39_tree = (CommonTree)adaptor.create(string_literal39);
                    root_0 = (CommonTree)adaptor.becomeRoot(string_literal39_tree, root_0);

                    char_literal40=(Token)match(input,26,FOLLOW_26_in_type866); 
                    pushFollow(FOLLOW_type_in_type869);
                    type41=type();

                    state._fsp--;

                    adaptor.addChild(root_0, type41.getTree());
                    char_literal42=(Token)match(input,27,FOLLOW_27_in_type871); 

                    }
                    break;
                case 6 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:121:5: 'FILE'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal43=(Token)match(input,35,FOLLOW_35_in_type879); 
                    string_literal43_tree = (CommonTree)adaptor.create(string_literal43);
                    adaptor.addChild(root_0, string_literal43_tree);


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

        Token ID44=null;
        Token char_literal45=null;
        Token char_literal47=null;
        Token INT48=null;
        Token DOUBLE49=null;
        Token STRING_LITERAL50=null;
        Token string_literal53=null;
        Token string_literal54=null;
        DQLGrammarParser.expression_return expression46 = null;

        DQLGrammarParser.function_call_return function_call51 = null;

        DQLGrammarParser.lambda_function_return lambda_function52 = null;


        CommonTree ID44_tree=null;
        CommonTree char_literal45_tree=null;
        CommonTree char_literal47_tree=null;
        CommonTree INT48_tree=null;
        CommonTree DOUBLE49_tree=null;
        CommonTree STRING_LITERAL50_tree=null;
        CommonTree string_literal53_tree=null;
        CommonTree string_literal54_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:126:3: ( ID | '(' expression ')' | INT | DOUBLE | STRING_LITERAL | function_call | lambda_function | 'TRUE' | 'FALSE' )
            int alt7=9;
            alt7 = dfa7.predict(input);
            switch (alt7) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:126:5: ID
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    ID44=(Token)match(input,ID,FOLLOW_ID_in_term893); 
                    ID44_tree = (CommonTree)adaptor.create(ID44);
                    adaptor.addChild(root_0, ID44_tree);


                    }
                    break;
                case 2 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:127:5: '(' expression ')'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    char_literal45=(Token)match(input,21,FOLLOW_21_in_term899); 
                    pushFollow(FOLLOW_expression_in_term902);
                    expression46=expression();

                    state._fsp--;

                    adaptor.addChild(root_0, expression46.getTree());
                    char_literal47=(Token)match(input,22,FOLLOW_22_in_term904); 

                    }
                    break;
                case 3 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:128:5: INT
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    INT48=(Token)match(input,INT,FOLLOW_INT_in_term911); 
                    INT48_tree = (CommonTree)adaptor.create(INT48);
                    adaptor.addChild(root_0, INT48_tree);


                    }
                    break;
                case 4 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:129:5: DOUBLE
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    DOUBLE49=(Token)match(input,DOUBLE,FOLLOW_DOUBLE_in_term917); 
                    DOUBLE49_tree = (CommonTree)adaptor.create(DOUBLE49);
                    adaptor.addChild(root_0, DOUBLE49_tree);


                    }
                    break;
                case 5 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:130:5: STRING_LITERAL
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    STRING_LITERAL50=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_term924); 
                    STRING_LITERAL50_tree = (CommonTree)adaptor.create(STRING_LITERAL50);
                    adaptor.addChild(root_0, STRING_LITERAL50_tree);


                    }
                    break;
                case 6 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:131:5: function_call
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_function_call_in_term930);
                    function_call51=function_call();

                    state._fsp--;

                    adaptor.addChild(root_0, function_call51.getTree());

                    }
                    break;
                case 7 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:132:5: lambda_function
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_lambda_function_in_term936);
                    lambda_function52=lambda_function();

                    state._fsp--;

                    adaptor.addChild(root_0, lambda_function52.getTree());

                    }
                    break;
                case 8 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:133:5: 'TRUE'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal53=(Token)match(input,36,FOLLOW_36_in_term943); 
                    string_literal53_tree = (CommonTree)adaptor.create(string_literal53);
                    adaptor.addChild(root_0, string_literal53_tree);


                    }
                    break;
                case 9 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:134:5: 'FALSE'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal54=(Token)match(input,37,FOLLOW_37_in_term949); 
                    string_literal54_tree = (CommonTree)adaptor.create(string_literal54);
                    adaptor.addChild(root_0, string_literal54_tree);


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

        Token string_literal55=null;
        DQLGrammarParser.term_return term56 = null;


        CommonTree string_literal55_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:138:3: ( ( 'not' )* term )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:138:5: ( 'not' )* term
            {
            root_0 = (CommonTree)adaptor.nil();

            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:138:5: ( 'not' )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==38) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:138:6: 'not'
            	    {
            	    string_literal55=(Token)match(input,38,FOLLOW_38_in_negation965); 
            	    string_literal55_tree = (CommonTree)adaptor.create(string_literal55);
            	    root_0 = (CommonTree)adaptor.becomeRoot(string_literal55_tree, root_0);


            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            pushFollow(FOLLOW_term_in_negation970);
            term56=term();

            state._fsp--;

            adaptor.addChild(root_0, term56.getTree());

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

        Token char_literal57=null;
        DQLGrammarParser.unary_negation_rewrite_return unary_negation_rewrite58 = null;

        DQLGrammarParser.negation_return negation59 = null;


        CommonTree char_literal57_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:142:3: ( ( '+' | unary_negation_rewrite )* negation )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:142:5: ( '+' | unary_negation_rewrite )* negation
            {
            root_0 = (CommonTree)adaptor.nil();

            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:142:5: ( '+' | unary_negation_rewrite )*
            loop9:
            do {
                int alt9=3;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==39) ) {
                    alt9=1;
                }
                else if ( (LA9_0==40) ) {
                    alt9=2;
                }


                switch (alt9) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:142:6: '+'
            	    {
            	    char_literal57=(Token)match(input,39,FOLLOW_39_in_unary986); 

            	    }
            	    break;
            	case 2 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:142:13: unary_negation_rewrite
            	    {
            	    pushFollow(FOLLOW_unary_negation_rewrite_in_unary991);
            	    unary_negation_rewrite58=unary_negation_rewrite();

            	    state._fsp--;

            	    root_0 = (CommonTree)adaptor.becomeRoot(unary_negation_rewrite58.getTree(), root_0);

            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);

            pushFollow(FOLLOW_negation_in_unary996);
            negation59=negation();

            state._fsp--;

            adaptor.addChild(root_0, negation59.getTree());

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

        Token char_literal60=null;

        CommonTree char_literal60_tree=null;
        RewriteRuleTokenStream stream_40=new RewriteRuleTokenStream(adaptor,"token 40");

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:146:3: ( '-' -> NEGATION )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:146:5: '-'
            {
            char_literal60=(Token)match(input,40,FOLLOW_40_in_unary_negation_rewrite1011);  
            stream_40.add(char_literal60);



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

        Token char_literal62=null;
        Token char_literal63=null;
        Token char_literal64=null;
        DQLGrammarParser.unary_return unary61 = null;

        DQLGrammarParser.unary_return unary65 = null;


        CommonTree char_literal62_tree=null;
        CommonTree char_literal63_tree=null;
        CommonTree char_literal64_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:150:3: ( unary ( ( '*' | '/' | '%' ) unary )* )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:150:5: unary ( ( '*' | '/' | '%' ) unary )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_unary_in_mult1030);
            unary61=unary();

            state._fsp--;

            adaptor.addChild(root_0, unary61.getTree());
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:150:11: ( ( '*' | '/' | '%' ) unary )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( ((LA11_0>=41 && LA11_0<=43)) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:150:12: ( '*' | '/' | '%' ) unary
            	    {
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:150:12: ( '*' | '/' | '%' )
            	    int alt10=3;
            	    switch ( input.LA(1) ) {
            	    case 41:
            	        {
            	        alt10=1;
            	        }
            	        break;
            	    case 42:
            	        {
            	        alt10=2;
            	        }
            	        break;
            	    case 43:
            	        {
            	        alt10=3;
            	        }
            	        break;
            	    default:
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 10, 0, input);

            	        throw nvae;
            	    }

            	    switch (alt10) {
            	        case 1 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:150:13: '*'
            	            {
            	            char_literal62=(Token)match(input,41,FOLLOW_41_in_mult1034); 
            	            char_literal62_tree = (CommonTree)adaptor.create(char_literal62);
            	            root_0 = (CommonTree)adaptor.becomeRoot(char_literal62_tree, root_0);


            	            }
            	            break;
            	        case 2 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:150:20: '/'
            	            {
            	            char_literal63=(Token)match(input,42,FOLLOW_42_in_mult1039); 
            	            char_literal63_tree = (CommonTree)adaptor.create(char_literal63);
            	            root_0 = (CommonTree)adaptor.becomeRoot(char_literal63_tree, root_0);


            	            }
            	            break;
            	        case 3 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:150:27: '%'
            	            {
            	            char_literal64=(Token)match(input,43,FOLLOW_43_in_mult1044); 
            	            char_literal64_tree = (CommonTree)adaptor.create(char_literal64);
            	            root_0 = (CommonTree)adaptor.becomeRoot(char_literal64_tree, root_0);


            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_unary_in_mult1048);
            	    unary65=unary();

            	    state._fsp--;

            	    adaptor.addChild(root_0, unary65.getTree());

            	    }
            	    break;

            	default :
            	    break loop11;
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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:153:1: add : mult ( ( '+' | '-' ) mult )* ;
    public final DQLGrammarParser.add_return add() throws RecognitionException {
        DQLGrammarParser.add_return retval = new DQLGrammarParser.add_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal67=null;
        Token char_literal68=null;
        DQLGrammarParser.mult_return mult66 = null;

        DQLGrammarParser.mult_return mult69 = null;


        CommonTree char_literal67_tree=null;
        CommonTree char_literal68_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:154:3: ( mult ( ( '+' | '-' ) mult )* )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:154:5: mult ( ( '+' | '-' ) mult )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_mult_in_add1063);
            mult66=mult();

            state._fsp--;

            adaptor.addChild(root_0, mult66.getTree());
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:154:10: ( ( '+' | '-' ) mult )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( ((LA13_0>=39 && LA13_0<=40)) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:154:11: ( '+' | '-' ) mult
            	    {
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:154:11: ( '+' | '-' )
            	    int alt12=2;
            	    int LA12_0 = input.LA(1);

            	    if ( (LA12_0==39) ) {
            	        alt12=1;
            	    }
            	    else if ( (LA12_0==40) ) {
            	        alt12=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 12, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt12) {
            	        case 1 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:154:12: '+'
            	            {
            	            char_literal67=(Token)match(input,39,FOLLOW_39_in_add1067); 
            	            char_literal67_tree = (CommonTree)adaptor.create(char_literal67);
            	            root_0 = (CommonTree)adaptor.becomeRoot(char_literal67_tree, root_0);


            	            }
            	            break;
            	        case 2 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:154:19: '-'
            	            {
            	            char_literal68=(Token)match(input,40,FOLLOW_40_in_add1072); 
            	            char_literal68_tree = (CommonTree)adaptor.create(char_literal68);
            	            root_0 = (CommonTree)adaptor.becomeRoot(char_literal68_tree, root_0);


            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_mult_in_add1076);
            	    mult69=mult();

            	    state._fsp--;

            	    adaptor.addChild(root_0, mult69.getTree());

            	    }
            	    break;

            	default :
            	    break loop13;
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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:157:1: relation : add ( ( '=' | '!=' | '<' | '<=' | '>=' | '>' ) add )* ;
    public final DQLGrammarParser.relation_return relation() throws RecognitionException {
        DQLGrammarParser.relation_return retval = new DQLGrammarParser.relation_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal71=null;
        Token string_literal72=null;
        Token char_literal73=null;
        Token string_literal74=null;
        Token string_literal75=null;
        Token char_literal76=null;
        DQLGrammarParser.add_return add70 = null;

        DQLGrammarParser.add_return add77 = null;


        CommonTree char_literal71_tree=null;
        CommonTree string_literal72_tree=null;
        CommonTree char_literal73_tree=null;
        CommonTree string_literal74_tree=null;
        CommonTree string_literal75_tree=null;
        CommonTree char_literal76_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:158:3: ( add ( ( '=' | '!=' | '<' | '<=' | '>=' | '>' ) add )* )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:158:5: add ( ( '=' | '!=' | '<' | '<=' | '>=' | '>' ) add )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_add_in_relation1091);
            add70=add();

            state._fsp--;

            adaptor.addChild(root_0, add70.getTree());
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:158:9: ( ( '=' | '!=' | '<' | '<=' | '>=' | '>' ) add )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==27) ) {
                    int LA15_2 = input.LA(2);

                    if ( ((LA15_2>=ID && LA15_2<=INT)||LA15_2==DOUBLE||LA15_2==STRING_LITERAL||LA15_2==19||LA15_2==21||(LA15_2>=36 && LA15_2<=40)) ) {
                        alt15=1;
                    }


                }
                else if ( (LA15_0==26||(LA15_0>=44 && LA15_0<=47)) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:158:10: ( '=' | '!=' | '<' | '<=' | '>=' | '>' ) add
            	    {
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:158:10: ( '=' | '!=' | '<' | '<=' | '>=' | '>' )
            	    int alt14=6;
            	    switch ( input.LA(1) ) {
            	    case 44:
            	        {
            	        alt14=1;
            	        }
            	        break;
            	    case 45:
            	        {
            	        alt14=2;
            	        }
            	        break;
            	    case 26:
            	        {
            	        alt14=3;
            	        }
            	        break;
            	    case 46:
            	        {
            	        alt14=4;
            	        }
            	        break;
            	    case 47:
            	        {
            	        alt14=5;
            	        }
            	        break;
            	    case 27:
            	        {
            	        alt14=6;
            	        }
            	        break;
            	    default:
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 14, 0, input);

            	        throw nvae;
            	    }

            	    switch (alt14) {
            	        case 1 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:158:11: '='
            	            {
            	            char_literal71=(Token)match(input,44,FOLLOW_44_in_relation1095); 
            	            char_literal71_tree = (CommonTree)adaptor.create(char_literal71);
            	            root_0 = (CommonTree)adaptor.becomeRoot(char_literal71_tree, root_0);


            	            }
            	            break;
            	        case 2 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:158:18: '!='
            	            {
            	            string_literal72=(Token)match(input,45,FOLLOW_45_in_relation1100); 
            	            string_literal72_tree = (CommonTree)adaptor.create(string_literal72);
            	            root_0 = (CommonTree)adaptor.becomeRoot(string_literal72_tree, root_0);


            	            }
            	            break;
            	        case 3 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:158:26: '<'
            	            {
            	            char_literal73=(Token)match(input,26,FOLLOW_26_in_relation1105); 
            	            char_literal73_tree = (CommonTree)adaptor.create(char_literal73);
            	            root_0 = (CommonTree)adaptor.becomeRoot(char_literal73_tree, root_0);


            	            }
            	            break;
            	        case 4 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:158:33: '<='
            	            {
            	            string_literal74=(Token)match(input,46,FOLLOW_46_in_relation1110); 
            	            string_literal74_tree = (CommonTree)adaptor.create(string_literal74);
            	            root_0 = (CommonTree)adaptor.becomeRoot(string_literal74_tree, root_0);


            	            }
            	            break;
            	        case 5 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:158:41: '>='
            	            {
            	            string_literal75=(Token)match(input,47,FOLLOW_47_in_relation1115); 
            	            string_literal75_tree = (CommonTree)adaptor.create(string_literal75);
            	            root_0 = (CommonTree)adaptor.becomeRoot(string_literal75_tree, root_0);


            	            }
            	            break;
            	        case 6 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:158:49: '>'
            	            {
            	            char_literal76=(Token)match(input,27,FOLLOW_27_in_relation1120); 
            	            char_literal76_tree = (CommonTree)adaptor.create(char_literal76);
            	            root_0 = (CommonTree)adaptor.becomeRoot(char_literal76_tree, root_0);


            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_add_in_relation1124);
            	    add77=add();

            	    state._fsp--;

            	    adaptor.addChild(root_0, add77.getTree());

            	    }
            	    break;

            	default :
            	    break loop15;
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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:161:1: expression : relation ( ( '&&' | '||' ) relation )* ( 'IS' privacy_decl ( 'AS' type )? )? ;
    public final DQLGrammarParser.expression_return expression() throws RecognitionException {
        DQLGrammarParser.expression_return retval = new DQLGrammarParser.expression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal79=null;
        Token string_literal80=null;
        Token string_literal82=null;
        Token string_literal84=null;
        DQLGrammarParser.relation_return relation78 = null;

        DQLGrammarParser.relation_return relation81 = null;

        DQLGrammarParser.privacy_decl_return privacy_decl83 = null;

        DQLGrammarParser.type_return type85 = null;


        CommonTree string_literal79_tree=null;
        CommonTree string_literal80_tree=null;
        CommonTree string_literal82_tree=null;
        CommonTree string_literal84_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:162:3: ( relation ( ( '&&' | '||' ) relation )* ( 'IS' privacy_decl ( 'AS' type )? )? )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:162:5: relation ( ( '&&' | '||' ) relation )* ( 'IS' privacy_decl ( 'AS' type )? )?
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_relation_in_expression1141);
            relation78=relation();

            state._fsp--;

            adaptor.addChild(root_0, relation78.getTree());
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:162:14: ( ( '&&' | '||' ) relation )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( ((LA17_0>=48 && LA17_0<=49)) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:162:15: ( '&&' | '||' ) relation
            	    {
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:162:15: ( '&&' | '||' )
            	    int alt16=2;
            	    int LA16_0 = input.LA(1);

            	    if ( (LA16_0==48) ) {
            	        alt16=1;
            	    }
            	    else if ( (LA16_0==49) ) {
            	        alt16=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 16, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt16) {
            	        case 1 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:162:16: '&&'
            	            {
            	            string_literal79=(Token)match(input,48,FOLLOW_48_in_expression1145); 
            	            string_literal79_tree = (CommonTree)adaptor.create(string_literal79);
            	            root_0 = (CommonTree)adaptor.becomeRoot(string_literal79_tree, root_0);


            	            }
            	            break;
            	        case 2 :
            	            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:162:24: '||'
            	            {
            	            string_literal80=(Token)match(input,49,FOLLOW_49_in_expression1150); 
            	            string_literal80_tree = (CommonTree)adaptor.create(string_literal80);
            	            root_0 = (CommonTree)adaptor.becomeRoot(string_literal80_tree, root_0);


            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_relation_in_expression1154);
            	    relation81=relation();

            	    state._fsp--;

            	    adaptor.addChild(root_0, relation81.getTree());

            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);

            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:162:42: ( 'IS' privacy_decl ( 'AS' type )? )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==50) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:162:43: 'IS' privacy_decl ( 'AS' type )?
                    {
                    string_literal82=(Token)match(input,50,FOLLOW_50_in_expression1159); 
                    string_literal82_tree = (CommonTree)adaptor.create(string_literal82);
                    root_0 = (CommonTree)adaptor.becomeRoot(string_literal82_tree, root_0);

                    pushFollow(FOLLOW_privacy_decl_in_expression1162);
                    privacy_decl83=privacy_decl();

                    state._fsp--;

                    adaptor.addChild(root_0, privacy_decl83.getTree());
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:162:62: ( 'AS' type )?
                    int alt18=2;
                    int LA18_0 = input.LA(1);

                    if ( (LA18_0==51) ) {
                        alt18=1;
                    }
                    switch (alt18) {
                        case 1 :
                            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:162:64: 'AS' type
                            {
                            string_literal84=(Token)match(input,51,FOLLOW_51_in_expression1166); 
                            string_literal84_tree = (CommonTree)adaptor.create(string_literal84);
                            root_0 = (CommonTree)adaptor.becomeRoot(string_literal84_tree, root_0);

                            pushFollow(FOLLOW_type_in_expression1169);
                            type85=type();

                            state._fsp--;

                            adaptor.addChild(root_0, type85.getTree());

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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:166:1: let : 'LET' ID '=' expression ;
    public final DQLGrammarParser.let_return let() throws RecognitionException {
        DQLGrammarParser.let_return retval = new DQLGrammarParser.let_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal86=null;
        Token ID87=null;
        Token char_literal88=null;
        DQLGrammarParser.expression_return expression89 = null;


        CommonTree string_literal86_tree=null;
        CommonTree ID87_tree=null;
        CommonTree char_literal88_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:166:5: ( 'LET' ID '=' expression )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:166:7: 'LET' ID '=' expression
            {
            root_0 = (CommonTree)adaptor.nil();

            string_literal86=(Token)match(input,52,FOLLOW_52_in_let1187); 
            ID87=(Token)match(input,ID,FOLLOW_ID_in_let1190); 
            ID87_tree = (CommonTree)adaptor.create(ID87);
            root_0 = (CommonTree)adaptor.becomeRoot(ID87_tree, root_0);

            char_literal88=(Token)match(input,44,FOLLOW_44_in_let1193); 
            pushFollow(FOLLOW_expression_in_let1196);
            expression89=expression();

            state._fsp--;

            adaptor.addChild(root_0, expression89.getTree());

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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:168:1: gather_statement : 'GATHER' ( let )+ ;
    public final DQLGrammarParser.gather_statement_return gather_statement() throws RecognitionException {
        DQLGrammarParser.gather_statement_return retval = new DQLGrammarParser.gather_statement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal90=null;
        DQLGrammarParser.let_return let91 = null;


        CommonTree string_literal90_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:169:3: ( 'GATHER' ( let )+ )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:169:7: 'GATHER' ( let )+
            {
            root_0 = (CommonTree)adaptor.nil();

            string_literal90=(Token)match(input,53,FOLLOW_53_in_gather_statement1210); 
            string_literal90_tree = (CommonTree)adaptor.create(string_literal90);
            root_0 = (CommonTree)adaptor.becomeRoot(string_literal90_tree, root_0);

            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:169:17: ( let )+
            int cnt20=0;
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==52) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:169:17: let
            	    {
            	    pushFollow(FOLLOW_let_in_gather_statement1213);
            	    let91=let();

            	    state._fsp--;

            	    adaptor.addChild(root_0, let91.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt20 >= 1 ) break loop20;
                        EarlyExitException eee =
                            new EarlyExitException(20, input);
                        throw eee;
                }
                cnt20++;
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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:171:1: forward_statement : 'FORWARD' ( 'MAX' 'DEPTH' INT )? 'TO' expression ;
    public final DQLGrammarParser.forward_statement_return forward_statement() throws RecognitionException {
        DQLGrammarParser.forward_statement_return retval = new DQLGrammarParser.forward_statement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal92=null;
        Token string_literal93=null;
        Token string_literal94=null;
        Token INT95=null;
        Token string_literal96=null;
        DQLGrammarParser.expression_return expression97 = null;


        CommonTree string_literal92_tree=null;
        CommonTree string_literal93_tree=null;
        CommonTree string_literal94_tree=null;
        CommonTree INT95_tree=null;
        CommonTree string_literal96_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:172:3: ( 'FORWARD' ( 'MAX' 'DEPTH' INT )? 'TO' expression )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:172:5: 'FORWARD' ( 'MAX' 'DEPTH' INT )? 'TO' expression
            {
            root_0 = (CommonTree)adaptor.nil();

            string_literal92=(Token)match(input,54,FOLLOW_54_in_forward_statement1224); 
            string_literal92_tree = (CommonTree)adaptor.create(string_literal92);
            root_0 = (CommonTree)adaptor.becomeRoot(string_literal92_tree, root_0);

            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:172:16: ( 'MAX' 'DEPTH' INT )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==55) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:172:17: 'MAX' 'DEPTH' INT
                    {
                    string_literal93=(Token)match(input,55,FOLLOW_55_in_forward_statement1228); 
                    string_literal94=(Token)match(input,56,FOLLOW_56_in_forward_statement1231); 
                    INT95=(Token)match(input,INT,FOLLOW_INT_in_forward_statement1234); 
                    INT95_tree = (CommonTree)adaptor.create(INT95);
                    adaptor.addChild(root_0, INT95_tree);


                    }
                    break;

            }

            string_literal96=(Token)match(input,57,FOLLOW_57_in_forward_statement1238); 
            pushFollow(FOLLOW_expression_in_forward_statement1241);
            expression97=expression();

            state._fsp--;

            adaptor.addChild(root_0, expression97.getTree());

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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:174:1: reduce_statement : 'REDUCE' expression EOF ;
    public final DQLGrammarParser.reduce_statement_return reduce_statement() throws RecognitionException {
        DQLGrammarParser.reduce_statement_return retval = new DQLGrammarParser.reduce_statement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal98=null;
        Token EOF100=null;
        DQLGrammarParser.expression_return expression99 = null;


        CommonTree string_literal98_tree=null;
        CommonTree EOF100_tree=null;

        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:175:3: ( 'REDUCE' expression EOF )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:175:5: 'REDUCE' expression EOF
            {
            root_0 = (CommonTree)adaptor.nil();

            string_literal98=(Token)match(input,58,FOLLOW_58_in_reduce_statement1251); 
            string_literal98_tree = (CommonTree)adaptor.create(string_literal98);
            root_0 = (CommonTree)adaptor.becomeRoot(string_literal98_tree, root_0);

            pushFollow(FOLLOW_expression_in_reduce_statement1254);
            expression99=expression();

            state._fsp--;

            adaptor.addChild(root_0, expression99.getTree());
            EOF100=(Token)match(input,EOF,FOLLOW_EOF_in_reduce_statement1256); 
            EOF100_tree = (CommonTree)adaptor.create(EOF100);
            adaptor.addChild(root_0, EOF100_tree);


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
    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:177:1: query : gather_statement forward_statement reduce_statement ;
    public final DQLGrammarParser.query_return query() throws RecognitionException {
        DQLGrammarParser.query_return retval = new DQLGrammarParser.query_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        DQLGrammarParser.gather_statement_return gather_statement101 = null;

        DQLGrammarParser.forward_statement_return forward_statement102 = null;

        DQLGrammarParser.reduce_statement_return reduce_statement103 = null;



        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:177:7: ( gather_statement forward_statement reduce_statement )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/java/org/nebulostore/query/grammar/DQLGrammar.g:178:4: gather_statement forward_statement reduce_statement
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_gather_statement_in_query1267);
            gather_statement101=gather_statement();

            state._fsp--;

            adaptor.addChild(root_0, gather_statement101.getTree());
            pushFollow(FOLLOW_forward_statement_in_query1272);
            forward_statement102=forward_statement();

            state._fsp--;

            adaptor.addChild(root_0, forward_statement102.getTree());
            pushFollow(FOLLOW_reduce_statement_in_query1277);
            reduce_statement103=reduce_statement();

            state._fsp--;

            adaptor.addChild(root_0, reduce_statement103.getTree());

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


    protected DFA7 dfa7 = new DFA7(this);
    static final String DFA7_eotS =
        "\13\uffff";
    static final String DFA7_eofS =
        "\1\uffff\1\12\11\uffff";
    static final String DFA7_minS =
        "\1\5\1\22\11\uffff";
    static final String DFA7_maxS =
        "\1\45\1\72\11\uffff";
    static final String DFA7_acceptS =
        "\2\uffff\1\2\1\3\1\4\1\5\1\7\1\10\1\11\1\6\1\1";
    static final String DFA7_specialS =
        "\13\uffff}>";
    static final String[] DFA7_transitionS = {
            "\1\1\1\3\1\uffff\1\4\10\uffff\1\5\1\uffff\1\6\1\uffff\1\2\16"+
            "\uffff\1\7\1\10",
            "\1\12\2\uffff\1\11\1\12\3\uffff\2\12\13\uffff\14\12\1\uffff"+
            "\1\12\1\uffff\1\12\3\uffff\1\12",
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
    public static final BitSet FOLLOW_function_call_parameters_in_function_call703 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_function_call706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_privacy_decl753 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_24_in_privacy_decl759 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_privacy_decl765 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_privacy_decl768 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_expression_in_privacy_decl771 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_privacy_decl773 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_privacy_decl781 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_privacy_decl787 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_privacy_decl789 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_expression_in_privacy_decl792 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_privacy_decl794 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_type_list809 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_18_in_type_list813 = new BitSet(new long[]{0x0000000FC0000000L});
    public static final BitSet FOLLOW_type_in_type_list816 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_30_in_type830 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_type836 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_type842 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_type848 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_type851 = new BitSet(new long[]{0x0000000FC0000000L});
    public static final BitSet FOLLOW_type_list_in_type854 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_type856 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_type863 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_type866 = new BitSet(new long[]{0x0000000FC0000000L});
    public static final BitSet FOLLOW_type_in_type869 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_type871 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_type879 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_term893 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_term899 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_expression_in_term902 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_term904 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_term911 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_in_term917 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_term924 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_call_in_term930 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lambda_function_in_term936 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_term943 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_term949 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_negation965 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_term_in_negation970 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_unary986 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_unary_negation_rewrite_in_unary991 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_negation_in_unary996 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_unary_negation_rewrite1011 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unary_in_mult1030 = new BitSet(new long[]{0x00000E0000000002L});
    public static final BitSet FOLLOW_41_in_mult1034 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_42_in_mult1039 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_43_in_mult1044 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_unary_in_mult1048 = new BitSet(new long[]{0x00000E0000000002L});
    public static final BitSet FOLLOW_mult_in_add1063 = new BitSet(new long[]{0x0000018000000002L});
    public static final BitSet FOLLOW_39_in_add1067 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_40_in_add1072 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_mult_in_add1076 = new BitSet(new long[]{0x0000018000000002L});
    public static final BitSet FOLLOW_add_in_relation1091 = new BitSet(new long[]{0x0000F0000C000002L});
    public static final BitSet FOLLOW_44_in_relation1095 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_45_in_relation1100 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_26_in_relation1105 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_46_in_relation1110 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_47_in_relation1115 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_27_in_relation1120 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_add_in_relation1124 = new BitSet(new long[]{0x0000F0000C000002L});
    public static final BitSet FOLLOW_relation_in_expression1141 = new BitSet(new long[]{0x0007000000000002L});
    public static final BitSet FOLLOW_48_in_expression1145 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_49_in_expression1150 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_relation_in_expression1154 = new BitSet(new long[]{0x0007000000000002L});
    public static final BitSet FOLLOW_50_in_expression1159 = new BitSet(new long[]{0x0000000033800000L});
    public static final BitSet FOLLOW_privacy_decl_in_expression1162 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_51_in_expression1166 = new BitSet(new long[]{0x0000000FC0000000L});
    public static final BitSet FOLLOW_type_in_expression1169 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_52_in_let1187 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_let1190 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_let1193 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_expression_in_let1196 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_gather_statement1210 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_let_in_gather_statement1213 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_54_in_forward_statement1224 = new BitSet(new long[]{0x0280000000000000L});
    public static final BitSet FOLLOW_55_in_forward_statement1228 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_56_in_forward_statement1231 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_INT_in_forward_statement1234 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_57_in_forward_statement1238 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_expression_in_forward_statement1241 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_58_in_reduce_statement1251 = new BitSet(new long[]{0x000001F0002A0160L});
    public static final BitSet FOLLOW_expression_in_reduce_statement1254 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_reduce_statement1256 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_gather_statement_in_query1267 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_forward_statement_in_query1272 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_reduce_statement_in_query1277 = new BitSet(new long[]{0x0000000000000002L});

}