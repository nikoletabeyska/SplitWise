package database.model;

import jakarta.persistence.*;

@Entity
@Table(name="friendship")
public class Friendship{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="friendship_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id_1")
    private User firstFriend;
    @ManyToOne
    @JoinColumn(name = "user_id_2")
    private User secondFriend;

    // it demands a no-arguments constructor
    public Friendship(){}
    public Friendship(User firstFriend, User secondFriend) {
        this.firstFriend = firstFriend;
        this.secondFriend = secondFriend;
    }
    // getters
    public User getFirstFriend() {
        return this.firstFriend;
    }

    public User getSecondFriend() {
        return secondFriend;
    }

    public int getId() {
        return id;
    }
}