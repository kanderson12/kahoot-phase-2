import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ListIterator;

class WriteToSQLServer {

    private static final String WRITTEN_SUCCESS = "Written successfully";
    private static final String WRITTEN_FAILURE = "Write failed";

    static void run() {
        writePollQuestions();
        writePollResults();
    }

    @SuppressWarnings("unchecked")
    private static void writePollQuestions() {

        try {
            //Creating a JSONParser object
            JSONParser jsonParser = new JSONParser();

            //for iterating through options JSONArray
            ListIterator<String> listIterator;

            //Parsing the contents of the JSON file
            JSONObject jsonObject = (JSONObject) jsonParser.parse(
                    new FileReader(
                            "C:/Users/Ethan/StudioProjects/kahoot-phase-2/json/pollQuestions.json"));

            //retrieve array from json file
            JSONArray jsonArray = (JSONArray) jsonObject.get("Questions");

            //connect to database
            Connection connection = ConnectToDatabase.connectToPollDB();

            //Insert a row into the pollquestions table
            PreparedStatement sqlStatement = connection.prepareStatement("INSERT INTO poll_questions VALUES (?, ?, ?, ?, ?)");

            //to reference each object inside JSONArray
            JSONObject record;

            for (Object arrayObject : jsonArray) {
                record = (JSONObject) arrayObject;

                //getting all fields of pollResults.json
                String type = (String) record.get("Type");
                String number = (String) record.get("Number");
                String question = (String) record.get("Question");
                JSONArray options = (JSONArray) record.get("Options");
                String user = (String) record.get("User");

                listIterator = options.listIterator();

                //building string to convert array to string
                StringBuilder optionString = new StringBuilder();

                while (listIterator.hasNext())
                    optionString.append(listIterator.next()).append(" ");

                //writing to sql script (pollquestions.sql)
                sqlStatement.setString(1, number);
                sqlStatement.setString(2, type);
                sqlStatement.setString(3, optionString.toString());
                sqlStatement.setString(4, question);
                sqlStatement.setString(5, user);

                //for executing the write to the ClickersDB server with table contents
                sqlStatement.executeUpdate();
                record.clear();
                options.clear();
            }

            //clears all contents of jsonObject
            jsonObject.clear();
            jsonArray.clear();
            sqlStatement.close();

            //closes connection
            connection.close();

            //success message to be output to terminal
            System.out.println("POLL QUESTIONS " + WRITTEN_SUCCESS);
        }
        //for catching any issues with connecitivity with MySQL server
        catch (SQLException sQLE) {
            System.out.println(WRITTEN_FAILURE);
            sQLE.printStackTrace();
        }
        //for catching any errors while opening json files
        catch (IOException iOE) {
            System.out.println(WRITTEN_FAILURE);
            iOE.printStackTrace();
        }
        //for catching any JSONParser erros
        catch (ParseException pE) {
            System.out.println(WRITTEN_FAILURE);
            pE.printStackTrace();
        }


    }

    private static void writePollResults() {
        try {
            //Parsing the contents of the JSON file
            JSONArray jsonArray = (JSONArray) new JSONParser().parse(new FileReader("C:/Users/Ethan/StudioProjects/kahoot-phase-2/json/pollResults.json"));

            //connect to database
            Connection connection = ConnectToDatabase.connectToPollDB();

            //Insert a row into the MyPlayers table
            PreparedStatement sqlStatement = connection.prepareStatement("INSERT INTO poll_results VALUES (?, ?, ?, ?)");

            for (Object object : jsonArray) {
                JSONObject record = (JSONObject) object;
                String response = (String) record.get("Response");
                String number = (String) record.get("Number");
                String question = (String) record.get("Question");
                String taker = (String) record.get("Taker");

                sqlStatement.setString(1, number);
                sqlStatement.setString(2, question);
                sqlStatement.setString(3, response);
                sqlStatement.setString(4, taker);

                sqlStatement.executeUpdate();
                record.clear();
            }

            //flushing of any contents
            sqlStatement.close();
            connection.close();

            System.out.println("POLL RESULTS " + WRITTEN_SUCCESS);
        }
        //for catching any issues with connecitivity with MySQL server
        catch (SQLException sQLE) {
            sQLE.printStackTrace();
        }
        //for catching any errors while opening json files
        catch (IOException iOE) {
            iOE.printStackTrace();
        }
        //for catching any JSONParser erros
        catch (ParseException pE) {
            pE.printStackTrace();
        }

    }
}