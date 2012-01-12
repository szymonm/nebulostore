// $ANTLR 3.3 Nov 30, 2010 12:50:56 /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g 2012-01-10 22:32:24

  package org.nebulostore.query.language.interpreter.antlr;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class DQLGrammarLexer extends Lexer {
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

    public DQLGrammarLexer() {;} 
    public DQLGrammarLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public DQLGrammarLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "/home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g"; }

    // $ANTLR start "T__18"
    public final void mT__18() throws RecognitionException {
        try {
            int _type = T__18;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:11:7: ( ',' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:11:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__18"

    // $ANTLR start "T__19"
    public final void mT__19() throws RecognitionException {
        try {
            int _type = T__19;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:12:7: ( 'LAMBDA' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:12:9: 'LAMBDA'
            {
            match("LAMBDA"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__19"

    // $ANTLR start "T__20"
    public final void mT__20() throws RecognitionException {
        try {
            int _type = T__20;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:13:7: ( ':' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:13:9: ':'
            {
            match(':'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__20"

    // $ANTLR start "T__21"
    public final void mT__21() throws RecognitionException {
        try {
            int _type = T__21;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:14:7: ( '(' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:14:9: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__21"

    // $ANTLR start "T__22"
    public final void mT__22() throws RecognitionException {
        try {
            int _type = T__22;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:15:7: ( ')' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:15:9: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__22"

    // $ANTLR start "T__23"
    public final void mT__23() throws RecognitionException {
        try {
            int _type = T__23;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:16:7: ( 'PRIVATE_MY' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:16:9: 'PRIVATE_MY'
            {
            match("PRIVATE_MY"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__23"

    // $ANTLR start "T__24"
    public final void mT__24() throws RecognitionException {
        try {
            int _type = T__24;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:17:7: ( 'PUBLIC_MY' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:17:9: 'PUBLIC_MY'
            {
            match("PUBLIC_MY"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__24"

    // $ANTLR start "T__25"
    public final void mT__25() throws RecognitionException {
        try {
            int _type = T__25;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:18:7: ( 'PRIVATE_COND_MY' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:18:9: 'PRIVATE_COND_MY'
            {
            match("PRIVATE_COND_MY"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__25"

    // $ANTLR start "T__26"
    public final void mT__26() throws RecognitionException {
        try {
            int _type = T__26;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:19:7: ( 'PUBLIC_OTHER' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:19:9: 'PUBLIC_OTHER'
            {
            match("PUBLIC_OTHER"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__26"

    // $ANTLR start "T__27"
    public final void mT__27() throws RecognitionException {
        try {
            int _type = T__27;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:20:7: ( 'PRIVATE_COND_OTHER' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:20:9: 'PRIVATE_COND_OTHER'
            {
            match("PRIVATE_COND_OTHER"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__27"

    // $ANTLR start "T__28"
    public final void mT__28() throws RecognitionException {
        try {
            int _type = T__28;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:21:7: ( 'INTEGER' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:21:9: 'INTEGER'
            {
            match("INTEGER"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__28"

    // $ANTLR start "T__29"
    public final void mT__29() throws RecognitionException {
        try {
            int _type = T__29;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:22:7: ( 'DOUBLE' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:22:9: 'DOUBLE'
            {
            match("DOUBLE"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__29"

    // $ANTLR start "T__30"
    public final void mT__30() throws RecognitionException {
        try {
            int _type = T__30;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:23:7: ( 'STRING' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:23:9: 'STRING'
            {
            match("STRING"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__30"

    // $ANTLR start "T__31"
    public final void mT__31() throws RecognitionException {
        try {
            int _type = T__31;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:24:7: ( 'TUPLE' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:24:9: 'TUPLE'
            {
            match("TUPLE"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__31"

    // $ANTLR start "T__32"
    public final void mT__32() throws RecognitionException {
        try {
            int _type = T__32;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:25:7: ( '<' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:25:9: '<'
            {
            match('<'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__32"

    // $ANTLR start "T__33"
    public final void mT__33() throws RecognitionException {
        try {
            int _type = T__33;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:26:7: ( '>' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:26:9: '>'
            {
            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__33"

    // $ANTLR start "T__34"
    public final void mT__34() throws RecognitionException {
        try {
            int _type = T__34;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:27:7: ( 'LIST' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:27:9: 'LIST'
            {
            match("LIST"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__34"

    // $ANTLR start "T__35"
    public final void mT__35() throws RecognitionException {
        try {
            int _type = T__35;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:28:7: ( 'not' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:28:9: 'not'
            {
            match("not"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__35"

    // $ANTLR start "T__36"
    public final void mT__36() throws RecognitionException {
        try {
            int _type = T__36;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:29:7: ( '+' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:29:9: '+'
            {
            match('+'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__36"

    // $ANTLR start "T__37"
    public final void mT__37() throws RecognitionException {
        try {
            int _type = T__37;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:30:7: ( '-' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:30:9: '-'
            {
            match('-'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__37"

    // $ANTLR start "T__38"
    public final void mT__38() throws RecognitionException {
        try {
            int _type = T__38;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:31:7: ( '*' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:31:9: '*'
            {
            match('*'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__38"

    // $ANTLR start "T__39"
    public final void mT__39() throws RecognitionException {
        try {
            int _type = T__39;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:32:7: ( '/' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:32:9: '/'
            {
            match('/'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__39"

    // $ANTLR start "T__40"
    public final void mT__40() throws RecognitionException {
        try {
            int _type = T__40;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:33:7: ( '%' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:33:9: '%'
            {
            match('%'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__40"

    // $ANTLR start "T__41"
    public final void mT__41() throws RecognitionException {
        try {
            int _type = T__41;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:34:7: ( '=' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:34:9: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__41"

    // $ANTLR start "T__42"
    public final void mT__42() throws RecognitionException {
        try {
            int _type = T__42;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:35:7: ( '!=' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:35:9: '!='
            {
            match("!="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__42"

    // $ANTLR start "T__43"
    public final void mT__43() throws RecognitionException {
        try {
            int _type = T__43;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:36:7: ( '<=' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:36:9: '<='
            {
            match("<="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__43"

    // $ANTLR start "T__44"
    public final void mT__44() throws RecognitionException {
        try {
            int _type = T__44;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:37:7: ( '>=' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:37:9: '>='
            {
            match(">="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__44"

    // $ANTLR start "T__45"
    public final void mT__45() throws RecognitionException {
        try {
            int _type = T__45;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:38:7: ( '&&' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:38:9: '&&'
            {
            match("&&"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__45"

    // $ANTLR start "T__46"
    public final void mT__46() throws RecognitionException {
        try {
            int _type = T__46;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:39:7: ( '||' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:39:9: '||'
            {
            match("||"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__46"

    // $ANTLR start "T__47"
    public final void mT__47() throws RecognitionException {
        try {
            int _type = T__47;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:40:7: ( 'IS' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:40:9: 'IS'
            {
            match("IS"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__47"

    // $ANTLR start "T__48"
    public final void mT__48() throws RecognitionException {
        try {
            int _type = T__48;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:41:7: ( 'AS' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:41:9: 'AS'
            {
            match("AS"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__48"

    // $ANTLR start "T__49"
    public final void mT__49() throws RecognitionException {
        try {
            int _type = T__49;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:42:7: ( 'LET' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:42:9: 'LET'
            {
            match("LET"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__49"

    // $ANTLR start "T__50"
    public final void mT__50() throws RecognitionException {
        try {
            int _type = T__50;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:43:7: ( 'GATHER' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:43:9: 'GATHER'
            {
            match("GATHER"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__50"

    // $ANTLR start "T__51"
    public final void mT__51() throws RecognitionException {
        try {
            int _type = T__51;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:44:7: ( 'FORWARD' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:44:9: 'FORWARD'
            {
            match("FORWARD"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__51"

    // $ANTLR start "T__52"
    public final void mT__52() throws RecognitionException {
        try {
            int _type = T__52;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:45:7: ( 'MAX' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:45:9: 'MAX'
            {
            match("MAX"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__52"

    // $ANTLR start "T__53"
    public final void mT__53() throws RecognitionException {
        try {
            int _type = T__53;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:46:7: ( 'DEPTH' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:46:9: 'DEPTH'
            {
            match("DEPTH"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__53"

    // $ANTLR start "T__54"
    public final void mT__54() throws RecognitionException {
        try {
            int _type = T__54;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:47:7: ( 'TO' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:47:9: 'TO'
            {
            match("TO"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__54"

    // $ANTLR start "T__55"
    public final void mT__55() throws RecognitionException {
        try {
            int _type = T__55;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:48:7: ( 'REDUCE' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:48:9: 'REDUCE'
            {
            match("REDUCE"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__55"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:23:5: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )* )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:23:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:23:31: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='0' && LA1_0<='9')||(LA1_0>='A' && LA1_0<='Z')||LA1_0=='_'||(LA1_0>='a' && LA1_0<='z')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:26:5: ( ( '0' .. '9' )+ )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:26:7: ( '0' .. '9' )+
            {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:26:7: ( '0' .. '9' )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>='0' && LA2_0<='9')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:26:7: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt2 >= 1 ) break loop2;
                        EarlyExitException eee =
                            new EarlyExitException(2, input);
                        throw eee;
                }
                cnt2++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INT"

    // $ANTLR start "DOUBLE"
    public final void mDOUBLE() throws RecognitionException {
        try {
            int _type = DOUBLE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:30:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? | '.' ( '0' .. '9' )+ ( EXPONENT )? | ( '0' .. '9' )+ EXPONENT )
            int alt9=3;
            alt9 = dfa9.predict(input);
            switch (alt9) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:30:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )?
                    {
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:30:9: ( '0' .. '9' )+
                    int cnt3=0;
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( ((LA3_0>='0' && LA3_0<='9')) ) {
                            alt3=1;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:30:10: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt3 >= 1 ) break loop3;
                                EarlyExitException eee =
                                    new EarlyExitException(3, input);
                                throw eee;
                        }
                        cnt3++;
                    } while (true);

                    match('.'); 
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:30:25: ( '0' .. '9' )*
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( ((LA4_0>='0' && LA4_0<='9')) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:30:26: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop4;
                        }
                    } while (true);

                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:30:37: ( EXPONENT )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0=='E'||LA5_0=='e') ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:30:37: EXPONENT
                            {
                            mEXPONENT(); 

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:31:9: '.' ( '0' .. '9' )+ ( EXPONENT )?
                    {
                    match('.'); 
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:31:13: ( '0' .. '9' )+
                    int cnt6=0;
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( ((LA6_0>='0' && LA6_0<='9')) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:31:14: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt6 >= 1 ) break loop6;
                                EarlyExitException eee =
                                    new EarlyExitException(6, input);
                                throw eee;
                        }
                        cnt6++;
                    } while (true);

                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:31:25: ( EXPONENT )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0=='E'||LA7_0=='e') ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:31:25: EXPONENT
                            {
                            mEXPONENT(); 

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:32:9: ( '0' .. '9' )+ EXPONENT
                    {
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:32:9: ( '0' .. '9' )+
                    int cnt8=0;
                    loop8:
                    do {
                        int alt8=2;
                        int LA8_0 = input.LA(1);

                        if ( ((LA8_0>='0' && LA8_0<='9')) ) {
                            alt8=1;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:32:10: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt8 >= 1 ) break loop8;
                                EarlyExitException eee =
                                    new EarlyExitException(8, input);
                                throw eee;
                        }
                        cnt8++;
                    } while (true);

                    mEXPONENT(); 

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOUBLE"

    // $ANTLR start "COMMENT"
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:36:5: ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' | '/*' ( options {greedy=false; } : . )* '*/' )
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0=='/') ) {
                int LA13_1 = input.LA(2);

                if ( (LA13_1=='/') ) {
                    alt13=1;
                }
                else if ( (LA13_1=='*') ) {
                    alt13=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 13, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:36:9: '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n'
                    {
                    match("//"); 

                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:36:14: (~ ( '\\n' | '\\r' ) )*
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( ((LA10_0>='\u0000' && LA10_0<='\t')||(LA10_0>='\u000B' && LA10_0<='\f')||(LA10_0>='\u000E' && LA10_0<='\uFFFF')) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:36:14: ~ ( '\\n' | '\\r' )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFF') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop10;
                        }
                    } while (true);

                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:36:28: ( '\\r' )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0=='\r') ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:36:28: '\\r'
                            {
                            match('\r'); 

                            }
                            break;

                    }

                    match('\n'); 
                    _channel=HIDDEN;

                    }
                    break;
                case 2 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:37:9: '/*' ( options {greedy=false; } : . )* '*/'
                    {
                    match("/*"); 

                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:37:14: ( options {greedy=false; } : . )*
                    loop12:
                    do {
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( (LA12_0=='*') ) {
                            int LA12_1 = input.LA(2);

                            if ( (LA12_1=='/') ) {
                                alt12=2;
                            }
                            else if ( ((LA12_1>='\u0000' && LA12_1<='.')||(LA12_1>='0' && LA12_1<='\uFFFF')) ) {
                                alt12=1;
                            }


                        }
                        else if ( ((LA12_0>='\u0000' && LA12_0<=')')||(LA12_0>='+' && LA12_0<='\uFFFF')) ) {
                            alt12=1;
                        }


                        switch (alt12) {
                    	case 1 :
                    	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:37:42: .
                    	    {
                    	    matchAny(); 

                    	    }
                    	    break;

                    	default :
                    	    break loop12;
                        }
                    } while (true);

                    match("*/"); 

                    _channel=HIDDEN;

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMENT"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:40:5: ( ( ' ' | '\\t' | '\\r' | '\\n' ) )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:40:9: ( ' ' | '\\t' | '\\r' | '\\n' )
            {
            if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:48:5: ( '\"' ( ESC_SEQ | ~ ( '\\\\' | '\"' ) )* '\"' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:48:8: '\"' ( ESC_SEQ | ~ ( '\\\\' | '\"' ) )* '\"'
            {
            match('\"'); 
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:48:12: ( ESC_SEQ | ~ ( '\\\\' | '\"' ) )*
            loop14:
            do {
                int alt14=3;
                int LA14_0 = input.LA(1);

                if ( (LA14_0=='\\') ) {
                    alt14=1;
                }
                else if ( ((LA14_0>='\u0000' && LA14_0<='!')||(LA14_0>='#' && LA14_0<='[')||(LA14_0>=']' && LA14_0<='\uFFFF')) ) {
                    alt14=2;
                }


                switch (alt14) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:48:14: ESC_SEQ
            	    {
            	    mESC_SEQ(); 

            	    }
            	    break;
            	case 2 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:48:24: ~ ( '\\\\' | '\"' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);

            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "CHAR"
    public final void mCHAR() throws RecognitionException {
        try {
            int _type = CHAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:51:5: ( '\\'' ( ESC_SEQ | ~ ( '\\'' | '\\\\' ) ) '\\'' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:51:8: '\\'' ( ESC_SEQ | ~ ( '\\'' | '\\\\' ) ) '\\''
            {
            match('\''); 
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:51:13: ( ESC_SEQ | ~ ( '\\'' | '\\\\' ) )
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0=='\\') ) {
                alt15=1;
            }
            else if ( ((LA15_0>='\u0000' && LA15_0<='&')||(LA15_0>='(' && LA15_0<='[')||(LA15_0>=']' && LA15_0<='\uFFFF')) ) {
                alt15=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;
            }
            switch (alt15) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:51:15: ESC_SEQ
                    {
                    mESC_SEQ(); 

                    }
                    break;
                case 2 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:51:25: ~ ( '\\'' | '\\\\' )
                    {
                    if ( (input.LA(1)>='\u0000' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;

            }

            match('\''); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CHAR"

    // $ANTLR start "EXPONENT"
    public final void mEXPONENT() throws RecognitionException {
        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:55:10: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:55:12: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:55:22: ( '+' | '-' )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0=='+'||LA16_0=='-') ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;

            }

            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:55:33: ( '0' .. '9' )+
            int cnt17=0;
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( ((LA17_0>='0' && LA17_0<='9')) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:55:34: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt17 >= 1 ) break loop17;
                        EarlyExitException eee =
                            new EarlyExitException(17, input);
                        throw eee;
                }
                cnt17++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "EXPONENT"

    // $ANTLR start "HEX_DIGIT"
    public final void mHEX_DIGIT() throws RecognitionException {
        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:58:11: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:58:13: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='F')||(input.LA(1)>='a' && input.LA(1)<='f') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "HEX_DIGIT"

    // $ANTLR start "ESC_SEQ"
    public final void mESC_SEQ() throws RecognitionException {
        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:62:5: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UNICODE_ESC | OCTAL_ESC )
            int alt18=3;
            int LA18_0 = input.LA(1);

            if ( (LA18_0=='\\') ) {
                switch ( input.LA(2) ) {
                case '\"':
                case '\'':
                case '\\':
                case 'b':
                case 'f':
                case 'n':
                case 'r':
                case 't':
                    {
                    alt18=1;
                    }
                    break;
                case 'u':
                    {
                    alt18=2;
                    }
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                    {
                    alt18=3;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 18, 1, input);

                    throw nvae;
                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;
            }
            switch (alt18) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:62:9: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' )
                    {
                    match('\\'); 
                    if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;
                case 2 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:63:9: UNICODE_ESC
                    {
                    mUNICODE_ESC(); 

                    }
                    break;
                case 3 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:64:9: OCTAL_ESC
                    {
                    mOCTAL_ESC(); 

                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end "ESC_SEQ"

    // $ANTLR start "OCTAL_ESC"
    public final void mOCTAL_ESC() throws RecognitionException {
        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:70:5: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
            int alt19=3;
            int LA19_0 = input.LA(1);

            if ( (LA19_0=='\\') ) {
                int LA19_1 = input.LA(2);

                if ( ((LA19_1>='0' && LA19_1<='3')) ) {
                    int LA19_2 = input.LA(3);

                    if ( ((LA19_2>='0' && LA19_2<='7')) ) {
                        int LA19_4 = input.LA(4);

                        if ( ((LA19_4>='0' && LA19_4<='7')) ) {
                            alt19=1;
                        }
                        else {
                            alt19=2;}
                    }
                    else {
                        alt19=3;}
                }
                else if ( ((LA19_1>='4' && LA19_1<='7')) ) {
                    int LA19_3 = input.LA(3);

                    if ( ((LA19_3>='0' && LA19_3<='7')) ) {
                        alt19=2;
                    }
                    else {
                        alt19=3;}
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 19, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 19, 0, input);

                throw nvae;
            }
            switch (alt19) {
                case 1 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:70:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:70:14: ( '0' .. '3' )
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:70:15: '0' .. '3'
                    {
                    matchRange('0','3'); 

                    }

                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:70:25: ( '0' .. '7' )
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:70:26: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }

                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:70:36: ( '0' .. '7' )
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:70:37: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;
                case 2 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:71:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:71:14: ( '0' .. '7' )
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:71:15: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }

                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:71:25: ( '0' .. '7' )
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:71:26: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;
                case 3 :
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:72:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); 
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:72:14: ( '0' .. '7' )
                    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:72:15: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end "OCTAL_ESC"

    // $ANTLR start "UNICODE_ESC"
    public final void mUNICODE_ESC() throws RecognitionException {
        try {
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:77:5: ( '\\\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:77:9: '\\\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
            {
            match('\\'); 
            match('u'); 
            mHEX_DIGIT(); 
            mHEX_DIGIT(); 
            mHEX_DIGIT(); 
            mHEX_DIGIT(); 

            }

        }
        finally {
        }
    }
    // $ANTLR end "UNICODE_ESC"

    // $ANTLR start "STRING_LITERAL"
    public final void mSTRING_LITERAL() throws RecognitionException {
        try {
            int _type = STRING_LITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:103:3: ( '\"' ( options {greedy=false; } : . )+ '\"' )
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:103:5: '\"' ( options {greedy=false; } : . )+ '\"'
            {
            match('\"'); 
            // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:103:9: ( options {greedy=false; } : . )+
            int cnt20=0;
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0=='\"') ) {
                    alt20=2;
                }
                else if ( ((LA20_0>='\u0000' && LA20_0<='!')||(LA20_0>='#' && LA20_0<='\uFFFF')) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:103:39: .
            	    {
            	    matchAny(); 

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

            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING_LITERAL"

    public void mTokens() throws RecognitionException {
        // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:8: ( T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | T__36 | T__37 | T__38 | T__39 | T__40 | T__41 | T__42 | T__43 | T__44 | T__45 | T__46 | T__47 | T__48 | T__49 | T__50 | T__51 | T__52 | T__53 | T__54 | T__55 | ID | INT | DOUBLE | COMMENT | WS | STRING | CHAR | STRING_LITERAL )
        int alt21=46;
        alt21 = dfa21.predict(input);
        switch (alt21) {
            case 1 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:10: T__18
                {
                mT__18(); 

                }
                break;
            case 2 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:16: T__19
                {
                mT__19(); 

                }
                break;
            case 3 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:22: T__20
                {
                mT__20(); 

                }
                break;
            case 4 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:28: T__21
                {
                mT__21(); 

                }
                break;
            case 5 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:34: T__22
                {
                mT__22(); 

                }
                break;
            case 6 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:40: T__23
                {
                mT__23(); 

                }
                break;
            case 7 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:46: T__24
                {
                mT__24(); 

                }
                break;
            case 8 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:52: T__25
                {
                mT__25(); 

                }
                break;
            case 9 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:58: T__26
                {
                mT__26(); 

                }
                break;
            case 10 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:64: T__27
                {
                mT__27(); 

                }
                break;
            case 11 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:70: T__28
                {
                mT__28(); 

                }
                break;
            case 12 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:76: T__29
                {
                mT__29(); 

                }
                break;
            case 13 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:82: T__30
                {
                mT__30(); 

                }
                break;
            case 14 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:88: T__31
                {
                mT__31(); 

                }
                break;
            case 15 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:94: T__32
                {
                mT__32(); 

                }
                break;
            case 16 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:100: T__33
                {
                mT__33(); 

                }
                break;
            case 17 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:106: T__34
                {
                mT__34(); 

                }
                break;
            case 18 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:112: T__35
                {
                mT__35(); 

                }
                break;
            case 19 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:118: T__36
                {
                mT__36(); 

                }
                break;
            case 20 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:124: T__37
                {
                mT__37(); 

                }
                break;
            case 21 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:130: T__38
                {
                mT__38(); 

                }
                break;
            case 22 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:136: T__39
                {
                mT__39(); 

                }
                break;
            case 23 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:142: T__40
                {
                mT__40(); 

                }
                break;
            case 24 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:148: T__41
                {
                mT__41(); 

                }
                break;
            case 25 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:154: T__42
                {
                mT__42(); 

                }
                break;
            case 26 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:160: T__43
                {
                mT__43(); 

                }
                break;
            case 27 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:166: T__44
                {
                mT__44(); 

                }
                break;
            case 28 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:172: T__45
                {
                mT__45(); 

                }
                break;
            case 29 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:178: T__46
                {
                mT__46(); 

                }
                break;
            case 30 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:184: T__47
                {
                mT__47(); 

                }
                break;
            case 31 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:190: T__48
                {
                mT__48(); 

                }
                break;
            case 32 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:196: T__49
                {
                mT__49(); 

                }
                break;
            case 33 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:202: T__50
                {
                mT__50(); 

                }
                break;
            case 34 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:208: T__51
                {
                mT__51(); 

                }
                break;
            case 35 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:214: T__52
                {
                mT__52(); 

                }
                break;
            case 36 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:220: T__53
                {
                mT__53(); 

                }
                break;
            case 37 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:226: T__54
                {
                mT__54(); 

                }
                break;
            case 38 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:232: T__55
                {
                mT__55(); 

                }
                break;
            case 39 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:238: ID
                {
                mID(); 

                }
                break;
            case 40 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:241: INT
                {
                mINT(); 

                }
                break;
            case 41 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:245: DOUBLE
                {
                mDOUBLE(); 

                }
                break;
            case 42 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:252: COMMENT
                {
                mCOMMENT(); 

                }
                break;
            case 43 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:260: WS
                {
                mWS(); 

                }
                break;
            case 44 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:263: STRING
                {
                mSTRING(); 

                }
                break;
            case 45 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:270: CHAR
                {
                mCHAR(); 

                }
                break;
            case 46 :
                // /home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/DQLGrammar/src/main/org/nebulostore/query/grammar/DQLGrammar.g:1:275: STRING_LITERAL
                {
                mSTRING_LITERAL(); 

                }
                break;

        }

    }


    protected DFA9 dfa9 = new DFA9(this);
    protected DFA21 dfa21 = new DFA21(this);
    static final String DFA9_eotS =
        "\5\uffff";
    static final String DFA9_eofS =
        "\5\uffff";
    static final String DFA9_minS =
        "\2\56\3\uffff";
    static final String DFA9_maxS =
        "\1\71\1\145\3\uffff";
    static final String DFA9_acceptS =
        "\2\uffff\1\2\1\1\1\3";
    static final String DFA9_specialS =
        "\5\uffff}>";
    static final String[] DFA9_transitionS = {
            "\1\2\1\uffff\12\1",
            "\1\3\1\uffff\12\1\13\uffff\1\4\37\uffff\1\4",
            "",
            "",
            ""
    };

    static final short[] DFA9_eot = DFA.unpackEncodedString(DFA9_eotS);
    static final short[] DFA9_eof = DFA.unpackEncodedString(DFA9_eofS);
    static final char[] DFA9_min = DFA.unpackEncodedStringToUnsignedChars(DFA9_minS);
    static final char[] DFA9_max = DFA.unpackEncodedStringToUnsignedChars(DFA9_maxS);
    static final short[] DFA9_accept = DFA.unpackEncodedString(DFA9_acceptS);
    static final short[] DFA9_special = DFA.unpackEncodedString(DFA9_specialS);
    static final short[][] DFA9_transition;

    static {
        int numStates = DFA9_transitionS.length;
        DFA9_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA9_transition[i] = DFA.unpackEncodedString(DFA9_transitionS[i]);
        }
    }

    class DFA9 extends DFA {

        public DFA9(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 9;
            this.eot = DFA9_eot;
            this.eof = DFA9_eof;
            this.min = DFA9_min;
            this.max = DFA9_max;
            this.accept = DFA9_accept;
            this.special = DFA9_special;
            this.transition = DFA9_transition;
        }
        public String getDescription() {
            return "29:1: DOUBLE : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? | '.' ( '0' .. '9' )+ ( EXPONENT )? | ( '0' .. '9' )+ EXPONENT );";
        }
    }
    static final String DFA21_eotS =
        "\2\uffff\1\34\3\uffff\5\34\1\57\1\61\1\34\3\uffff\1\64\5\uffff\5"+
        "\34\1\uffff\1\72\4\uffff\6\34\1\104\4\34\1\111\4\uffff\1\34\2\uffff"+
        "\1\113\4\34\3\uffff\1\127\2\34\1\132\3\34\1\uffff\4\34\1\uffff\1"+
        "\142\1\uffff\2\34\1\145\1\34\1\125\5\uffff\1\127\1\uffff\1\34\1"+
        "\153\1\uffff\7\34\1\uffff\2\34\1\uffff\1\34\3\uffff\1\34\1\uffff"+
        "\4\34\1\175\1\34\1\177\3\34\2\uffff\1\u0084\3\34\1\u0088\1\uffff"+
        "\1\u0089\1\uffff\1\u008a\1\34\1\u008c\2\uffff\2\34\1\u0091\3\uffff"+
        "\1\u0092\2\uffff\3\34\2\uffff\2\34\1\u0099\1\34\1\u009b\1\34\1\uffff"+
        "\1\34\1\uffff\3\34\1\u00a1\1\34\1\uffff\2\34\1\u00a6\1\34\1\uffff"+
        "\2\34\1\u00aa\1\uffff";
    static final String DFA21_eofS =
        "\u00ab\uffff";
    static final String DFA21_minS =
        "\1\11\1\uffff\1\101\3\uffff\1\122\1\116\1\105\1\124\1\117\2\75\1"+
        "\157\3\uffff\1\52\5\uffff\1\123\1\101\1\117\1\101\1\105\1\uffff"+
        "\1\56\2\uffff\1\0\1\uffff\1\115\1\123\1\124\1\111\1\102\1\124\1"+
        "\60\1\125\1\120\1\122\1\120\1\60\4\uffff\1\164\2\uffff\1\60\1\124"+
        "\1\122\1\130\1\104\1\uffff\3\0\1\102\1\124\1\60\1\126\1\114\1\105"+
        "\1\uffff\1\102\1\124\1\111\1\114\1\uffff\1\60\1\uffff\1\110\1\127"+
        "\1\60\1\125\5\0\1\uffff\1\0\1\uffff\1\104\1\60\1\uffff\1\101\1\111"+
        "\1\107\1\114\1\110\1\116\1\105\1\uffff\1\105\1\101\1\uffff\1\103"+
        "\3\0\1\101\1\uffff\1\124\1\103\2\105\1\60\1\107\1\60\2\122\1\105"+
        "\2\0\1\60\1\105\1\137\1\122\1\60\1\uffff\1\60\1\uffff\1\60\1\104"+
        "\1\60\1\0\1\uffff\1\137\1\115\1\60\3\uffff\1\60\1\uffff\1\0\1\103"+
        "\1\131\1\124\2\uffff\1\131\1\117\1\60\1\110\1\60\1\116\1\uffff\1"+
        "\105\1\uffff\1\104\1\122\1\137\1\60\1\115\1\uffff\1\131\1\124\1"+
        "\60\1\110\1\uffff\1\105\1\122\1\60\1\uffff";
    static final String DFA21_maxS =
        "\1\174\1\uffff\1\111\3\uffff\1\125\1\123\1\117\1\124\1\125\2\75"+
        "\1\157\3\uffff\1\57\5\uffff\1\123\1\101\1\117\1\101\1\105\1\uffff"+
        "\1\145\2\uffff\1\uffff\1\uffff\1\115\1\123\1\124\1\111\1\102\1\124"+
        "\1\172\1\125\1\120\1\122\1\120\1\172\4\uffff\1\164\2\uffff\1\172"+
        "\1\124\1\122\1\130\1\104\1\uffff\3\uffff\1\102\1\124\1\172\1\126"+
        "\1\114\1\105\1\uffff\1\102\1\124\1\111\1\114\1\uffff\1\172\1\uffff"+
        "\1\110\1\127\1\172\1\125\5\uffff\1\uffff\1\uffff\1\uffff\1\104\1"+
        "\172\1\uffff\1\101\1\111\1\107\1\114\1\110\1\116\1\105\1\uffff\1"+
        "\105\1\101\1\uffff\1\103\3\uffff\1\101\1\uffff\1\124\1\103\2\105"+
        "\1\172\1\107\1\172\2\122\1\105\2\uffff\1\172\1\105\1\137\1\122\1"+
        "\172\1\uffff\1\172\1\uffff\1\172\1\104\1\172\1\uffff\1\uffff\1\137"+
        "\1\117\1\172\3\uffff\1\172\1\uffff\1\uffff\1\115\1\131\1\124\2\uffff"+
        "\1\131\1\117\1\172\1\110\1\172\1\116\1\uffff\1\105\1\uffff\1\104"+
        "\1\122\1\137\1\172\1\117\1\uffff\1\131\1\124\1\172\1\110\1\uffff"+
        "\1\105\1\122\1\172\1\uffff";
    static final String DFA21_acceptS =
        "\1\uffff\1\1\1\uffff\1\3\1\4\1\5\10\uffff\1\23\1\24\1\25\1\uffff"+
        "\1\27\1\30\1\31\1\34\1\35\5\uffff\1\47\1\uffff\1\51\1\53\1\uffff"+
        "\1\55\14\uffff\1\32\1\17\1\33\1\20\1\uffff\1\52\1\26\5\uffff\1\50"+
        "\11\uffff\1\36\4\uffff\1\45\1\uffff\1\37\11\uffff\1\56\1\uffff\1"+
        "\54\2\uffff\1\40\7\uffff\1\22\2\uffff\1\43\5\uffff\1\21\21\uffff"+
        "\1\44\1\uffff\1\16\4\uffff\1\2\3\uffff\1\14\1\15\1\41\1\uffff\1"+
        "\46\4\uffff\1\13\1\42\6\uffff\1\7\1\uffff\1\6\5\uffff\1\11\4\uffff"+
        "\1\10\3\uffff\1\12";
    static final String DFA21_specialS =
        "\40\uffff\1\15\32\uffff\1\17\1\3\1\13\22\uffff\1\5\1\0\1\7\1\6\1"+
        "\1\1\uffff\1\14\20\uffff\1\12\1\20\1\2\14\uffff\1\11\1\4\13\uffff"+
        "\1\10\11\uffff\1\16\35\uffff}>";
    static final String[] DFA21_transitionS = {
            "\2\37\2\uffff\1\37\22\uffff\1\37\1\24\1\40\2\uffff\1\22\1\25"+
            "\1\41\1\4\1\5\1\20\1\16\1\1\1\17\1\36\1\21\12\35\1\3\1\uffff"+
            "\1\13\1\23\1\14\2\uffff\1\27\2\34\1\10\1\34\1\31\1\30\1\34\1"+
            "\7\2\34\1\2\1\32\2\34\1\6\1\34\1\33\1\11\1\12\6\34\4\uffff\1"+
            "\34\1\uffff\15\34\1\15\14\34\1\uffff\1\26",
            "",
            "\1\42\3\uffff\1\44\3\uffff\1\43",
            "",
            "",
            "",
            "\1\45\2\uffff\1\46",
            "\1\47\4\uffff\1\50",
            "\1\52\11\uffff\1\51",
            "\1\53",
            "\1\55\5\uffff\1\54",
            "\1\56",
            "\1\60",
            "\1\62",
            "",
            "",
            "",
            "\1\63\4\uffff\1\63",
            "",
            "",
            "",
            "",
            "",
            "\1\65",
            "\1\66",
            "\1\67",
            "\1\70",
            "\1\71",
            "",
            "\1\36\1\uffff\12\35\13\uffff\1\36\37\uffff\1\36",
            "",
            "",
            "\42\74\1\75\71\74\1\73\uffa3\74",
            "",
            "\1\76",
            "\1\77",
            "\1\100",
            "\1\101",
            "\1\102",
            "\1\103",
            "\12\34\7\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
            "\1\105",
            "\1\106",
            "\1\107",
            "\1\110",
            "\12\34\7\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
            "",
            "",
            "",
            "",
            "\1\112",
            "",
            "",
            "\12\34\7\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
            "\1\114",
            "\1\115",
            "\1\116",
            "\1\117",
            "",
            "\42\125\1\120\4\125\1\124\10\125\4\122\4\123\44\125\1\124\5"+
            "\125\1\124\3\125\1\124\7\125\1\124\3\125\1\124\1\125\1\124\1"+
            "\121\uff8a\125",
            "\42\74\1\126\71\74\1\73\uffa3\74",
            "\0\125",
            "\1\130",
            "\1\131",
            "\12\34\7\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
            "\1\133",
            "\1\134",
            "\1\135",
            "",
            "\1\136",
            "\1\137",
            "\1\140",
            "\1\141",
            "",
            "\12\34\7\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
            "",
            "\1\143",
            "\1\144",
            "\12\34\7\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
            "\1\146",
            "\42\74\1\126\71\74\1\73\uffa3\74",
            "\60\125\12\147\7\125\6\147\32\125\6\147\uff99\125",
            "\42\74\1\126\15\74\10\150\44\74\1\73\uffa3\74",
            "\42\74\1\126\15\74\10\151\44\74\1\73\uffa3\74",
            "\42\74\1\126\71\74\1\73\uffa3\74",
            "",
            "\0\125",
            "",
            "\1\152",
            "\12\34\7\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
            "",
            "\1\154",
            "\1\155",
            "\1\156",
            "\1\157",
            "\1\160",
            "\1\161",
            "\1\162",
            "",
            "\1\163",
            "\1\164",
            "",
            "\1\165",
            "\60\125\12\166\7\125\6\166\32\125\6\166\uff99\125",
            "\42\74\1\126\15\74\10\167\44\74\1\73\uffa3\74",
            "\42\74\1\126\71\74\1\73\uffa3\74",
            "\1\170",
            "",
            "\1\171",
            "\1\172",
            "\1\173",
            "\1\174",
            "\12\34\7\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
            "\1\176",
            "\12\34\7\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
            "\1\u0080",
            "\1\u0081",
            "\1\u0082",
            "\60\125\12\u0083\7\125\6\u0083\32\125\6\u0083\uff99\125",
            "\42\74\1\126\71\74\1\73\uffa3\74",
            "\12\34\7\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
            "\1\u0085",
            "\1\u0086",
            "\1\u0087",
            "\12\34\7\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
            "",
            "\12\34\7\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
            "",
            "\12\34\7\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
            "\1\u008b",
            "\12\34\7\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
            "\60\125\12\u008d\7\125\6\u008d\32\125\6\u008d\uff99\125",
            "",
            "\1\u008e",
            "\1\u008f\1\uffff\1\u0090",
            "\12\34\7\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
            "",
            "",
            "",
            "\12\34\7\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
            "",
            "\42\74\1\126\71\74\1\73\uffa3\74",
            "\1\u0094\11\uffff\1\u0093",
            "\1\u0095",
            "\1\u0096",
            "",
            "",
            "\1\u0097",
            "\1\u0098",
            "\12\34\7\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
            "\1\u009a",
            "\12\34\7\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
            "\1\u009c",
            "",
            "\1\u009d",
            "",
            "\1\u009e",
            "\1\u009f",
            "\1\u00a0",
            "\12\34\7\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
            "\1\u00a2\1\uffff\1\u00a3",
            "",
            "\1\u00a4",
            "\1\u00a5",
            "\12\34\7\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
            "\1\u00a7",
            "",
            "\1\u00a8",
            "\1\u00a9",
            "\12\34\7\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
            ""
    };

    static final short[] DFA21_eot = DFA.unpackEncodedString(DFA21_eotS);
    static final short[] DFA21_eof = DFA.unpackEncodedString(DFA21_eofS);
    static final char[] DFA21_min = DFA.unpackEncodedStringToUnsignedChars(DFA21_minS);
    static final char[] DFA21_max = DFA.unpackEncodedStringToUnsignedChars(DFA21_maxS);
    static final short[] DFA21_accept = DFA.unpackEncodedString(DFA21_acceptS);
    static final short[] DFA21_special = DFA.unpackEncodedString(DFA21_specialS);
    static final short[][] DFA21_transition;

    static {
        int numStates = DFA21_transitionS.length;
        DFA21_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA21_transition[i] = DFA.unpackEncodedString(DFA21_transitionS[i]);
        }
    }

    class DFA21 extends DFA {

        public DFA21(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 21;
            this.eot = DFA21_eot;
            this.eof = DFA21_eof;
            this.min = DFA21_min;
            this.max = DFA21_max;
            this.accept = DFA21_accept;
            this.special = DFA21_special;
            this.transition = DFA21_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | T__36 | T__37 | T__38 | T__39 | T__40 | T__41 | T__42 | T__43 | T__44 | T__45 | T__46 | T__47 | T__48 | T__49 | T__50 | T__51 | T__52 | T__53 | T__54 | T__55 | ID | INT | DOUBLE | COMMENT | WS | STRING | CHAR | STRING_LITERAL );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA21_81 = input.LA(1);

                        s = -1;
                        if ( ((LA21_81>='0' && LA21_81<='9')||(LA21_81>='A' && LA21_81<='F')||(LA21_81>='a' && LA21_81<='f')) ) {s = 103;}

                        else if ( ((LA21_81>='\u0000' && LA21_81<='/')||(LA21_81>=':' && LA21_81<='@')||(LA21_81>='G' && LA21_81<='`')||(LA21_81>='g' && LA21_81<='\uFFFF')) ) {s = 85;}

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA21_84 = input.LA(1);

                        s = -1;
                        if ( (LA21_84=='\"') ) {s = 86;}

                        else if ( (LA21_84=='\\') ) {s = 59;}

                        else if ( ((LA21_84>='\u0000' && LA21_84<='!')||(LA21_84>='#' && LA21_84<='[')||(LA21_84>=']' && LA21_84<='\uFFFF')) ) {s = 60;}

                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA21_105 = input.LA(1);

                        s = -1;
                        if ( (LA21_105=='\"') ) {s = 86;}

                        else if ( (LA21_105=='\\') ) {s = 59;}

                        else if ( ((LA21_105>='\u0000' && LA21_105<='!')||(LA21_105>='#' && LA21_105<='[')||(LA21_105>=']' && LA21_105<='\uFFFF')) ) {s = 60;}

                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA21_60 = input.LA(1);

                        s = -1;
                        if ( (LA21_60=='\"') ) {s = 86;}

                        else if ( (LA21_60=='\\') ) {s = 59;}

                        else if ( ((LA21_60>='\u0000' && LA21_60<='!')||(LA21_60>='#' && LA21_60<='[')||(LA21_60>=']' && LA21_60<='\uFFFF')) ) {s = 60;}

                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA21_119 = input.LA(1);

                        s = -1;
                        if ( (LA21_119=='\"') ) {s = 86;}

                        else if ( (LA21_119=='\\') ) {s = 59;}

                        else if ( ((LA21_119>='\u0000' && LA21_119<='!')||(LA21_119>='#' && LA21_119<='[')||(LA21_119>=']' && LA21_119<='\uFFFF')) ) {s = 60;}

                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA21_80 = input.LA(1);

                        s = -1;
                        if ( (LA21_80=='\"') ) {s = 86;}

                        else if ( (LA21_80=='\\') ) {s = 59;}

                        else if ( ((LA21_80>='\u0000' && LA21_80<='!')||(LA21_80>='#' && LA21_80<='[')||(LA21_80>=']' && LA21_80<='\uFFFF')) ) {s = 60;}

                        else s = 85;

                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA21_83 = input.LA(1);

                        s = -1;
                        if ( ((LA21_83>='0' && LA21_83<='7')) ) {s = 105;}

                        else if ( (LA21_83=='\"') ) {s = 86;}

                        else if ( (LA21_83=='\\') ) {s = 59;}

                        else if ( ((LA21_83>='\u0000' && LA21_83<='!')||(LA21_83>='#' && LA21_83<='/')||(LA21_83>='8' && LA21_83<='[')||(LA21_83>=']' && LA21_83<='\uFFFF')) ) {s = 60;}

                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA21_82 = input.LA(1);

                        s = -1;
                        if ( ((LA21_82>='0' && LA21_82<='7')) ) {s = 104;}

                        else if ( (LA21_82=='\"') ) {s = 86;}

                        else if ( (LA21_82=='\\') ) {s = 59;}

                        else if ( ((LA21_82>='\u0000' && LA21_82<='!')||(LA21_82>='#' && LA21_82<='/')||(LA21_82>='8' && LA21_82<='[')||(LA21_82>=']' && LA21_82<='\uFFFF')) ) {s = 60;}

                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA21_131 = input.LA(1);

                        s = -1;
                        if ( ((LA21_131>='0' && LA21_131<='9')||(LA21_131>='A' && LA21_131<='F')||(LA21_131>='a' && LA21_131<='f')) ) {s = 141;}

                        else if ( ((LA21_131>='\u0000' && LA21_131<='/')||(LA21_131>=':' && LA21_131<='@')||(LA21_131>='G' && LA21_131<='`')||(LA21_131>='g' && LA21_131<='\uFFFF')) ) {s = 85;}

                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA21_118 = input.LA(1);

                        s = -1;
                        if ( ((LA21_118>='0' && LA21_118<='9')||(LA21_118>='A' && LA21_118<='F')||(LA21_118>='a' && LA21_118<='f')) ) {s = 131;}

                        else if ( ((LA21_118>='\u0000' && LA21_118<='/')||(LA21_118>=':' && LA21_118<='@')||(LA21_118>='G' && LA21_118<='`')||(LA21_118>='g' && LA21_118<='\uFFFF')) ) {s = 85;}

                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA21_103 = input.LA(1);

                        s = -1;
                        if ( ((LA21_103>='0' && LA21_103<='9')||(LA21_103>='A' && LA21_103<='F')||(LA21_103>='a' && LA21_103<='f')) ) {s = 118;}

                        else if ( ((LA21_103>='\u0000' && LA21_103<='/')||(LA21_103>=':' && LA21_103<='@')||(LA21_103>='G' && LA21_103<='`')||(LA21_103>='g' && LA21_103<='\uFFFF')) ) {s = 85;}

                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA21_61 = input.LA(1);

                        s = -1;
                        if ( ((LA21_61>='\u0000' && LA21_61<='\uFFFF')) ) {s = 85;}

                        else s = 87;

                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA21_86 = input.LA(1);

                        s = -1;
                        if ( ((LA21_86>='\u0000' && LA21_86<='\uFFFF')) ) {s = 85;}

                        else s = 87;

                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA21_32 = input.LA(1);

                        s = -1;
                        if ( (LA21_32=='\\') ) {s = 59;}

                        else if ( ((LA21_32>='\u0000' && LA21_32<='!')||(LA21_32>='#' && LA21_32<='[')||(LA21_32>=']' && LA21_32<='\uFFFF')) ) {s = 60;}

                        else if ( (LA21_32=='\"') ) {s = 61;}

                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA21_141 = input.LA(1);

                        s = -1;
                        if ( (LA21_141=='\"') ) {s = 86;}

                        else if ( (LA21_141=='\\') ) {s = 59;}

                        else if ( ((LA21_141>='\u0000' && LA21_141<='!')||(LA21_141>='#' && LA21_141<='[')||(LA21_141>=']' && LA21_141<='\uFFFF')) ) {s = 60;}

                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA21_59 = input.LA(1);

                        s = -1;
                        if ( (LA21_59=='\"') ) {s = 80;}

                        else if ( (LA21_59=='u') ) {s = 81;}

                        else if ( ((LA21_59>='0' && LA21_59<='3')) ) {s = 82;}

                        else if ( ((LA21_59>='4' && LA21_59<='7')) ) {s = 83;}

                        else if ( (LA21_59=='\''||LA21_59=='\\'||LA21_59=='b'||LA21_59=='f'||LA21_59=='n'||LA21_59=='r'||LA21_59=='t') ) {s = 84;}

                        else if ( ((LA21_59>='\u0000' && LA21_59<='!')||(LA21_59>='#' && LA21_59<='&')||(LA21_59>='(' && LA21_59<='/')||(LA21_59>='8' && LA21_59<='[')||(LA21_59>=']' && LA21_59<='a')||(LA21_59>='c' && LA21_59<='e')||(LA21_59>='g' && LA21_59<='m')||(LA21_59>='o' && LA21_59<='q')||LA21_59=='s'||(LA21_59>='v' && LA21_59<='\uFFFF')) ) {s = 85;}

                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA21_104 = input.LA(1);

                        s = -1;
                        if ( ((LA21_104>='0' && LA21_104<='7')) ) {s = 119;}

                        else if ( (LA21_104=='\"') ) {s = 86;}

                        else if ( (LA21_104=='\\') ) {s = 59;}

                        else if ( ((LA21_104>='\u0000' && LA21_104<='!')||(LA21_104>='#' && LA21_104<='/')||(LA21_104>='8' && LA21_104<='[')||(LA21_104>=']' && LA21_104<='\uFFFF')) ) {s = 60;}

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 21, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

}