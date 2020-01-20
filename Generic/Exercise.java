
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
        case 0:
            System.out.println("Digit a comment");
            query = scan.next();
            if (CommentParse(query))
                System.out.print("Success");
            else
                System.out.print("Is not A comment");
            break;

        case 1:
            System.out.println("Digit an identifier");
            query = scan.next();
            if (IDParse(query))
                System.out.println("This id is correct");
            else
                System.out.print("This id isn't correct");
            break;

        case 2:
            System.out.println("Digit a Student");
            query = scan.next();
            if (StudentParse(query))
                System.out.println("You are of turn T2 or T3");
            else
                System.out.println("Sorry you are not in either of those turns");
        }
        scan.close();
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

    public static Boolean StudentParse(String q) {
        int i = 0;
        int state = 0;
        while (state >= 0 && i < q.length()) {
            char ch = q.toLowerCase().charAt(i++);
            switch (state) {
            case 0:
                if (ch >= 'a' && ch <= 'z')
                    state = -1;
                else if (Integer.parseInt(String.valueOf(ch)) % 2 == 0)
                    state = 2;
                else if (Integer.parseInt(String.valueOf(ch)) % 2 != 0)
                    state = 1;

                break;

            case '2':
                if (ch >= 'a' && ch <= 'k')
                    state = 3;
                else if (ch >= 'l' && ch <= 'z')
                    state = -1;
                else if (Integer.parseInt(String.valueOf(ch)) % 2 == 0)
                    state = 2;
                else if (Integer.parseInt(String.valueOf(ch)) % 2 != 0)
                    state = 0;
                break;

            case '1':
                if (ch >= 'a' && ch <= 'k')
                    state = -1;
                else if (ch >= 'l' && ch <= 'z')
                    state = 3;
                else if (Integer.parseInt(String.valueOf(ch)) % 2 != 0)
                    state = 1;
                else if (Integer.parseInt(String.valueOf(ch)) % 2 == 0)
                    state = 0;

            }
        }
        return state == 3;
    }

}