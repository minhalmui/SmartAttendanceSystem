import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.util.*;

public class SmartAttendanceSystem {
    static Connection conn;
    static String sessionCode = "";

    public static void main(String[] args) {
        connectDatabase();
        showLoginScreen();
    }

    static void connectDatabase() {
        try {
            
            conn = DriverManager.getConnection("jdbc:sqlite:attendance.db");
            Statement stmt = conn.createStatement();
            
            stmt.execute("CREATE TABLE IF NOT EXISTS attendance (name TEXT, date DATE);");
        } catch (SQLException e) {
            System.out.println("Error connecting to database.");
            e.printStackTrace();
        }
    }

    // Teacher Login
    static void showLoginScreen() {
        JFrame frame = new JFrame("Login");
        JTextField user = new JTextField(10);
        JPasswordField pass = new JPasswordField(10);
        JButton login = new JButton("Login");

        frame.setLayout(new FlowLayout());
        frame.add(new JLabel("Username:"));
        frame.add(user);
        frame.add(new JLabel("Password:"));
        frame.add(pass);
        frame.add(login);

        frame.setSize(250, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        login.addActionListener(e -> {
            if (user.getText().equals("teacher") && new String(pass.getPassword()).equals("1234")) {
                frame.dispose();
                showDashboard();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Login");
            }
        });
    }

    // Teacher Dashboard
    static void showDashboard() {
        JFrame frame = new JFrame("Teacher Dashboard");
        JButton genCode = new JButton("Generate Session Code");
        JButton openStudent = new JButton("Student Entry");
        JButton manualEntry = new JButton("Manual Entry");

        frame.setLayout(new FlowLayout());
        frame.add(genCode);
        frame.add(openStudent);
        frame.add(manualEntry);
        frame.setSize(300, 150);
        frame.setVisible(true);

        genCode.addActionListener(e -> {
            sessionCode = "S" + new Random().nextInt(10000);
            JOptionPane.showMessageDialog(frame, "Session Code: " + sessionCode);
        });

        openStudent.addActionListener(e -> showStudentEntry());

        manualEntry.addActionListener(e -> showManualEntry());
    }

    // Student Entry
    static void showStudentEntry() {
        JFrame frame = new JFrame("Student Entry");
        JTextField name = new JTextField(10);
        JTextField code = new JTextField(10);
        JButton submit = new JButton("Submit");

        frame.setLayout(new FlowLayout());
        frame.add(new JLabel("Name:"));
        frame.add(name);
        frame.add(new JLabel("Session Code:"));
        frame.add(code);
        frame.add(submit);
        frame.setSize(300, 150);
        frame.setVisible(true);

        submit.addActionListener(e -> {
            if (code.getText().equals(sessionCode)) {
                saveAttendance(name.getText());
                JOptionPane.showMessageDialog(frame, "Attendance Marked");
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Code");
            }
        });
    }

    // Manual Entry
    static void showManualEntry() {
        JFrame frame = new JFrame("Manual Entry");
        JTextField name = new JTextField(10);
        JButton submit = new JButton("Submit");

        frame.setLayout(new FlowLayout());
        frame.add(new JLabel("Student Name:"));
        frame.add(name);
        frame.add(submit);
        frame.setSize(250, 100);
        frame.setVisible(true);

        submit.addActionListener(e -> {
            saveAttendance(name.getText());
            JOptionPane.showMessageDialog(frame, "Manually Marked Present");
        });
    }

    // Save Attendance to SQLite Database
    static void saveAttendance(String name) {
        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());  
        try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO attendance (name, date) VALUES (?, ?)")) {
            pstmt.setString(1, name);
            pstmt.setDate(2, date);  
            pstmt.executeUpdate();
            System.out.println("Attendance saved to database.");
        } catch (SQLException e) {
            System.out.println("Error saving attendance.");
            e.printStackTrace();
        }
    }
}
