package com.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Hello world!
 */
public final class App {

    private App() {
    }

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        final int port = 8080;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");

                handleClient(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
            InputStream input = clientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            OutputStream output = clientSocket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
        ) {
            String firstLine = reader.readLine();
            System.out.println("Request: " + firstLine);

            if (firstLine != null && firstLine.startsWith("GET")) {
                writer.println("HTTP/1.1 200 OK");
                writer.println("Content-Type: text/html");
                writer.println("Connection: close");
                writer.println();

                serveHtmlFile(writer, "target/site/jacoco/index.html");

                writer.flush();
            }

            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Client handling exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void serveHtmlFile(PrintWriter writer, String filePath) {
        try (BufferedReader fileReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                writer.println(line);
            }
        } catch (IOException e) {
            writer.println("<h1>404 Not Found</h1>");
        }
    }
}
