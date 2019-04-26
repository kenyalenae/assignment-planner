// planner database class

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Vector;


public class PlannerDB {

    private static final String DB_URL = "jdbc:sqlite:planner.sqlite";

    private static final String TABLE_NAME = "planner";
    private static final String ID_COL = "id";
    private static final String CLASS_NAME_COL = "class_name";
    private static final String CLASS_CODE_COL = "class_code";
    private static final String ASSIGNMENT_COL = "assignment";
    private static final String DUE_DATE_COL = "due_date";

    PlannerDB() { createTable(); }

    private void createTable() {

        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {

            // create table in datable if does not exist
            String createTableSQLTemplate = "CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY, %s TEXT, " +
                    "%s INTEGER, %s TEXT, %s TEXT)";
            String createTableSQL = String.format(createTableSQLTemplate, TABLE_NAME, ID_COL, CLASS_NAME_COL,
                    CLASS_CODE_COL, ASSIGNMENT_COL, DUE_DATE_COL);

            statement.execute(createTableSQL);

        } catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        }

    }

    Vector<Assignment> getAllAssignments() {

        // vector to hold assignments
        Vector<Assignment> allAssignments = new Vector<>();

        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {

            // sql statement
            String selectAllSql = "SELECT * FROM " + TABLE_NAME;
            ResultSet rsAll = statement.executeQuery(selectAllSql);

            // while there are rows in table, add data to assignments vector
            while (rsAll.next()) {
                // get data from columns
                try {
                    String className = rsAll.getString(CLASS_NAME_COL);
                    int classCode = rsAll.getInt(CLASS_CODE_COL);
                    String assignment = rsAll.getString(ASSIGNMENT_COL);
                    String dateString = rsAll.getString(DUE_DATE_COL);

                    // convert date string to date
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = formatter.parse(dateString); // TODO unhandled exception ParseException

                    // create assignment using data from table
                    Assignment assignmentRecord = new Assignment(className, classCode, assignment, date);
                    // add assignment to vector of assignments
                    allAssignments.add(assignmentRecord);
                } catch (ParseException e ) {
                    e.printStackTrace();
                }

            }

            rsAll.close();

            return allAssignments;

        } catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        }


    }


}
