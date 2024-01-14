package database.model;

import jakarta.persistence.*;

import java.util.*;


@Entity
@Table(name="users")
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;
    @Column(name="username")
    private String username;
    @Column(name="password")
    private String password;

    @ManyToMany(mappedBy = "members")
    private List<Group> groups;
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
    public int getId() { return this.id; }

    // setters
    public void setUserName(String username){
        this.username=username;
    }
    public void setPassword(String password){
        this.password=password;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        User otherUser = (User) obj;
        return id == otherUser.id && username.equals(otherUser.username) && password.equals(otherUser.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password);
    }

}
