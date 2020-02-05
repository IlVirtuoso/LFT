import java.io.*;





public class Translator {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;
    
    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count=0;

    public Translator(Lexer l, BufferedReader br) {
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
        } else error("syntax error while parsing " + look);
    }

    public void prog() {        
	if(look.tag == '('){
        int lnext_prog = code.newLabel();
        stat(lnext_prog);
        code.emitLabel(lnext_prog);
        match(Tag.EOF);
        try {
        	code.toJasmin();
        }
        catch(java.io.IOException e) {
        	System.out.println("IO error\n");
        }
    }
    else {
         error("Error in start");
        }
    }

    public void statlist(int lnext){
        stat(lnext);
        statlistp(lnext);
    }

    public void statlistp(int lnext){
        switch(look.tag){
            case '(':
            lnext = code.newLabel();
            code.emitLabel(lnext);
            stat(lnext);
            statlistp(lnext);
            break;

            case ')':
            
            break;


            default:
            error("error in statlistp");
            break;
        }
    }

    public void stat(int lnext){
        switch(look.tag){
            case '(':
            match('(');
            statp(lnext);
            match(')');
            break;

            default:
            error("error in stat after read" + look);
            break;
        }
    }

    public void statp(int lnext) {
        switch(look.tag) {
            case '=':
            match('=');
            if(look.tag == Tag.ID){
                int read_id_addr = st.lookupAddress(((Word)look).lexeme);
                if(read_id_addr == -1){
                    read_id_addr = count;
                    st.insert(((Word)look).lexeme, count++);
                }
                match(Tag.ID);
                expr();
                code.emit(OpCode.istore, read_id_addr);
            }
            else{
                error("error in stat " + look + " unsepected token after assign");
            }
            break;

            case Tag.DO:
            match(Tag.DO);
            statlist(lnext);
            break;

            case Tag.COND:
            match(Tag.COND);
            int true_label = code.newLabel();
            int false_label = code.newLabel();
            bexpr(true_label);
            code.emit(OpCode.GOto,false_label);
            code.emitLabel(true_label);
            stat(true_label);
            elseopt(false_label);
            break;

            case Tag.WHILE:
            match(Tag.WHILE);
            int cicle_label = lnext;
            int while_label = code.newLabel();
            int exit_label = code.newLabel();           
            bexpr(while_label);
            code.emit(OpCode.GOto,exit_label);
            code.emitLabel(while_label);
            stat(while_label);
            code.emit(OpCode.GOto,cicle_label);
            code.emitLabel(exit_label);
            break;

            case Tag.PRINT:
            match(Tag.PRINT);
            exprlist();
            code.emit(OpCode.invokestatic, 1);
            break;

            case Tag.READ:
                match(Tag.READ);
                if (look.tag==Tag.ID) {
                    int read_id_addr = st.lookupAddress(((Word)look).lexeme);
                    if (read_id_addr== -1) {
                        read_id_addr = count;
                        st.insert(((Word)look).lexeme,count++);
                    }                    
                    match(Tag.ID);
                    code.emit(OpCode.invokestatic,0);
                    code.emit(OpCode.istore,read_id_addr);   
                }
                else
                    error("Error in grammar (stat) after read with " + look);
                break;

                default:
                error("Error in stat unexpected token" + look + "while parsing");
        }
     }

     public void elseopt(int lnext){
         if(look.tag == '('){
             match('(');
             match(Tag.ELSE);
             code.emitLabel(lnext);
             stat(lnext);
             match(')');
         }
         else{
             code.emitLabel(lnext);
         }
     }

     public void bexpr(int lnext){
         switch(look.tag){
         case '(':
            match('(');
            bexprp(lnext);
            match(')');
            break;
        default:
        error("Error in bexpr while parsing " + look);
         break;

         }
     }

     public void bexprp(int lnext){
         if(look.tag == Tag.RELOP){
             String cond = (((Word)look).lexeme);
             match(Tag.RELOP);
             switch(cond){
                 case "<":
                 expr();
                 expr();
                 code.emit(OpCode.if_icmplt,lnext);
                 break;

                 case "==":
                 expr();
                 expr();
                 code.emit(OpCode.if_icmpeq,lnext);
                 break;

                 case ">":
                 expr();
                 expr();
                 code.emit(OpCode.if_icmpgt,lnext);
                 break;

                 case "<=":
                 expr();
                 expr();
                 code.emit(OpCode.if_icmple,lnext);
                 break;

                 case ">=":
                 expr();
                 expr();
                 code.emit(OpCode.if_icmpge,lnext);
                 break;

                 case "<>":
                 expr();
                 expr();
                 code.emit(OpCode.if_icmpne,lnext);
                 break;
                 
             }
            
         }
         else if(look.tag == Tag.AND){
             match(Tag.AND);
             bexpr(lnext);
             bexpr(lnext);
         }
         else if(look.tag == Tag.OR){
             match(Tag.OR);
             bexpr(lnext);
             bexpr(lnext);
         }
         else{
             error("Error in bexrpr while parsing " + look);
         }
     }

     public void expr(){
        switch(look.tag){
            case Tag.NUM:
            NumberTok num = (NumberTok) look;
            code.emit(OpCode.ldc, num.num);
            match(Tag.NUM);
            break;

            case Tag.ID:
            code.emit(OpCode.iload,st.lookupAddress(((Word)look).lexeme));
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

    private void exprp() {
        switch(look.tag) {
            case '+':
                match('+');
                exprlist();
                code.emit(OpCode.iadd);
                break;
            case '-':
                match('-');
                expr();
                expr();
                code.emit(OpCode.isub);
                break;
            case '*':
                match('*');
                exprlist();
                code.emit(OpCode.imul);
                break;
            case '/':
                match('/');
                expr();
                expr();
                code.emit(OpCode.idiv);
                break;

            default:
            error("Error in exprp while parsing: " + look);
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
        String path = "C:\\Users\\matte\\Desktop\\programmazione\\LFT\\Translator\\try"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Translator translator = new Translator(lex, br);
            translator.prog();
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }

}