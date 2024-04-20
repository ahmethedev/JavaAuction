import java.io.*;
import java.net.*;

public class AuctionClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 12345;
    private static BufferedReader userInput;
    private static PrintWriter out;
    private static BufferedReader in;

    public static void main(String[] args) {
        userInput = new BufferedReader(new InputStreamReader(System.in));
        try {
            Socket socket = new Socket(SERVER_ADDRESS, PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("Connected to Auction Server. Type your commands.");

            String inputLine;
            while ((inputLine = userInput.readLine()) != null) {
                out.println(inputLine);
                String response = in.readLine();
                System.out.println(response);
                if (inputLine.equals("VIEW_ONGOING_AUCTIONS") || inputLine.equals("VIEW_CLOSED_AUCTIONS")) {
                    System.out.println(in.readLine());
                } else if (inputLine.startsWith("CLOSE_AUCTION")) {
                    System.out.println(in.readLine());
                }
                if (inputLine.equals("EXIT")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                userInput.close();
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
