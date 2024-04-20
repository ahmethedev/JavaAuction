import java.io.*;
import java.net.*;
import java.util.*;

public class AuctionServer {
    private static final int PORT = 12345;
    private ServerSocket serverSocket;
    private List<Auction> auctions;

    public AuctionServer() {
        auctions = new ArrayList<>();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Auction Server started on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void createAuction(String itemName, int startingPrice) {
        Auction newAuction = new Auction(itemName, startingPrice);
        auctions.add(newAuction);
        new Thread(newAuction).start();
    }

    public synchronized boolean placeBid(String bidder, String itemName, int bidAmount) {
        for (Auction auction : auctions) {
            if (auction.getItemName().equals(itemName)) {
                return auction.placeBid(bidder, bidAmount);
            }
        }
        return false;
    }

    public synchronized List<Auction> getAuctions() {
        return auctions;
    }

    public synchronized void closeAuction(Auction auction) {
        auction.closeAuction();
        auctions.remove(auction);
    }

    public synchronized void closeAuction(String itemName) {
        for (Auction auction : auctions) {
            if (auction.getItemName().equals(itemName) && auction.isOpen()) {
                auction.closeAuction();
                System.out.println("Auction for " + itemName + " closed.");
                return;
            }
        }
        System.out.println("Auction for " + itemName + " not found or already closed.");
    }

    public synchronized String viewOngoingAuctions() {
        StringBuilder ongoingAuctions = new StringBuilder();
        for (Auction auction : auctions) {
            if (auction.isOpen()) {
                ongoingAuctions.append("Item: ").append(auction.getItemName())
                               .append(", Current Price: ").append(auction.getCurrentPrice())
                               .append(", Current Winner: ").append(auction.getCurrentWinner())
                               .append("\n");
            }
        }
        if (ongoingAuctions.length() == 0) {
            ongoingAuctions.append("No ongoing auctions.");
        }
        return ongoingAuctions.toString();
    }

    public synchronized String viewClosedAuctions() {
        StringBuilder closedAuctions = new StringBuilder();
        for (Auction auction : auctions) {
            if (!auction.isOpen()) {
                closedAuctions.append("Item: ").append(auction.getItemName())
                              .append(", Final Price: ").append(auction.getCurrentPrice())
                              .append(", Winner: ").append(auction.getCurrentWinner())
                              .append("\n");
            }
        }
        if (closedAuctions.length() == 0) {
            closedAuctions.append("No closed auctions.");
        }
        return closedAuctions.toString();
    }

    public static void main(String[] args) {
        AuctionServer server = new AuctionServer();
        server.start();
    }
}
