/**
 * Exercise
 */
public class Exercise {

    public static void main(String[] args) {
        System.out.print(automa("aaa/*/aa") ? "OK" : "ERROR");
    }

    public static boolean automa(String q){
        int i = 0;
        int state = 0;
        q.toLowerCase();
        while(state >= 0 && i < q.length()){
            char ch = q.charAt(i);
            switch(state){
                case 0:
                if(ch == '/') state = 1;
                else state = 0;
                break;
                
                case 1:
                if(ch == '*') state = 2;
                else state = 0;
                break;

                case 2:
                if(ch == '*') state = 3;
                else state = 2;
                break;

                case 3:
                if(ch == '/') state = 0;
                else state = 2;
                break;

                
            }

            i++;
        }
        System.out.println("Done iterations: "+i);
        
        return state == 0;
        
    }
}