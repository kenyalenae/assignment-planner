// class to hold main class
// controller.java will contain methods to connect database to GUI

import java.util.Vector;

public class Controller {

    private PlannerGUI gui;
    private PlannerDB db;

    public static void main(String[] args) {
        new Controller().startApp();
    }

    private void startApp() {

        db = new PlannerDB();

        gui = new PlannerGUI(this);
    }

    // get all assignments in database
    Vector<Vector> getAllAssignments() {
        return db.getAllAssignments();
    }

    // get column names for JTable in GUI
    Vector getColumnNames() {
        return db.getColumnNames();
    }

    // add new assignment to database
    void addAssignment(Assignment assignment) {
        db.addAssignment(assignment);
    }

    // delete assignment from database and table
    void deleteAssignment(int id) {
        db.deleteAssignment(id);
    }

    // update assignment in database
    void updateAssignment(int id, String assignment) {
        db.updateAssignment(id, assignment);
    }

    // export assignment list to excel spreadsheet
    String exportToExcel() {
        return ExportToExcel.export();
    }


}
