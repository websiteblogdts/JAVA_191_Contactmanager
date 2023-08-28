import com.asm.managercontacts.Contacts;

import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class Screen extends JFrame{
    private JPanel panelMain;
    private JTextField txtSearch;
    private JButton btnSearch;
    private JTable listContacts;
    private JTextField txtPhone;
    private JTextField txtName;
    private JTextField txtAddress;
    private JComboBox cbbGender;
    private JButton btnDelete;
    private JButton btnAdd;
    private JButton btnReset;
    private JButton btnUpdate;
    private JButton btnExit;

    private JPanel jp1;
    private JPanel jpbutton;
    private JPanel jpapp;
    private JButton openFile;
    private JButton saveFile;
//    private JButton importFile;
//    private JButton exportFile;
    private Contacts selectedContact; // Add this line

    private List<Contacts> list = new ArrayList<>();

    private Contacts modelContact = new Contacts();
    private DefaultTableModel model = new DefaultTableModel();

    private final String[] columHeaders = new String[]{"Phone", "Name", "Address", "Gender"};

    public Screen() {
        super("Contacts Manager");
        this.setContentPane(this.panelMain);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        setLocationRelativeTo(null);
//      readFile();
        initTable(); // cái này là hàm để hiển thị
        // cái bảng trong list contacts ( không gọi thì nó không hiện ra)


        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    StringBuilder errors = new StringBuilder(); // để hiện thông báo
                    if (txtPhone.getText().equals("")) {
                        errors.append("Phone must be entered. ");
                        txtPhone.setBackground(Color.RED);

                    } else {
                        txtPhone.setBackground(Color.WHITE);
                    }
                    if (txtName.getText().equals("")) {
                        errors.append("Name must be entered");
                    }
                    if (!errors.isEmpty()) {
                        JOptionPane.showMessageDialog(btnAdd, errors.toString());
                        return;
                    }
                    Contacts contacts = new Contacts();
//                    contacts.setPhone(txtPhone.getText());
                    contacts.setPhone(Integer.parseInt(txtPhone.getText()));
                    contacts.setName(txtName.getText());
                    contacts.setAddress(txtAddress.getText());
                    contacts.setGender(cbbGender.getSelectedItem().toString());

                    addContacts(contacts, contacts.getPhone());

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(btnAdd, "Error: " + e.getMessage());
                }
            }
        });


        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (selectedContact == null) {
                        JOptionPane.showMessageDialog(btnUpdate, "No contact selected for update. Please search and select a contact.");
                        return;
                    }

                    StringBuilder errors = new StringBuilder();
                    if (txtName.getText().equals("")) {
                        errors.append("Name must be entered.");
                    }
                    if (!errors.isEmpty()) {
                        JOptionPane.showMessageDialog(btnUpdate, errors.toString());
                        return;
                    }

                    // Check if the phone number is being updated
                    if (selectedContact.getPhone() != Integer.parseInt(txtPhone.getText())) {
                        JOptionPane.showMessageDialog(btnUpdate, "Cannot update phone number. Only name, address, and gender can be updated.");
                        return;
                    }

                    selectedContact.setName(txtName.getText());
                    selectedContact.setAddress(txtAddress.getText());
                    selectedContact.setGender(cbbGender.getSelectedItem().toString());

                    updateContacts(selectedContact);

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(btnUpdate, "Error: " + e.getMessage());
                }
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (txtPhone.getText().equals("")) {
                        JOptionPane.showMessageDialog(btnDelete, "Please select the phone you want to delete.");
                        return;
                    }
                    if (JOptionPane.showConfirmDialog(btnDelete, "Do you want to deleted the phone "
                            + txtPhone.getText() + "?", "Confirmation", JOptionPane.YES_NO_CANCEL_OPTION) == JOptionPane.NO_OPTION) {
                        return;
                    }

                    deleteContacts();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(btnDelete, "Error: " + e.getMessage());
                }
            }
        });

        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtPhone.setText("");
                txtName.setText("");
                txtAddress.setText("");
            }
        });

        listContacts.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                int selectedRow = listContacts.getSelectedRow();

                if (selectedRow >= 0) {
                    Contacts contacts = list.get(selectedRow);
                    setModel(contacts);
                }
            }
        });


        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (txtSearch.getText().equals("")) {
                        JOptionPane.showMessageDialog(btnSearch, "Please enter the contact you want to find.");
                        return;
                    }
                    Contacts find = findByPhone(Integer.parseInt(txtSearch.getText()));
                    if (find != null) {
                        selectedContact = find; // Set the selectedContact to the found contact
                        setModel(find);
                    } else {
                        selectedContact = null; // Reset selectedContact
                        JOptionPane.showMessageDialog(btnSearch, "The contact was not found.");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(btnSearch, "Error: " + e.getMessage());
                }
            }
        });

        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                fExit();
            }
        });

        openFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                readFile();
                initTable();
            }
        });
        saveFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               writeFile();
            }
        });
    }
    private void refreshData() {
        model.setRowCount(0); //xoá trắng các thông tin đã nhập
        list.forEach(item -> {
            model.addRow(new Object[]{item.getPhone(), item.getName(),
                    item.getAddress(), item.getGender()});
        });
        model.fireTableDataChanged(); //cập nhật lại thông tin được hiển thị
    }
    public boolean addContacts(Contacts contacts, int phone) {

        boolean isExisted = false; // Kiểm tra xem liên hệ đã tồn tại trong danh sách chưa.
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPhone() == phone) {
                isExisted = true;
                break;
            }
        }
        if (!isExisted) {
            list.add(contacts);
            refreshData();
            JOptionPane.showMessageDialog(btnAdd, "Add contacts successfully!");
            return true;
        } else {
            JOptionPane.showMessageDialog(btnAdd, "Phone already exists. Add Phone does not successful!");
            return false;
        }
    }
    private void deleteContacts() {
        int selectedRow = listContacts.getSelectedRow();
        Contacts contacts = list.get(selectedRow);
        for (int i = 0; i < list.size(); i++) {
            if ((list.get(i).getPhone() == contacts.getPhone())) {
                contacts = list.get(i);  //trả về phần tử hiện diện ở vị trí đã chỉ định.
                break;
            }
        }
        if (contacts != null) {
            list.remove(contacts);
            refreshData();
            JOptionPane.showMessageDialog(btnDelete, "The contact has been deleted.");
        } else {
            JOptionPane.showMessageDialog(btnDelete, "The contact does not found.");
        }
    }
    private void updateContacts(Contacts contacts) {
        boolean isUpdated = false;
        for (int i = 0; i < list.size(); i++) {
            Contacts contact = list.get(i);
            if (Objects.equals(contact.getPhone(), contacts.getPhone())) {  //kiểm tra xem hai object có bằng nhau hay không.
                list.add(i, contacts);
                list.remove(contact);
                break;
            }
        }
        if (!isUpdated) {
            refreshData();
            JOptionPane.showMessageDialog(btnUpdate, "Contact successfully updated!");
        } else {
            JOptionPane.showMessageDialog(btnUpdate, "The contact does not found.");
        }
    }

    //cung la chuc nang update, nhung viet theo cach khac
   /* private void updateContacts(Contacts contacts) {
        boolean isUpdated = false;
        for (int i = 0; i < list.size(); i++) {
            Contacts contact = list.get(i);
            if (Objects.equals(contact.getPhone(), contacts.getPhone())) {
                list.set(i, contacts); // Update the contact at index i
                isUpdated = true; // Set the flag to true
                break;
            }
        }
        if (isUpdated) {
            refreshData();
            JOptionPane.showMessageDialog(btnUpdate, "Contact successfully updated!");
        } else {
            JOptionPane.showMessageDialog(btnUpdate, "The contact does not found.");
        }
    }*/



    private Contacts findByPhone(int phone) {
        for (Contacts contact : list) {
            if (contact.getPhone() == phone) {
                return contact;
            }
        }
        return null;
    }
    public void setModel(Contacts contact) {
        txtPhone.setText("" + contact.getPhone());
        txtName.setText(contact.getName());
        txtAddress.setText(contact.getAddress());
        cbbGender.setSelectedItem(contact.getGender());
    }
//    private void readFile() {
//        list.clear();  // xoá tất cả các phần khỏi mảng
//        String fName = "List_Products.txt";
//        try {
//            File f = new File(fName);
//            if (!f.exists()) {
//                JOptionPane.showMessageDialog(this,fName + " doesn't exist.");
//                return;
//            }
//            FileReader fr = new FileReader(f); //doc file
//            BufferedReader br = new BufferedReader(fr); // doc van ban tu file
//            String line;
//            while ((line = br.readLine()) != null) { // doc tung dong
//                String attribute[] = line.split(" ");
//                list.add(new Contacts (Integer.parseInt(attribute[0]), attribute[1], attribute[2],attribute[3]));
//            }
//            br.close();
//            fr.close();
//        } catch (IOException e) {
//            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
//            return;
//        }
//    }
private void readFile() {
    list.clear(); // Clear the list before reading new data
    String fName = "List_Products.txt";
    try {
        File f = new File(fName);
        if (!f.exists()) {
            JOptionPane.showMessageDialog(this, fName + " doesn't exist.");
            return;
        }
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        String line;
        while ((line = br.readLine()) != null) {
//            String[] attribute = line.split(" ");
            String[] attribute = line.split(", ");

            if (attribute.length >= 4) {
                try {
                    int phone = Integer.parseInt(attribute[0].trim());
                    list.add(new Contacts(phone, attribute[1], attribute[2], attribute[3]));
                } catch (NumberFormatException ex) {
                    System.out.println("Invalid phone format: " + attribute[0]);
                }
            } else {
                // Handle cases where line doesn't have enough attributes
                System.out.println("Invalid line format: " + line);
            }
        }
        br.close();
        fr.close();
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    }
}



    private void writeFile() {
        String fName = "List_Products.txt";
        try {
            File f = new File(fName);
            if (!f.exists()) {
                JOptionPane.showMessageDialog(this,fName + " doesn't exist.");
                return;
            }
            FileWriter fw = new FileWriter(f); // ghi
            PrintWriter pw = new PrintWriter(fw); // xuat van ban da ghi
            for (Contacts contacts : list) {
                pw.println(contacts.getPhone() + ", " + contacts.getName() + ", " + contacts.getAddress() + ", " + contacts.getGender());
            }
            pw.close();
            fw.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    private void fExit(){
        int confirmed = JOptionPane.showConfirmDialog(this, "Do you want to exit?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if(confirmed == JOptionPane.YES_OPTION){
             writeFile();
            System.exit(0);
        }
    }
//Hàm initTable() trong đoạn mã của bạn là một phương thức được sử dụng để khởi tạo bảng hiển thị dữ liệu trong giao diện người dùng.
// Hàm này tạo một bảng (JTable) và đặt mô hình dữ liệu (DefaultTableModel) cho bảng đó.
    private void initTable() {
        String[] columHeaders = new String[]{"ID", "Name", "Address", "Gender"};
        model = new DefaultTableModel();
        model.setColumnIdentifiers(columHeaders);
        list.forEach(item -> {
            model.addRow(new Object[]{item.getPhone(), item.getName(),
                    item.getAddress(), item.getGender()});
        });
        listContacts.setModel(model);
    }

    public static void main(String[] args) {
        Screen s = new Screen();
        s.setContentPane(s.panelMain);
        s.setTitle("Contacts Manager");
        s.setVisible(true);
        s.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}