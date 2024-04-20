import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Auction implements Runnable {
    private String itemName;
    private int startingPrice;
    private int currentPrice;
    private String currentWinner;
    private ConcurrentLinkedQueue<Bid> bids;
    private AtomicBoolean isOpen;
    private static final int AUCTION_DURATION = 60000; // 60 seconds

    public Auction(String itemName, int startingPrice) {
        this.itemName = itemName;
        this.startingPrice = startingPrice;
        this.currentPrice = startingPrice;
        this.currentWinner = "No one";
        this.bids = new ConcurrentLinkedQueue<>();
        this.isOpen = new AtomicBoolean(true);
    }

    public synchronized boolean placeBid(String bidder, int bidAmount) {
        if (isOpen.get() && bidAmount > currentPrice) {
            currentPrice = bidAmount;
            currentWinner = bidder;
            bids.add(new Bid(bidder, bidAmount));
            return true;
        }
        return false;
    }

    public synchronized String getCurrentWinner() {
        return currentWinner;
    }

    public synchronized int getCurrentPrice() {
        return currentPrice;
    }

    public synchronized void closeAuction() {
        isOpen.set(false);
    }

    public boolean isOpen() {
        return isOpen.get();
    }

    public String getItemName() {
        return itemName;
    }

    public String getBidHistory() {
        StringBuilder history = new StringBuilder();
        for (Bid bid : bids) {
            history.append(bid.getBidder()).append(" bid ").append(bid.getAmount()).append("\n");
        }
        return history.toString();
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        while (isOpen.get()) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime >= AUCTION_DURATION) {
                closeAuction();
                break;
            }
            try {
                Thread.sleep(1000); // Check every second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
