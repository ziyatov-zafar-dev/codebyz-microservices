package uz.codebyz.user.config;

public class User {
    Long id;
    String fullname;
    String role;

    public User(Long id, String fullname, String role) {
        this.id = id;
        this.fullname = fullname;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
