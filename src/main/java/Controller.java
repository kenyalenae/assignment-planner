// class to hold main class
// controller.java will contain methods to connect database to GUI

public class Controller {

    private PlannerGUI gui;
    private PlannerDB db;

    public static void main(String[] args) {
        new Controller().startApp();
    }

    private void startApp() {

        db = new PlannerDB();

        gui = new PlannerGUI();
    }



}