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
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Server {

    private static final int PORT = 65432;
    private static final int MAX_CLIENTS = 5;

    // Shared list to store client handlers
    private static final List<ClientHandler> clientHandlers = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sequence Sender");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 150);

        JPanel panel = new JPanel();
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

        JLabel userLabel = new JLabel("Sequence:");
        userLabel.setBounds(10, 20, 80, 25);
        panel.add(userLabel);

        JTextField sequenceText = new JTextField(20);
        sequenceText.setBounds(100, 20, 265, 25);
        panel.add(sequenceText);

        JButton sendButton = new JButton("Send");
        sendButton.setBounds(150, 60, 80, 25);
        panel.add(sendButton);

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String sequence = sequenceText.getText();
                sendMessageToAllClients(sequence);
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

                // Esperar pela resposta do cliente
                String response = in.readLine();
                if (response != null) {
                    System.out.println("Response from " + getClientAddress() + ": " + response);
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
