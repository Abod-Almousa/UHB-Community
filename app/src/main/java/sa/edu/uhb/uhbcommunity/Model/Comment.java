package sa.edu.uhb.uhbcommunity.Model;

public class Comment {

    private String commentid;
    private String comment;
    private String publisher;
    private String date;
    private String time;

    public Comment() {
    }

    public Comment(String commentid, String comment, String publisher, String date, String time) {
        this.commentid = commentid;
        this.comment = comment;
        this.publisher = publisher;
        this.date = date;
        this.time = time;
    }

    public String getCommentid() { return commentid; }

    public void setCommentid(String commentid) { this.commentid = commentid; }

    public String getComment() { return comment; }

    public void setComment(String comment) { this.comment = comment; }

    public String getPublisher() { return publisher; }

    public void setPublisher(String publisher) { this.publisher = publisher; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }

    public void setTime(String time) { this.time = time; }
}