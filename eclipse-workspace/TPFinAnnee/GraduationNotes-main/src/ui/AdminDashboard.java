package ui;

import dao.GradeDAO;
import dao.SubjectDAO;
import dao.UserDAO;
import model.Grade;
import model.Student;
import model.Subject;
import model.Teacher;
import model.User;
import security.AuthenticationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Dashboard for admin users.
 * SECURITY FLAWS:
 * 1. No access control validation
 * 2. No input validation
 * 3. No CSRF protection
 * 4. XSS vulnerability in displaying user data
 * 5. Passwords displayed in plain text
 */
public class AdminDashboard extends JFrame {

    private User admin;
    private UserDAO userDAO;
    private SubjectDAO subjectDAO;
    private GradeDAO gradeDAO;

    private JTabbedPane tabbedPane;
    private JTable usersTable;
    private DefaultTableModel usersTableModel;
    private JTable subjectsTable;
    private DefaultTableModel subjectsTableModel;
    private JTable gradesTable;
    private DefaultTableModel gradesTableModel;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public AdminDashboard(User admin) {
        this.admin = admin;
        this.userDAO = new UserDAO();
        this.subjectDAO = new SubjectDAO();
        this.gradeDAO = new GradeDAO();

        setTitle("Admin Dashboard - " + admin.getFullName());
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        // Main tabbed pane
        tabbedPane = new JTabbedPane();

        // Create tabs
        JPanel usersPanel = createUsersPanel();
        JPanel subjectsPanel = createSubjectsPanel();
        JPanel gradesPanel = createGradesPanel();

        // Add tabs to tabbed pane
        tabbedPane.addTab("Users", usersPanel);
        tabbedPane.addTab("Subjects", subjectsPanel);
        tabbedPane.addTab("Grades", gradesPanel);

        // Header panel with welcome message and logout button
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome, " + admin.getFullName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });

        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Users table
        String[] columns = {"ID", "Username", "Password", "Role", "Full Name", "Email", "Class Group", "Actions"};
        usersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0 && column != 7; // All columns except ID and Actions are editable
            }
        };

        usersTable = new JTable(usersTableModel);
        JScrollPane scrollPane = new JScrollPane(usersTable);

        // Add button column for delete action
        usersTable.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
        usersTable.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor(new JCheckBox()) {
            @Override
            protected void buttonClicked() {
                int row = usersTable.getSelectedRow();
                if (row >= 0) {
                    int userId = (int) usersTableModel.getValueAt(row, 0);
                    deleteUser(userId, row);
                }
            }
        });

        // SECURITY FLAW: Allow editing user data directly in the table
        usersTable.getModel().addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                updateUser(row);
            }
        });

        // Load users
        loadUsers();

        // Add user panel
        JPanel addUserPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addUserPanel.add(new JLabel("Add New User:"));

        JButton addUserButton = new JButton("Add User");
        addUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddUserDialog();
            }
        });
        addUserPanel.add(addUserButton);

        // Add components to panel
        panel.add(addUserPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSubjectsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Subjects table
        String[] columns = {"ID", "Code", "Name", "Description", "Default Coefficient", "Actions"};
        subjectsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0 && column != 5; // All columns except ID and Actions are editable
            }
        };

        subjectsTable = new JTable(subjectsTableModel);
        JScrollPane scrollPane = new JScrollPane(subjectsTable);

        // Add button column for delete action
        subjectsTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        subjectsTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()) {
            @Override
            protected void buttonClicked() {
                int row = subjectsTable.getSelectedRow();
                if (row >= 0) {
                    int subjectId = (int) subjectsTableModel.getValueAt(row, 0);
                    deleteSubject(subjectId, row);
                }
            }
        });

        // SECURITY FLAW: Allow editing subject data directly in the table
        subjectsTable.getModel().addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                updateSubject(row);
            }
        });

        // Load subjects
        loadSubjects();

        // Add subject panel
        JPanel addSubjectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addSubjectPanel.add(new JLabel("Add New Subject:"));

        JButton addSubjectButton = new JButton("Add Subject");
        addSubjectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddSubjectDialog();
            }
        });
        addSubjectPanel.add(addSubjectButton);

        // Add components to panel
        panel.add(addSubjectPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createGradesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Student selection panel
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.add(new JLabel("Select Student:"));

        // SECURITY FLAW: Allow selecting any student
        List<Student> students = userDAO.getAllStudents();
        JComboBox<Student> studentComboBox = new JComboBox<>(students.toArray(new Student[0]));
        studentComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Student) {
                    Student student = (Student) value;
                    setText(student.getFullName() + " (" + student.getClassGroup() + ")");
                }
                return this;
            }
        });

        selectionPanel.add(studentComboBox);

        JButton viewGradesButton = new JButton("View Grades");
        viewGradesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Student selectedStudent = (Student) studentComboBox.getSelectedItem();
                if (selectedStudent != null) {
                    loadGrades(selectedStudent.getId());
                }
            }
        });
        selectionPanel.add(viewGradesButton);

        // Grades table
        String[] columns = {"ID", "Student", "Subject", "Title", "Value", "Coefficient", "Date", "Comment", "Actions"};
        gradesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 || column == 5 || column == 7; // Value, Coefficient, and Comment columns are editable
            }
        };

        gradesTable = new JTable(gradesTableModel);
        gradesTable.getColumnModel().getColumn(0).setPreferredWidth(30); // ID
        gradesTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Student
        gradesTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Subject
        gradesTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Title
        gradesTable.getColumnModel().getColumn(4).setPreferredWidth(50); // Value
        gradesTable.getColumnModel().getColumn(5).setPreferredWidth(80); // Coefficient
        gradesTable.getColumnModel().getColumn(6).setPreferredWidth(80); // Date
        gradesTable.getColumnModel().getColumn(7).setPreferredWidth(200); // Comment
        gradesTable.getColumnModel().getColumn(8).setPreferredWidth(100); // Actions

        // Add button column for delete action
        gradesTable.getColumnModel().getColumn(8).setCellRenderer(new ButtonRenderer());
        gradesTable.getColumnModel().getColumn(8).setCellEditor(new ButtonEditor(new JCheckBox()) {
            @Override
            protected void buttonClicked() {
                int row = gradesTable.getSelectedRow();
                if (row >= 0) {
                    int gradeId = (int) gradesTableModel.getValueAt(row, 0);
                    deleteGrade(gradeId, row);
                }
            }
        });

        // SECURITY FLAW: Allow editing grades directly in the table
        gradesTable.getModel().addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();

                if (column == 4 || column == 5 || column == 7) { // Value, Coefficient, or Comment column
                    updateGrade(row);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(gradesTable);

        // Add components to panel
        panel.add(selectionPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadUsers() {
        // Clear existing data
        usersTableModel.setRowCount(0);

        // Load all students
        List<Student> students = userDAO.getAllStudents();
        for (Student student : students) {
            Object[] row = {
                student.getId(),
                student.getUsername(),
                student.getPassword(), // SECURITY FLAW: Displaying password in plain text
                student.getRole(),
                student.getFullName(),
                student.getEmail(),
                student.getClassGroup(),
                "Delete"
            };
            usersTableModel.addRow(row);
        }

        // Load all teachers
        List<Teacher> teachers = userDAO.getAllTeachers();
        for (Teacher teacher : teachers) {
            Object[] row = {
                teacher.getId(),
                teacher.getUsername(),
                teacher.getPassword(), // SECURITY FLAW: Displaying password in plain text
                teacher.getRole(),
                teacher.getFullName(),
                teacher.getEmail(),
                "",
                "Delete"
            };
            usersTableModel.addRow(row);
        }
    }

    private void loadSubjects() {
        // Clear existing data
        subjectsTableModel.setRowCount(0);

        // Load all subjects
        List<Subject> subjects = subjectDAO.getAllSubjects();
        for (Subject subject : subjects) {
            Object[] row = {
                subject.getId(),
                subject.getCode(),
                subject.getName(),
                subject.getDescription(),
                subject.getDefaultCoefficient(),
                "Delete"
            };
            subjectsTableModel.addRow(row);
        }
    }

    // SECURITY FLAW: No access control, admin can view any student's grades
    private void loadGrades(int studentId) {
        // Clear existing data
        gradesTableModel.setRowCount(0);

        // Load grades for the specified student
        List<Grade> grades = gradeDAO.getGradesByStudentId(studentId);

        // Get student name
        Student student = null;
        List<Student> students = userDAO.getAllStudents();
        for (Student s : students) {
            if (s.getId() == studentId) {
                student = s;
                break;
            }
        }

        String studentName = student != null ? student.getFullName() : "Unknown";

        for (Grade grade : grades) {
            Object[] row = {
                grade.getId(),
                studentName,
                grade.getSubject().getName(),
                grade.getTitle(),
                grade.getValue(),
                grade.getCoefficient(),
                grade.getDate() != null ? DATE_FORMAT.format(grade.getDate()) : "",
                grade.getComment(), // SECURITY FLAW: XSS vulnerability (no escaping of HTML)
                "Delete"
            };
            gradesTableModel.addRow(row);
        }
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog(this, "Add New User", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField usernameField = new JTextField(20);
        panel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField passwordField = new JTextField(20); // SECURITY FLAW: Password in plain text
        panel.add(passwordField, gbc);

        // Role
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Role:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        String[] roles = {"student", "teacher", "admin"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);
        panel.add(roleComboBox, gbc);

        // Full Name
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Full Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextField fullNameField = new JTextField(20);
        panel.add(fullNameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextField emailField = new JTextField(20);
        panel.add(emailField, gbc);

        // Class Group (for students)
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel classGroupLabel = new JLabel("Class Group:");
        panel.add(classGroupLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        JTextField classGroupField = new JTextField(20);
        panel.add(classGroupField, gbc);

        // Update visibility based on role selection
        roleComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedRole = (String) roleComboBox.getSelectedItem();
                boolean isStudent = "student".equals(selectedRole);
                classGroupLabel.setVisible(isStudent);
                classGroupField.setVisible(isStudent);
            }
        });

        // Initial visibility
        classGroupLabel.setVisible(true);
        classGroupField.setVisible(true);

        // Add button
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton addButton = new JButton("Add User");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = passwordField.getText();
                String role = (String) roleComboBox.getSelectedItem();
                String fullName = fullNameField.getText();
                String email = emailField.getText();
                String classGroup = classGroupField.getText();

                // SECURITY FLAW: No input validation

                User user;
                if ("student".equals(role)) {
                    user = new Student(0, username, password, fullName, email, classGroup);
                } else if ("teacher".equals(role)) {
                    user = new Teacher(0, username, password, fullName, email);
                } else {
                    user = new User(0, username, password, role, fullName, email);
                }

                boolean success = userDAO.addUser(user);

                if (success) {
                    JOptionPane.showMessageDialog(dialog, 
                            "User added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadUsers(); // Refresh the users table
                } else {
                    JOptionPane.showMessageDialog(dialog, 
                            "Failed to add user", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(addButton, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAddSubjectDialog() {
        JDialog dialog = new JDialog(this, "Add New Subject", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Code
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Code:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField codeField = new JTextField(20);
        panel.add(codeField, gbc);

        // Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField nameField = new JTextField(20);
        panel.add(nameField, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField descriptionField = new JTextField(20);
        panel.add(descriptionField, gbc);

        // Default Coefficient
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Default Coefficient:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextField coefficientField = new JTextField("1.0");
        panel.add(coefficientField, gbc);

        // Add button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton addButton = new JButton("Add Subject");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String code = codeField.getText();
                    String name = nameField.getText();
                    String description = descriptionField.getText();
                    double coefficient = Double.parseDouble(coefficientField.getText());

                    // SECURITY FLAW: No input validation

                    Subject subject = new Subject(0, code, name, description, coefficient);
                    boolean success = subjectDAO.addSubject(subject);

                    if (success) {
                        JOptionPane.showMessageDialog(dialog, 
                                "Subject added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        loadSubjects(); // Refresh the subjects table
                    } else {
                        JOptionPane.showMessageDialog(dialog, 
                                "Failed to add subject", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Invalid coefficient. Please enter a valid number.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(addButton, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    // SECURITY FLAW: No access control, admin can update any user
    private void updateUser(int row) {
        try {
            int userId = (int) usersTableModel.getValueAt(row, 0);
            String username = (String) usersTableModel.getValueAt(row, 1);
            String password = (String) usersTableModel.getValueAt(row, 2);
            String role = (String) usersTableModel.getValueAt(row, 3);
            String fullName = (String) usersTableModel.getValueAt(row, 4);
            String email = (String) usersTableModel.getValueAt(row, 5);
            String classGroup = (String) usersTableModel.getValueAt(row, 6);

            // SECURITY FLAW: No input validation

            User user;
            if ("student".equals(role)) {
                user = new Student(userId, username, password, fullName, email, classGroup);
            } else if ("teacher".equals(role)) {
                user = new Teacher(userId, username, password, fullName, email);
            } else {
                user = new User(userId, username, password, role, fullName, email);
            }

            boolean success = userDAO.updateUser(user);

            if (success) {
                System.out.println("User updated successfully: " + userId);
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Failed to update user", "Error", JOptionPane.ERROR_MESSAGE);
                loadUsers(); // Reload to reset invalid values
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                    "Error updating user: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            loadUsers(); // Reload to reset invalid values
        }
    }

    // SECURITY FLAW: No access control, admin can delete any user
    private void deleteUser(int userId, int row) {
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this user?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = userDAO.deleteUser(userId);

            if (success) {
                usersTableModel.removeRow(row);
                System.out.println("User deleted successfully: " + userId);
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Failed to delete user", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // SECURITY FLAW: No access control, admin can update any subject
    private void updateSubject(int row) {
        try {
            int subjectId = (int) subjectsTableModel.getValueAt(row, 0);
            String code = (String) subjectsTableModel.getValueAt(row, 1);
            String name = (String) subjectsTableModel.getValueAt(row, 2);
            String description = (String) subjectsTableModel.getValueAt(row, 3);
            double coefficient = Double.parseDouble(subjectsTableModel.getValueAt(row, 4).toString());

            // SECURITY FLAW: No input validation

            Subject subject = new Subject(subjectId, code, name, description, coefficient);
            boolean success = subjectDAO.updateSubject(subject);

            if (success) {
                System.out.println("Subject updated successfully: " + subjectId);
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Failed to update subject", "Error", JOptionPane.ERROR_MESSAGE);
                loadSubjects(); // Reload to reset invalid values
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                    "Invalid coefficient. Please enter a valid number.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            loadSubjects(); // Reload to reset invalid values
        }
    }

    // SECURITY FLAW: No access control, admin can delete any subject
    private void deleteSubject(int subjectId, int row) {
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this subject?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = subjectDAO.deleteSubject(subjectId);

            if (success) {
                subjectsTableModel.removeRow(row);
                System.out.println("Subject deleted successfully: " + subjectId);
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Failed to delete subject", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // SECURITY FLAW: No access control, admin can update any grade
    private void updateGrade(int row) {
        try {
            int gradeId = (int) gradesTableModel.getValueAt(row, 0);
            double newValue = Double.parseDouble(gradesTableModel.getValueAt(row, 4).toString());
            double newCoefficient = Double.parseDouble(gradesTableModel.getValueAt(row, 5).toString());
            String newComment = (String) gradesTableModel.getValueAt(row, 7);

            // Get the grade from database
            Grade grade = gradeDAO.getGradeById(gradeId);

            if (grade != null) {
                // Update the grade
                grade.setValue(newValue);
                grade.setCoefficient(newCoefficient);
                grade.setComment(newComment);

                // Save to database
                boolean success = gradeDAO.updateGrade(grade);

                if (success) {
                    System.out.println("Grade updated successfully: " + gradeId);
                } else {
                    JOptionPane.showMessageDialog(this, 
                            "Failed to update grade", "Error", JOptionPane.ERROR_MESSAGE);

                    // Reload grades to reset the invalid value
                    int studentId = grade.getStudentId();
                    loadGrades(studentId);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                    "Invalid grade value or coefficient. Please enter valid numbers.", 
                    "Error", JOptionPane.ERROR_MESSAGE);

            // Reload grades to reset the invalid value
            int row2 = gradesTable.getSelectedRow();
            if (row2 >= 0) {
                int gradeId = (int) gradesTableModel.getValueAt(row2, 0);
                Grade grade = gradeDAO.getGradeById(gradeId);
                if (grade != null) {
                    loadGrades(grade.getStudentId());
                }
            }
        }
    }

    // SECURITY FLAW: No access control, admin can delete any grade
    private void deleteGrade(int gradeId, int row) {
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this grade?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = gradeDAO.deleteGrade(gradeId);

            if (success) {
                gradesTableModel.removeRow(row);
                System.out.println("Grade deleted successfully: " + gradeId);
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Failed to delete grade", "Error", JOptionPane.ERROR_MESSAGE);
            }
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

    // Button renderer for the action column
    private class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    // Button editor for the action column
    private abstract class ButtonEditor extends DefaultCellEditor {
        protected JButton button;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    buttonClicked();
                }
            });
        }

        protected abstract void buttonClicked();

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            button.setText((value == null) ? "" : value.toString());
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return button.getText();
        }
    }
}
