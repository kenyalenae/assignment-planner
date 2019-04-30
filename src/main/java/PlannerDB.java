// planner database class

import org.sqlite.core.DB;

import java.sql.*;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Vector;


public class PlannerDB {

    private static final String DB_URL = "jdbc:sqlite:planner.sqlite"; // database url

    // strings to hold database information
    private static final String TABLE_NAME = "planner";
    private static final String ID_COL = "id";
    private static final String CLASS_NAME_COL = "class_name";
    private static final String CLASS_CODE_COL = "class_code";
    private static final String ASSIGNMENT_COL = "assignment";
    private static final String DUE_DATE_COL = "due_date";

    PlannerDB() { createTable(); }

    // create table
    private void createTable() {

        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {

            // create table in datable if does not exist
            String createTableSQLTemplate = "CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY, %s TEXT, " +
                    "%s INTEGER, %s TEXT, %s TEXT)";
            String createTableSQL = String.format(createTableSQLTemplate, TABLE_NAME, ID_COL, CLASS_NAME_COL,
                    CLASS_CODE_COL, ASSIGNMENT_COL, DUE_DATE_COL);

            statement.execute(createTableSQL);
            System.out.println("Created planner table");

        } catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        }

    }

    // column names for JTable in GUI
    Vector getColumnNames() {

        Vector colNames = new Vector();
        colNames.add("ID");
        colNames.add("CLASS NAME");
        colNames.add("CLASS CODE");
        colNames.add("ASSIGNMENT");
        colNames.add("DUE DATE");

        return colNames;

    }

    // get all assignments in database
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
                String className = rsAll.getString(CLASS_NAME_COL);
                int classCode = rsAll.getInt(CLASS_CODE_COL);
                String assignment = rsAll.getString(ASSIGNMENT_COL);
                String dateString = rsAll.getString(DUE_DATE_COL);
                // convert date string to Date
                Date date = new Date(dateString);

                // create assignment using data from table
                Assignment assignmentRecord = new Assignment(className, classCode, assignment, date);
                // add assignment to vector of assignments
                allAssignments.add(assignmentRecord);

            }

            rsAll.close(); // close result set

            return allAssignments; // return vector of assignments

        } catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        }


    }

    // add assignment to database
    public void addAssignment(Assignment assignment) {

        // sql statement to add assignment
        String addAssignmentSql = "INSERT INTO planner (class_name, class_code, assignment, due_date) VALUES (?,?,?,?)";

        try (Connection connection = DriverManager.getConnection(DB_URL);
        PreparedStatement preparedStatement = connection.prepareStatement(addAssignmentSql)) {

            // prepared statements
            preparedStatement.setString(1, assignment.getClassName());
            preparedStatement.setInt(2, assignment.getClassCode());
            preparedStatement.setString(3, assignment.getAssignment());
            preparedStatement.setString(4, assignment.getDate().toString()); // convert date to string format

            preparedStatement.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }



    }


}
