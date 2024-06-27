import java.awt.Color;
import java.awt.Font;
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
import javax.swing.SwingConstants;

public class Server {

    private static final int PORT = 65432;
    private static final int MAX_CLIENTS = 5;

    private static final List<ClientHandler> clientHandlers = Collections.synchronizedList(new ArrayList<>());
    private static final List<String> clientResponses = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sequence Sender");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JPanel panel = new JPanel();
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
        panel.setLayout(null);

        JLabel userLabel = new JLabel("Sequence:", SwingConstants.RIGHT);
        userLabel.setBounds(10, 20, 80, 25);
        panel.add(userLabel);

        JTextField sequenceText = new JTextField(20);
        sequenceText.setBounds(100, 20, 365, 25);
        panel.add(sequenceText);

        JButton sendButton = new JButton("Send");
        sendButton.setBounds(480, 20, 80, 25);
        sendButton.setBackground(Color.GREEN);
        sendButton.setForeground(Color.BLACK);
        panel.add(sendButton);

        JTextArea resultArea = new JTextArea();
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultArea.setEditable(false);
        resultArea.setBackground(Color.LIGHT_GRAY);
        resultArea.setForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBounds(10, 60, 550, 250);
        panel.add(scrollPane);

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String sequence = sequenceText.getText();
                sendMessageToAllClients(sequence);
            }
        });

        JButton analyzeButton = new JButton("Analyze");
        analyzeButton.setBounds(480, 320, 80, 25);
        analyzeButton.setBackground(Color.BLUE);
        analyzeButton.setForeground(Color.WHITE);
        panel.add(analyzeButton);

        analyzeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String bestResult = analyzeBestResult();
                resultArea.setText(formatResult(bestResult));
            }
        });

        JButton resetButton = new JButton("Reset");
        resetButton.setBounds(380, 320, 80, 25);
        resetButton.setBackground(Color.RED);
        resetButton.setForeground(Color.WHITE);
        panel.add(resetButton);

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

        formattedResult.append("Client: ").append(parts[0]).append("\n");
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
