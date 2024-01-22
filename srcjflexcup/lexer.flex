package es4;
import java_cup.runtime.Symbol;

%%

%class Lexer
%unicode 
%cup

%{
// Dichiarazioni di variabili e funzioni per il lexer
StringBuffer string = new StringBuffer();  // Buffer per memorizzare il contenuto delle stringhe
String errorMessage = new String();  // Messaggio di errore per i commenti non chiusi
%}

// Definizione di variabili regolari per il lexer
Identifier = [[:alpha:]_][[:alnum:]_]*  // Pattern per identificatori
LineTerminator = \r|\n|\r\n  // Pattern per terminatori di riga
InputCharacter = [^\r\n]  // Pattern per caratteri di input
WhiteSpace = {LineTerminator} | [ \t\f]  // Pattern per spazi vuoti
Letter = [A-Za-z]  // Pattern per lettere
Digit = [0-9]  // Pattern per cifre
Id = {Letter}({Letter} | {Digit})*  // Pattern per identificatori
ScientificNotation = [-+]?({Digit}+("."{Digit}*)?|"."{Digit}+)(["eE"]["-+"]?{Digit}+)?  // Pattern per numeri scientifici

/* comments */
Comment = {TraditionalComment} | {EndOfLineComment} | {DocumentationComment}
TraditionalComment = "%" [^*] ~"%"
// Comment can be the last line of the file, without line terminator.
EndOfLineComment = "//" {InputCharacter}* {LineTerminator}?
DocumentationComment = "/**" {CommentContent} "*"+ "/"
CommentContent = ( [^*] | \*+ [^/*] )*
ErrorComment = "%" [^*]

// Definizione di uno stato per gestire le stringhe
%state STRING_CONST
// Stato iniziale
%state YYINITIAL
%state NUMBER

%%

/* keywords */
<YYINITIAL> "if" { return new Symbol(sym.IF); }
<YYINITIAL> "true" { return new Symbol(sym.TRUE); }
<YYINITIAL> "false" { return new Symbol(sym.FALSE); }
<YYINITIAL> "var" { return new Symbol(sym.VAR); }
<YYINITIAL> "\\" { return new Symbol(sym.ENDVAR); }
<YYINITIAL> "else" { return new Symbol(sym.ELSE); }
<YYINITIAL> "then" { return new Symbol(sym.THEN); }
<YYINITIAL> "while" { return new Symbol(sym.WHILE); }
<YYINITIAL> "endwhile" { return new Symbol(sym.ENDWHILE); }
<YYINITIAL> "do" { return new Symbol(sym.DO); }
<YYINITIAL> "endif" { return new Symbol(sym.ENDIF); }
<YYINITIAL> "elseif" { return new Symbol(sym.ELIF); }
<YYINITIAL> "endfunc" { return new Symbol(sym.ENDFUNCTION); }
<YYINITIAL> "real" { return new Symbol(sym.REAL); }
<YYINITIAL> "integer" { return new Symbol(sym.INTEGER); }
<YYINITIAL> "string" { return new Symbol(sym.STRING ); }
<YYINITIAL> "boolean" { return new Symbol(sym.BOOLEAN ); }
<YYINITIAL> "return" { return new Symbol(sym.RETURN ); }
<YYINITIAL> "func" { return new Symbol(sym.FUNCTION ); }
<YYINITIAL> "proc" { return new Symbol(sym.PROCEDURE ); }
<YYINITIAL> "endproc" { return new Symbol(sym.ENDPROCEDURE  ); }
<YYINITIAL> "out" { return new Symbol(sym.OUT  ); }



<YYINITIAL> {

\" { string.setLength(0); yybegin(STRING_CONST); }
{Digit} { string.setLength(0); string.append(yytext()); yybegin(NUMBER); }

/* operators */
"^=" { return new Symbol(sym.ASSIGN); }
"=" { return new Symbol(sym.EQ); }
"+" { return new Symbol(sym.PLUS); }
"-" { return new Symbol(sym.MINUS); }
"*" { return new Symbol(sym.TIMES); }
"/" { return new Symbol(sym.DIV); }
"<" {return new Symbol(sym.LT);}
">" {return new Symbol(sym.GT);}
">=" {return new Symbol(sym.GE);}
"<=" {return new Symbol(sym.LE);}
"!=" {return new Symbol(sym.NE);}
"->" {return new Symbol(sym.TYPERETURN);}
"-->" {return new Symbol(sym.WRITE);}
"<--" {return new Symbol(sym.READ);}
"-->!" {return new Symbol(sym.WRITERETURN);}
"$" {return new Symbol(sym.DOLLARSIGN );}
"<>" {return new Symbol(sym.NE );}
"&&" {return new Symbol(sym.AND);}
"||" {return new Symbol(sym.OR);}
"!" {return new Symbol(sym.NOT);}
"@" {return new Symbol(sym.REF);}

/* separator */
"," { return new Symbol(sym.COMMA); }
";" { return new Symbol(sym.SEMI); }
"(" { return new Symbol(sym.LPAR); }
")" { return new Symbol(sym.RPAR); }
":" { return new Symbol(sym.COLON); }

/* identifiers */
{Id} { return new Symbol(sym.ID,yytext());}

/* comments */
{Comment} { /* ignore */ }
{ErrorComment} { errorMessage = "Commento non chiuso: " + yytext();
         return new Symbol(sym.error, errorMessage);
      } //throw  Error(" commento non chiuso ");
/* whitespace */
{WhiteSpace} { /* ignore */ }

}

// Regole per lo stato STRING_CONST
<STRING_CONST> {
    \" {
        yybegin(YYINITIAL);
        return new Symbol(sym.STRING_CONST,string.toString());
    }
    [^\n\r\"\\]+ { string.append( yytext() ); }
    \\t { string.append('\t'); }
    \\n { string.append('\n'); }
    \\r { string.append('\r'); }
    \\\" { string.append('\"');}
    \\ { string.append('\\'); }
    \\[^tnr\"\\] {
          yybegin(YYINITIAL);  /* Torna allo stato iniziale */
          return new Symbol(sym.error, "Carattere di escape non valido: " + yytext());
      }
    {WhiteSpace} { string.append(yytext()); }
    /* Regola per gestire una stringa non chiusa prima della fine del file */
    <<EOF>> {
        yybegin(YYINITIAL);  /* Torna allo stato iniziale */
        return new Symbol(sym.error, "Stringa costante non completata");
    }
}

// Regole per lo stato NUMBER
<NUMBER> {
    [0-9] {
              if(string.toString().endsWith("E")){
                   yybegin(YYINITIAL);  /* Torna allo stato iniziale */
                   return new Symbol(sym.error, "Numero mal formattato (Inserire segno esponente).");
              }
              string.append( yytext() );
      }
    [.] {
          if(string.toString().contains(".")){
               yybegin(YYINITIAL);  /* Torna allo stato iniziale */
               return new Symbol(sym.error, "Numero mal formattato ('.' già inserito).");
          }
          string.append( yytext() );
      }
    [E] {
          if(string.toString().contains("E") || !string.toString().contains(".") || string.toString().endsWith(".")){
               yybegin(YYINITIAL);  /* Torna allo stato iniziale */
               return new Symbol(sym.error, "Numero mal formattato (E inseribile solo una volta e dopo il '.' + parte decimale).");
          }
          string.append( yytext() );
      }
    [+-] {
          if(!string.toString().endsWith("E")){
               yybegin(YYINITIAL);  /* Torna allo stato iniziale */
               return new Symbol(sym.error, "Numero mal formattato (segno esponente inseribile dopo la E).");
          }
          string.append( yytext() );
      }

    {WhiteSpace} {
              if(   string.toString().endsWith(".") ||
                    string.toString().endsWith("E") ||
                    string.toString().endsWith("+") ||
                    string.toString().endsWith("-")){
                    yybegin(YYINITIAL);  /* Torna allo stato iniziale */
                   return new Symbol(sym.error, "Numero non terminato");
              }
               yybegin(YYINITIAL);  /* Torna allo stato iniziale */
               return new Symbol(sym.REAL_CONST,string.toString());
           }

    [;,] {
                      if(   string.toString().endsWith(".") ||
                            string.toString().endsWith("E") ||
                            string.toString().endsWith("+") ||
                            string.toString().endsWith("-")){
                            yybegin(YYINITIAL);  /* Torna allo stato iniziale */
                           return new Symbol(sym.error, "Numero non terminato");
                      }
                       yybegin(YYINITIAL);  /* Torna allo stato iniziale */
                       yypushback(1);
                       return new Symbol(sym.REAL_CONST,string.toString());
                   }



    /* Regola per gestire una stringa non chiusa prima della fine del file */
    <<EOF>> {
           yybegin(YYINITIAL);  /* Torna allo stato iniziale */

          if(   string.toString().endsWith(".") ||
                string.toString().endsWith("E") ||
                string.toString().endsWith("+") ||
                string.toString().endsWith("-")){
               return new Symbol(sym.error, "Numero non terminato a fine file");
          }
           return new Symbol(sym.REAL_CONST,string.toString());
       }
}


/* Regola fallback per gestire caratteri non validi */
[^] { throw new Error("Illegal character <"+yytext()+">"); }


/*REAL_CONST espressione per numero reale
INTEGER_CONST  espressione per numero intero*/

