import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

//shopping system GUI will start hear 
public class ShopBillingSystemGUI {
    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField itemField, quantityField, priceField;
    private JLabel totalLabel;
    private HashMap<String, Integer> stock;
    private HashMap<String, Double> priceMap;
    private int billNumber;

    public ShopBillingSystemGUI() {
        stock = new HashMap<>();
        priceMap = new HashMap<>();
        billNumber = new Random().nextInt(10000) + 1; // Generate unique  bill number it may help to understand all bill details 
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Shop Billing System");
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Header Panel of GUI
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new GridLayout(2, 1));
        JLabel titleLabel = new JLabel("Shop Billing System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerPanel.add(titleLabel);

        JLabel billLabel = new JLabel("Bill Number: " + billNumber, SwingConstants.CENTER);
        headerPanel.add(billLabel);

        frame.add(headerPanel, BorderLayout.NORTH);

        // Table Panel
        tableModel = new DefaultTableModel(new Object[]{"Item", "Quantity", "Price", "Total"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 4, 10, 10));

        itemField = new JTextField();
        quantityField = new JTextField();
        priceField = new JTextField();

        //add all the datas to the frame

        JButton addButton = new JButton("Add Item");
        JButton removeButton = new JButton("Remove Item");
        JButton printButton = new JButton("Print Receipt");
        JButton addStockButton = new JButton("Add to Stock");
        JButton viewStockButton = new JButton("View Stock");
        JButton paymentButton = new JButton("Make Payment");

        inputPanel.add(new JLabel("Item:"));
        inputPanel.add(itemField);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(quantityField);
        // inputPanel.add(new JLabel("Price:"));
        // inputPanel.add(priceField);

        inputPanel.add(addButton);
        inputPanel.add(removeButton);
        inputPanel.add(addStockButton);
        inputPanel.add(viewStockButton);
        inputPanel.add(printButton);
        inputPanel.add(paymentButton);

        frame.add(inputPanel, BorderLayout.SOUTH);

        // Total Panel
        JPanel totalPanel = new JPanel();
        totalPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        totalLabel = new JLabel("Total: 0");
        totalPanel.add(totalLabel);
        frame.add(totalPanel, BorderLayout.EAST);

        // Action Listeners
        addButton.addActionListener(e -> addItem());
        removeButton.addActionListener(e -> removeItem());
        printButton.addActionListener(e -> printReceipt());
        addStockButton.addActionListener(e -> addStock());
        viewStockButton.addActionListener(e -> viewStock());
        paymentButton.addActionListener(e -> makePayment());

        frame.setVisible(true);
    }
    // this commented portion is for the QR genetatin  option for the billing system currently i don't need this option so it will be commented
    // // Key Listener for searching items in stock
    // itemField.addKeyListener(new KeyAdapter() {
    //     @Override
    //     public void keyReleased(KeyEvent e) {
    //         String query = itemField.getText().toLowerCase();
    //         List<String> suggestions = stock.keySet().stream()
    //                 .filter(item -> item.toLowerCase().contains(query))
    //                 .collect(Collectors.toList());
    //         if (suggestions.isEmpty()) {
    //             suggestionsList.setListData(new String[]{});
    //             suggestionScrollPane.setVisible(false);
    //         } else {
    //             suggestionsList.setListData(suggestions.toArray(new String[0]));
    //             suggestionScrollPane.setVisible(true);
    //         }
    //         suggestionsList.addListSelectionListener(new ListSelectionListener() {
    //             @Override
    //             public void valueChanged(ListSelectionEvent e) {
    //                 if (!e.getValueIsAdjusting()) {
    //                     String selectedItem = suggestionsList.getSelectedValue();
    //                     if (selectedItem != null) {
    //                         itemField.setText(selectedItem);
    //                         suggestionScrollPane.setVisible(false); // Hide suggestions once selected
    //                     }
    //                 }
    //             }
    //         });
    //     }
    // });
    //form hear wee need this  portions 

    private void addItem() {
        try {
            String item = itemField.getText();
            int quantity = Integer.parseInt(quantityField.getText());
            double price = priceMap.getOrDefault(item, 0.0);

            if (!stock.containsKey(item) || stock.get(item) < quantity) {
                JOptionPane.showMessageDialog(frame, "Insufficient stock for " + item, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            stock.put(item, stock.get(item) - quantity); // Deduct quantity from stock

            double total = quantity * price;
            tableModel.addRow(new Object[]{item, quantity, price, total});
            updateTotal();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid input. Please check the fields.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeItem() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String item = (String) tableModel.getValueAt(selectedRow, 0);
            int quantity = (int) tableModel.getValueAt(selectedRow, 1);

            stock.put(item, stock.getOrDefault(item, 0) + quantity); // Restore stock quantity
            tableModel.removeRow(selectedRow);
            updateTotal();
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a row to remove.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addStock() {
        try {
            String item = JOptionPane.showInputDialog(frame, "Enter item name:");
            int quantity = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter quantity:"));
            double price = Double.parseDouble(JOptionPane.showInputDialog(frame, "Enter price:"));

            stock.put(item, stock.getOrDefault(item, 0) + quantity);
            priceMap.put(item, price);

            JOptionPane.showMessageDialog(frame, "Stock updated for " + item, "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid input.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewStock() {
        StringBuilder stockDetails = new StringBuilder("Item\tQuantity\tPrice\n");
        for (String item : stock.keySet()) {
            stockDetails.append(item).append("\t").append(stock.get(item)).append("\t").append(priceMap.getOrDefault(item, 0.0)).append("\n");
        }
        JOptionPane.showMessageDialog(frame, new JTextArea(stockDetails.toString()), "Stock Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateTotal() {
        double total = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            total += (double) tableModel.getValueAt(i, 3);
        }
        totalLabel.setText("Total: " + total);
    }

    private void printReceipt() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("\tShop Billing System\n");
        receipt.append("Bill Number: ").append(billNumber).append("\n");
        receipt.append("Date: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n\n");
        receipt.append("Item\tQty\tPrice\tTotal\n");

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            receipt.append(tableModel.getValueAt(i, 0)).append("\t")
                    .append(tableModel.getValueAt(i, 1)).append("\t")
                    .append(tableModel.getValueAt(i, 2)).append("\t")
                    .append(tableModel.getValueAt(i, 3)).append("\n");
        }

        receipt.append("\nTotal: ").append(totalLabel.getText().substring(7)).append("\n");

        JTextArea receiptArea = new JTextArea(receipt.toString());
        receiptArea.setEditable(false);
        JOptionPane.showMessageDialog(frame, new JScrollPane(receiptArea), "Receipt", JOptionPane.INFORMATION_MESSAGE);

        // Print the receipt
        try {
            boolean printed = receiptArea.print();
            if (printed) {
                JOptionPane.showMessageDialog(frame, "Receipt printed successfully.", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Printing cancelled.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error while printing: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void makePayment() {
        String[] paymentMethods = {"Cash", "Credit Card", "Debit Card"};
        String selectedPaymentMethod = (String) JOptionPane.showInputDialog(
                frame,
                "Select Payment Method:",
                "Payment",
                JOptionPane.QUESTION_MESSAGE,
                null,
                paymentMethods,
                paymentMethods[0]
        );

        if (selectedPaymentMethod != null) {
            double totalAmount = Double.parseDouble(totalLabel.getText().substring(7));
            String amountPaidStr = JOptionPane.showInputDialog(frame, "Enter Amount Paid:");
            try {
                double amountPaid = Double.parseDouble(amountPaidStr);

                if (amountPaid < totalAmount) {
                    JOptionPane.showMessageDialog(frame, "Insufficient amount paid.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    double change = amountPaid - totalAmount;
                    JOptionPane.showMessageDialog(frame, "Payment successful! Change: " + change, "Payment Success", JOptionPane.INFORMATION_MESSAGE);
                    resetBill();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid amount entered.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetBill() {
        tableModel.setRowCount(0);
        totalLabel.setText("Total: 0");
        billNumber = new Random().nextInt(10000) + 1; // Generate a new bill number
        frame.setTitle("Shop Billing System - Bill Number: " + billNumber);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new ShopBillingSystemGUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

