import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class DashboardScreen {

    private static Connection connection;

    public static void main(String[] args) {
        // Establish the database connection
        connectToDatabase();
    
        // Create the JFrame for Dashboard
        JFrame frame = new JFrame("Dashboard");
        frame.setSize(900, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Set the background color of the content pane to #191919
        frame.getContentPane().setBackground(Color.decode("#191919"));

        // Make the window visible
        frame.setVisible(true);
    
        // Center the window on the screen
        frame.setLocationRelativeTo(null);
    
        // Create a JPanel for the title and set its background color
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.decode("#191919")); // Set background color of the title panel

        // Create and style the title label
        JLabel titleLabel = new JLabel("Welcome to the Attendance System Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE); // Set the color for the title text

        // Add the title label to the title panel
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Create a toggle button to show/hide the sidebar
        JButton toggleButton = new JButton("|||");
        toggleButton.setFont(new Font("Arial", Font.BOLD, 10));
        toggleButton.setBackground(Color.decode("#333333")); // Set the button background color
        toggleButton.setForeground(Color.WHITE); // Set the button text color
        toggleButton.setPreferredSize(new Dimension(45, 20)); // Set preferred size for the button

        // Add the toggle button to the title panel (you can decide whether you want it on the left, right, etc.)
        titlePanel.add(toggleButton, BorderLayout.EAST); // You can place it where needed, e.g., EAST

        // Now add the title panel to the JFrame (or another container as needed)
        frame.add(titlePanel, BorderLayout.NORTH); // Position the title panel at the top of the frame
    
        // Add toggle button to the left of the title
        titlePanel.add(toggleButton, BorderLayout.WEST); // Position toggle button on the left
        titlePanel.add(titleLabel, BorderLayout.CENTER); // Keep title in the center
    
        // Create a panel for sidebar
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS)); // Vertical layout
        sidebarPanel.setPreferredSize(new Dimension(200, 500)); // Sidebar width
        sidebarPanel.setBackground(Color.decode("#333333"));
    
        // Sidebar buttons
        JButton addSectionButton = new JButton("Add Section");
        JButton addStudentButton = new JButton("Add Student");
        JButton viewUserButton = new JButton("View User");
        JButton attendanceRecordsButton = new JButton("Attendance Records");
        JButton scanQRCodeButton = new JButton("Scan QR Code");
    
        // Set button styles
        Font buttonFont = new Font("Times New Roman", Font.BOLD, 16);
        addSectionButton.setFont(buttonFont);
        addStudentButton.setFont(buttonFont);
        viewUserButton.setFont(buttonFont);
        attendanceRecordsButton.setFont(buttonFont);
        scanQRCodeButton.setFont(buttonFont);
    
        // Set all buttons to have the same preferred width (full sidebar width)
        Dimension buttonSize = new Dimension(200, 40); // Button width matches sidebar width
        addSectionButton.setPreferredSize(buttonSize);
        addStudentButton.setPreferredSize(buttonSize);
        viewUserButton.setPreferredSize(buttonSize);
        attendanceRecordsButton.setPreferredSize(buttonSize);
        scanQRCodeButton.setPreferredSize(buttonSize);

        // Button Background Color
        addSectionButton.setBackground(Color.decode("#FF4D00"));
        addStudentButton.setBackground(Color.decode("#FF4D00"));
        viewUserButton.setBackground(Color.decode("#FF4D00"));
        attendanceRecordsButton.setBackground(Color.decode("#FF4D00"));
        scanQRCodeButton.setBackground(Color.decode("#FF4D00"));

        // Button Text Color
        addSectionButton.setForeground(Color.WHITE);
        addStudentButton.setForeground(Color.WHITE);
        viewUserButton.setForeground(Color.WHITE);
        attendanceRecordsButton.setForeground(Color.WHITE);
        scanQRCodeButton.setForeground(Color.WHITE);
    
        // Set the maximum size to avoid any resizing based on content
        addSectionButton.setMaximumSize(buttonSize);
        addStudentButton.setMaximumSize(buttonSize);
        viewUserButton.setMaximumSize(buttonSize);
        attendanceRecordsButton.setMaximumSize(buttonSize);
        scanQRCodeButton.setMaximumSize(buttonSize);

        // Add action listeners to buttons
        addSectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Replace content panel with Add Section form
                showAddSectionForm(frame);
            }
        });

        addStudentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Retrieve the current content panel from the frame
                JSplitPane splitPane = (JSplitPane) frame.getContentPane().getComponent(1); // Get the split pane
                JPanel contentPanel = (JPanel) splitPane.getRightComponent(); // Get the content panel
        
                // Create a new panel for "Add Student"
                JPanel addStudentPanel = new JPanel();
                addStudentPanel.setLayout(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(10, 10, 10, 10); // Padding around components
                gbc.anchor = GridBagConstraints.WEST; // Align components to the left
        
                // Font for components
                Font font = new Font("Arial", Font.PLAIN, 16);
        
                // Add input fields and labels
                JLabel firstNameLabel = new JLabel("First Name:");
                firstNameLabel.setFont(font);  // Set font size for label
                JTextField firstNameField = new JTextField(20);
                firstNameField.setFont(font);  // Set font size for text field
        
                JLabel lastNameLabel = new JLabel("Last Name:");
                lastNameLabel.setFont(font);  // Set font size for label
                JTextField lastNameField = new JTextField(20);
                lastNameField.setFont(font);  // Set font size for text field
        
                JLabel genderLabel = new JLabel("Gender:");
                genderLabel.setFont(font);  // Set font size for label
                JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
                genderComboBox.setFont(font);  // Set font size for dropdown
        
                JLabel sectionLabel = new JLabel("Section:");
                sectionLabel.setFont(font);  // Set font size for label
                JComboBox<String> sectionDropdown = new JComboBox<>();
                sectionDropdown.setFont(font);  // Set font size for dropdown
                loadSectionsIntoDropdown(sectionDropdown);
        
                // Add Save button
                JButton saveButton = new JButton("Save");
                saveButton.setPreferredSize(new Dimension(150, 40));
                saveButton.setFont(font);  // Set font size for button
                saveButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String firstName = firstNameField.getText().trim();
                        String lastName = lastNameField.getText().trim();
                        String gender = (String) genderComboBox.getSelectedItem();
                        String selectedSection = (String) sectionDropdown.getSelectedItem();
        
                        if (firstName.isEmpty() || lastName.isEmpty() || selectedSection == null) {
                            JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
                        } else {
                            try {
                                // Instead of getting the section_id, directly use the section_name
                                String sectionName = selectedSection;
        
                                // Generate QR Code content, including section_name
                                String qrCodeContent = "Name: " + firstName + " " + lastName + ", Gender: " + gender + ", Section: " + sectionName;
        
                                // Generate QR Code as Base64 string and save the PNG file
                                String qrCodeBase64 = generateQRCode(firstName, lastName, qrCodeContent);
        
                                // Insert student into the database with QR Code Base64 string
                                String query = "INSERT INTO students (first_name, last_name, gender, section_name, qrcode) VALUES (?, ?, ?, ?, ?)";
                                PreparedStatement stmt = connection.prepareStatement(query);
                                stmt.setString(1, firstName);
                                stmt.setString(2, lastName);
                                stmt.setString(3, gender);
                                stmt.setString(4, sectionName); // Save section name instead of section id
                                stmt.setString(5, qrCodeBase64); // Save QR Code Base64 string
                                stmt.executeUpdate();
        
                                JOptionPane.showMessageDialog(frame, "Student added successfully!");
        
                                // Clear input fields
                                firstNameField.setText("");
                                lastNameField.setText("");
                                genderComboBox.setSelectedIndex(0);
                                sectionDropdown.setSelectedIndex(0);
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });
        
                // Add components to the panel with proper GridBagLayout constraints
                gbc.gridx = 0; gbc.gridy = 0; addStudentPanel.add(firstNameLabel, gbc);
                gbc.gridx = 1; addStudentPanel.add(firstNameField, gbc);
        
                gbc.gridx = 0; gbc.gridy = 1; addStudentPanel.add(lastNameLabel, gbc);
                gbc.gridx = 1; addStudentPanel.add(lastNameField, gbc);
        
                gbc.gridx = 0; gbc.gridy = 2; addStudentPanel.add(genderLabel, gbc);
                gbc.gridx = 1; addStudentPanel.add(genderComboBox, gbc);
        
                gbc.gridx = 0; gbc.gridy = 3; addStudentPanel.add(sectionLabel, gbc);
                gbc.gridx = 1; addStudentPanel.add(sectionDropdown, gbc);
        
                gbc.gridx = 1; gbc.gridy = 4; addStudentPanel.add(saveButton, gbc);
        
                // Update the content panel with the Add Student form
                contentPanel.removeAll(); // Clear current content
                contentPanel.add(addStudentPanel, BorderLayout.CENTER); // Add new form to content panel
                contentPanel.revalidate(); // Revalidate the content panel to refresh the display
                contentPanel.repaint(); // Repaint the content panel to reflect the changes
            }
        });
        
        viewUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show student table
                showStudentTable(frame);
            }
        });

        attendanceRecordsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Retrieve the current content panel from the frame
                JSplitPane splitPane = (JSplitPane) frame.getContentPane().getComponent(1); // Get the split pane
                JPanel contentPanel = (JPanel) splitPane.getRightComponent(); // Get the content panel

                // Clear the current content
                contentPanel.removeAll();
                contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // Use BoxLayout to stack tables vertically

                // Query the database to get data from the "records" table
                String query = "SELECT name, gender, date_scanned, section_name FROM records ORDER BY section_name";
                try (Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery(query)) {

                    // HashMap to group records by section_name
                    Map<String, DefaultTableModel> sectionModels = new LinkedHashMap<>();
                    
                    while (rs.next()) {
                        String name = rs.getString("name");
                        String gender = rs.getString("gender");
                        String dateTime = rs.getString("date_scanned");
                        String sectionName = rs.getString("section_name");

                        // Parse the date_scanned into separate Date and Time columns
                        String[] dateTimeParts = dateTime.split(" "); // Split by space
                        String date = dateTimeParts[0]; // Date part (YYYY-MM-DD)
                        String time = dateTimeParts[1]; // Time part (HH:mm:ss)

                        // Create a new table model for each section if it doesn't exist
                        if (!sectionModels.containsKey(sectionName)) {
                            DefaultTableModel tableModel = new DefaultTableModel();
                            tableModel.addColumn("Name");
                            tableModel.addColumn("Gender");
                            tableModel.addColumn("Date");
                            tableModel.addColumn("Time");
                            tableModel.addColumn("Section");

                            sectionModels.put(sectionName, tableModel);
                        }

                        // Add the row to the correct table model for the section
                        DefaultTableModel tableModel = sectionModels.get(sectionName);
                        tableModel.addRow(new Object[]{name, gender, date, time, sectionName});
                    }

                    // Create a table for each section and add them to the content panel
                    for (Map.Entry<String, DefaultTableModel> entry : sectionModels.entrySet()) {
                        String sectionName = entry.getKey();
                        DefaultTableModel tableModel = entry.getValue();

                        // Create a JTable with the table model
                        JTable sectionTable = new JTable(tableModel);

                        // Set the font size for the table content
                        Font tableFont = new Font("Arial", Font.PLAIN, 14);
                        sectionTable.setFont(tableFont);

                        // Set column widths
                        sectionTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                        sectionTable.getColumnModel().getColumn(0).setPreferredWidth(200); // Name column
                        sectionTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Gender column
                        sectionTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Date column
                        sectionTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Time column
                        sectionTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Section column

                        // Center align the data in all columns
                        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
                        centerRenderer.setHorizontalAlignment(JLabel.CENTER);  // Center the data horizontally

                        for (int i = 0; i < tableModel.getColumnCount(); i++) {
                            sectionTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
                        }

                        // Add the section name as a title for each table
                        JPanel sectionPanel = new JPanel(new BorderLayout());
                        JLabel sectionTitle = new JLabel("Section: " + sectionName, JLabel.CENTER);
                        sectionTitle.setFont(new Font("Arial", Font.BOLD, 16));
                        sectionPanel.add(sectionTitle, BorderLayout.NORTH);
                        
                        // Add the table to a scroll pane
                        JScrollPane scrollPane = new JScrollPane(sectionTable);
                        sectionPanel.add(scrollPane, BorderLayout.CENTER);

                        // Add the section panel to the content panel
                        contentPanel.add(sectionPanel);
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error fetching data from the database.");
                    return;
                }

                // Revalidate and repaint the content panel to show the new tables
                contentPanel.revalidate();
                contentPanel.repaint();
            }
        });

        scanQRCodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Declare the webcam reference
                final Webcam[] activeWebcam = new Webcam[1];
        
                // Retrieve the current content panel from the frame
                JSplitPane splitPane = (JSplitPane) frame.getContentPane().getComponent(1); // Get the split pane
                JPanel contentPanel = (JPanel) splitPane.getRightComponent(); // Get the content panel
        
                // Add a listener to close the webcam when navigating away
                contentPanel.addPropertyChangeListener("ancestor", _ -> {
                    if (activeWebcam[0] != null && activeWebcam[0].isOpen()) {
                        activeWebcam[0].close(); // Close the webcam when navigating away
                    }
                });
        
                // Initialize the webcam
                activeWebcam[0] = Webcam.getDefault(); // Get the default webcam
                if (activeWebcam[0].isOpen()) {
                    activeWebcam[0].close(); // Close the webcam if already open
                }
                activeWebcam[0].setViewSize(WebcamResolution.VGA.getSize()); // Set resolution to VGA (640x480)
                activeWebcam[0].open(); // Open the webcam after setting the resolution
        
                // Create a new panel for the camera display
                JPanel scanQRCodePanel = new JPanel();
                scanQRCodePanel.setLayout(new BorderLayout());
        
                // Create a panel to display the webcam feed
                WebcamPanel webcamPanel = new WebcamPanel(activeWebcam[0]);
                webcamPanel.setPreferredSize(new Dimension(600, 400)); // Set the camera box size
                webcamPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Add border to the panel
                webcamPanel.setFPSDisplayed(true); // Display the FPS of the webcam feed
        
                // Add the webcam panel to the center of the scan QR code panel
                scanQRCodePanel.add(webcamPanel, BorderLayout.CENTER);
        
                // Update the content panel with the Scan QR Code panel
                contentPanel.removeAll(); // Clear current content
                contentPanel.add(scanQRCodePanel, BorderLayout.CENTER); // Add new scan QR panel to content
                contentPanel.revalidate(); // Revalidate the content panel to refresh the display
                contentPanel.repaint(); // Repaint the content panel to reflect the changes
        
                // Start scanning QR code continuously
                new Thread(() -> {
                    while (activeWebcam[0] != null && activeWebcam[0].isOpen()) {
                        BufferedImage image = activeWebcam[0].getImage();
                        try {
                            // Create a LuminanceSource from the webcam image
                            LuminanceSource source = new BufferedImageLuminanceSource(image);
                            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        
                            // Use the ZXing QRCodeReader to decode the QR code
                            QRCodeReader reader = new QRCodeReader();
                            com.google.zxing.Result result = reader.decode(bitmap);
        
                            // Get the QR code data
                            String qrData = result.getText();
        
                            // Print the QR data to help debug
                            System.out.println("Scanned QR Data: " + qrData); // Debugging line
        
                            // Parse the QR data (assuming it's in the format: "Name: John Doe, Gender: Male, Section: A1")
                            String[] parts = qrData.split(", ");
                            if (parts.length < 3) {
                                System.out.println("QR Data format is invalid: " + qrData);
                                return; // Exit the scanning process if the data format is wrong
                            }
        
                            String name = parts[0].split(": ")[1];  // Extract Name
                            String gender = parts[1].split(": ")[1]; // Extract Gender
                            String sectionName = parts[2].split(": ")[1]; // Extract Section
        
                            // Get the current date and time
                            LocalDateTime now = LocalDateTime.now();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            String dateScanned = now.format(formatter);
        
                            // Insert the data into the records table in the database (including section_name)
                            String query = "INSERT INTO records (name, gender, section_name, date_scanned) VALUES (?, ?, ?, ?)";
                            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                                stmt.setString(1, name);
                                stmt.setString(2, gender);
                                stmt.setString(3, sectionName); // Insert the section name
                                stmt.setString(4, dateScanned);
                                stmt.executeUpdate();
        
                                // Display success message with Name included
                                String successMessage = "QR Code scanned and attendance for " + name + " in Section " + sectionName + " saved successfully!";
                                JOptionPane.showMessageDialog(frame, successMessage);
        
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(frame, "Error saving data to the database.");
                            }
        
                        } catch (NotFoundException ex) {
                            // QR Code not found; continue scanning
                        } catch (ChecksumException | FormatException ex) {
                            JOptionPane.showMessageDialog(frame, "Error decoding the QR Code.");
                        }
                    }
                }).start();
            }
        });
        
        // Add buttons to sidebar
        sidebarPanel.add(addSectionButton);
        sidebarPanel.add(addStudentButton);
        sidebarPanel.add(viewUserButton);
        sidebarPanel.add(attendanceRecordsButton);
        sidebarPanel.add(scanQRCodeButton);

        // Create the "Log Out" button
        JButton logoutButton = new JButton("Log Out");
        logoutButton.setFont(buttonFont);
        logoutButton.setBackground(Color.decode("#FF4D00"));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setPreferredSize(buttonSize);
        logoutButton.setMaximumSize(buttonSize);
        logoutButton.addActionListener(_ -> {
            frame.dispose(); // Close the dashboard
            SwingUtilities.invokeLater(() -> {
                new LoginScreen().setVisible(true); // Navigate to LoginScreen
            });
        });               

        // Add the Log Out button to the bottom of the sidebar
        sidebarPanel.add(Box.createVerticalGlue()); // This pushes the Log Out button to the bottom
        sidebarPanel.add(logoutButton);

        // Create a panel for the content area
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(Color.decode("#333333"));
        // JLabel contentLabel = new JLabel("This is the Dashboard. You can add more features here.", JLabel.CENTER);
        // contentLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        // contentPanel.add(contentLabel, BorderLayout.CENTER);

        // Create a JSplitPane to split the sidebar and content area
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebarPanel, contentPanel);
        splitPane.setDividerLocation(200); // Initial sidebar width
        splitPane.setDividerSize(10); // Size of the divider between panels

        // Add split pane to the frame
        frame.add(titlePanel, BorderLayout.NORTH);
        frame.add(splitPane, BorderLayout.CENTER);

        // Action to toggle sidebar visibility
        toggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Toggle the visibility of the sidebar
                if (sidebarPanel.isVisible()) {
                    sidebarPanel.setVisible(false);
                    splitPane.setDividerLocation(0); // Move divider to the left
                } else {
                    sidebarPanel.setVisible(true);
                    splitPane.setDividerLocation(200); // Reset divider location to sidebar width
                }
            }
        });

        // Make the window visible
        frame.setVisible(true);
    }

    // Utility function to load sections into the dropdown
    private static Map<String, Integer> loadSectionsIntoDropdown(JComboBox<String> sectionDropdown) {
        Map<String, Integer> sectionMap = new HashMap<>();
        try {
            String query = "SELECT id, section_name FROM sections";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                int id = rs.getInt("id");
                String sectionName = rs.getString("section_name");
                sectionDropdown.addItem(sectionName); // Add section name to dropdown
                sectionMap.put(sectionName, id); // Map section name to its ID
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sectionMap;
    }

    private static void connectToDatabase() {
        try {
            // Setup connection to MySQL database
            String url = "jdbc:mysql://localhost:3306/attendancesystem";
            String username = "root";  // Replace with your MySQL username
            String password = "";  // Replace with your MySQL password

            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to the database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String generateQRCode(String firstName, String lastName, String content) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 200, 200);
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
    
            // Save QR code as .png in the qr_codes folder
            String filePath = "qr_codes/" + firstName + "_" + lastName + ".png";
            File qrCodeFile = new File(filePath);
            ImageIO.write(qrImage, "PNG", qrCodeFile);  // Save QR code as file
    
            // Convert image to Base64 string
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);  // Return Base64 encoded string
    
        } catch (WriterException | java.io.IOException e) {
            e.printStackTrace();
        }
        return null;
    }      

    private static void showAddSectionForm(JFrame parentFrame) {
        // Retrieve the current content panel from the frame
        JSplitPane splitPane = (JSplitPane) parentFrame.getContentPane().getComponent(1); // Get the split pane
        JPanel contentPanel = (JPanel) splitPane.getRightComponent(); // Get the content panel

        // Create the Add Section panel
        JPanel addSectionPanel = new JPanel();
        addSectionPanel.setLayout(new BorderLayout());

        // Top panel for input fields and Save button
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)); // Align left with spacing

        // Input fields
        inputPanel.add(new JLabel("Section Name:")); // Add label
        JTextField sectionNameField = new JTextField(15);
        inputPanel.add(sectionNameField); // Add text field

        // Save button
        JButton saveButton = new JButton("Save Section");
        inputPanel.add(saveButton); // Add button

        // Add the input panel to the top of the Add Section panel
        addSectionPanel.add(inputPanel, BorderLayout.NORTH);

        // Bottom panel for the table
        JPanel tablePanel = new JPanel(new BorderLayout());
        JTable sectionTable = new JTable();

        // Fetch and display sections in the table
        Vector<Vector<Object>> data = new Vector<>();
        Vector<String> columnNames = new Vector<>();
        columnNames.add("ID");
        columnNames.add("Section Name");

        try {
            // Query the database for sections
            String query = "SELECT id, section_name FROM sections";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("section_name"));
                data.add(row);
            }

            // Create the table model with fetched data
            DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
            sectionTable.setModel(tableModel);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(sectionTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Add the table panel to the bottom of the Add Section panel
        addSectionPanel.add(tablePanel, BorderLayout.CENTER);

        // Save button action listener
        saveButton.addActionListener(_ -> {
            String sectionName = sectionNameField.getText().trim();

            if (sectionName.isEmpty()) {
                JOptionPane.showMessageDialog(parentFrame, "Section Name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Insert the new section into the database
                String insertQuery = "INSERT INTO sections (section_name) VALUES (?)";
                PreparedStatement pstmt = connection.prepareStatement(insertQuery);
                pstmt.setString(1, sectionName);
                pstmt.executeUpdate();

                // Refresh the table
                DefaultTableModel model = (DefaultTableModel) sectionTable.getModel();
                model.setRowCount(0); // Clear existing rows

                // Create a new Statement here for querying
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id, section_name FROM sections");
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("id"));
                    row.add(rs.getString("section_name"));
                    model.addRow(row);
                }

                sectionNameField.setText(""); // Clear input field
                JOptionPane.showMessageDialog(parentFrame, "Section added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "Error saving section.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Update the content panel with the Add Section panel
        contentPanel.removeAll(); // Clear current content
        contentPanel.add(addSectionPanel, BorderLayout.CENTER); // Add new Add Section panel
        contentPanel.revalidate(); // Revalidate the content panel to refresh the display
        contentPanel.repaint(); // Repaint the content panel to reflect the changes
    }

    private static void showStudentTable(JFrame parentFrame) {
        // Retrieve the current content panel from the frame
        JSplitPane splitPane = (JSplitPane) parentFrame.getContentPane().getComponent(1); // Get the split pane
        JPanel contentPanel = (JPanel) splitPane.getRightComponent(); // Get the content panel
    
        // Create a new panel for the student table
        JPanel studentTablePanel = new JPanel(new BorderLayout());
    
        // Query the database to get student records
        Vector<Vector<Object>> data = new Vector<>();
        try {
            // SQL query to join students and sections table
            String query = """
                SELECT 
                    students.id, 
                    students.first_name, 
                    students.last_name, 
                    students.gender, 
                    students.section_name,  -- Changed from 'sections.section_name' to 'students.section_name'
                    students.qrcode
                FROM students
            """;
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
    
            // Define column names manually
            Vector<String> columnNames = new Vector<>();
            columnNames.add("ID");
            columnNames.add("First Name");
            columnNames.add("Last Name");
            columnNames.add("Gender");
            columnNames.add("Section");
            columnNames.add("QR Code");
    
            // Retrieve data and decode QR Code Base64
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= columnNames.size(); i++) {
                    if (i == columnNames.size()) { // QR Code column
                        String qrCodeBase64 = rs.getString(i);
                        BufferedImage qrImage = decodeQRCodeBase64(qrCodeBase64);
    
                        if (qrImage != null) {
                            // Resize the QR code to fit in the table cell
                            Image scaledImage = qrImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                            row.add(new ImageIcon(scaledImage));
                        } else {
                            row.add("No QR");
                        }
                    } else {
                        row.add(rs.getObject(i)); // Add other column data
                    }
                }
                data.add(row);
            }
    
            // Create the table with the data and custom renderer for the QR code
            JTable table = new JTable(data, columnNames) {
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == columnNames.size() - 1) { // QR Code column
                        return ImageIcon.class; // Set the QR Code column to be rendered as an image
                    }
                    return super.getColumnClass(columnIndex);
                }
    
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Make table non-editable
                }
            };
    
            // Set font size for the table content
            Font tableFont = new Font("Arial", Font.PLAIN, 14);
            table.setFont(tableFont);
            table.setRowHeight(50); // Match the scaled image height
    
            // Center align text columns (excluding QR Code column)
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            for (int i = 0; i < columnNames.size() - 1; i++) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
    
            // Add the table to a scroll pane
            JScrollPane scrollPane = new JScrollPane(table);
            studentTablePanel.add(scrollPane, BorderLayout.CENTER);
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        // Update the content panel with the student table
        contentPanel.removeAll(); // Clear current content
        contentPanel.add(studentTablePanel, BorderLayout.CENTER); // Add new student table
        contentPanel.revalidate(); // Revalidate the content panel to refresh the display
        contentPanel.repaint(); // Repaint the content panel to reflect the changes
    }
    
    private static BufferedImage decodeQRCodeBase64(String base64) {
        try {
            if (base64 == null || base64.isEmpty()) {
                System.out.println("Base64 string is null or empty.");
                return null;
            }

            // Decode the Base64 string into a byte array
            byte[] imageBytes = Base64.getDecoder().decode(base64);
            System.out.println("Decoded Base64 string into byte array, length: " + imageBytes.length);

            ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
            BufferedImage img = ImageIO.read(bais);

            if (img != null) {
                System.out.println("Successfully decoded image.");
                // Optionally save the image to disk to verify it
                try {
                    File outputfile = new File("decoded_image.png");
                    ImageIO.write(img, "PNG", outputfile);
                    System.out.println("Saved image to disk as decoded_image.png.");
                } catch (IOException e) {
                    System.out.println("Failed to save image to disk.");
                    e.printStackTrace();
                }
                return img;
            } else {
                System.out.println("Failed to decode image: BufferedImage is null.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid Base64 format.");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    static class QRCodeCellRenderer extends JLabel implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            if (value instanceof BufferedImage) {
                ImageIcon icon = new ImageIcon((BufferedImage) value);
                setIcon(icon);
                setText(""); // Remove any default text
            } else {
                setIcon(null);
                setText("No QR");
            }
            return this;
        }
    }    
}
