// planner gui

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DateFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class PlannerGUI extends JFrame {

    private JPanel mainPanel;
    private JTextField className;
    private JTextField classCode;
    private JTextField assignment;
    private JSpinner dueDateSpinner;
    private JTable plannerTable;
    private JButton deleteAssignmentButton;
    private JButton addAssignmentButton;
    private JButton addToGoogleCalendarButton;
    private JButton exportToExcelButton;

    private Controller controller;

    private DefaultTableModel tableModel;
    private Vector columnNames;

    PlannerGUI(Controller controller) {

        // store a reference to the controller object
        this.controller = controller;

        // configure dateSpinner
        configureDateSpinner();

        // configure JTable
        configureTable();

        // add listeners
        addListeners();

        // GUI set up
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(mainPanel);
        pack();
        setVisible(true);
        setTitle("Assignment Planner");

    }

    // configure and set up the JTable
    private void configureTable() {

        // set up JTable
        // plannerTable.setGridColor(Color.BLACK);

        // enable sorting
        plannerTable.setAutoCreateRowSorter(true);

        // TODO auto size columns to width of contents
        plannerTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        columnNames = controller.getColumnNames();
        Vector<Vector> data = controller.getAllAssignments();

        // custom method to determine which cells are editable
        tableModel = new DefaultTableModel(data, columnNames) {
            // only let user edit assignment field
            @Override
            public boolean isCellEditable(int row, int col) {
                return (col == 3); // assignment column
            }

            @Override
            public void setValueAt(Object val, int row, int col) {

                // get row and send new value to DB for update
                int id = (int) getValueAt(row, 0);

                String updateAssignment = val.toString();

                controller.updateAssignment(id, updateAssignment);
                updateTable();
            }

        };

        plannerTable.setModel(tableModel);

    }

    // method to update table when user makes changes such as add/update/delete
    private void updateTable() {

        Vector<Vector> data = controller.getAllAssignments();
        tableModel.setDataVector(data, columnNames);

    }

    private void addListeners() {

        // add assignment listener
        addAssignmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    // get user input
                    String name = className.getText();
                    String classAssignment = assignment.getText();
                    //Date dueDate = (Date) dueDateSpinner.getValue();
                    int code;

                    // convert dueDateSpinner to simpler date format
                    String startDate = dueDateSpinner.getValue().toString();
                    DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                    Date date = formatter.parse(startDate);
                    DateFormat newFormat = new SimpleDateFormat("MM dd yyyy");
                    String finalDate = newFormat.format(date);
                    Date newDate = newFormat.parse(finalDate);

                    // if name or assignment field are empty, show error dialog
                    if (name.isEmpty() || classAssignment.isEmpty()) {
                        errorDiolog("Make sure all fields are filled in.");
                        return;
                    }

                    // TODO - check if name field has any characters other than a-z

                    try {
                        // if class code is not positive number or longer than 4 digits, show error dialog
                        code = Integer.parseInt(classCode.getText());
                        if (code <= 0 || classCode.getText().trim().length() > 4) {
                            errorDiolog("Please enter positive number no longer than 4 digits.");
                            return;
                        }

                    } catch (NumberFormatException nfe) {
                        errorDiolog("Enter a number for class code.");
                        return;
                    }

                    // TODO - convert date to MM-DD-YYYY format before adding

                    // create assignment using user entered information
                    Assignment assignmentRecord = new Assignment(name, code, classAssignment, newDate);
                    // add assignment to database
                    controller.addAssignment(assignmentRecord);
                    // update JTable
                    updateTable();

                } catch (ParseException p) {
                    p.printStackTrace();
                }


            }
        });

        // listener for delete assignment button
        deleteAssignmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int currentRow = plannerTable.getSelectedRow();

                if (currentRow == -1) {
                    errorDiolog("Please select an assignment in table to delete");
                }

                else {
                    // if user selects yes to dialog, delete solver from database and update solversTable
                    if (showYesNoDialog("Delete this assignment?") == JOptionPane.YES_OPTION) {
                        // get the id of the selected solver
                        int id = (Integer) tableModel.getValueAt(currentRow, 0);
                        controller.deleteAssignment(id);
                        updateTable();
                    }

                }

            }
        });

        // export table of assignments to Excel
        exportToExcelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int rowCount = plannerTable.getModel().getRowCount();

                // if plannerTable is empty show user error dialog because nothing to export
                if (rowCount == 0) {
                    errorDiolog("There are no assignments to export.");
                }
                else {
                    // ask user to confirm they want to export to excel
                    if (showYesNoDialog("Export table of assignments to Excel?") == JOptionPane.YES_OPTION) {
                        // write data to excel file
                        // which is saved in root directory of project
                        String success = controller.exportToExcel();

                        // let user know if export was successful
                        if (success.equals(ExportToExcel.OK)) {
                            messageDiolog("Export was successful.");
                        }

                        // let user know if export failed
                        if (success.equals(ExportToExcel.NOTOK)) {
                            errorDiolog("Failed to export to Excel.");
                        }

                    }

                }

            }
        });

        // TODO - export to Google calendar
        addToGoogleCalendarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int currentRow = plannerTable.getSelectedRow();

                if (currentRow == -1) {
                    errorDiolog("Please select an assignment in table to add to Google Calendar.");
                }

                else {

                    // get assignment description and due date to add to calendar
                    Object assignmentObj = getData(plannerTable, currentRow, 3);
                    System.out.println("Assignment of column 3 and current row: " + assignmentObj);
                    String assignment = assignmentObj.toString(); // convert assignment object to string

                    Object dueDateObj = getData(plannerTable, currentRow, 4);
                    System.out.println("Due date of column 4 and current row: " + dueDateObj);
                    Date dueDate = (Date) dueDateObj; // convert due date object to Date

                    GoogleCalendar.addEvent(assignment, dueDate);

                    // TODO - let user know adding to calendar was successful

                }

            }
        });

    }

    // method to get data from plannerTable
    public Object getData(JTable plannerTable, int row, int col) {
        return plannerTable.getModel().getValueAt(row, col);
    }

    // I GOT THIS METHOD FROM LAB 8 GARDEN PROGRAM
    private void configureDateSpinner() {

        // Dates between Jan 1, 1970 and some time in 2920. I don't suppose this program will be around this long though...
        SpinnerDateModel spinnerDateModel = new SpinnerDateModel(new Date(), new Date(0), new Date(30000000000000L), Calendar.DAY_OF_YEAR);
        dueDateSpinner.setModel(spinnerDateModel);
        // Create a DateEditor to configure the way dates are displayed and edited
        // Define format the dates will have
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dueDateSpinner, "MM-dd-yyyy");
        DateFormatter formatter = (DateFormatter) editor.getTextField().getFormatter();
        // Attempt to prevent invalid input
        formatter.setAllowsInvalid(false);
        // Allow user to type as well as use up/down buttons
        formatter.setOverwriteMode(true);
        // And tell the serviceDataSpinner to use this Editor
        dueDateSpinner.setEditor(editor);

    }

    // display informative message to user
    private void messageDiolog(String msg) {
        JOptionPane.showMessageDialog(PlannerGUI.this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    // error dialog to use if user enters invalid data
    private void errorDiolog(String msg) {
        JOptionPane.showMessageDialog(PlannerGUI.this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private int showYesNoDialog(String message) {
        return JOptionPane.showConfirmDialog(this, message, null, JOptionPane.YES_NO_OPTION);
    }

}
