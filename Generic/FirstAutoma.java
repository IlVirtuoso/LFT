/**
 * FirstAutoma
 */
public class FirstAutoma {



    public static boolean parse(String q){
        int i = 0;
        int state = 0;
        while(state >= 0 && i < q.length()){
            char ch = q.toLowerCase().charAt(i++);
            switch(state){
                case 0:
                if(ch >= 'a' && ch <= 'z') state = 2;
                else if(ch >= '0' && ch <= '9') state = -1;
                else if(ch == '_') state = 1;
                break;

                case 1:
                if(ch >= '0' && ch <= '9') state = -1;
                else if(ch >= 'a' && ch <= 'z') state = 2;
                else if(ch == '_') state = -1;
                break;

                case 2:
                if(ch >= '0' && ch <= '9') state = 2;
                else if(ch >= 'a' && ch <= 'z') state = 2;
                else if(ch == '_') state = 2;
                break;
            }
        }
        return state == 2;
    }
    public static void main(String[] args) {
        System.out.print(parse("221B") ? "OK" : "NOPE");
        
    }
}