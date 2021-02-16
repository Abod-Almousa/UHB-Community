package sa.edu.uhb.uhbcommunity.Model;

public class User {

    private String bio;
    private String email;
    private String fullname;
    private String id;
    private String image;
    private String role;
    private String username;

    public User() {

    }

    public User(String bio, String email, String fullname, String id, String image, String role, String username) {
        this.bio = bio;
        this.email = email;
        this.fullname = fullname;
        this.id = id;
        this.image = image;
        this.role = role;
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
