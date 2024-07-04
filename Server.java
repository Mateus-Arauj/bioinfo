import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Server {

    private static final String PYTHON_SERVER_HOST = "localhost";
    private static final int PYTHON_SERVER_PORT = 65431;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sequence Sender");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel userLabel1 = new JLabel("Sequence 1:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(userLabel1, gbc);

        JTextField sequenceText1 = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(sequenceText1, gbc);

        JLabel userLabel2 = new JLabel("Sequence 2:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(userLabel2, gbc);

        JTextField sequenceText2 = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(sequenceText2, gbc);

        JButton sendButton = new JButton("Send");
        sendButton.setBackground(new Color(34, 139, 34));  // Verde
        sendButton.setForeground(Color.WHITE);
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(sendButton, gbc);

        JTextArea resultArea = new JTextArea();
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultArea.setEditable(false);
        resultArea.setBackground(Color.LIGHT_GRAY);
        resultArea.setForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(resultArea);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel.add(scrollPane, gbc);

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String sequence1 = sequenceText1.getText();
                String sequence2 = sequenceText2.getText();
                String result = sendSequencesToPythonServer(sequence1, sequence2);
                System.out.println("Received from Python server: " + result);
                resultArea.setText(formatResult(result));
            }
        });

        JButton resetButton = new JButton("Reset");
        resetButton.setBackground(new Color(255, 69, 0));  // Laranja
        resetButton.setForeground(Color.WHITE);
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(resetButton, gbc);

        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resultArea.setText("");
                System.out.println("Responses have been reset.");
            }
        });
    }

    private static String sendSequencesToPythonServer(String sequence1, String sequence2) {
        String response = "";

        try (Socket socket = new Socket(PYTHON_SERVER_HOST, PYTHON_SERVER_PORT)) {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(sequence1 + ";" + sequence2);
            response = in.readLine();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    private static String formatResult(String result) {
        if (result == null || result.isEmpty()) {
            return "No results received.";
        }

        String[] parts = result.split(";");
        if (parts.length < 12) {  
            return parts.length + "";// Verificação para 13 partes incluindo e-value
            // return "Invalid response format.";
        }

        StringBuilder formattedResult = new StringBuilder();

        formattedResult.append("Source: ").append(parts[0]).append("\n\n");
        formattedResult.append("Lines of Code: ").append(parts[1].split(":")[1]).append("\n\n");
        formattedResult.append("Needleman-Wunsch\n");
        formattedResult.append("  Alignment Score: ").append(parts[3].split(":")[1]).append("\n");
        formattedResult.append("  Gap: ").append(parts[4].split(":")[1]).append("\n");
        formattedResult.append("  Execution Time: ").append(parts[5].split(":")[1]).append("\n");
        formattedResult.append("  E-Value: ").append(parts[6].split(":")[1]).append("\n\n");
        formattedResult.append("Smith-Waterman\n");
        formattedResult.append("  Alignment Score: ").append(parts[8].split(":")[1]).append("\n");
        formattedResult.append("  Gap: ").append(parts[9].split(":")[1]).append("\n");
        formattedResult.append("  Execution Time: ").append(parts[10].split(":")[1]).append("\n");
        formattedResult.append("  E-Value: ").append(parts[11].split(":")[1]).append("\n");

        return formattedResult.toString();
    }
}
