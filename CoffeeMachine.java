import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

public class CoffeeMachine {

    private JFrame frame;
    private JPanel panel;
    private JButton powerButton, espressoButton, latteButton, insertMoneyButton, refillButton;
    private JLabel messageLabel;
    private double insertedMoney;
    private String selectedDrink;
    private boolean resourcesAvailable = true; // Assume resources are available initially
    private Timer timer;
    private boolean isPoweredOn = false; // Tracks the power state

    public CoffeeMachine() {
        initializeGUI();
        setupEventListeners();
    }

    private void initializeGUI() {
        frame = new JFrame("Coffee Machine");
        panel = new JPanel();
        panel.setLayout(new FlowLayout());

        powerButton = new JButton("Power");
        espressoButton = new JButton("Espresso (0.5 BD)");
        latteButton = new JButton("Coffee Latte (0.7 BD)");
        insertMoneyButton = new JButton("Insert Money");
        refillButton = new JButton("Refill Ingredients");

        messageLabel = new JLabel("Press Power to Start");

        panel.add(powerButton);
        panel.add(espressoButton);
        panel.add(latteButton);
        panel.add(insertMoneyButton);
        panel.add(refillButton);
        panel.add(messageLabel);

        espressoButton.setEnabled(false);
        latteButton.setEnabled(false);
        insertMoneyButton.setEnabled(false);
        refillButton.setEnabled(false);

        frame.add(panel);
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void togglePower() {
        isPoweredOn = !isPoweredOn; // Toggle the power state
        if (isPoweredOn) {
            messageLabel.setText("Machine is ON. Select a drink.");
            espressoButton.setEnabled(true);
            latteButton.setEnabled(true);
            refillButton.setEnabled(true);
        } else {
            messageLabel.setText("Machine is OFF. Press Power to Start.");
            espressoButton.setEnabled(false);
            latteButton.setEnabled(false);
            insertMoneyButton.setEnabled(false);
            refillButton.setEnabled(false);
            if (timer != null) {
                timer.cancel(); // Stop the timer when powered off
            }
        }
        
    }

    private void setupEventListeners() {
        powerButton.addActionListener(e -> togglePower());
        espressoButton.addActionListener(e -> selectDrink("Espresso"));
        latteButton.addActionListener(e -> selectDrink("Coffee Latte"));
        insertMoneyButton.addActionListener(e -> insertMoney());
        insertMoneyButton.addActionListener(e -> returnChange(insertedMoney)); // Example for returning change
        refillButton.addActionListener(e -> refillResources());
    }

    private void selectDrink(String drink) {
        if (!resourcesAvailable) {
            messageLabel.setText("Not enough ingredients. Please refill.");
            return;
        }
        selectedDrink = drink;
        messageLabel.setText("Insert money for " + drink);
        insertedMoney = 0;
        espressoButton.setEnabled(false);
        latteButton.setEnabled(false);
        insertMoneyButton.setEnabled(true);
        resetTimer();
    }

    // Moved the change calculation to occur immediately after money is inserted
    private void insertMoney() {
        String input = JOptionPane.showInputDialog("Insert money (0.5, 1.0, 0.1 BD)");
        try {
            double money = Double.parseDouble(input);
            if (money == 0.5 || money == 1.0 || money == 0.1) {
                insertedMoney += money;
                double price = selectedDrink.equals("Espresso") ? 0.5 : 0.7;
                if (insertedMoney >= price) {
                    returnChange(price);
                    dispenseDrink();
                } else {
                    messageLabel.setText("Inserted " + insertedMoney + " BD. Continue inserting.");
                }
            } else {
                messageLabel.setText("Invalid coin. Please insert 0.5, 1.0, or 0.1 BD.");
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Invalid input. Please enter a number.");
        }
        resetTimer();
    }

    // Updated the returnChange method to ensure change is returned correctly
    private void returnChange(double price) {
        if (insertedMoney > price) {
            double change = insertedMoney - price;
            JOptionPane.showMessageDialog(frame, "Returning change: " + change + " BD.");
        } else {
            JOptionPane.showMessageDialog(frame, "No change to return.");
        }
        insertedMoney = 0; // Reset inserted money
    }

    private void checkPayment() {
        double price = selectedDrink.equals("Espresso") ? 0.5 : 0.7;
        if (insertedMoney >= price) {
            returnChange(price);
            dispenseDrink();
        } else {
            messageLabel.setText("Inserted " + insertedMoney + " BD. Continue inserting.");
        }
    }

    private void dispenseDrink() {
        messageLabel.setText("Dispensing " + selectedDrink);
        resetTimer();
    }

    private void refillResources() {
        resourcesAvailable = true; // Assume resources are refilled
        messageLabel.setText("Ingredients have been refilled.");
        resetTimer();
    }

    private void resetTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                resetMachine();
            }
        }, 300000); // 5 minutes
    }

    private void resetMachine() {
        JOptionPane.showMessageDialog(frame, "Returning to start state due to inactivity.");
        messageLabel.setText("Press Power to Start");
        insertedMoney = 0;
        espressoButton.setEnabled(false);
        latteButton.setEnabled(false);
        insertMoneyButton.setEnabled(false);
        refillButton.setEnabled(false);
        resourcesAvailable = false; // Assume resources are not available after reset
        if (timer != null) {
            timer.cancel();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CoffeeMachine());
    }
}