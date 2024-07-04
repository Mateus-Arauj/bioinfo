import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Server {

    private static final int PORT = 65432;
    private static final int MAX_CLIENTS = 5;

    private static final List<ClientHandler> clientHandlers = Collections.synchronizedList(new ArrayList<>());
    private static final List<String> clientResponses = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sequence Sender");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server listening on port " + PORT);

            while (clientHandlers.size() < MAX_CLIENTS) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
                sendMessageToAllClients(sequence1 + ";" + sequence2);
            }
        });

        JButton analyzeButton = new JButton("Analyze");
        analyzeButton.setBackground(new Color(0, 0, 255));  // Azul
        analyzeButton.setForeground(Color.WHITE);
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(analyzeButton, gbc);

        analyzeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String bestResult = analyzeBestResult();
                resultArea.setText(formatResult(bestResult));
            }
        });

        JButton resetButton = new JButton("Reset");
        resetButton.setBackground(new Color(255, 69, 0));  // Laranja
        resetButton.setForeground(Color.WHITE);
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(resetButton, gbc);

        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clientResponses.clear();
                resultArea.setText("");
                System.out.println("Responses have been reset.");
            }
        });
    }

    private static void sendMessageToAllClients(String message) {
        synchronized (clientHandlers) {
            for (ClientHandler clientHandler : clientHandlers) {
                clientHandler.sendMessage(message);
            }
        }
    }

    private static String analyzeBestResult() {
        String bestResult = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        double bestGap = Double.POSITIVE_INFINITY;
        double bestTime = Double.POSITIVE_INFINITY;

        for (String response : clientResponses) {
            String[] parts = response.split(";");
            double needlemanScore = Double.parseDouble(parts[2].split(":")[1]);
            double needlemanGap = Double.parseDouble(parts[3].split(":")[1]);
            double needlemanTime = Double.parseDouble(parts[4].split(":")[1]);

            if ((needlemanScore > bestScore) ||
                (needlemanScore == bestScore && needlemanGap < bestGap) ||
                (needlemanScore == bestScore && needlemanGap == bestGap && needlemanTime < bestTime)) {
                bestResult = response;
                bestScore = needlemanScore;
                bestGap = needlemanGap;
                bestTime = needlemanTime;
            }
        }

        return bestResult;
    }

    private static String formatResult(String result) {
        if (result == null) {
            return "No results received.";
        }

        String[] parts = result.split(";");
        StringBuilder formattedResult = new StringBuilder();

        formattedResult.append("Client: ").append(parts[0]).append("\n\n");
        formattedResult.append("Needleman-Wunsch\n");
        formattedResult.append("  Alignment Score: ").append(parts[2].split(":")[1]).append("\n");
        formattedResult.append("  Gap: ").append(parts[3].split(":")[1]).append("\n");
        formattedResult.append("  Execution Time: ").append(parts[4].split(":")[1]).append("\n\n");
        formattedResult.append("Smith-Waterman\n");
        formattedResult.append("  Alignment Score: ").append(parts[6].split(":")[1]).append("\n");
        formattedResult.append("  Gap: ").append(parts[7].split(":")[1]).append("\n");
        formattedResult.append("  Execution Time: ").append(parts[8].split(":")[1]).append("\n");

        return formattedResult.toString();
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Wait for response from the client
                String response = in.readLine();
                if (response != null) {
                    System.out.println("Response from " + getClientAddress() + ": " + response);
                    synchronized (clientResponses) {
                        clientResponses.add(response);
                    }
                }

                clientSocket.close();
                System.out.println("Connection closed with " + getClientAddress());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(String message) {
            if (out != null) {
                out.println(message);
                out.flush();  // Certifique-se de que a mensagem seja enviada imediatamente
            }
        }

        public String getClientAddress() {
            return clientSocket.getRemoteSocketAddress().toString();
        }
    }
}
