//package com.example.serverapp;
//
//import android.annotation.SuppressLint;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.TextView;
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.ServerSocket;
//import java.net.Socket;
//
//public class MainActivity extends AppCompatActivity {
//    private TextView textView,textView2;
//    private Button btnStartServer;
//    private static final int PORT = 4000; // Server port
//    private boolean isServerRunning = false;
//    BufferedReader bufferedReader;
//
//    @SuppressLint("MissingInflatedId")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        textView = findViewById(R.id.textView);
//        btnStartServer = findViewById(R.id.btnStartServer);
//        textView2=findViewById(R.id.textView2);
//
//        // Show local IP
//        textView.setText("IP:192.168.0.207");
//
//        // Start Server when button is clicked
//        btnStartServer.setOnClickListener(view -> {
//            if (!isServerRunning) {
//                isServerRunning = true;
//                new Thread(this::startServer).start(); // Run server in background
//            } else {
//                textView.append("\nServer is already running.");
//            }
//        });
//    }
//    private void startServer() {
//        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
//            runOnUiThread(() -> textView.append("\nServer running on port " + PORT));
//
//            while (isServerRunning) {
//                Socket socket = serverSocket.accept(); // Wait for client connection
//
//                new Thread(() -> handleClient(socket)).start(); // Handle client in a new thread
//            }
//
//        } catch (Exception e) {
//            runOnUiThread(() -> textView.append("\nError: " + e.getMessage()));
//        }
//    }
//
//    private void handleClient(Socket socket) {
//        try {
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//
//            runOnUiThread(() -> textView.append("\nClient Connected: " + socket.getInetAddress()));
//
//            String text;
//            while ((text = bufferedReader.readLine()) != null) { // Keep reading messages
//                String finalText = text;
//                runOnUiThread(() -> textView2.append("\nClient: " + finalText));
//            }
//
//            out.println("Goodbye from server!"); // Send response back
//            bufferedReader.close();
//            out.close();
//            socket.close();
//
//        } catch (Exception e) {
//            runOnUiThread(() -> textView.append("\nError: " + e.getMessage()));
//        }
//    }
//
//}

package com.example.serverapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private TextView textView, textView2;
    private EditText etServerMessage;
    private Button btnStartServer, btnSendMessage;

    private static final int PORT = 4000;
    private boolean isServerRunning = false;

    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private Socket clientSocket;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        etServerMessage = findViewById(R.id.etServerMessage);
        btnStartServer = findViewById(R.id.btnStartServer);
        btnSendMessage = findViewById(R.id.btnSendMessage);

        textView.setText("IP: 172.20.10.3");

        btnStartServer.setOnClickListener(view -> {
            if (!isServerRunning) {
                isServerRunning = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startServer();
                    }
                }).start();
            } else {
                textView.append("\nServer is already running.");
            }
        });

        btnSendMessage.setOnClickListener(view -> sendMessageToClient());
    }

    private void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            runOnUiThread(() -> textView.append("\nServer running on port " + PORT));

            while (isServerRunning) {
                clientSocket = serverSocket.accept();
                printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                runOnUiThread(() -> textView.append("\nClient Connected: " + clientSocket.getInetAddress()));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        receiveMessagesFromClient();
                    }
                }).start();
            }
        } catch (Exception e) {
            runOnUiThread(() -> textView.append("\nError: " + e.getMessage()));
        }
    }

    private void receiveMessagesFromClient() {
        try {
            String text;
            while ((text = bufferedReader.readLine()) != null) {
                String finalText = text;
                runOnUiThread(() -> textView2.append("\nClient: " + finalText)); // Show client message
            }
        } catch (Exception e) {
            runOnUiThread(() -> textView.append("\nClient Disconnected."));
        }
    }

    private void sendMessageToClient() {
        if (printWriter == null || clientSocket == null) {
            runOnUiThread(() -> textView.append("\nNo client connected."));
            return;
        }

        String message = etServerMessage.getText().toString().trim();
        if (!message.isEmpty()) {
            new Thread(() -> {
                printWriter.println(message);
                runOnUiThread(() -> etServerMessage.setText("")); // Clear input after sending
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView2.append("\nServer :"+message);
                        etServerMessage.setText("");
                    }
                });
            }).start();
        }
    }
}
