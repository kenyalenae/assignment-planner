/* class to export table to excel
so that user can keep a list of assignments on computer
and they can edit the list with different fonts/make other changes
or they could email the list to themselves to have on other devices
*/

import java.sql.*;
import java.util.Date;
import java.util.Vector;

public class ExportToExcel extends PlannerDB {

    /*
    I am basing my code off of code I found on the website
    http://thinktibits.blogspot.com/2012/12/POI-Create-Excel-File-JDBC-Oracle-Table-Data-Example.html
    */

    private static final String DB_URL = "jdbc:sqlite:planner.sqlite"; // database url

    // strings to hold database information
    private static final String TABLE_NAME = "planner";
    private static final String ID_COL = "id";
    private static final String CLASS_NAME_COL = "class_name";
    private static final String CLASS_CODE_COL = "class_code";
    private static final String ASSIGNMENT_COL = "assignment";
    private static final String DUE_DATE_COL = "due_date";

    public static void WriteToExcel() {

        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {

            // sql statement
            String selectAllSql = "SELECT * FROM " + TABLE_NAME;
            ResultSet rsAll = statement.executeQuery(selectAllSql);

            // while there are rows in table, add data to assignments vector
            while (rsAll.next()) {

                // get data from columns
                int id = rsAll.getInt(ID_COL);
                String className = rsAll.getString(CLASS_NAME_COL);
                int classCode = rsAll.getInt(CLASS_CODE_COL);
                String assignment = rsAll.getString(ASSIGNMENT_COL);
                String dateString = rsAll.getString(DUE_DATE_COL);
                // convert date string to Date
                Date date = new Date(dateString);

                // TODO finish this method


            }

            rsAll.close(); // close result set

        } catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        }


    }




}
