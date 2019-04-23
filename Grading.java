/**
 * Yixing Zheng
 * 2293298
 * zheng129@mail.chapman.edu
 * CPSC 408
 * Final Project
 *
 * A Java commend line database project that manages grades for students and teachers
 *
 */

/**
 *
 * Importing all the goodies that saved my life
 *
 **/
import java.sql.*;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * The grading class, should have had multiply classes for different objects but have not had change to implement it
 *
 **/
public class Grading {

    /**
     *
     * The connection function connects to the MySQL database from Java
     *
     **/
    private static Connection estConnection (String dbName, String username, String password){
        Connection con = null;
        Statement stmt;
        String myDriver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/" + dbName + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        try {
            Class.forName(myDriver);
            System.out.println("Connecting to a selected database...");

            con = DriverManager.getConnection(url, username, password);
            System.out.println("Connected database successfully...");

            con.setAutoCommit(false);

            stmt = con.createStatement();

            String query =
                    "CREATE TABLE IF NOT EXISTS Users " +
                            "(UserID INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                            "FirstName VARCHAR(35), " +
                            "LastName VARCHAR(35), " +
                            "IsFaculty VARCHAR(1))";

            stmt.executeUpdate(query);

            query =
                    "CREATE TABLE IF NOT EXISTS UserDetails " +
                            "(UserID INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                            "UserName VARCHAR (15), " +
                            "Passwords VARCHAR(35))";

            stmt.executeUpdate(query);

            query =
                    "CREATE TABLE IF NOT EXISTS Courses " +
                            "(CourseID INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                            "CourseTitle VARCHAR(35), " +
                            "Years INTEGER, " +
                            "Term VARCHAR(10))";

            stmt.executeUpdate(query);

            query =
                    "CREATE TABLE IF NOT EXISTS CourseDetails " +
                            "(CourseTitle VARCHAR(35) PRIMARY KEY, " +
                            "CourseDescription VARCHAR(255))";

            stmt.executeUpdate(query);

            query =
                    "CREATE TABLE IF NOT EXISTS CourseLists " +
                            "(ListID INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                            "UserID INTEGER, " +
                            "CourseID INTEGER)";

            stmt.executeUpdate(query);

            query =
                    "CREATE TABLE IF NOT EXISTS Grades " +
                            "(GradeID INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                            "UserID integer, " +
                            "CourseID INTEGER, " +
                            "GradeType INTEGER, " +
                            "GradeTitle VARCHAR(35))";

            stmt.executeUpdate(query);

            query =
                    "CREATE TABLE IF NOT EXISTS GradeValues " +
                            "(GradeID INTEGER PRIMARY KEY, " +
                            "GradeValue INTEGER)";

            stmt.executeUpdate(query);

            query =
                    "CREATE TABLE IF NOT EXISTS GradeDetails " +
                            "(GradeType INTEGER PRIMARY KEY, " +
                            "MaxGradevalue INTEGER)";

            stmt.executeUpdate(query);

            con.commit();

        }
        catch (ClassNotFoundException ex){
            System.out.println("ClassNotFoundException: " + ex.getMessage());
        }
        catch (SQLException ex){
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            System.out.println("Rolling back data...");
            try{
                if(con != null){
                    con.rollback();
                }
            }
            catch(SQLException ex1){
                ex1.printStackTrace();
            }
        }
        return con;
    }

    /**
     *
     * The print all result function prints all the records stored in the database
     *
     **/
    private static void printALLResult(Connection con){
        ResultSet rs;
        try {
            Statement stmt = con.createStatement();

            String query =
                    "SELECT cl.UserID, u.FirstName, u.LastName, u.IsFaculty, c.CourseID, c.CourseTitle, c.Years, c.Term, cd.CourseDescription, g.GradeID, g.GradeType, g.GradeTitle, gv.GradeValue, gd.MaxGradevalue, ((gv.GradeValue / gd.MaxGradevalue) * 100) AS Percentage " +
                            "FROM Users AS u " +
                            "LEFT JOIN CourseLists AS cl on " +
                            "u.UserID = cl.UserID " +
                            "LEFT JOIN Courses AS c ON " +
                            "c.CourseID = cl.CourseID " +
                            "LEFT JOIN CourseDetails AS cd ON " +
                            "cd.CourseTitle = c.CourseTitle " +
                            "LEFT JOIN Grades AS g ON " +
                            "u.UserID = g.UserID AND c.CourseID = g.CourseID " +
                            "LEFT JOIN GradeValues AS gv on " +
                            "g.GradeID = gv.GradeID " +
                            "LEFT JOIN GradeDetails AS gd on " +
                            "g.GradeType = gd.GradeType " +
                            "GROUP BY cl.UserID, u.FirstName, u.LastName, u.IsFaculty, c.CourseID, c.CourseTitle, cd.CourseDescription, g.GradeID, g.GradeType, g.GradeTitle, gv.GradeValue, gd.MaxGradevalue " +
                            "ORDER BY c.CourseID ASC, cl.UserID ASC, g.GradeType ASC";

            rs = stmt.executeQuery(query);

            System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            System.out.println("UserID | " + "FirstName | " + "LastName | " + "IsFaculty | " + "CourseID | " + "CourseTitle | " + "Year | " + "Term | " + "CourseDescription | " + "GradeID | " + "GradeType | " + "GradeTitle | " + "GradeValue | " + "MaxGradeValue | " + "Percentage |");
            while (rs.next()) {
                System.out.println(rs.getInt(1) + " | " +
                        rs.getString(2) + " | " +
                        rs.getString(3) + " | " +
                        rs.getInt(4) + " | " +
                        rs.getInt(5) + " | " +
                        rs.getString(6) + " | " +
                        rs.getString(8) + " | " +
                        rs.getInt(7) + " | " +
                        rs.getString(9) + " | " +
                        rs.getInt(10) + " | " +
                        rs.getInt(11) + " | " +
                        rs.getString(12) + " | " +
                        rs.getInt(13) + " | " +
                        rs.getInt(14) + " | " +
                        rs.getFloat(15) + " | ");
            }

            System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        }
        catch (SQLException ex){
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    /**
     *
     * The print result function prints results of certain user and for certain course id
     *
     **/
    private static void printResult(Connection con, int uid){
        Statement stmt;
        ResultSet rs;
        String courseID;
        Scanner input = new Scanner(System.in);
        try {
            stmt = con.createStatement();

            String query =
                    "SELECT InnerQuery.CourseID, InnerQuery.CourseTitle, InnerQuery.Years, InnerQuery.Term, InnerQuery.CourseDescription, COUNT(InnerQuery.CourseID) " +
                            "FROM " +
                            "(SELECT c.CourseID, c.CourseTitle, c.Years, c.Term, cd.CourseDescription, u.UserID " +
                            "FROM Courses AS c " +
                            "INNER JOIN CourseDetails AS cd " +
                            "ON c.CourseTitle = cd.CourseTitle " +
                            "INNER JOIN CourseLists AS cl " +
                            "ON c.CourseID = cl.CourseID " +
                            "INNER JOIN Users AS u " +
                            "ON u.UserID = cl.UserID) AS InnerQuery WHERE InnerQuery.UserID = " +
                            "\"" + uid + "\"" +
                            "GROUP BY InnerQuery.CourseID, InnerQuery.CourseTitle, InnerQuery.Years, InnerQuery.Term, InnerQuery.CourseDescription " +
                            "ORDER BY InnerQuery.CourseID ASC";

            rs = stmt.executeQuery(query);
            if (rs.next()){
                if (rs.getInt(6) == 0) {
                    System.out.println("You are not taking any course at the moment, please add a course and try again. \n");
                }
                else {
                    rs = stmt.executeQuery(query);

                    System.out.println("These are the courses you are taking: ");

                    System.out.println("-----------------------------------------------------------");
                    System.out.println("CourseID | " + "CourseTitle | " + "Year | " + "Term | " + "Course Description | ");
                    while(rs.next()) {

                        System.out.println(rs.getInt(1) + " | " +
                                rs.getString(2) + " | " +
                                rs.getInt(3) + " | " +
                                rs.getString(4) + " | " +
                                rs.getString(5) + " | ");
                    }
                    System.out.println("----------------------------------------------------------- \n");

                    System.out.println("Please enter the id of the course you want to check grade: ");
                    courseID = input.next();

                    query =
                            "SELECT InnerQuery.CourseTitle, InnerQuery.FirstName, InnerQuery.LastName, InnerQuery.GradeID, InnerQuery.GradeType, InnerQuery.GradeTitle, InnerQuery.GradeValue, InnerQuery.MaxGradevalue, InnerQuery.Percentage " +
                                    "FROM\n" +
                                    "(SELECT c.CourseID,c.CourseTitle, u.UserID, u.FirstName, u.LastName, g.GradeID, g.GradeType, g.GradeTitle, gv.GradeValue, gd.MaxGradevalue, ((gv.GradeValue / gd.MaxGradevalue) * 100) AS Percentage " +
                                    "FROM Users AS u " +
                                    "INNER JOIN CourseLists AS cl on " +
                                    "u.UserID = cl.UserID " +
                                    "INNER JOIN Courses AS c ON " +
                                    "c.CourseID = cl.CourseID " +
                                    "INNER JOIN CourseDetails AS cd ON " +
                                    "cd.CourseTitle = c.CourseTitle " +
                                    "INNER JOIN Grades AS g ON " +
                                    "u.UserID = g.UserID AND c.CourseID = g.CourseID " +
                                    "INNER JOIN GradeValues AS gv on " +
                                    "g.GradeID = gv.GradeID " +
                                    "INNER JOIN GradeDetails AS gd on " +
                                    "g.GradeType = gd.GradeType) AS InnerQuery " +
                                    "WHERE InnerQuery.UserID = " +
                                    "\"" + uid + "\"" +
                                    "AND InnerQuery.CourseID = " +
                                    "\"" + courseID + "\"" +
                                    "ORDER BY InnerQuery.CourseID ASC";

                    rs = stmt.executeQuery(query);

                    System.out.println("---------------------------------------------------------------------------------------------------------");
                    System.out.println("CourseTitle | " + "FirstName | " + "LastName | " + "GradeID | " + "GradeType | " + "GradeTitle | " + "Score | " + "TotalScore | " + "Percentage |");
                    while (rs.next()) {
                        System.out.println(rs.getString(1) + " | " +
                                rs.getString(2) + " | " +
                                rs.getString(3) + " | " +
                                rs.getInt(4) + " | " +
                                rs.getInt(5) + " | " +
                                rs.getString(6) + " | " +
                                rs.getInt(7) + " | " +
                                rs.getInt(8) + " | " +
                                rs.getFloat(9) + " | ");
                    }

                    System.out.println("---------------------------------------------------------------------------------------------------------");
                }
            }
        }
        catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
    }

    /**
     *
     * The create course function creates a course, includes title, year, term, and description
     *
     **/
    private static void CreateCourse(Connection con, int uid) {
        Statement stmt;
        ResultSet rs;
        int courseID = 0;
        String courseTitle = "";
        String year = "";
        String term = "";
        String courseDes = "";
        String query;
        boolean proceed = false;
        Scanner input = new Scanner(System.in);
        try {
            System.out.println("Please enter your Course Title: ");
            courseTitle = input.nextLine();

            System.out.println("Please enter the year: ");
            year = input.next();

            System.out.println("Please enter the term: ");
            term = input.next();

            System.out.println("Please enter the description for the course: ");
            input.nextLine();
            courseDes = input.nextLine();
        }
        catch (Exception e){
            System.out.println("Invalid input.");
        }
        try {
            stmt = con.createStatement();

            query =
                    "SELECT COUNT(CourseTitle) FROM Courses AS c " +
                            "WHERE c.CourseTitle = " +
                            "\"" + courseTitle + "\"" +
                            "AND c.Years = " +
                            "\"" + year + "\"" +
                            "AND c.Term = " +
                            "\"" + term + "\"";

            rs = stmt.executeQuery(query);
            if (rs.next()) {
                if(rs.getInt(1) == 0){
                    proceed = true;
                }
            }
            if (proceed) {
                query =
                        "INSERT INTO Courses (CourseTitle, Years, Term)" +
                                "VALUES (" + "\"" + courseTitle + "\"" + ", " + "\"" + year + "\"" + ", " + "\"" + term + "\"" + ")";

                stmt.executeUpdate(query);

                query =
                        "SELECT COUNT(CourseTitle) FROM CourseDetails WHERE CourseTitle = " + "\"" + courseTitle + "\"";

                rs = stmt.executeQuery(query);
                if (rs.next()) {
                    if (rs.getInt(1) == 0) {
                        query =
                                "INSERT INTO CourseDetails (CourseTitle, CourseDescription)" +
                                        "VALUES (" + "\"" + courseTitle + "\"" + ", " + "\"" + courseDes + "\"" + ")";

                        stmt.executeUpdate(query);
                    }
                }

                query =
                        "SELECT MAX(CourseID) FROM Courses";

                rs = stmt.executeQuery(query);
                if (rs.next()) {
                    courseID = rs.getInt(1);
                }

                query =
                        "INSERT INTO CourseLists (UserID, CourseID)" +
                                "VALUES (" + "\"" + uid + "\"" + ", " + "\"" + courseID + "\"" + ")";

                stmt.executeUpdate(query);

                con.commit();
                System.out.println("Course created and automatically added to your Courses. \n");
            }
            else {
                System.out.println("There already is the course in the system, please double check. \n");
            }

        }
        catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());

            System.out.println("Creating course failed. Data Rolled back. \n");
            try {
                if (con != null) {
                    con.rollback();
                }
            }
            catch (SQLException ex1) {
                ex1.printStackTrace();
            }
        }
    }

    /**
     *
     * The add course function adds a course to the database
     *
     **/
    private static void AddCourse(Connection con, int uid) {
        Statement stmt;
        ResultSet rs;
        String courseID;
        String query;
        Scanner input = new Scanner(System.in);
        try {
            stmt = con.createStatement();

            query =
                    "SELECT InnerQuery.CourseID, " +
                            "InnerQuery.CourseTitle, " +
                            "InnerQuery.Years, " +
                            "InnerQuery.Term, " +
                            "InnerQuery.CourseDescription " +
                            "FROM " +
                            "(SELECT c.CourseID, c.CourseTitle, c.Years, c.Term, cd.CourseDescription, cl.UserID " +
                            "FROM Courses AS c INNER JOIN CourseDetails AS cd " +
                            "ON c.CourseTitle = cd.CourseTitle INNER JOIN CourseLists AS cl " +
                            "ON c.CourseID = cl.CourseID) AS InnerQuery " +
                            "WHERE InnerQuery.UserID = " +
                            "" + "\"" + uid + "\"" +
                            "ORDER BY InnerQuery.CourseID ASC";

            rs = stmt.executeQuery(query);
            System.out.println("These are the courses you are having: ");
            System.out.println("-------------------------------------------------------------------------------");
            System.out.println("CourseID | " + "CourseTitle | " + "Year | " + "Term | " + "Course Description |");
            while(rs.next()){
                System.out.println(rs.getInt(1) + " | " +
                        rs.getString(2) + " | " +
                        rs.getInt(3) + " | " +
                        rs.getString(4) + " | ");
            }
            System.out.println("------------------------------------------------------------------------------- \n");

            query =
                    "SELECT c.CourseID, c.CourseTitle, c.Years, c.Term, cd.CourseDescription " +
                            "FROM Courses AS c INNER JOIN CourseDetails AS cd " +
                            "ON c.CourseTitle = cd.CourseTitle INNER JOIN CourseLists AS cl " +
                            "ON c.CourseID = cl.CourseID " +
                            "WHERE cl.UserID <> " +
                            "" + "\"" + uid + "\"" +
                            "ORDER BY c.CourseID ASC";

            rs = stmt.executeQuery(query);

            System.out.println("These are all the courses : ");
            System.out.println("-------------------------------------------------------------------------------");
            System.out.println("CourseID | " + "CourseTitle | " + "Year | " + "Term | " + "Course Description |");
            while(rs.next()){
                System.out.println(rs.getInt(1) + " | " +
                        rs.getString(2) + " | " +
                        rs.getInt(3) + " | " +
                        rs.getString(4) + " | ");
            }
            System.out.println("------------------------------------------------------------------------------- \n");

            System.out.println("Please enter the id of the course you want to add: ");
            courseID = input.next();

            query =
                    "SELECT COUNT(InnerQuery.UserID) " +
                            "FROM (SELECT c.CourseID, c.CourseTitle, cl.UserID " +
                            "FROM CourseLists  AS cl INNER JOIN Courses AS c " +
                            "ON cl.CourseID = c.CourseID) AS InnerQuery " +
                            "WHERE InnerQuery.CourseID = " +
                            "\"" + courseID + "\"" +
                            " AND InnerQuery.UserID = " +
                            "\"" + uid + "\"";

            rs = stmt.executeQuery(query);
            if (rs.next()) {
                if(rs.getInt(1) == 0){
                    query =
                            "INSERT INTO CourseLists (UserID, CourseID)" +
                                    "VALUES (" + "\"" + uid + "\"" + ", " + "\"" + courseID + "\"" + ")";

                    stmt.executeUpdate(query);
                    con.commit();
                    System.out.println("Course added \n");
                }
                else {
                    System.out.println("You have already taken this course.");
                }
            }

        }
        catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());

            System.out.println("Data Rolled back.");
            try {
                if (con != null) {
                    con.rollback();
                }
            }
            catch (SQLException ex1) {
                ex1.printStackTrace();
            }
        }
    }

    /**
     *
     * The update course function updates a course's year, term, and description
     *
     **/
    private static void UpdateCourse(Connection con, int uid) {
        Statement stmt;
        ResultSet rs;
        String courseID;
        String courseTitle;
        String courseDes = "";
        String year = "";
        String term = "";
        Scanner input = new Scanner(System.in);
        try {
            stmt = con.createStatement();

            String query =
                    "SELECT InnerQuery.CourseID, InnerQuery.CourseTitle, InnerQuery.Years, InnerQuery.Term, InnerQuery.CourseDescription " +
                            "FROM (SELECT c.CourseID, c.CourseTitle, c.Years, c.Term, cd.CourseDescription, cl.UserID " +
                            "FROM Courses AS c INNER JOIN CourseDetails AS cd " +
                            "ON c.CourseTitle = cd.CourseTitle INNER JOIN CourseLists AS cl " +
                            "ON c.CourseID = cl.CourseID) AS InnerQuery WHERE InnerQuery.UserID = " +
                            "\"" + uid + "\"";

            rs = stmt.executeQuery(query);
            System.out.println("These are the courses you can update: ");
            System.out.println("CourseID " + "CourseTitle " + "Year " + "Term " + "Course Description ");
            while(rs.next()){
                System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getInt(3) + " " + rs.getString(4) + " " + rs.getString(5));
            }

            System.out.println("Please enter the id of the course you want to update: ");
            courseID = input.next();

            query =
                    "SELECT COUNT(InnerQuery.CourseID), InnerQuery.CourseTitle " +
                            "FROM (SELECT c.CourseID, c.CourseTitle " +
                            "FROM CourseDetails AS cd INNER JOIN Courses AS c " +
                            "ON cd.CourseTitle = c.CourseTitle) AS InnerQuery " +
                            "WHERE InnerQuery.CourseID = " +
                            "\"" + courseID + "\"";

            rs = stmt.executeQuery(query);

            if (rs.next()) {
                if(rs.getInt(1) == 1){
                    courseTitle = rs.getString(2);
                    try {
                        System.out.println("Please enter the year: ");
                        year = input.next();

                        System.out.println("Please enter the term: ");
                        term = input.next();

                        System.out.println("Please enter the description for the course: ");
                        input.nextLine();
                        courseDes = input.nextLine();
                    }
                    catch (Exception e){
                        System.out.println("Invalid input.");
                    }

                    query =
                            "UPDATE CourseDetails SET CourseDescription = " +
                                    "\"" + courseDes + "\"" +
                                    " WHERE CourseTitle = " +
                                    "\"" + courseTitle + "\"";

                    stmt.executeUpdate(query);

                    query =
                            "UPDATE Courses SET " +
                                    " Years = " +
                                    "\"" + year + "\"" +
                                    " , Term = " +
                                    "\"" + term + "\"" +
                                    " WHERE CourseID = " +
                                    "\"" + courseID + "\"";

                    stmt.executeUpdate(query);
                    con.commit();
                    System.out.println("Course updated \n");
                }
                else {
                    System.out.println("Invalid input.");
                }
            }

        }
        catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());

            System.out.println("Data Rolled back.");

            try {
                if (con != null) {
                    con.rollback();
                }
            }
            catch (SQLException ex1) {
                ex1.printStackTrace();
            }
        }
    }

    /**
     *
     * The delete course function deletes a course
     *
     * Should have also delete all the grades of the course, but have not got a change to implement it
     *
     **/
    private static void DeleteCourse(Connection con, int uid) {
        Statement stmt;
        ResultSet rs;
        String courseID;
        String courseTitle;
        Scanner input = new Scanner(System.in);
        try {
            stmt = con.createStatement();

            String query =
                    "SELECT InnerQuery.CourseID, InnerQuery.CourseTitle, InnerQuery.Years, InnerQuery.Term, InnerQuery.CourseDescription " +
                            "FROM (SELECT c.CourseID, c.CourseTitle, c.Years, c.Term, cd.CourseDescription, cl.UserID " +
                            "FROM Courses AS c INNER JOIN CourseDetails AS cd " +
                            "ON c.CourseTitle = cd.CourseTitle INNER JOIN CourseLists AS cl " +
                            "ON c.CourseID = cl.CourseID) AS InnerQuery WHERE InnerQuery.UserID = " +
                            "\"" + uid + "\"";

            rs = stmt.executeQuery(query);
            System.out.println("These are the courses you can delete: ");
            System.out.println("CourseID " + "CourseTitle " + "Year " + "Term " + "Course Description ");
            while(rs.next()){
                System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getInt(3) + " " + rs.getString(4) + " " + rs.getString(5));
            }

            System.out.println("Please enter the id of the course you want to delete: ");
            courseID = input.next();

            query =
                    "SELECT COUNT(InnerQuery.CourseID), InnerQuery.CourseTitle " +
                            "FROM (SELECT c.CourseID, c.CourseTitle " +
                            "FROM CourseDetails AS cd INNER JOIN Courses AS c " +
                            "ON cd.CourseTitle = c.CourseTitle) AS InnerQuery " +
                            "WHERE InnerQuery.CourseID = " +
                            "\"" + courseID + "\"";

            rs = stmt.executeQuery(query);
            if (rs.next()) {
                if(rs.getInt(1) == 1){
                    courseTitle = rs.getString(2);

                    query =
                            "DELETE FROM CourseDetails " +
                                    "WHERE CourseTitle = " +
                                    "\"" + courseTitle + "\"";

                    stmt.executeUpdate(query);

                    query =
                            "DELETE FROM Courses " +
                                    "WHERE CourseID = " +
                                    "\"" + courseID + "\"";

                    stmt.executeUpdate(query);

                    query =
                            "DELETE FROM CourseLists " +
                                    "WHERE CourseID = " +
                                    "\"" + courseID + "\"" +
                                    "AND UserID = " +
                                    "\"" + uid + "\"";

                    stmt.executeUpdate(query);

                    query =
                            "DELETE FROM CourseLists " +
                                    "WHERE CourseID = " +
                                    "\"" + courseID + "\"" +
                                    "AND UserID = " +
                                    "\"" + uid + "\"";

                    stmt.executeUpdate(query);

                    con.commit();
                    System.out.println("Course deleted \n");
                }
                else {
                    System.out.println("Invalid input.");
                }
            }
        }
        catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("Data Rolled back.");
            try {
                if (con != null) {
                    con.rollback();
                }
            }
            catch (SQLException ex1) {
                ex1.printStackTrace();
            }
        }
    }

    /**
     *
     * The create grade function creates a new type of grade. i.e. quiz, midterm, final, etc.
     *
     **/
    private static void CreateGrade(Connection con) {
        Statement stmt;
        ResultSet rs;
        String type = "";
        String maxValue = "";
        Scanner input = new Scanner(System.in);
        try {
            System.out.println("Please enter the type of the grade: ");
            type = input.next();

            System.out.println("Please enter the max value of grade: ");
            maxValue = input.next();

        }
        catch (Exception e){
            System.out.println("Invalid input.");
        }
        try {
            stmt = con.createStatement();

            String query =
                    "SELECT COUNT(GradeType) FROM GradeDetails WHERE GradeType = " + "\"" + type + "\"";

            rs = stmt.executeQuery(query);

            if (rs.next()) {
                if(rs.getInt(1) == 0){
                    query =
                            "INSERT INTO GradeDetails (GradeType, MaxGradeValue)" +
                                    "VALUES (" + "\"" + type + "\"" + ", " + "\"" + maxValue + "\"" + ")";

                    stmt.executeUpdate(query);
                    con.commit();
                    System.out.println("Record Created \n");
                }
                else {
                    System.out.println("Grade type already exists.\n");
                }
            }

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());

            System.out.println("Data Rolled back.");
            try {
                if (con != null) {
                    con.rollback();
                }
            }
            catch (SQLException ex1) {
                ex1.printStackTrace();
            }
        }
    }

    /**
     *
     * The add grade function adds a grade to a student for a course
     *
     **/
    private static void AddGrade(Connection con, int uid) {
        Statement stmt;
        ResultSet rs;
        String courseID;
        String studentID;
        String gid = "";
        String value;
        String type;
        String title;
        Scanner input = new Scanner(System.in);
        try {
            stmt = con.createStatement();

            String query =
                    "SELECT InnerQuery.CourseID, " +
                            "InnerQuery.CourseTitle, " +
                            "InnerQuery.Years, " +
                            "InnerQuery.Term, " +
                            "InnerQuery.CourseDescription " +
                            "FROM " +
                            "(SELECT c.CourseID, c.CourseTitle, c.Years, c.Term, cd.CourseDescription, cl.UserID " +
                            "FROM Courses AS c INNER JOIN CourseDetails AS cd " +
                            "ON c.CourseTitle = cd.CourseTitle INNER JOIN CourseLists AS cl " +
                            "ON c.CourseID = cl.CourseID) AS InnerQuery WHERE InnerQuery.UserID = " + "\"" + uid + "\"";

            rs = stmt.executeQuery(query);
            System.out.println("These are the courses you can add grade to: ");
            System.out.println("CourseID " + "CourseTitle " + "Year " + "Term " + "Course Description ");
            while(rs.next()){
                System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getInt(3) + " " + rs.getString(4)+ " " + rs.getString(5));
            }

            System.out.println("Please enter the id of the course you want to add grade to: ");
            courseID = input.next();

            query = "SELECT COUNT(InnerQuery.UserID) " +
                    "FROM (SELECT c.CourseID, c.CourseTitle, cl.UserID " +
                    "FROM CourseLists  AS cl INNER JOIN Courses AS c " +
                    "ON cl.CourseID = c.CourseID) AS InnerQuery " +
                    "WHERE InnerQuery.CourseID = " +
                    "\"" + courseID + "\"" +
                    " AND InnerQuery.UserID = " +
                    "\"" + uid + "\"";

            rs = stmt.executeQuery(query);
            if (rs.next()) {
                if(rs.getInt(1) == 1){

                    query =
                            "SELECT InnerQuery.UserID, InnerQuery.FirstName, InnerQuery.LastName " +
                                    "FROM (SELECT cl.UserID, cl.CourseID, u.FirstName, u.LastName, u.IsFaculty " +
                                    "FROM CourseLists AS cl INNER JOIN Users AS u " +
                                    "ON cl.UserID = u.UserID) AS InnerQuery " +
                                    "WHERE InnerQuery.CourseID = " +
                                    "\"" + courseID + "\"" +
                                    "AND InnerQuery.IsFaculty = '0'" +
                                    "ORDER BY InnerQuery.UserID ASC";

                    rs = stmt.executeQuery(query);

                    System.out.println("These are the students you can add grade to: ");
                    System.out.println("UserID " + "First Name " + "Last Name ");
                    while(rs.next()){
                        System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getString(3));
                    }

                    System.out.println("Please enter the id of the student you want to add grade to: ");
                    studentID = input.next();

                    System.out.println("Please enter the type of the grade: ");
                    type = input.next();

                    System.out.println("Please enter the title of the grade: ");
                    input.next();
                    title = input.nextLine();

                    System.out.println("Please enter the value of the grade: ");
                    value = input.next();

                    query =
                            "INSERT INTO Grades (UserID, CourseID, GradeType, GradeTitle) " +
                                    "VALUES(" + "\"" + studentID + "\"" + ", " + "\"" + courseID + "\"" + ", " + "\"" + type + "\"" + ", " + "\"" + title + "\"" + ")";

                    stmt.executeUpdate(query);


                    query =
                            "SELECT GradeID FROM Grades ORDER BY GradeID DESC LIMIT 1; ";

                    rs = stmt.executeQuery(query);
                    while(rs.next()){
                        gid = rs.getString(1);
                    }

                    query =
                            "INSERT INTO GradeValues (GradeID, GradeValue)" +
                                    "VALUES (" + "\"" + gid + "\"" + ", " + "\"" + value + "\"" + ")";

                    stmt.executeUpdate(query);
                    con.commit();

                    System.out.println("Grade added \n");
                }
                else {
                    System.out.println("invalid input");
                }
            }
        }
        catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());

            System.out.println("Data Rolled back.");
            try {
                if (con != null) {
                    con.rollback();
                }
            }
            catch (SQLException ex1) {
                ex1.printStackTrace();
            }
        }
    }

    /**
     *
     * The upgrade function upgrades a grade of a student for a course
     *
     **/
    private static void UpdateGrade(Connection con, int uid) {
        Statement stmt;
        ResultSet rs;
        String gradeID;
        String maxGrade = "";
        String courseID;
        int oldType = 0;
        boolean valid= false;
        String value = "";
        String title = "";
        Scanner input = new Scanner(System.in);
        try {
            stmt = con.createStatement();

            String query =
                    "SELECT InnerQuery.CourseID, " +
                            "InnerQuery.CourseTitle, " +
                            "InnerQuery.Years, " +
                            "InnerQuery.Term, " +
                            "InnerQuery.CourseDescription " +
                            "FROM " +
                            "(SELECT c.CourseID, c.CourseTitle, c.Years, c.Term, cd.CourseDescription, cl.UserID " +
                            "FROM Courses AS c INNER JOIN CourseDetails AS cd " +
                            "ON c.CourseTitle = cd.CourseTitle INNER JOIN CourseLists AS cl " +
                            "ON c.CourseID = cl.CourseID) AS InnerQuery WHERE InnerQuery.UserID = " + "\"" + uid + "\"";

            rs = stmt.executeQuery(query);
            System.out.println("These are the courses you can change the grade: ");
            System.out.println("CourseID " + "CourseTitle " + "Year " + "Term " + "Course Description ");
            while(rs.next()){
                System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getInt(3) + " " + rs.getString(4)+ " " + rs.getString(5));
            }

            System.out.println("Please enter the id of the course you want to add grade to: ");
            courseID = input.next();

            query =
                    "SELECT COUNT(InnerQuery.CourseID) FROM " +
                            "(SELECT c.CourseID, cl.UserID " +
                            "FROM Courses AS c INNER JOIN CourseLists AS cl " +
                            "ON c.CourseID = cl.CourseID) AS InnerQuery " +
                            "WHERE InnerQuery.UserID = " +
                            "" + "\"" + uid + "\"" +
                            "AND InnerQuery.CourseID = " +
                            "" + "\"" + courseID + "\"";

            rs = stmt.executeQuery(query);
            if (rs.next()) {
                if(rs.getInt(1) == 1) {

                    query =
                            "SELECT InnerQuery.GradeID, InnerQuery.GradeType, " +
                                    "InnerQuery.GradeTitle, InnerQuery.FirstName, " +
                                    "InnerQuery.LastName, InnerQuery.GradeValue, " +
                                    "InnerQuery.MaxGradeValue " +
                                    "FROM " +
                                    "(SELECT g.GradeID, g.GradeType, g.GradeTitle, u.FirstName, u.LastName, g.CourseID, gv.GradeValue, gd.MaxGradeValue " +
                                    "FROM Grades AS g " +
                                    "INNER JOIN Users AS u " +
                                    "ON g.UserID = U.UserID " +
                                    "INNER JOIN GradeValues AS gv " +
                                    "ON g.GradeID = gv.GradeID " +
                                    "INNER JOIN GradeDetails AS gd " +
                                    "ON gd.GradeType = g.GradeType) AS InnerQuery " +
                                    "WHERE CourseID = " +
                                    "\"" + courseID + "\"";

                    rs = stmt.executeQuery(query);
                    System.out.println("These are the grades you can update: ");
                    System.out.println("GradeID " + "GradeType " + "GradeTitle " + "FirstName " + "LastName " + "Score " + "TotalScore");
                    while (rs.next()) {
                        System.out.println(rs.getInt(1) + " " + rs.getInt(2) + " " + rs.getString(3) + " " + rs.getString(4) + " " + rs.getString(5) + " " + rs.getInt(6) + " " + rs.getInt(7));
                    }

                    System.out.println("Please enter the id of the grade you want to update: ");
                    gradeID = input.next();
                    int grade = Integer.parseInt(gradeID);

                    query =
                            "SELECT InnerQuery.GradeID, InnerQuery.GradeType FROM " +
                                    "(SELECT g.GradeID, g.CourseID, gd.GradeType " +
                                    "FROM Grades AS g " +
                                    "INNER JOIN Users AS u " +
                                    "ON g.UserID = U.UserID " +
                                    "INNER JOIN GradeValues AS gv " +
                                    "ON g.GradeID = gv.GradeID " +
                                    "INNER JOIN GradeDetails AS gd " +
                                    "ON gd.GradeType = g.GradeType) AS InnerQuery " +
                                    "WHERE CourseID = " +
                                    "\"" + courseID + "\"";

                    rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        if (rs.getInt(1) == grade) {
                            oldType = rs.getInt(2);
                            valid = true;
                            break;
                        }
                    }

                    if (valid) {
                        try {
                            System.out.println("Please enter the title of the grade: ");
                            title = input.next();

                            System.out.println("Please enter the max value of the grade: ");
                            maxGrade = input.next();

                            System.out.println("Please enter the value of the grade: ");
                            value = input.next();
                        }
                        catch (Exception e) {
                            System.out.println("Invalid input.");
                        }

                        query =
                                "UPDATE Grades SET GradeTitle = " +
                                        "\"" + title + "\"" +
                                        " WHERE GradeID = " +
                                        "\"" + gradeID + "\"";

                        stmt.executeUpdate(query);

                        query =
                                "UPDATE GradeDetails SET maxGradeValue = " +
                                        "\"" + maxGrade + "\"" +
                                        " WHERE GradeType = " +
                                        "\"" + oldType + "\"";

                        stmt.executeUpdate(query);

                        query =
                                "UPDATE GradeValues SET GradeValue = " +
                                        "\"" + value + "\"" +
                                        " WHERE GradeID = " +
                                        "\"" + gradeID + "\"";

                        stmt.executeUpdate(query);

                        con.commit();
                        System.out.println("Grade updated \n");
                    }
                }
                else {
                    System.out.println("Invalid input.");
                }
            }
        }
        catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());

            System.out.println("Data Rolled back.");
            try {
                if (con != null) {
                    con.rollback();
                }
            }
            catch (SQLException ex1) {
                ex1.printStackTrace();
            }
        }
    }

    /**
     *
     * The delete grade function deletes a grade of a student for a course
     *
     **/
    private static void DeleteGrade(Connection con, int uid) {
        Statement stmt;
        ResultSet rs;
        String gradeID;
        String courseID;
        int oldType = 0;
        boolean valid= false;
        Scanner input = new Scanner(System.in);
        try {
            stmt = con.createStatement();

            String query =
                    "SELECT InnerQuery.CourseID, " +
                            "InnerQuery.CourseTitle, " +
                            "InnerQuery.Years, " +
                            "InnerQuery.Term, " +
                            "InnerQuery.CourseDescription " +
                            "FROM " +
                            "(SELECT c.CourseID, c.CourseTitle, c.Years, c.Term, cd.CourseDescription, cl.UserID " +
                            "FROM Courses AS c INNER JOIN CourseDetails AS cd " +
                            "ON c.CourseTitle = cd.CourseTitle INNER JOIN CourseLists AS cl " +
                            "ON c.CourseID = cl.CourseID) AS InnerQuery WHERE InnerQuery.UserID = " + "\"" + uid + "\"";

            rs = stmt.executeQuery(query);
            System.out.println("These are the courses you can delete the grade: ");
            System.out.println("CourseID " + "CourseTitle " + "Year " + "Term " + "Course Description ");
            while(rs.next()){
                System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getInt(3) + " " + rs.getString(4)+ " " + rs.getString(5));
            }

            System.out.println("Please enter the id of the course you want to delete the grade from: ");
            courseID = input.next();

            query =
                    "SELECT COUNT(InnerQuery.CourseID) FROM " +
                            "(SELECT c.CourseID, cl.UserID " +
                            "FROM Courses AS c INNER JOIN CourseLists AS cl " +
                            "ON c.CourseID = cl.CourseID) AS InnerQuery " +
                            "WHERE InnerQuery.UserID = " +
                            "" + "\"" + uid + "\"" +
                            "AND InnerQuery.CourseID = " +
                            "" + "\"" + courseID + "\"";

            rs = stmt.executeQuery(query);
            if (rs.next()) {
                if(rs.getInt(1) == 1) {

                    query =
                            "SELECT InnerQuery.GradeID, InnerQuery.GradeType, " +
                                    "InnerQuery.GradeTitle, InnerQuery.FirstName, " +
                                    "InnerQuery.LastName, InnerQuery.GradeValue, " +
                                    "InnerQuery.MaxGradeValue " +
                                    "FROM " +
                                    "(SELECT g.GradeID, g.GradeType, g.GradeTitle, u.FirstName, u.LastName, g.CourseID, gv.GradeValue, gd.MaxGradeValue " +
                                    "FROM Grades AS g " +
                                    "INNER JOIN Users AS u " +
                                    "ON g.UserID = U.UserID " +
                                    "INNER JOIN GradeValues AS gv " +
                                    "ON g.GradeID = gv.GradeID " +
                                    "INNER JOIN GradeDetails AS gd " +
                                    "ON gd.GradeType = g.GradeType) AS InnerQuery " +
                                    "WHERE CourseID = " +
                                    "\"" + courseID + "\"";

                    rs = stmt.executeQuery(query);
                    System.out.println("These are the grades you can delete: ");
                    System.out.println("GradeID " + "GradeType " + "GradeTitle " + "FirstName " + "LastName " + "Score " + "TotalScore");
                    while (rs.next()) {
                        System.out.println(rs.getInt(1) + " " + rs.getInt(2) + " " + rs.getString(3) + " " + rs.getString(4) + " " + rs.getString(5) + " " + rs.getInt(6) + " " + rs.getInt(7));
                    }

                    System.out.println("\nPlease enter the id of the grade you want to delete: ");
                    gradeID = input.next();
                    int grade = Integer.parseInt(gradeID);

                    query =
                            "SELECT InnerQuery.GradeID, InnerQuery.GradeType FROM " +
                                    "(SELECT g.GradeID, g.CourseID, gd.GradeType " +
                                    "FROM Grades AS g " +
                                    "INNER JOIN Users AS u " +
                                    "ON g.UserID = U.UserID " +
                                    "INNER JOIN GradeValues AS gv " +
                                    "ON g.GradeID = gv.GradeID " +
                                    "INNER JOIN GradeDetails AS gd " +
                                    "ON gd.GradeType = g.GradeType) AS InnerQuery " +
                                    "WHERE CourseID = " +
                                    "\"" + courseID + "\"";

                    rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        if (rs.getInt(1) == grade) {
                            oldType = rs.getInt(2);
                            valid = true;
                            break;
                        }
                    }
                    if (valid) {
                        query =
                                "DELETE FROM Grades " +
                                        "WHERE GradeID = " +
                                        "\"" + gradeID + "\"";

                        stmt.executeUpdate(query);

                        query =
                                "DELETE FROM GradeDetails " +
                                        "WHERE GradeType = " +
                                        "\"" + oldType + "\"";

                        stmt.executeUpdate(query);

                        query =
                                "DELETE FROM GradeValues " +
                                        "WHERE GradeID = " +
                                        "\"" + gradeID + "\"";

                        stmt.executeUpdate(query);

                        con.commit();
                        System.out.println("Grade Deleted \n");
                    }
                }
                else {
                    System.out.println("Invalid input.");
                }
            }
        }
        catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("Data Rolled back.");
            try {
                if (con != null) {
                    con.rollback();
                }
            }
            catch (SQLException ex1) {
                ex1.printStackTrace();
            }
        }
    }

    /**
     *
     * The check status helper function checks if a user is a student or a teacher
     *
     **/
    private static int checkStatus(Connection con, int uid){
        Statement stmt;
        ResultSet rs;
        try {
            stmt = con.createStatement();

            String query =
                    "SELECT u.IsFaculty " +
                            "FROM Users AS u " +
                            "WHERE u.UserID = " +
                            "\"" + uid + "\"";

            rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return 0;
    }

    /**
     *
     * The log in function that helps the user to register and/or login
     *
     **/
    private static int loggingIn (Connection con) {
        ResultSet rs;
        Statement stmt;
        int choice = -1;
        String choiceStr;
        boolean notLoggedIn = true;
        String username = "";
        String password;
        String pwConfirm;
        String status = "";
        String query;
        boolean proceed = false;
        Scanner input = new Scanner(System.in);

        while (notLoggedIn) {

            System.out.println("Hello, would you like to Login or Register: \n"
                    + "1) Login \n"
                    + "2) Register");
            try {
                choiceStr = input.next();
                choice = Integer.parseInt(choiceStr);
            }
            catch (Exception e){
            }

            if (choice == 1) {
                while(true) {
                    System.out.println("Please enter your user name: ");
                    username = input.next();

                    System.out.println("Please enter your password: ");
                    password = input.next();

                    try {
                        stmt = con.createStatement();

                        query =
                                "SELECT DISTINCT COUNT(InnerQuery.UserID), InnerQuery.IsFaculty AS status, InnerQuery.FirstName, InnerQuery.UserID " +
                                        "FROM " +
                                        "(SELECT u.UserID, u.IsFaculty, u.FirstName, ud.UserName, ud.Passwords FROM UserDetails AS ud INNER JOIN Users AS u " +
                                        "ON u.UserID = ud.UserID) AS InnerQuery " +
                                        "WHERE InnerQuery.UserName = " +
                                        "\"" + username + "\"" +
                                        "AND InnerQuery.Passwords = " +
                                        "\"" + password + "\"" +
                                        "GROUP BY status, InnerQuery.FirstName, InnerQuery.UserID";

                        rs = stmt.executeQuery(query);

                        if (rs.next()) {
                            if (rs.getInt(1) == 1){
                                return rs.getInt(4);
                            }
                            else{
                                System.out.println("Login information invalid, please try again.");
                            }
                        }

                    }
                    catch (SQLException ex) {
                        System.out.println("SQLException: " + ex.getMessage());
                        System.out.println("SQLState: " + ex.getSQLState());
                        System.out.println("VendorError: " + ex.getErrorCode());
                    }
                }
            }
            else if (choice == 2) {
                try {
                    stmt = con.createStatement();

                    System.out.println("Please enter your user name: ");
                    username = input.next();
                    input.nextLine();

                    query =
                            "SELECT COUNT(UserName) FROM UserDetails AS ud " +
                                    "WHERE ud.UserName = " +
                                    "\"" + username + "\"";

                    rs = stmt.executeQuery(query);

                    if (rs.next()) {
                        if (rs.getInt(1) == 0) {
                            proceed = true;
                        }
                    }
                }
                catch (SQLException ex) {
                    System.out.println("SQLException: " + ex.getMessage());
                }

                if(proceed) {
                    System.out.print("Please enter your password: \n");
                    password = input.nextLine();
                    System.out.print("Please re-enter the password to confirm: \n");
                    pwConfirm = input.nextLine();

                    List<String> errorList = new ArrayList<String>();

                    while (!isValid(password, pwConfirm, errorList)) {
                        System.out.println("The password you entered is invalid.\n");
                        for (String error : errorList) {
                            System.out.println(error);
                        }
                        System.out.print("Please enter your password: \n");
                        password = input.nextLine();
                        System.out.print("Please re-enter the password to confirm: \n");
                        pwConfirm = input.nextLine();
                    }

                    System.out.println("Please enter your first name: ");
                    String fName = input.next();

                    System.out.println("Please enter your last name: ");
                    String lName = input.next();
                    while (true) {
                        System.out.println("Are you a student or teacher? \n"
                                + "0) Student \n"
                                + "1) Teacher");
                        try {
                            status = input.next();
                        } catch (Exception e) {
                            System.out.println("Invalid input.");
                        }
                        if (status.equals("0") || status.equals("1")) {
                            break;
                        }
                    }
                    try {
                        stmt = con.createStatement();

                        query =
                                "INSERT INTO UserDetails (UserName, Passwords)" +
                                        "VALUES (" + "\"" + username + "\"" + ", " + "\"" + password + "\"" + ")";

                        stmt.executeUpdate(query);

                        query =
                                "INSERT INTO Users (FirstName, LastName, IsFaculty)" +
                                        "VALUES (" + "\"" + fName + "\"" + ", " + "\"" + lName + "\"" + ", " + "\"" + status + "\"" + ")";

                        stmt.executeUpdate(query);
                        con.commit();

                    } catch (SQLException ex) {
                        System.out.println("SQLException: " + ex.getMessage());

                        System.out.println("Rolling back data...");

                        try {
                            if (con != null) {
                                con.rollback();
                            }
                        } catch (SQLException ex1) {
                            ex1.printStackTrace();
                        }
                    }
                }
                else {
                    System.out.println("Oops, user name already exists. Please try another one. \n");
                }
            }
            else {
                notLoggedIn = true;
            }
        }
        return 0;
    }

    /**
     *
     * The helper function for password verification
     *
     **/
    public static boolean isValid(String password, String pwConfirm, List<String> errorList) {

        Pattern specailCharPatten = Pattern.compile("[^a - z 0 - 9]", Pattern.CASE_INSENSITIVE);
        Pattern UpperCasePatten = Pattern.compile("[A - Z]");
        Pattern lowerCasePatten = Pattern.compile("[a - z]");
        Pattern digitCasePatten = Pattern.compile("[0 - 9]");
        errorList.clear();

        boolean flag=true;

        if (!password.equals(pwConfirm)) {
            errorList.add("password and confirm password does not match.");
            flag=false;
        }
        if (password.length() < 8) {
            errorList.add("Password lenght must have alleast 8 character !!");
            flag=false;
        }
        if (!specailCharPatten.matcher(password).find()) {
            errorList.add("Password must have at least one special character !!");
            flag=false;
        }
        if (!UpperCasePatten.matcher(password).find()) {
            errorList.add("Password must have at least one uppercase character !!");
            flag=false;
        }
        if (!lowerCasePatten.matcher(password).find()) {
            errorList.add("Password must have at least one lowercase character !!");
            flag=false;
        }
        if (!digitCasePatten.matcher(password).find()) {
            errorList.add("Password must have at least one digit character !!");
            flag=false;
        }
        return flag;
    }

    /**
     *
     * The main method, initiates everything
     *
    **/
    public static void main(String[] args) {
        boolean notEnd = true;
        int choice = -1;
        int csv;
        String choiceStr;
        final String username = "root";
        final String pwd = "zhengyixing19304";
        String database = "Grades_Test";
        Scanner input = new Scanner(System.in);
        Connection con = estConnection(database, username, pwd);
        Statement stmt;
        ResultSet rs;

        int uid = loggingIn(con);

        while(notEnd) {
            if (checkStatus(con, uid) == 1) {
                System.out.println("Welcome, Your are logged in as Teacher.\n");
                System.out.println("Please choose what would you like to do: \n"
                        + "1) See all the results in table \n"
                        + "2) Create a course \n"
                        + "3) Add a course \n"
                        + "4) Update a course \n"
                        + "5) Delete a course \n"
                        + "6) Create a grade \n"
                        + "7) Add a grade \n"
                        + "8) Update a grade \n"
                        + "9) Delete a grade \n"
                        + "0) Exit \n");
                while (notEnd) {
                    try {
                        choiceStr = input.next();
                        choice = Integer.parseInt(choiceStr);
                    }
                    catch (Exception e) {
                    }
                    if (choice == 1) {
                        printALLResult(con);
                        System.out.println();

                        System.out.println("Would you like to export the result into a CSV file? 1/0 ");
                        csv = input.nextInt();

                        if (csv == 1){
                            String filename = "presumably all the data in the world.csv";
                            try {
                                FileWriter fw = new FileWriter(filename);

                                String query =
                                        "SELECT cl.UserID, u.FirstName, u.LastName, u.IsFaculty, c.CourseID, c.CourseTitle, c.Years, c.Term, cd.CourseDescription, g.GradeID, g.GradeType, g.GradeTitle, gv.GradeValue, gd.MaxGradevalue, ((gv.GradeValue / gd.MaxGradevalue) * 100) AS Percentage " +
                                                "FROM Users AS u " +
                                                "INNER JOIN CourseLists AS cl on " +
                                                "u.UserID = cl.UserID\n" +
                                                "INNER JOIN Courses AS c ON " +
                                                "c.CourseID = cl.CourseID\n" +
                                                "INNER JOIN CourseDetails AS cd ON " +
                                                "cd.CourseTitle = c.CourseTitle\n" +
                                                "INNER JOIN Grades AS g ON " +
                                                "u.UserID = g.UserID\n" +
                                                "INNER JOIN GradeValues AS gv on " +
                                                "g.GradeID = gv.GradeID\n" +
                                                "INNER JOIN GradeDetails AS gd on " +
                                                "g.GradeType = gd.GradeType " +
                                                "GROUP BY cl.UserID, u.FirstName, u.LastName, u.IsFaculty, c.CourseID, c.CourseTitle, cd.CourseDescription, g.GradeID, g.GradeType, g.GradeTitle, gv.GradeValue, gd.MaxGradevalue";

                                stmt = con.createStatement();
                                rs = stmt.executeQuery(query);

                                fw.append("UserID,FirstName,LastName,IsFaculty,CourseID,CourseTitle,Year,Term,CourseDescription,GradeID,GradeType,GradeTitle,GradeValue,MaxGradeValue,Percentage\n");

                                while (rs.next()) {
                                    fw.append(Integer.toString(rs.getInt(1)));
                                    fw.append(',');
                                    fw.append(rs.getString(2));
                                    fw.append(',');
                                    fw.append(rs.getString(3));
                                    fw.append(',');
                                    fw.append(Integer.toString(rs.getInt(4)));
                                    fw.append(',');
                                    fw.append(Integer.toString(rs.getInt(5)));
                                    fw.append(',');
                                    fw.append(rs.getString(6));
                                    fw.append(',');
                                    fw.append(Integer.toString(rs.getInt(7)));
                                    fw.append(',');
                                    fw.append(rs.getString(8));
                                    fw.append(',');
                                    fw.append(rs.getString(9));
                                    fw.append(',');
                                    fw.append(Integer.toString(rs.getInt(10)));
                                    fw.append(',');
                                    fw.append(Integer.toString(rs.getInt(11)));
                                    fw.append(',');
                                    fw.append(rs.getString(12));
                                    fw.append(',');
                                    fw.append(Integer.toString(rs.getInt(13)));
                                    fw.append(',');
                                    fw.append(Integer.toString(rs.getInt(14)));
                                    fw.append(',');
                                    fw.append(Float.toString(rs.getFloat(15)));
                                    fw.append('\n');
                                }
                                fw.flush();
                                fw.close();
                                System.out.println("CSV File is created successfully.");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    }
                    else if (choice == 2) {
                        CreateCourse(con, uid);
                        break;
                    }
                    else if (choice == 3) {
                        AddCourse(con, uid);
                        break;
                    }
                    else if (choice == 4) {
                        UpdateCourse(con, uid);
                        break;
                    }
                    else if (choice == 5) {
                        DeleteCourse(con, uid);
                        break;
                    }
                    else if (choice == 6) {
                        CreateGrade(con);
                        break;
                    }
                    else if (choice == 7) {
                        AddGrade(con, uid);
                        break;
                    }
                    else if (choice == 8) {
                        UpdateGrade(con, uid);
                        break;
                    }
                    else if (choice == 9) {
                        DeleteGrade(con, uid);
                        break;
                    }
                    else if (choice == 0) {
                        System.out.println("Program ended.");
                        notEnd = false;
                    }
                }
            }
            else if (checkStatus(con, uid) == 0){
                System.out.println("Welcome, Your are logged in as Student.\n");
                System.out.println("Please choose what would you like to do: \n"
                        + "1) See my grades \n"
                        + "2) Add a course \n"
                        + "3) Delete a course \n"
                        + "0) Exit \n");
                while (notEnd) {
                    try {
                        choiceStr = input.next();
                        choice = Integer.parseInt(choiceStr);
                    }
                    catch (Exception e) {
                    }
                    if (choice == 1) {
                        printResult(con, uid);
                        System.out.println();
                        break;
                    }
                    else if (choice == 2) {
                        AddCourse(con, uid);
                        break;
                    }
                    else if (choice == 3) {
                        DeleteCourse(con, uid);
                        break;
                    }
                    else if (choice == 0) {
                        System.out.println("Program ended.");
                        notEnd = false;
                    }
                }
            }
            else {
                break;
            }
        }
    }
}