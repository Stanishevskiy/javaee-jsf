package io.tolstjak.jsf.jdbc;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet("/TestServlet")
public class TestServlet extends HttpServlet {

    @Resource(name = "jdbc/student_tracker")
    private DataSource dataSource;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        PrintWriter out = resp.getWriter();
        resp.setContentType("text/plain");

        Connection myConn;
        Statement myStmt;
        ResultSet myRs;

        try {
            myConn = dataSource.getConnection();
            String sql = "SELECT * FROM student;";
            myStmt = myConn.createStatement();
            myRs = myStmt.executeQuery(sql);

            while (myRs.next()) {
                String email = myRs.getString("email");
                out.println(email);
                System.out.println(email);
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.println(e.getMessage());
        }
    }
}
