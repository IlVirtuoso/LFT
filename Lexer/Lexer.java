import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;





public class Lexer {

    public static int line = 1;
    private char peek = ' ';
    
    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    public Token lexical_scan(BufferedReader br) {
        while (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r') {
            if (peek == '\n') line++;
            readch(br);
        }
        
        switch (peek) {
            case '!':
                peek = ' ';
                return Token.not;

    // ... gestire i casi di (, ), {, }, +, -, *, /, =, ; ... //
            case '(':
                peek = ' ';
                return Token.lpt;

            case ')':
            peek = ' ';
            return Token.rpt;

            case '{':
            peek = ' ';
            return Token.lpg;

            case '}':
            peek = ' ';
            return Token.rpg;

            case '+':
            peek = ' ';
            return Token.plus;

            case '-':
            peek = ' ';
            return Token.minus;

            case '*':
            peek = ' ';
            return Token.mult;

            case '/':
            peek = ' ';
            readch(br);
            if(peek == '*'){
                int state = 0;
                peek = ' ';
                while(state != 2){
                    switch(state){
                        case 0:
                        readch(br);
                        if(peek == '*') {
                            state = 1; 
                            peek = ' ';
                        }
                        else if(peek == -1){
                            state = 2;
                        }
                        else{
                            state = 0;
                            peek = ' ';
                        }
                        break;
    
                        case 1:
                        readch(br);
                        if(peek == '/'){
                            state = 2;
                            peek = ' ';
                        }
                        else{
                            state = 0;
                            peek = ' ';
                        }
                    }
                }
                return lexical_scan(br);
            }
            else if(peek == '/'){
                while(peek != -1 && peek != '\n'){
                    peek = ' ';
                    readch(br);
                }
                return lexical_scan(br);
            }
            else{
                return Token.div;
            }

            case ';':
            peek = ' ';
            return Token.semicolon;

            case '&':
                readch(br);
                if (peek == '&') {
                    peek = ' ';
                    return Word.and;
                } else {
                    System.err.println("Erroneous character"
                            + " after & : "  + peek );
                    return null;
                }

   
            case '|':
            readch(br);
            if(peek == '|'){
                peek = ' ';
                return Word.or;
            }
            else{
                System.err.println("Error");
                return null;
            }

            case '<':
            readch(br);
            if(peek == '='){
                peek = ' ';
                return Word.le;
            }
            else if(peek == '>'){
                peek = ' ';
                return Word.ne;
            }
            else{
                peek = ' ';
                return Word.lt;
            }

            case '>':
            readch(br);
            if(peek == '='){
                peek = ' ';
                return Word.ge;
            }
            else{
                peek = ' ';
                return Word.gt;
            }

            case '=':
            readch(br);
            if(peek == '='){
                peek = ' ';
                return Word.eq;
            }
            else{
                peek = ' ';
                return Token.assign;
            }
          
            case (char)-1:
                return new Token(Tag.EOF);

            default:
                if (Character.isLetter(peek) || peek == '_') {
                    String parsed = "";
                    while(peek>='a' && peek<='z' || peek >= '0' && peek<= '9' || peek == '_'){
                        parsed = parsed + peek;
                        peek = ' ';
                        readch(br);
                    }
                    if(parsed.equals("while")){
                        return Word.whiletok;
                    }
                    else if(parsed.equals("then")){
                        return Word.then;
                    }
                    else if(parsed.equals("else")){
                        return Word.elsetok;
                    }
                    else if(parsed.equals("do")){
                    return Word.dotok;
                    }
                    else if(parsed.equals("print")){
                        return Word.print;
                    }
                    else if(parsed.equals("read")){
                        return Word.print;
                    }
                    else if(parsed.equals("when")){
                        return Word.when;
                    }
                    else if(parsed.equals("cond")){
                        return Word.cond;
                    }
                    else{
                        boolean onlyunderscore = true;
                        for(int i = 0; i < parsed.length(); i++){
                            if(parsed.charAt(i) != '_'){
                                onlyunderscore = false;
                            }
                        }
                        if(onlyunderscore){
                            throw new IllegalArgumentException("identifiers cannot have only _");
                        }
                        else{
                            return new Word(Tag.ID, parsed);
                        }
                    }
                    
                } else if (Character.isDigit(peek)) {
                    String number = "";
                    while(peek>='a' && peek<='z' || peek >= '0' && peek<= '9' || peek == '_'){
                        if(peek <= '9' && peek >= '0'){
                        number = number + peek;
                        peek = ' ';
                        readch(br);
                        }
                        else{
                            throw new IllegalArgumentException("Identificator can't start with number");
                        }
                    }
                    return new NumberTok(Integer.parseInt(number));

                } else {
                        System.err.println("Erroneous character: " 
                                + peek );
                        return null;
                }
         }
    }
		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "./try"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Token tok;
            do {
                tok = lex.lexical_scan(br);
                System.out.println("Scan: " + tok);
            } while (tok.tag != Tag.EOF);
            br.close();
        } catch (IOException e) {e.printStackTrace();}    
    }

}