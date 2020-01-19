
/**
 * Exercise
 */

import java.util.Scanner;

public class Exercise {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.print("Select Automa: ");
        int automa = scan.nextInt();
        String query;
        switch (automa) {
        case '0':
            System.out.println("Digit a comment");
            query = scan.nextLine();
            if (CommentParse(q))
                System.out.print("Success");
            else
                System.out.print("Is not A comment");
            break;

        case '1':
            System.out.println("Digit an identifier");
            query = scan.nextLine();
            if (IDParse(q))
                System.out.println("This id is correct");
            else
                System.out.print("This id isn't correct");
            break;

        }
    }

    public static boolean CommentParse(String q) {
        int i = 0;
        int state = 0;
        q.toLowerCase();
        while (state >= 0 && i < q.length()) {
            char ch = q.charAt(i);
            switch (state) {
            case 0:
                if (ch == '/')
                    state = 1;
                else
                    state = 0;
                break;

            case 1:
                if (ch == '*')
                    state = 2;
                else
                    state = 0;
                break;

            case 2:
                if (ch == '*')
                    state = 3;
                else
                    state = 2;
                break;

            case 3:
                if (ch == '/')
                    state = 0;
                else
                    state = 2;
                break;

            }

            i++;
        }
        System.out.println("Done iterations: " + i);

        return state == 0;

    }

    public static boolean IDParse(String q) {
        int i = 0;
        int state = 0;
        while (state >= 0 && i < q.length()) {
            char ch = q.toLowerCase().charAt(i++);
            switch (state) {
            case 0:
                if (ch >= 'a' && ch <= 'z')
                    state = 2;
                else if (ch >= '0' && ch <= '9')
                    state = -1;
                else if (ch == '_')
                    state = 1;
                break;

            case 1:
                if (ch >= '0' && ch <= '9')
                    state = -1;
                else if (ch >= 'a' && ch <= 'z')
                    state = 2;
                else if (ch == '_')
                    state = -1;
                break;

            case 2:
                if (ch >= '0' && ch <= '9')
                    state = 2;
                else if (ch >= 'a' && ch <= 'z')
                    state = 2;
                else if (ch == '_')
                    state = 2;
                break;
            }
        }
        return state == 2;
    }

    public static boolean StudentParse(String query){
        int i = 0;
        int state = 0;
        while (state >= 0 && i < q.length()) {
            char ch = q.toLowerCase().charAt(i++);
            switch (state) {
                
            }
        }
        return state == 0;
    }

}