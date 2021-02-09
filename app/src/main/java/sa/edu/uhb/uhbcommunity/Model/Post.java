package sa.edu.uhb.uhbcommunity.Model;

public class Post {


    private String category;
    private String date;
    private String description;
    private String image;
    private String postid;
    private String publisher;

    public Post() {
    }

    public Post(String category, String date, String description, String image, String postid, String publisher) {
        this.category = category;
        this.date = date;
        this.description = description;
        this.image = image;
        this.postid = postid;
        this.publisher = publisher;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

}
