package ui;

import model.Student;
import model.Teacher;
import model.User;
import security.AuthenticationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Login screen for the application.
 * SECURITY FLAWS:
 * 1. Password shown in plain text
 * 2. No input validation
 * 3. No HTTPS/SSL
 * 4. No protection against brute force attacks
 */
public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JTextField passwordField; // SECURITY FLAW: Using JTextField instead of JPasswordField
    private JButton loginButton;
    private JLabel statusLabel;

    private AuthenticationService authService;

    public LoginFrame() {
        authService = new AuthenticationService();

        setTitle("Student Grade Manager - Login");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();

        // SECURITY FLAW: Hardcoded credentials in comments
        // Default users:
        // admin/admin123
        // teacher1/teacher123
        // student1/student123
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JTextField(20); // SECURITY FLAW: Using JTextField instead of JPasswordField

        loginButton = new JButton("Login");
        statusLabel = new JLabel("");
        statusLabel.setForeground(Color.RED);

        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(new JLabel("")); // Empty cell for alignment
        formPanel.add(loginButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        // Add action listener to login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        add(mainPanel);
    }

    private void login() {
        String username = usernameField.getText();
        String password = passwordField.getText(); // SECURITY FLAW: Getting password as plain text

        // SECURITY FLAW: No input validation

        System.out.println("Login attempt: " + username + " / " + password); // SECURITY FLAW: Logging credentials

        if (authService.authenticate(username, password)) {
            User currentUser = AuthenticationService.getCurrentUser();

            // Open appropriate dashboard based on user role
            if (currentUser instanceof Student) {
                openStudentDashboard((Student) currentUser);
            } else if (currentUser instanceof Teacher) {
                openTeacherDashboard((Teacher) currentUser);
            } else if ("admin".equals(currentUser.getRole())) {
                openAdminDashboard(currentUser);
            } else {
                statusLabel.setText("Unknown user role: " + currentUser.getRole());
            }

            // Close login window
            dispose();
        } else {
            statusLabel.setText("Invalid username or password");
        }
    }

    private void openStudentDashboard(Student student) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                StudentDashboard dashboard = new StudentDashboard(student);
                dashboard.setVisible(true);
            }
        });
    }

    private void openTeacherDashboard(Teacher teacher) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TeacherDashboard dashboard = new TeacherDashboard(teacher);
                dashboard.setVisible(true);
            }
        });
    }

    private void openAdminDashboard(User admin) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                AdminDashboard dashboard = new AdminDashboard(admin);
                dashboard.setVisible(true);
            }
        });
    }

    // Main method for testing
    public static void main(String[] args) {
        // Initialize database
        dao.DatabaseConnection.initializeDatabase();

        // Create and show login frame
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            }
        });
    }
}

// Dashboards will be implemented in separate files
