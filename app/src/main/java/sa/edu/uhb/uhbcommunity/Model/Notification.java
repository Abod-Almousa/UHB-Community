package sa.edu.uhb.uhbcommunity.Model;

public class Notification {

    private String notificationId;
    private String from;
    private String postId;
    private String text;
    private String date;
    private String time;
    private Boolean seen;

    public Notification() {
    }

    public Notification(String notificationId, String from, String postId, String text, String date, String time, Boolean seen) {
        this.notificationId = notificationId;
        this.from = from;
        this.postId = postId;
        this.text = text;
        this.date = date;
        this.time = time;
        this.seen = seen;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
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

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }
}