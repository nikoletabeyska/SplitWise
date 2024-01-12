package database.model;

import jakarta.persistence.*;


@Entity
@Table(name="user")
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;
    @Column(name="username")
    private String username;
    @Column(name="password")
    private String password;

    // it demands a no-arguments constructor
    public User(){
        this.username="";
        this.password="";
    }
    // Constructors, getters, and setters
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    // getters
    public String getUsername() { return this.username; }
    public String getPassword() { return this.password; }
    // setters
    public void setUserName(String username){
        this.username=username;
    }
    public void setPassword(String password){
        this.password=password;
    }
}
