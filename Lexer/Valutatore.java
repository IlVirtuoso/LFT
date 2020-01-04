import java.io.*; 

public class Valutatore {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Valutatore(Lexer l, BufferedReader br) { 
	lex = l; 
	pbr = br;
	move(); 
    }
   
    void move() { 
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) { 
        throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {
        if (look.tag == t) {
            if (look.tag != Tag.EOF) move();
        } else error("syntax error");
    }


    public void start() { 
        int expr_val = 0;
        if(look.tag == Tag.NUM || look.tag == '('){
            expr_val = expr();
            System.out.println(expr_val);
            match(Tag.EOF);
            
           
        }
        else{
            error("error in start");
        }
    }

    private int expr() { 
	    int term_val, exprp_val;
        
        if(look.tag == Tag.EOF){
            error("error in expr");
        }
        
    	term_val = term();
	    exprp_val = exprp(term_val);
        
	    return exprp_val;
    }

    private int exprp(int exprp_i) {
	    int term_val = 0, exprp_val = 0;
	    switch (look.tag) {
	    case '+':
            match('+');
            term_val = term();
            exprp_val = exprp(exprp_i + term_val);
            break;

    	
        case '-':
        match('-');
        term_val = term();
        exprp_val = exprp(exprp_i - term_val);
        break;


        default:
        exprp_val = exprp_i;
        break;
        }
        return exprp_val;
    }

    private int term() { 
        int termp_i = 0 , termp_val = 0;
        switch(look.tag){
            case '(':
            case Tag.NUM:
            termp_i = fact();
            termp_val = termp(termp_i);
            break;
        }
        return termp_val; 
    }
    
    private int termp(int termp_i) { 
        int termp1_i = 0 , termp_val = 0;
        switch(look.tag){
            case '*':
            match('*');
            termp1_i = termp_i * fact();
            termp_val = termp(termp1_i);
            break;

            case '/':
            match('/');
            termp1_i = termp_i / fact();
            termp_val = termp(termp1_i);
            break;

            default:
            termp_val = termp_i;
        }
        return termp_val;
    }
    
    private int fact() { 
        int fact_val = 0;
        switch(look.tag){
            case '(':
            match('(');
            fact_val = expr();
            match(')');
            break;

            case Tag.NUM:
            NumberTok tok = (NumberTok) look;
            fact_val = tok.num;
            match(Tag.NUM);
            break;

            default:
            error("Error in fact");
            break;
        }
        return fact_val;
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "C:\\Users\\matte\\Desktop\\programmazione\\LFT\\Lexer\\try"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Valutatore valutatore = new Valutatore(lex, br);
            valutatore.start();
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}