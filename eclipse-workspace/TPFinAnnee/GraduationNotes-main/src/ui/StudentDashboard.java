package ui;

import dao.GradeDAO;
import dao.SubjectDAO;
import model.Grade;
import model.Student;
import model.Subject;
import security.AuthenticationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Dashboard for student users.
 * SECURITY FLAWS:
 * 1. No access control (can view/modify other students' grades)
 * 2. No input validation
 * 3. No CSRF protection
 * 4. XSS vulnerability in displaying comments
 */
public class StudentDashboard extends JFrame {
    
    private Student student;
    private GradeDAO gradeDAO;
    private SubjectDAO subjectDAO;
    
    private JTable gradesTable;
    private DefaultTableModel tableModel;
    private JTextField studentIdField; // SECURITY FLAW: Allowing to enter any student ID
    private JButton viewGradesButton;
    private JButton logoutButton;
    private JLabel averageLabel;
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    
    public StudentDashboard(Student student) {
        this.student = student;
        this.gradeDAO = new GradeDAO();
        this.subjectDAO = new SubjectDAO();
        
        setTitle("Student Dashboard - " + student.getFullName());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        loadGrades(student.getId()); // Initially load the current student's grades
    }
    
    private void initComponents() {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome, " + student.getFullName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        // Student ID input panel (SECURITY FLAW: Allowing to view any student's grades)
        JPanel studentIdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        studentIdPanel.add(new JLabel("Student ID:"));
        studentIdField = new JTextField(10);
        studentIdField.setText(String.valueOf(student.getId())); // Default to current student
        studentIdPanel.add(studentIdField);
        
        viewGradesButton = new JButton("View Grades");
        viewGradesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int studentId = Integer.parseInt(studentIdField.getText());
                    loadGrades(studentId);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(StudentDashboard.this, 
                            "Invalid student ID", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        studentIdPanel.add(viewGradesButton);
        
        // Average label
        averageLabel = new JLabel("Overall Average: N/A");
        averageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        studentIdPanel.add(Box.createHorizontalStrut(50));
        studentIdPanel.add(averageLabel);
        
        // Table for grades
        String[] columns = {"ID", "Subject", "Title", "Value", "Coefficient", "Date", "Comment"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // SECURITY FLAW: Allow editing the grade value
                return column == 3; // Only the value column is editable
            }
        };
        
        gradesTable = new JTable(tableModel);
        gradesTable.getColumnModel().getColumn(0).setPreferredWidth(30); // ID
        gradesTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Subject
        gradesTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Title
        gradesTable.getColumnModel().getColumn(3).setPreferredWidth(50); // Value
        gradesTable.getColumnModel().getColumn(4).setPreferredWidth(80); // Coefficient
        gradesTable.getColumnModel().getColumn(5).setPreferredWidth(80); // Date
        gradesTable.getColumnModel().getColumn(6).setPreferredWidth(200); // Comment
        
        // SECURITY FLAW: Allow editing grades directly in the table
        gradesTable.getModel().addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                
                if (column == 3) { // Value column
                    updateGrade(row);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(gradesTable);
        
        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(studentIdPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);
        
        // Set main panel as content pane
        setContentPane(mainPanel);
    }
    
    // SECURITY FLAW: No access control, any student can view any other student's grades
    private void loadGrades(int studentId) {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Load grades for the specified student
        List<Grade> grades = gradeDAO.getGradesByStudentId(studentId);
        
        for (Grade grade : grades) {
            Object[] row = {
                grade.getId(),
                grade.getSubject().getName(),
                grade.getTitle(),
                grade.getValue(),
                grade.getCoefficient(),
                grade.getDate() != null ? DATE_FORMAT.format(grade.getDate()) : "",
                grade.getComment() // SECURITY FLAW: XSS vulnerability (no escaping of HTML)
            };
            tableModel.addRow(row);
        }
        
        // Update average
        double average = gradeDAO.calculateOverallAverageForStudent(studentId);
        averageLabel.setText("Overall Average: " + String.format("%.2f", average));
    }
    
    // SECURITY FLAW: No access control, any student can modify any grade
    private void updateGrade(int row) {
        try {
            int gradeId = (int) tableModel.getValueAt(row, 0);
            double newValue = Double.parseDouble(tableModel.getValueAt(row, 3).toString());
            
            // Get the grade from database
            Grade grade = gradeDAO.getGradeById(gradeId);
            
            if (grade != null) {
                // Update the grade value
                grade.setValue(newValue);
                
                // Save to database
                boolean success = gradeDAO.updateGrade(grade);
                
                if (success) {
                    System.out.println("Grade updated successfully: " + gradeId);
                    
                    // Refresh the average
                    int studentId = Integer.parseInt(studentIdField.getText());
                    double average = gradeDAO.calculateOverallAverageForStudent(studentId);
                    averageLabel.setText("Overall Average: " + String.format("%.2f", average));
                } else {
                    JOptionPane.showMessageDialog(this, 
                            "Failed to update grade", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                    "Invalid grade value. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
            
            // Reload grades to reset the invalid value
            int studentId = Integer.parseInt(studentIdField.getText());
            loadGrades(studentId);
        }
    }
    
    private void logout() {
        AuthenticationService.logout();
        
        // Open login screen
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
                dispose(); // Close current window
            }
        });
    }
}