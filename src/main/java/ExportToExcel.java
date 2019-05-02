/* class to export table to excel
so that user can keep a list of assignments on computer
and they can edit the list with different fonts/make other changes
or they could email the list to themselves to have on other devices
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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

    // output file path
    private static final String FILE_NAME = "src/AssignmentList.xlsx";

    public static void WriteToExcel() {

        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {

            // create excel workbook and worksheet
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Assignment List");

            // sql statement
            String selectAllSql = "SELECT * FROM " + TABLE_NAME;
            ResultSet rsAll = statement.executeQuery(selectAllSql);

            // use a HashMap to store Excel data
            HashMap<String, Object[]> excelAssignmentData = new HashMap<>();
            int rowCounter = 0;

            // while there are rows in table, add data to assignments vector
            while (rsAll.next()) {

                rowCounter++;

                // get data from columns
                int id = rsAll.getInt(ID_COL);
                String className = rsAll.getString(CLASS_NAME_COL);
                int classCode = rsAll.getInt(CLASS_CODE_COL);
                String assignment = rsAll.getString(ASSIGNMENT_COL);
                String dateString = rsAll.getString(DUE_DATE_COL);

                // put data into HashMap
                excelAssignmentData.put(Integer.toString(rowCounter), new Object[] {id, className, classCode, assignment, dateString});

            }

            rsAll.close(); // close result set

            // put data from HashMap into worksheet
            Set<String> keyset = excelAssignmentData.keySet();
            int rowNum = 0;
            // loop through the HashMap and add each line of data to each cell
            for (String assignment : keyset) {
                Row row = sheet.createRow(rowNum++);
                Object[] assignmentArray = excelAssignmentData.get(assignment);
                int cellNum = 0;
                for (Object obj : assignmentArray) {
                    Cell cell = row.createCell(cellNum++);
                    if (obj instanceof Double) {
                        cell.setCellValue((Double)obj);
                    } else {
                        cell.setCellValue((String)obj);
                    }
                }
            }

            try {
                try {
                    // creates xlsx file
                    FileOutputStream excelOutputFile = new FileOutputStream(new File(FILE_NAME));

                    // write to file
                    workbook.write(excelOutputFile);

                    // close file
                    excelOutputFile.close();
                } catch (FileNotFoundException fnfe) {
                    fnfe.printStackTrace();
                    System.out.println("File not found.");
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
                System.out.println("Unable to write to file");
            }

        } catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        }

    }




}
