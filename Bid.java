public class Bid {
    private String bidder;
    private int amount;

    public Bid(String bidder, int amount) {
        this.bidder = bidder;
        this.amount = amount;
    }

    public String getBidder() {
        return bidder;
    }

    public int getAmount() {
        return amount;
    }
}
