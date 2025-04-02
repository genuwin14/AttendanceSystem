import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginScreen extends JFrame {

    public LoginScreen() {
        // Set up the JFrame
        setTitle("Attendance System Login");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null); // Use null layout (absolute positioning)

        // Center the window on the screen
        setLocationRelativeTo(null);

        // Set background color to #191919 (dark grey)
        getContentPane().setBackground(Color.decode("#191919"));

        // Custom fonts
        Font labelFont = new Font("Arial", Font.PLAIN, 16);
        Font buttonFont = new Font("Arial", Font.BOLD, 14);

        // Title label
        JLabel titleLabel = new JLabel("User Login", JLabel.CENTER);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);

        // Set the position and size for the title label
        titleLabel.setBounds(0, 50, 400, 50); // X, Y, Width, Height
        add(titleLabel);

        // Define widths and heights for components
        int fieldWidth = 300;
        int fieldHeight = 40;
        int labelWidth = 100;
        int labelHeight = 30;
        int buttonWidth = 300;
        int buttonHeight = 40;

        // Calculate the X position for centering based on frame width
        int xPosition = (getWidth() - fieldWidth) / 2;

        // Set a margin for the label (to align with the input fields)

        // Email label and field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setBounds(50, 130, labelWidth, labelHeight); // X, Y, Width, Height
        add(emailLabel);

        JTextField emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setBackground(Color.decode("#333333"));
        emailField.setForeground(Color.WHITE);
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#dddddd"), 1), // Outer border
            BorderFactory.createEmptyBorder(10, 10, 10, 10) // Padding inside the field (top, left, bottom, right)
        ));
        emailField.setBounds(xPosition, 160, fieldWidth, fieldHeight); // X, Y, Width, Height
        add(emailField);

        // Password label and field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(labelFont);
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setBounds(50, 210, labelWidth, labelHeight); // X, Y, Width, Height
        add(passwordLabel);

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBackground(Color.decode("#333333"));
        passwordField.setForeground(Color.WHITE);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#dddddd"), 1), // Outer border
            BorderFactory.createEmptyBorder(10, 10, 10, 10) // Padding inside the field (top, left, bottom, right)
        ));
        passwordField.setBounds(xPosition, 240, fieldWidth, fieldHeight); // X, Y, Width, Height
        add(passwordField);

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(buttonFont);
        loginButton.setBackground(Color.decode("#FF4D00"));
        loginButton.setBorder(BorderFactory.createLineBorder(Color.decode("#FF4D00"), 1));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBounds(xPosition, 330, buttonWidth, buttonHeight); // X, Y, Width, Height
        add(loginButton);

        // Go to sign-up button
        JButton backButton = new JButton("Do you have an account? Register");
        backButton.setFont(buttonFont);
        backButton.setBackground(Color.decode("#333333"));
        loginButton.setBorder(BorderFactory.createLineBorder(Color.decode("#dddddd"), 1));
        backButton.setForeground(Color.WHITE);
        backButton.setBounds(xPosition, 380, buttonWidth, buttonHeight); // X, Y, Width, Height
        add(backButton);

        // Button actions
        backButton.addActionListener(_ -> {
            dispose();
            AttendanceSystem.main(null); // Navigate back to Sign-Up
        });

        loginButton.addActionListener(_ -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/AttendanceSystem", "root", "");

                String query = "SELECT * FROM users WHERE email = ? AND password = ?";
                stmt = conn.prepareStatement(query);
                stmt.setString(1, email);
                stmt.setString(2, password);
                rs = stmt.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login Successful!");
                    dispose();
                    DashboardScreen.main(null); // Navigate to the dashboard
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid email or password. Please try again.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginScreen().setVisible(true);
        });
    }
}
