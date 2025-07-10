package ui;

import dao.GradeDAO;
import dao.SubjectDAO;
import dao.UserDAO;
import model.Grade;
import model.Student;
import model.Subject;
import model.Teacher;
import security.AuthenticationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Dashboard for teacher users.
 * SECURITY FLAWS:
 * 1. No access control (can modify any grade)
 * 2. No input validation
 * 3. No CSRF protection
 * 4. XSS vulnerability in displaying comments
 */
public class TeacherDashboard extends JFrame {

    private Teacher teacher;
    private GradeDAO gradeDAO;
    private SubjectDAO subjectDAO;
    private UserDAO userDAO;

    private JTabbedPane tabbedPane;
    private JTable gradesTable;
    private DefaultTableModel gradesTableModel;
    private JTable studentsTable;
    private DefaultTableModel studentsTableModel;
    private JTable subjectsTable;
    private DefaultTableModel subjectsTableModel;
    private JComboBox<Student> studentComboBox;
    private JComboBox<Subject> subjectComboBox;
    private JTextField valueField;
    private JTextField coefficientField;
    private JTextField titleField;
    private JTextArea commentArea;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public TeacherDashboard(Teacher teacher) {
        this.teacher = teacher;
        this.gradeDAO = new GradeDAO();
        this.subjectDAO = new SubjectDAO();
        this.userDAO = new UserDAO();

        setTitle("Teacher Dashboard - " + teacher.getFullName());
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        // Main tabbed pane
        tabbedPane = new JTabbedPane();

        // Create tabs
        JPanel gradesPanel = createGradesPanel();
        JPanel addGradePanel = createAddGradePanel();
        JPanel studentsPanel = createStudentsPanel();
        JPanel subjectsPanel = createSubjectsPanel();

        // Add tabs to tabbed pane
        tabbedPane.addTab("View Grades", gradesPanel);
        tabbedPane.addTab("Add Grade", addGradePanel);
        tabbedPane.addTab("Students", studentsPanel);
        tabbedPane.addTab("Subjects", subjectsPanel);

        // Header panel with welcome message and logout button
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome, " + teacher.getFullName() + "!");
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

    private JPanel createGradesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Student selection panel
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.add(new JLabel("Select Student:"));

        // SECURITY FLAW: Allow selecting any student
        List<Student> students = userDAO.getAllStudents();
        studentComboBox = new JComboBox<>(students.toArray(new Student[0]));
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
        String[] columns = {"ID", "Subject", "Title", "Value", "Coefficient", "Date", "Comment", "Actions"};
        gradesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 4 || column == 6; // Value, Coefficient, and Comment columns are editable
            }
        };

        gradesTable = new JTable(gradesTableModel);
        gradesTable.getColumnModel().getColumn(0).setPreferredWidth(30); // ID
        gradesTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Subject
        gradesTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Title
        gradesTable.getColumnModel().getColumn(3).setPreferredWidth(50); // Value
        gradesTable.getColumnModel().getColumn(4).setPreferredWidth(80); // Coefficient
        gradesTable.getColumnModel().getColumn(5).setPreferredWidth(80); // Date
        gradesTable.getColumnModel().getColumn(6).setPreferredWidth(200); // Comment
        gradesTable.getColumnModel().getColumn(7).setPreferredWidth(100); // Actions

        // Add button column for delete action
        gradesTable.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
        gradesTable.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor(new JCheckBox()) {
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

                if (column == 3 || column == 4 || column == 6) { // Value, Coefficient, or Comment column
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

    private JPanel createAddGradePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Student selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Student:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        // SECURITY FLAW: Allow selecting any student
        List<Student> students = userDAO.getAllStudents();
        JComboBox<Student> studentDropdown = new JComboBox<>(students.toArray(new Student[0]));
        studentDropdown.setRenderer(new DefaultListCellRenderer() {
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
        formPanel.add(studentDropdown, gbc);

        // Subject selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Subject:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        // SECURITY FLAW: Allow selecting any subject, not just those taught by the teacher
        List<Subject> subjects = subjectDAO.getAllSubjects();
        JComboBox<Subject> subjectDropdown = new JComboBox<>(subjects.toArray(new Subject[0]));
        subjectDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Subject) {
                    Subject subject = (Subject) value;
                    setText(subject.getName() + " (" + subject.getCode() + ")");
                }
                return this;
            }
        });
        formPanel.add(subjectDropdown, gbc);

        // Title
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Title:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JTextField titleInput = new JTextField(20);
        formPanel.add(titleInput, gbc);

        // Value
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Value:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JTextField valueInput = new JTextField(10);
        formPanel.add(valueInput, gbc);

        // Coefficient
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Coefficient:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JTextField coefficientInput = new JTextField(10);
        coefficientInput.setText("1.0"); // Default value
        formPanel.add(coefficientInput, gbc);

        // Comment
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Comment:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JTextArea commentInput = new JTextArea(5, 20);
        commentInput.setLineWrap(true);
        JScrollPane commentScrollPane = new JScrollPane(commentInput);
        formPanel.add(commentScrollPane, gbc);

        // Add button
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        JButton addButton = new JButton("Add Grade");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Student student = (Student) studentDropdown.getSelectedItem();
                    Subject subject = (Subject) subjectDropdown.getSelectedItem();
                    String title = titleInput.getText();
                    double value = Double.parseDouble(valueInput.getText());
                    double coefficient = Double.parseDouble(coefficientInput.getText());
                    String comment = commentInput.getText();

                    // SECURITY FLAW: No validation if the teacher teaches this subject
                    Grade grade = new Grade(0, value, coefficient, subject, student.getId(), title, comment, new Date());
                    boolean success = gradeDAO.addGrade(grade);

                    if (success) {
                        JOptionPane.showMessageDialog(TeacherDashboard.this, 
                                "Grade added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);

                        // Clear form
                        titleInput.setText("");
                        valueInput.setText("");
                        coefficientInput.setText("1.0");
                        commentInput.setText("");

                        // Refresh grades if the current student is selected
                        if (studentComboBox.getSelectedItem() != null && 
                                ((Student)studentComboBox.getSelectedItem()).getId() == student.getId()) {
                            loadGrades(student.getId());
                        }
                    } else {
                        JOptionPane.showMessageDialog(TeacherDashboard.this, 
                                "Failed to add grade", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(TeacherDashboard.this, 
                            "Invalid value or coefficient. Please enter valid numbers.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        formPanel.add(addButton, gbc);

        // Reset button
        gbc.gridx = 2;
        gbc.gridy = 6;
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                titleInput.setText("");
                valueInput.setText("");
                coefficientInput.setText("1.0");
                commentInput.setText("");
            }
        });
        formPanel.add(resetButton, gbc);

        panel.add(formPanel, BorderLayout.NORTH);

        return panel;
    }

    private JPanel createStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Students table
        String[] columns = {"ID", "Name", "Email", "Class Group"};
        studentsTableModel = new DefaultTableModel(columns, 0);

        studentsTable = new JTable(studentsTableModel);
        JScrollPane scrollPane = new JScrollPane(studentsTable);

        // Load students
        List<Student> students = userDAO.getAllStudents();
        for (Student student : students) {
            Object[] row = {
                student.getId(),
                student.getFullName(),
                student.getEmail(),
                student.getClassGroup()
            };
            studentsTableModel.addRow(row);
        }

        panel.add(new JLabel("All Students:"), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSubjectsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Subjects table
        String[] columns = {"ID", "Code", "Name", "Description", "Default Coefficient"};
        subjectsTableModel = new DefaultTableModel(columns, 0);

        subjectsTable = new JTable(subjectsTableModel);
        JScrollPane scrollPane = new JScrollPane(subjectsTable);

        // Load subjects
        List<Subject> subjects = subjectDAO.getAllSubjects();
        for (Subject subject : subjects) {
            Object[] row = {
                subject.getId(),
                subject.getCode(),
                subject.getName(),
                subject.getDescription(),
                subject.getDefaultCoefficient()
            };
            subjectsTableModel.addRow(row);
        }

        panel.add(new JLabel("All Subjects:"), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // SECURITY FLAW: No access control, any teacher can view any student's grades
    private void loadGrades(int studentId) {
        // Clear existing data
        gradesTableModel.setRowCount(0);

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
                grade.getComment(), // SECURITY FLAW: XSS vulnerability (no escaping of HTML)
                "Delete"
            };
            gradesTableModel.addRow(row);
        }
    }

    // SECURITY FLAW: No access control, any teacher can modify any grade
    private void updateGrade(int row) {
        try {
            int gradeId = (int) gradesTableModel.getValueAt(row, 0);
            double newValue = Double.parseDouble(gradesTableModel.getValueAt(row, 3).toString());
            double newCoefficient = Double.parseDouble(gradesTableModel.getValueAt(row, 4).toString());
            String newComment = (String) gradesTableModel.getValueAt(row, 6);

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
                    Student selectedStudent = (Student) studentComboBox.getSelectedItem();
                    if (selectedStudent != null) {
                        loadGrades(selectedStudent.getId());
                    }
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                    "Invalid grade value or coefficient. Please enter valid numbers.", 
                    "Error", JOptionPane.ERROR_MESSAGE);

            // Reload grades to reset the invalid value
            Student selectedStudent = (Student) studentComboBox.getSelectedItem();
            if (selectedStudent != null) {
                loadGrades(selectedStudent.getId());
            }
        }
    }

    // SECURITY FLAW: No access control, any teacher can delete any grade
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
