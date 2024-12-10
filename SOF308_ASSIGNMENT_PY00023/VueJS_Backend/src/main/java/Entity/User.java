package Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users") // ánh xạ đến bảng "users" trong cơ sở dữ liệu
public class User {

    @Id
    private String userName; // sử dụng userName làm khóa chính

    private String pass;
    private String fullname;
    private boolean role; // boolean được ánh xạ với kiểu DEFAULT 0 NOT NULL
    private String email;
    private String avatar;

    public User() {
    }

    // Constructor đầy đủ
    public User(String userName, String pass, String fullname, boolean role, String email, String avatar) {
        this.userName = userName;
        this.pass = pass;
        this.fullname = fullname;
        this.role = role;
        this.email = email;
        this.avatar = avatar;
    }

    // Getter và Setter cho userName
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    // Getter và Setter cho pass
    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    // Getter và Setter cho fullname
    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    // Getter và Setter cho role
    public boolean isRole() {
        return role;
    }

    public void setRole(boolean role) {
        this.role = role;
    }

    // Getter và Setter cho email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter và Setter cho avatar
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
