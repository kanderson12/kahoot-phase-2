import java.sql.*;
import java.util.Scanner;

public class TakePoll extends ReadSQLServer {

    public static void main(String[] args) {
        takePoll();
    }

    public static void takePoll() {
        Scanner keyboard = new Scanner(System.in);
        Connection connection = null;
        Statement connectionStatement = null;

        String dBName = ReadSQLServer.findDB("Poll");
        String pollName = findTable(dBName, "Poll");
        String pollTaker;
        String individualResults;

        if (!pollName.equals("")) {
            try {
                connection = SQLInstructions.connectToDB(dBName);

                System.out.println("Enter your name to start " + pollName);
                pollTaker = keyboard.nextLine();
                individualResults = pollName + "_" + pollTaker;

                connectionStatement = connection.createStatement();

                //Create table to store individual results of poll
                //Referenced https://www.tutorialspoint.com/jdbc/jdbc-create-tables.htm
                String sql = "CREATE TABLE " + individualResults + " " +
                        "(QuestionNumber INTEGER, " +
                        "Response varchar(100), " +
                        "PRIMARY KEY ( QuestionNumber ))";
                connectionStatement.executeUpdate(sql);

                String readQuestions = ("SELECT * FROM " + pollName);

                connectionStatement = connection.createStatement();

                ResultSet resultSet = connectionStatement.executeQuery(readQuestions);

                while (resultSet.next()) {

                    connectionStatement = connection.createStatement();

                    int number = resultSet.getInt("QuestionNumber");
                    String options = resultSet.getString("Options");
                    String question = resultSet.getString("Question");

                    System.out.println("Number: " + number + "\nOptions: " + options + "\nQuestion: " + question);
                    System.out.println("*------------------*");

                    System.out.println("enter answer here: ");
                    String answer = keyboard.nextLine();
                    sql =   "INSERT INTO " + individualResults +
                            " VALUES(" + number + ", '" + answer + "')";
                    connectionStatement.executeUpdate(sql);
                }

                System.out.println("Your answers were recorded.");
                
            } catch (SQLException sQLE) {
                sQLE.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                try {
                    if (connectionStatement != null) {
                        connection.close();
                    } } catch (SQLException se) {
                    }
                try {
                    if (connection != null) {
                        connection.close();
                    } } catch (SQLException se) {
                    se.printStackTrace();
                }
            }
        }
    }
}