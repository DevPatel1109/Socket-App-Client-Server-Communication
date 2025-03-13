//package com.example.socket_app;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import androidx.appcompat.app.AppCompatActivity;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.Socket;
//
//public class MainActivity extends AppCompatActivity {
//    private TextView textView, messageView;
//    private Button btnConnect, btnSend;
//    private EditText etMessage;
//
//    private static final String SERVER_IP = "192.168.0.207"; // Change to your server IP
//    private static final int SERVER_PORT = 4000;
//
//    private Socket socket;
//    private PrintWriter printWriter;
//    private BufferedReader bufferedReader;
//    private boolean isConnected = false;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        textView = findViewById(R.id.textView);
//        messageView = findViewById(R.id.message);
//        btnConnect = findViewById(R.id.button);
//        btnSend = findViewById(R.id.button1);
//        etMessage = findViewById(R.id.editTextText);
//
//        btnConnect.setOnClickListener(v -> new Thread(this::connectToServer).start());
//        btnSend.setOnClickListener(v -> sendMessage());
//    }
//
//    private void connectToServer() {
//        try {
//            socket = new Socket(SERVER_IP, SERVER_PORT);
//            printWriter = new PrintWriter(socket.getOutputStream(), true);
//            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            isConnected = true;
//
//            runOnUiThread(() -> textView.setText("Connected to server"));
//
//            // Start listening for messages from the server
//            new Thread(this::Message).start();
//        } catch (Exception e) {
//            runOnUiThread(() -> textView.setText("Error: " + e.getMessage()));
//            isConnected = false;
//        }
//    }
//    private void Message() {
//        try {
//            String receivedMessage;
//            while ((receivedMessage = bufferedReader.readLine()) != null) {
//                String finalMessage = receivedMessage.trim(); // Trim to avoid unwanted spaces
//                runOnUiThread(() -> messageView.append("\nServer: " + finalMessage));
//            }
//        } catch (Exception e) {
//            runOnUiThread(() -> {
//                textView.append("\nDisconnected.");
//                isConnected = false;
//            });
//        }
//    }
//    private void sendMessage() {
//        if (!isConnected) {
//            textView.setText("Not connected to server");
//            return;
//        }
//
//        String message = etMessage.getText().toString().trim();
//        if (!message.isEmpty()) {
//            new Thread(() -> {
//                printWriter.println(message);
//                runOnUiThread(() -> etMessage.setText("")); // Clear input after sending
//            }).start();
//        }
//    }
//}

package com.example.socket_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private TextView textView, messageView;
    private Button btnConnect, btnSend;
    private EditText etMessage;

    private static final String SERVER_IP = "172.20.10.3"; // Change to your server IP
    private static final int SERVER_PORT = 4000;

    private Socket socket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        messageView = findViewById(R.id.message);
        btnConnect = findViewById(R.id.button);
        btnSend = findViewById(R.id.button1);
        etMessage = findViewById(R.id.editTextText);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        connectToServer();
                    }
                }).start();
            }
        });
        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void connectToServer() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            isConnected = true;

            runOnUiThread(() -> textView.setText("Connected to server"));

            // Start listening for messages from the server
            new Thread(new Runnable() {
                @Override
                public void run() {
                    receiveMessages();
                }
            }).start();
        } catch (Exception e) {
            runOnUiThread(() -> textView.setText("Error: " + e.getMessage()));
            isConnected = false;
        }
    }

    private void receiveMessages() {
        try {
            String receivedMessage;
            while ((receivedMessage = bufferedReader.readLine()) != null) {
                String finalMessage = receivedMessage.trim();
                runOnUiThread(() -> messageView.append("\nServer: " + finalMessage)); // Show server messages
            }
        } catch (Exception e) {
            runOnUiThread(() -> {
                textView.append("\nDisconnected.");
                isConnected = false;
            });
        }
    }

    private void sendMessage() {
        if (!isConnected) {
            textView.setText("Not connected to server");
            return;
        }

        String message = etMessage.getText().toString().trim();
        if (!message.isEmpty()) {
            new Thread(() -> {
                printWriter.println(message);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageView.append("\nClient :"+message);
                        etMessage.setText("");
                    }
                });
            }).start();
        }
    }
}
