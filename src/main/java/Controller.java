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
    Vector<Assignment> getAllAssignments() {
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



}
