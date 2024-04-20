import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private AuctionServer server;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket socket, AuctionServer server) {
        this.clientSocket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String[] tokens = inputLine.split(" ");
                String command = tokens[0];

                switch (command) {
                    case "CREATE_AUCTION":
                        if (tokens.length == 3) {
                            String itemName = tokens[1];
                            int startingPrice = Integer.parseInt(tokens[2]);
                            server.createAuction(itemName, startingPrice);
                            out.println("Auction created for " + itemName + " with starting price " + startingPrice);
                        } else {
                            out.println("Invalid command format for CREATE_AUCTION. Usage: CREATE_AUCTION <itemName> <startingPrice>");
                        }
                        break;

                    case "PLACE_BID":
                        if (tokens.length == 4) {
                            String bidder = tokens[1];
                            String itemName = tokens[2];
                            int bidAmount = Integer.parseInt(tokens[3]);
                            boolean success = server.placeBid(bidder, itemName, bidAmount);
                            if (success) {
                                out.println("Bid placed successfully.");
                            } else {
                                out.println("Failed to place bid. Auction may not exist or bid amount is not higher.");
                            }
                        } else {
                            out.println("Invalid command format for PLACE_BID. Usage: PLACE_BID <bidder> <itemName> <bidAmount>");
                        }
                        break;

                    case "VIEW_ONGOING_AUCTIONS":
                        out.println(server.viewOngoingAuctions());
                        break;

                    case "VIEW_CLOSED_AUCTIONS":
                        out.println(server.viewClosedAuctions());
                        break;

                    case "CLOSE_AUCTION":
                        if (tokens.length == 2) {
                            String itemNameToClose = tokens[1];
                            server.closeAuction(itemNameToClose);
                        } else {
                            out.println("Invalid command format for CLOSE_AUCTION. Usage: CLOSE_AUCTION <itemName>");
                        }
                        break;

                    case "EXIT":
                        out.println("Exiting...");
                        return;

                    default:
                        out.println("Unknown command: " + command);
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
