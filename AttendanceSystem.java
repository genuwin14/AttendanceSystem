import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AttendanceSystem {

    public static void main(String[] args) {
        // Create the JFrame
        JFrame frame = new JFrame("Attendance System Registration");
        frame.setSize(400, 650); // Set the size of the window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null); // Use null layout (absolute positioning)

        // Center the window on the screen
        frame.setLocationRelativeTo(null);

        // Set background color to #191919 (dark grey)
        frame.getContentPane().setBackground(Color.decode("#191919"));

        // Custom fonts
        Font labelFont = new Font("Arial", Font.PLAIN, 16);
        Font buttonFont = new Font("Arial", Font.BOLD, 14);

        // Title label
        JLabel titleLabel = new JLabel("User Registration", JLabel.CENTER);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 50, 400, 50); // X, Y, Width, Height
        frame.add(titleLabel);

        // Define widths and heights for components
        int fieldWidth = 300;
        int fieldHeight = 40;
        int labelWidth = 100;
        int labelHeight = 30;
        int buttonWidth = 300;
        int buttonHeight = 40;

        // Calculate the X position for centering based on frame width
        int xPosition = (frame.getWidth() - fieldWidth) / 2;

        // Full Name label and field
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(labelFont);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBounds(50, 130, labelWidth, labelHeight); // X, Y, Width, Height
        frame.add(nameLabel);

        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        nameField.setBackground(Color.decode("#333333"));
        nameField.setForeground(Color.WHITE);
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#dddddd"), 1), // Outer border
            BorderFactory.createEmptyBorder(10, 10, 10, 10) // Padding inside the field (top, left, bottom, right)
        ));
        nameField.setBounds(xPosition, 160, fieldWidth, fieldHeight); // X, Y, Width, Height
        frame.add(nameField);

        // Email label and field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setBounds(50, 210, labelWidth, labelHeight); // X, Y, Width, Height
        frame.add(emailLabel);

        JTextField emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setBackground(Color.decode("#333333"));
        emailField.setForeground(Color.WHITE);
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#dddddd"), 1), // Outer border
            BorderFactory.createEmptyBorder(10, 10, 10, 10) // Padding inside the field (top, left, bottom, right)
        ));
        emailField.setBounds(xPosition, 240, fieldWidth, fieldHeight); // X, Y, Width, Height
        frame.add(emailField);

        // Password label and field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(labelFont);
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setBounds(50, 290, labelWidth, labelHeight); // X, Y, Width, Height
        frame.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBackground(Color.decode("#333333"));
        passwordField.setForeground(Color.WHITE);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#dddddd"), 1), // Outer border
            BorderFactory.createEmptyBorder(10, 10, 10, 10) // Padding inside the field (top, left, bottom, right)
        ));
        passwordField.setBounds(xPosition, 320, fieldWidth, fieldHeight); // X, Y, Width, Height
        frame.add(passwordField);

        // Date of Birth label and field
        JLabel dobLabel = new JLabel("Date of Birth:");
        dobLabel.setFont(labelFont);
        dobLabel.setForeground(Color.WHITE);
        dobLabel.setBounds(50, 370, labelWidth, labelHeight); // X, Y, Width, Height
        frame.add(dobLabel);

        JTextField dobField = new JTextField("YYYY-MM-DD", 20);
        dobField.setFont(new Font("Arial", Font.PLAIN, 14));
        dobField.setBackground(Color.decode("#333333"));
        dobField.setForeground(Color.WHITE);
        dobField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#dddddd"), 1), // Outer border
            BorderFactory.createEmptyBorder(10, 10, 10, 10) // Padding inside the field (top, left, bottom, right)
        ));
        dobField.setBounds(xPosition, 400, fieldWidth, fieldHeight); // X, Y, Width, Height
        frame.add(dobField);

        // Register button
        JButton registerButton = new JButton("Register");
        registerButton.setFont(buttonFont);
        registerButton.setBackground(Color.decode("#FF4D00"));
        registerButton.setBorder(BorderFactory.createLineBorder(Color.decode("#ffffff"), 1));
        registerButton.setForeground(Color.WHITE);
        registerButton.setBounds(xPosition, 460, buttonWidth, buttonHeight); // X, Y, Width, Height
        frame.add(registerButton);

        // Go Login button
        JButton goLoginButton = new JButton("Already have an account? Login");
        goLoginButton.setFont(buttonFont);
        goLoginButton.setBackground(Color.decode("#333333"));
        goLoginButton.setForeground(Color.WHITE);
        goLoginButton.setBorder(BorderFactory.createLineBorder(Color.decode("#dddddd"), 1));
        goLoginButton.setBounds(xPosition, 510, buttonWidth, buttonHeight); // X, Y, Width, Height
        frame.add(goLoginButton);

        // Button actions
        goLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                LoginScreen.main(null); // Open the Login screen
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                String dob = dobField.getText();
        
                Connection conn = null;
                PreparedStatement stmt = null;
                ResultSet rs = null;
        
                try {
                    // Explicitly load MySQL driver
                    Class.forName("com.mysql.cj.jdbc.Driver");
        
                    // Database connection
                    conn = DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/AttendanceSystem", "root", "");
        
                    // Check if the email already exists
                    String checkEmailQuery = "SELECT * FROM users WHERE email = ?";
                    stmt = conn.prepareStatement(checkEmailQuery);
                    stmt.setString(1, email);
                    rs = stmt.executeQuery();
        
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(frame, "Email already exists. Please use a different email.");
                    } else {
                        // If email doesn't exist, insert new user
                        String query = "INSERT INTO users (name, email, password, dob) VALUES (?, ?, ?, ?)";
                        stmt = conn.prepareStatement(query);
                        stmt.setString(1, name);
                        stmt.setString(2, email);
                        stmt.setString(3, password);
                        stmt.setString(4, dob);
        
                        stmt.executeUpdate();
                        JOptionPane.showMessageDialog(frame, "Registration Successful!");
        
                        // Clear the input fields after successful registration
                        nameField.setText("");
                        emailField.setText("");
                        passwordField.setText("");
                        dobField.setText("YYYY-MM-DD");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                } finally {
                    try {
                        if (rs != null) rs.close();
                        if (stmt != null) stmt.close();
                        if (conn != null) conn.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });        

        // Make the window visible
        frame.setVisible(true);
    }
}
