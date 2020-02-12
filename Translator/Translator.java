import java.io.*;
import java.nio.file.Paths;



import java.nio.file.*;

public class Translator {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count = 0;

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
            if (look.tag != Tag.EOF)
                move();
        } else
            error("syntax error");
    }

    public void prog() {
        if (look.tag == '(') {
            int lnext_prog = code.newLabel();
            stat(lnext_prog);
            code.emitLabel(lnext_prog);
            match(Tag.EOF);
            try {
                code.toJasmin();
            } catch (java.io.IOException e) {
                System.out.println("IO error\n");
            }
        } else {
            error("Error in prog");
        }
    }

    public void statlist(int lnext) {
        if (look.tag == '(') {
            stat(lnext);
            statlistp(lnext);
        } else {
            error("Error in statlist");
        }
    }

    public void statlistp(int lnext) {
        switch (look.tag) {
        case '(':
            lnext = code.newLabel();
            code.emitLabel(lnext);
            stat(lnext);
            statlistp(lnext);
            break;
        case ')':
            break;
        default:
            error("Error in statlistp");
            break;
        }
    }

    public void stat(int lnext) {
        switch (look.tag) {
        case '(':
            match('(');
            statp(lnext);
            match(')');
            break;
        default:
            error("Error in stat");
            break;
        }
    }

    public void statp(int lnext) {
        switch (look.tag) {
        case '=':
            match('=');
            if (look.tag == Tag.ID) {
                int read_id_addr = st.lookupAddress(((Word) look).lexeme);
                if (read_id_addr == -1) {
                    read_id_addr = count;
                    st.insert(((Word) look).lexeme, count++);
                }
                match(Tag.ID);
                expr();
                code.emit(OpCode.istore, read_id_addr);
            } else
                error("Error in stat, Tag.ID");
            break;
        case Tag.DO:
            match(Tag.DO);
            statlist(lnext);
            break;
            
            case Tag.COND:
            match(Tag.COND);
            int true_label = code.newLabel();
            int false_label = code.newLabel();
            bexpr(true_label,false_label);
            code.emitLabel(true_label);
            lnext = code.newLabel();
            stat(lnext);
            code.emit(OpCode.GOto,lnext);
            code.emitLabel(false_label);
            elseopt(lnext);
            code.emitLabel(lnext);
            break;

        case Tag.WHILE:
            match(Tag.WHILE);
            int while_label = code.newLabel();
            int exit_label = code.newLabel();
            bexpr(while_label,exit_label);
            code.emitLabel(while_label);
            stat(while_label);
            code.emit(OpCode.GOto, lnext);
            code.emitLabel(exit_label);
            break;

        case Tag.PRINT:
            match(Tag.PRINT);
            switch (look.tag) {
            case Tag.NUM:
                NumberTok num = (NumberTok) look;
                code.emit(OpCode.ldc, num.num);
                match(Tag.NUM);
                code.emit(OpCode.invokestatic, 1);
                break;

            case Tag.ID:
                code.emit(OpCode.iload, st.lookupAddress(((Word) look).lexeme));
                match(Tag.ID);
                code.emit(OpCode.invokestatic, 1);
                break;

            case '(':
                exprlist(null);
                code.emit(OpCode.invokestatic, 1);
                break;
            }
            break;
        case Tag.READ:
            match(Tag.READ);
            if (look.tag == Tag.ID) {
                int read_id_addr = st.lookupAddress(((Word) look).lexeme);
                if (read_id_addr == -1) {
                    read_id_addr = count;
                    st.insert(((Word) look).lexeme, count++);
                }
                match(Tag.ID);
                code.emit(OpCode.invokestatic, 0);
                code.emit(OpCode.istore, read_id_addr);
            } else
                error("Grammar Error");
            break;
        default:
            error("Error in statp");
        }

    }

    public void elseopt(int lnext) {
        if (look.tag == '(') {
            match('(');
            match(Tag.ELSE);
            stat(lnext);
            match(')');
        } 
    }

    public void bexpr(int ltrue, int lfalse) {
        switch (look.tag) {
        case '(':
            match('(');
            bexprp(ltrue,lfalse);
            match(')');
            break;
        default:
            error("Error in bexpr");
            break;
        }
    }

    public void bexprp(int ltrue, int lfalse) {
        if (look.tag == Tag.RELOP) {
            String cond = (((Word) look).lexeme);
            match(Tag.RELOP);
            switch (cond) {
            case "==":
                expr();
                expr();
                code.emit(OpCode.if_icmpeq, ltrue);
                code.emit(OpCode.GOto,lfalse);
                break;
            case "<":
                expr();
                expr();
                code.emit(OpCode.if_icmplt, ltrue);
                code.emit(OpCode.GOto,lfalse);
                break;
            case ">":
                expr();
                expr();
                code.emit(OpCode.if_icmpgt, ltrue);
                code.emit(OpCode.GOto,lfalse);
                break;
            case "<=":
                expr();
                expr();
                code.emit(OpCode.if_icmple, ltrue);
                code.emit(OpCode.GOto,lfalse);
                break;
            case ">=":
                expr();
                expr();
                code.emit(OpCode.if_icmpge, ltrue);
                code.emit(OpCode.GOto,lfalse);
                break;
            case "<>":
                expr();
                expr();
                code.emit(OpCode.if_icmpne, ltrue);
                code.emit(OpCode.GOto,lfalse);
                break;
            }
        } else if (look.tag == Tag.AND) {
            match(Tag.AND);
            int true_label = code.newLabel();
            bexpr(true_label,lfalse);
            code.emitLabel(true_label);
            bexpr(ltrue,lfalse);
            

        } else if (look.tag == Tag.OR) {
            match(Tag.OR);
            int false_label = code.newLabel();
            bexpr(ltrue,false_label);
            code.emitLabel(false_label);
            bexpr(ltrue,false_label);
        } 
        else if(look.tag == '!'){
            match('!');
            
        }
        else {
            error("Error in bexrprp");
        }
    }

    public void expr() {
        switch (look.tag) {
        case Tag.NUM:
            NumberTok num = (NumberTok) look;
            code.emit(OpCode.ldc, num.num);
            match(Tag.NUM);
            break;
        case Tag.ID:
            code.emit(OpCode.iload, st.lookupAddress(((Word) look).lexeme));
            match(Tag.ID);
            break;
        case '(':
            match('(');
            exprp();
            match(')');
            break;
        default:
            error("error in expr");
            break;
        }
    }

    private void exprp() {
        String optype;
        switch (look.tag) {

        case '+':
            match('+');
            optype = "plus";
            exprlist(optype);
            break;
        case '-':
            match('-');
            expr();
            expr();
            code.emit(OpCode.isub);
            break;
        case '*':
            match('*');
            optype = "times";
            exprlist(optype);
            break;
        case '/':
            match('/');
            expr();
            expr();
            code.emit(OpCode.idiv);
            break;

        default:
            error("Error in exprp");
        }
    }

    public void exprlist(String optype) {
        if (look.tag == '(' || look.tag == Tag.NUM || look.tag == Tag.ID) {
            expr();
            exprlistp(optype);
        } else {
            error("Error in exprlist");
        }
    }

    public void exprlistp(String optype) {
        switch (look.tag) {
        case ')':
            break;

        default:
            expr();
            if(optype == null){}
            else if (optype.equals("plus")) {
                code.emit(OpCode.iadd);
            } else if (optype.equals("times")) {
                code.emit(OpCode.imul);
            }
            exprlistp(optype);
            break;
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        Path currentDir = Paths.get(".");
        currentDir = currentDir.normalize();
        String path = currentDir.toAbsolutePath() + "\\Translator\\try";
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Translator translator = new Translator(lex, br);
            translator.prog();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}