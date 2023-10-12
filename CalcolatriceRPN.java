import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

public class CalcolatriceRodighiero {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CalculatorFrame frame = new CalculatorFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });
    }
}

class CalculatorFrame extends JFrame {

    private JTextField inputField;
    private JTextArea resultArea;
    private String expression = "";

    public CalculatorFrame() {
        setTitle("Calculator");
        JPanel panel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        inputField.setEditable(false);
        panel.add(inputField, BorderLayout.NORTH);

        resultArea = new JTextArea(10, 20);
        resultArea.setEditable(false);
        panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 4));
        String[] buttonLabels = {
                "7", "8", "9", "+",
                "4", "5", "6", "-",
                "1", "2", "3", "*",
                "0", ".", "=", "/",
                "C"
        };

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.addActionListener(new ButtonClickListener());
            buttonPanel.add(button);
        }

        panel.add(buttonPanel, BorderLayout.SOUTH);
        add(panel);
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = ((JButton) e.getSource()).getText();

            if (command.equals("=")) {
                calculate();
            } else if (command.equals("C")) {
                expression = "";
            } else {
                expression += command;
            }

            inputField.setText(expression);
        }

        private void calculate() {
            try {
                String result = evaluateExpression(expression);
                resultArea.append(expression + " = " + result + "\n");
                expression = result;
                inputField.setText(result);
            } catch (Exception e) {
                inputField.setText("Error");
            }
        }

        private String evaluateExpression(String expression) {
            Stack<Integer> numberStack = new Stack<>();
            Stack<Character> operatorStack = new Stack<>();
            int number = 0;
            boolean isNumber = false;

            for (char c : expression.toCharArray()) {
                if (Character.isDigit(c)) {
                    number = number * 10 + (c - '0');
                    isNumber = true;
                } else {
                    if (isNumber) {
                        numberStack.push(number);
                        number = 0;
                        isNumber = false;
                    }

                    if (c == '+' || c == '-' || c == '*' || c == '/') {
                        while (!operatorStack.isEmpty() && hasPrecedence(c, operatorStack.peek())) {
                            int b = numberStack.pop();
                            int a = numberStack.pop();
                            char op = operatorStack.pop();
                            numberStack.push(applyOperator(a, b, op));
                        }
                        operatorStack.push(c);
                    }
                }
            }

            if (isNumber) {
                numberStack.push(number);
            }

            while (!operatorStack.isEmpty()) {
                int b = numberStack.pop();
                int a = numberStack.pop();
                char op = operatorStack.pop();
                numberStack.push(applyOperator(a, b, op));
            }

            return numberStack.isEmpty() ? "Invalid Expression" : String.valueOf(numberStack.pop());
        }

        private boolean hasPrecedence(char op1, char op2) {
            if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) {
                return false;
            }
            return true;
        }

        private int applyOperator(int a, int b, char operator) {
            switch (operator) {
                case '+':
                    return a + b;
                case '-':
                    return a - b;
                case '*':
                    return a * b;
                case '/':
                    if (b == 0) {
                        throw new ArithmeticException("Division by zero");
                    }
                    return a / b;
                default:
                    throw new IllegalArgumentException("Invalid operator: " + operator);
            }
        }
    }
}
