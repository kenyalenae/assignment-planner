// planner gui

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DateFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    private Controller controller;

    private DefaultTableModel tableModel;
    private Vector columnNames;

    PlannerGUI(Controller controller) {

        this.controller = controller; // store a reference to the controller object

        //dateSpinner model & editor set up
        dueDateSpinner.setModel(new SpinnerDateModel());
        dueDateSpinner.setEditor(new JSpinner.DateEditor(dueDateSpinner, "MM-dd-yyyy"));

        // configure JTable
        configureTable();

        // TODO add listeners
        addListeners();

        // GUI set up
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(mainPanel);
        pack();
        setVisible(true);
        setTitle("Assignment Planner");

    }

    private void configureTable() {

        // set up JTable
        plannerTable.setGridColor(Color.BLACK);

        // enable sorting
        plannerTable.setAutoCreateRowSorter(true);

        columnNames = controller.getColumnNames();
        Vector data = controller.getAllAssignments();

        // custom method to determine which cells are editable
        tableModel = new DefaultTableModel(data, columnNames) {
//            @Override
//            public boolean isCellEditable(int row, int col) {
//                return (col == 3); // assignment and due date columns
//            }
//
//            @Override
//            public void setValueAt(Object val, int row, int col) {
//
//                // get row and send new value to DB for update
//                int id = (int) getValueAt(row, 0);
//
//                String updateAssignment = val.toString();
//
//                controller.updateAssignment(id, updateAssignment);
//                updateTable();
//            }

        };


        plannerTable.setModel(tableModel);

    }

    private void updateTable() {

        Vector data = controller.getAllAssignments();
        tableModel.setDataVector(data, columnNames);

    }

    private void addListeners() {

        addAssignmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String name = className.getText();
                String classAssignment = assignment.getText();
                Date dueDate = (Date) dueDateSpinner.getValue();

                int code;

                if (name.isEmpty() || classAssignment.isEmpty()) {
                    errorDiolog("Make sure all fields are not empty.");
                    return;
                }

                try {
                    code = Integer.parseInt(classCode.getText());
                    if (code <= 0) {
                        errorDiolog("Please enter positive number for class code.");
                    }

                } catch (NumberFormatException nfe) {
                    errorDiolog("Enter a number for class code.");
                    return;
                }

                Assignment assignmentRecord = new Assignment(name, code, classAssignment, dueDate);
                controller.addAssignment(assignmentRecord);
                updateTable();

            }
        });

    }

    // error dialog to use if user enters invalid data
    private void errorDiolog(String msg) {
        JOptionPane.showMessageDialog(PlannerGUI.this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

}
