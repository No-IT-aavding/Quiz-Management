package manage;

import java.io.IOException;
import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
public class Student extends person{
    private final dbConnect db;
    //private final Connection conn = db.connect_to_db("quiz", "postgres", "admin");
    private String s_Fname;
    private String s_Lname;
    private String s_Pass;
    private String s_ID;
    Scanner sc = new Scanner(System.in);

    public Student(dbConnect db){
        this.db = db;
    }

    public boolean login(){
        Main.clr();
        System.out.println("=========================================");
        System.out.println("Please enter you ID and password (without space) given by the Faculty, Or type exit to return");
        System.out.println("**If you forgot your ID/Password, please contact the respective Faculty");
        System.out.println();
        System.out.print("ID: ");
        this.s_ID = sc.nextLine();

        if(s_ID.equalsIgnoreCase("exit"))return false;

        System.out.print("Password: ");
        this.s_Pass = sc.nextLine();

        if(verify(s_ID, s_Pass)){
            System.out.println("You have successfully logged in");
            for(int i = 5; i>=1; i--){
                System.out.println("Starting Quiz in " + i + " sec");
                Main.sleep(1000);
            }

            String subject = dispRule();

            Quiz quiz = new Quiz(subject, db);
            quiz.start();
            return false;
       }else{
            System.out.println("Your ID/pass combination is incorrect, try again");
            Main.sleep(2000);
            return true;
        }
    }

    protected boolean verify(String ID, String pass){
        ResultSet stud_cred = db.verify_Stud(ID);

        if(does_Element_exist(stud_cred) && match_pass(stud_cred, pass)){
            try {
                s_Fname = stud_cred.getString("first_name");
                s_Lname = stud_cred.getString("last_name");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return true;
        }else{
            return false;
        }
    }

//    protected boolean verify(String ID, String pass){
//        ResultSet stud_cred = db.verify_Stud(conn, ID);
//
//
//        try {
//            if (!stud_cred.isBeforeFirst()) {
//                return false;
//            }else{
//                stud_cred.next();
//                if(!pass.equals(stud_cred.getString(4)))
//                    return false;
//            }
//            s_Fname = stud_cred.getString("first_name");
//            s_Lname = stud_cred.getString("last_name");
//        }catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return true;
//    }

    private String dispRule() {
        while (true) {
            Main.clr();
            System.out.println("=========================================");
            System.out.println("Welcome " + s_Fname + ", please read the rules carefully");
            System.out.println("1. Exam will have 10 (or less) Questions.");
            System.out.println("2. Cheating will not be tolerated.");
            System.out.println("3. Each question will be followed by 4 options and 1 correct answer.");
            System.out.println("4. There is not time limit.");
            System.out.println("5. You cannot go back once an answer is marked, so choose carefully.");
            System.out.println("6. Continue to select subject");

            Main.pressEnter();
            ResultSet quiz_sub = db.dispQuizCode();
            System.out.println("All    - " + "Random questions from all Subjects");
            System.out.print("Enter the subject code to begin test (case sensitive): ");
            String sub_code = sc.next();
            sub_code = sub_code.toUpperCase();
            if(sub_code.equals("ALL")){
                return "ALL";
            }
            String subject = verify_Qid(quiz_sub, sub_code);
            if(db.verify_sub(sub_code)){
                return subject;
            }
            System.out.println("This code doesn't have any question, check again");
            Main.sleep(2000);
        }
    }

}
