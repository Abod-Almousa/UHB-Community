package sa.edu.uhb.uhbcommunity.Model;

public class Notification {

    private String from;
    private String postId;
    private String text;
    private String date;
    private String time;

    public Notification() {
    }

    public Notification(String from, String postId, String text, String date, String time) {
        this.from = from;
        this.postId = postId;
        this.text = text;
        this.date = date;
        this.time = time;
    }

    public String getFrom() { return from; }

    public void setFrom(String from) { this.from = from; }

    public String getPostId() { return postId; }

    public void setPostId(String postId) { this.postId = postId; }

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }

    public void setTime(String time) { this.time = time; }
    
}