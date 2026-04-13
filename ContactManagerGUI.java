import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class ContactManagerGUI {

    JFrame frame;
    JTextField nameField, numberField, searchField;
    JTable table;
    DefaultTableModel model;
    JLabel totalLabel;

    HashMap<String, String> contacts = new HashMap<>();

    public ContactManagerGUI() {

        frame = new JFrame("📱 Smart Contact Manager");
        frame.setSize(700, 520);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Color bg = new Color(30, 30, 30);
        Color fg = Color.WHITE;

        // TOP PANEL
        JPanel top = new JPanel(new GridLayout(3, 2, 10, 10));
        top.setBackground(bg);

        nameField = new JTextField();
        numberField = new JTextField();
        searchField = new JTextField();

        styleField(nameField);
        styleField(numberField);
        styleField(searchField);

        top.add(label("Name:", fg));
        top.add(nameField);
        top.add(label("Phone:", fg));
        top.add(numberField);
        top.add(label("Search:", fg));
        top.add(searchField);

        frame.add(top, BorderLayout.NORTH);

        // TABLE
        model = new DefaultTableModel(new String[]{"Name", "Phone"}, 0);
        table = new JTable(model);
        table.setRowHeight(25);
        table.setBackground(bg);
        table.setForeground(fg);

        JScrollPane scroll = new JScrollPane(table);
        frame.add(scroll, BorderLayout.CENTER);

        // BOTTOM PANEL
        JPanel bottom = new JPanel();
        bottom.setBackground(bg);

        JButton addBtn = button("Add");
        JButton updateBtn = button("Update");
        JButton deleteBtn = button("Delete");
        JButton saveBtn = button("Save");
        JButton sortBtn = button("Sort A-Z");
        JButton exportBtn = button("Export CSV");

        totalLabel = new JLabel("Total: 0");
        totalLabel.setForeground(Color.WHITE);

        bottom.add(addBtn);
        bottom.add(updateBtn);
        bottom.add(deleteBtn);
        bottom.add(saveBtn);
        bottom.add(sortBtn);
        bottom.add(exportBtn);
        bottom.add(totalLabel);

        frame.add(bottom, BorderLayout.SOUTH);

        // ACTIONS

        addBtn.addActionListener(e -> addContact());
        updateBtn.addActionListener(e -> updateContact());
        deleteBtn.addActionListener(e -> deleteContact());
        saveBtn.addActionListener(e -> saveToFile());
        sortBtn.addActionListener(e -> sortContacts());
        exportBtn.addActionListener(e -> exportCSV());

        // SEARCH LIVE
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                searchContact(searchField.getText());
            }
        });

        // CLICK + DOUBLE CLICK
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                nameField.setText(model.getValueAt(row, 0).toString());
                numberField.setText(model.getValueAt(row, 1).toString());

                if (e.getClickCount() == 2) {
                    deleteContact();
                }
            }
        });

        // KEYBOARD SHORTCUTS
        nameField.addActionListener(e -> addContact());
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    deleteContact();
                }
            }
        });

        loadFromFile();
        updateCount();

        frame.setVisible(true);
    }

    // ADD
    void addContact() {
        String name = nameField.getText().trim();
        String number = numberField.getText().trim();

        if (name.isEmpty() || number.length() != 10) {
            JOptionPane.showMessageDialog(frame, "Invalid Input!");
            return;
        }
 contacts.put(name, number);
        model.addRow(new Object[]{name, number});

        nameField.setText("");
        numberField.setText("");

        updateCount();
    }

    // UPDATE
    void updateContact() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        String name = nameField.getText();
        String number = numberField.getText();

        contacts.put(name, number);
        model.setValueAt(name, row, 0);
        model.setValueAt(number, row, 1);
    }

    // DELETE
    void deleteContact() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int confirm = JOptionPane.showConfirmDialog(frame, "Delete contact?");
        if (confirm == 0) {
            String name = model.getValueAt(row, 0).toString();
            contacts.remove(name);
            model.removeRow(row);
            updateCount();
        }
    }

    // SEARCH
    void searchContact(String text) {
        model.setRowCount(0);

        for (String name : contacts.keySet()) {
            if (name.toLowerCase().contains(text.toLowerCase())) {
                model.addRow(new Object[]{name, contacts.get(name)});
            }
        }
    }

    // SORT
    void sortContacts() {
        List<String> keys = new ArrayList<>(contacts.keySet());
        Collections.sort(keys);

        model.setRowCount(0);
        for (String name : keys) {
            model.addRow(new Object[]{name, contacts.get(name)});
        }
    }

    // EXPORT CSV
    void exportCSV() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("contacts.csv"));
            for (String name : contacts.keySet()) {
                bw.write(name + "," + contacts.get(name));
                bw.newLine();
            }
            bw.close();
            JOptionPane.showMessageDialog(frame, "Exported as contacts.csv");
        } catch (Exception e) {}
    }

    // SAVE
    void saveToFile() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("contacts.txt"));
            for (String name : contacts.keySet()) {
                bw.write(name + "," + contacts.get(name));
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {}
    }

    // LOAD
    void loadFromFile() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("contacts.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                contacts.put(data[0], data[1]);
                model.addRow(new Object[]{data[0], data[1]});
            }
            br.close();
        } catch (Exception e) {}
    }

    // COUNT
    void updateCount() {
        totalLabel.setText("Total: " + contacts.size());
    }

    // UI
    JLabel label(String t, Color c) {
        JLabel l = new JLabel(t);
        l.setForeground(c);
        return l;
    }

    void styleField(JTextField f) {
        f.setBackground(new Color(50, 50, 50));
        f.setForeground(Color.WHITE);
    }

    JButton button(String t) {
        JButton b = new JButton(t);
        b.setBackground(new Color(70, 130, 180));
        b.setForeground(Color.WHITE);
        return b;
    }

    public static void main(String[] args) {
        new ContactManagerGUI();
    }
}