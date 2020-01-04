import java.io.*;

public class ParserPrefix {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public ParserPrefix(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() {
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) {
	throw new Error("near line " + lex.line + ": " + s + "  on token :" + look.tag);
    }

    void match(int t) {
	if (look.tag == t) {
	    if (look.tag != Tag.EOF) move();
	} else error("syntax error");
    }

    public void start() {
    if(look.tag == '('){
        stat();
	    match(Tag.EOF);
    }
    else{
        error("Error in start");
    }
    
	
    }

    //sono tutti da completare ,ricordati che tutti gli operatori sono in notazione prefissa
    public void statlist(){
        stat();
        statlistp();
    }

    public void statlistp(){
        switch(look.tag){

            case '(':
            stat();
            statlistp();

            case ')':
            break;

            default:
            error("error in statlistp");
            break;
        }
    }

    public void stat(){
        switch(look.tag){
            case '(':
            match('(');
            statp();
            match(')');
            break;

            default:
            error("error in stat");
            break;
        }
    }

    public void statp(){
        switch(look.tag){
            case Tag.COND:
            match(Tag.COND);
            bexpr();
            stat();
            elseopt();
            break;

            case Tag.WHILE:
            match(Tag.WHILE);
            bexpr();
            stat();
            break;

            case Tag.DO:
            match(Tag.DO);
            statlist();
            break;

            case Tag.PRINT:
            match(Tag.PRINT);
            exprlist();
            break;

            case Tag.READ:
            match(Tag.READ);
            match(Tag.ID);
            break;

            case '=':
            match('=');
            match(Tag.ID);
            expr();
            break;
        }
    }

    public void elseopt(){
        switch(look.tag){
            case '(':
            match('(');
            match(Tag.ELSE);
            stat();
            match(')');

            case ')':

            break;

            default:
            error("error in elseopt");
            break;
        }
    }

    public void bexpr(){
        switch(look.tag){
            case '(':
            match('(');
            bexprp();
            match(')');
            break;

            default:
            error("error in bexpr");
            break;
        }
    }


    public void bexprp(){
        switch(look.tag){
            case Tag.RELOP:
            match(Tag.RELOP);
            expr();
            expr();
            break;

            default:
            error("Error in bexprp");
            break;
        }
    }

    public void expr(){
        switch(look.tag){
            case Tag.NUM:
            match(Tag.NUM);
            break;

            case Tag.ID:
            match(Tag.ID);
            break;

            case '(':
            match('(');
            exprp();
            match(')');
            break;



            default:
            error("error on expr");
            break;
        }
    }

    public void exprp(){
        switch(look.tag){
            case '+':
            match('+');
            exprlist();
            break;

            case '-':
            match('-');
            expr();
            expr();
            break;

            case '*':
            match('*');
            exprlist();
            break;

            case '/':
            match('/');
            expr();
            expr();
            break;

            default:
            error("error in exprp");
            break;
        }
    }

    public void exprlist(){
        expr();
        exprlistp();
    }


    public void exprlistp(){
        switch(look.tag){
            case ')':

            break;

            default:
            expr();
            exprlistp();
            break;
        }
    }
		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "C:\\Users\\Virtuoso\\Desktop\\programmazione\\LFT\\Lexer\\input"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            ParserPrefix parser = new ParserPrefix(lex, br);
            parser.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}