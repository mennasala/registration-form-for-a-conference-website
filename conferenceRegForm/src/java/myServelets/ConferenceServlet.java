package myServelets;

import java.sql.*;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/ConferenceServlet")
public class ConferenceServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");

        if (name == null || email == null || phone == null || name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            request.setAttribute("error", "All fields are required.");
            request.getRequestDispatcher("index.html").forward(request, response);
            return;
        }

        Connection myConn = null;
        PreparedStatement myStmt = null;

        try {
            // 1. Get a connection to database
            Class.forName("com.mysql.cj.jdbc.Driver");
            myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/conference_db", "student", "student");
            System.out.println("Database connection successful!\n");

            // 2. Create a SQL query to insert participant data
            String query = "INSERT INTO participants (name, email, phone) VALUES (?, ?, ?)";
            myStmt = myConn.prepareStatement(query);
            myStmt.setString(1, name);
            myStmt.setString(2, email);
            myStmt.setString(3, phone);

            // 3. Execute the SQL query
            int rowsAffected = myStmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);

            // Forward to confirmation page
            request.setAttribute("name", name);
            request.getRequestDispatcher("confirmation.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database connection failed");
        } finally {
            try {
                if (myStmt != null) {
                    myStmt.close();
                }

                if (myConn != null) {
                    myConn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
