package io.tolstjak.jsf.jdbc;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StudentDbUtils {

    private static StudentDbUtils instance;
    private DataSource dataSource;
    private String jndiName = "java:comp/env/jdbc/student_tracker";

    public static StudentDbUtils getInstance() throws Exception {
        if (instance == null) {
            instance = new StudentDbUtils();
        }
        return instance;
    }

    public StudentDbUtils() throws Exception {
        dataSource = getDataSource();
    }

    private DataSource getDataSource() throws NamingException {
        Context context = new InitialContext();

        return (DataSource) context.lookup(jndiName);
    }

    public List<Student> getStudents() throws Exception {
        List<Student> students = new ArrayList<>();

        Connection myConn = null;
        Statement myStmt = null;
        ResultSet myRs = null;

        try {
            myConn = getConnection();
            String sql = "SELECT * FROM student ORDER BY last_name";
            myStmt = myConn.createStatement();
            myRs = myStmt.executeQuery(sql);

            while (myRs.next()) {
                int id = myRs.getInt("id");
                String firstName = myRs.getString("first_name");
                String lastName = myRs.getString("last_name");
                String email = myRs.getString("email");

                Student student = new Student(id, firstName, lastName, email);

                students.add(student);
            }

            return students;
        } finally {
            close(myConn, myStmt, myRs);
        }
    }

    public void addStudent(Student student) throws Exception {

        Connection myConn = null;
        PreparedStatement myStmt = null;

        try {
            myConn = getConnection();
            String sql = "INSERT INTO student (first_name, last_name, email) VALUES (?, ?, ?)";
            myStmt = myConn.prepareStatement(sql);

            myStmt.setString(1, student.getFirstName());
            myStmt.setString(2, student.getLastName());
            myStmt.setString(3, student.getEmail());

            myStmt.execute();
        } finally {
            close(myConn, myStmt);
        }
    }

    public Student getStudent(int studentId) throws Exception {
        Connection myConn = null;
        PreparedStatement myStmt = null;
        ResultSet myRs = null;

        try {
            myConn = getConnection();
            String sql = "SELECT * FROM student WHERE id=?";
            myStmt = myConn.prepareStatement(sql);
            myStmt.setInt(1, studentId);
            myRs = myStmt.executeQuery();
            Student student = null;

            if (myRs.next()) {
                int id = myRs.getInt("id");
                String firstName = myRs.getString("first_name");
                String lastName = myRs.getString("last_name");
                String email = myRs.getString("email");

                student = new Student(id, firstName, lastName, email);
            }
            else {
                throw new Exception("Could not find student id: " + studentId);
            }

            return student;
        }
        finally {
            close(myConn, myStmt, myRs);
        }
    }

    public void updateStudent(Student student) throws Exception {

        Connection myConn = null;
        PreparedStatement myStmt = null;

        try {
            myConn = getConnection();

            String sql = "UPDATE student" +
                    " SET first_name=?, last_name=?, email=?" +
                    " WHERE id=?";

            myStmt = myConn.prepareStatement(sql);

            myStmt.setString(1, student.getFirstName());
            myStmt.setString(2, student.getLastName());
            myStmt.setString(3, student.getEmail());
            myStmt.setInt(4, student.getId());

            myStmt.execute();
        }
        finally {
            close(myConn, myStmt);
        }
    }

    public void deleteStudent(int studentId) throws Exception {

        Connection myConn = null;
        PreparedStatement myStmt = null;

        try {
            myConn = getConnection();

            String sql = "DELETE FROM student WHERE id=?";
            myStmt = myConn.prepareStatement(sql);
            myStmt.setInt(1, studentId);
            myStmt.execute();
        }
        finally {
            close(myConn, myStmt);
        }
    }

    private Connection getConnection() throws Exception {

        return dataSource.getConnection();
    }

    private void close(Connection conn, Statement stmt) {
        close(conn, stmt, null);
    }

    private void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
