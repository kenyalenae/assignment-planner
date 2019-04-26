// class to define an assignment

import java.sql.Date;

public class Assignment {

    private String className;
    private int classCode;
    private String assignment;
    private Date date;

    // CONSTRUCTOR

    public Assignment(String className, int classCode, String assignment, Date date) {
        this.className = className;
        this.classCode = classCode;
        this.assignment = assignment;
        this.date = date;
    }

    // GETTERS AND SETTERS

    public String getClassName() { return className; }

    public void setClassName(String className) { this.className = className; }

    public int getClassCode() { return classCode; }

    public void setClassCode(int classCode) { this.classCode = classCode; }

    public String getAssignment() { return assignment; }

    public void setAssignment(String assignment) { this.assignment = assignment; }

    public Date getDate() { return date; }

    public void setDate(Date date) { this.date = date; }

    // TO STRING METHOD

    @Override
    public String toString() {
        return "Assignment{" +
                "className='" + className + '\'' +
                ", classCode=" + classCode +
                ", assignment='" + assignment + '\'' +
                ", date=" + date +
                '}';
    }
}
