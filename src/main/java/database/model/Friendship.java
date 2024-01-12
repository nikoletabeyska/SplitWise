package database.model;

import jakarta.persistence.*;

@Entity
@Table(name="friendship")
public class Friendship{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    // suggestion for foreign key
    //@ManyToOne
    //@JoinColumn(name = "user_id_1", referencedColumnName = "id")
    //private User firstFriend;
    @Column(name="firstFriendId")
    private int firstFriendId;
    @Column(name="secondFriendId")
    private int secondFriendId;
    @Column(name="amountOwnedToSecondFriend")
    private double amount;

    // it demands a no-arguments constructor
    public Friendship(){
        this.amount = 0;
    }
    // Constructors, getters, and setters
    public Friendship(int firstFriendId, int secondFriendId) {
        this.firstFriendId = firstFriendId;
        this.secondFriendId = secondFriendId;
    }
    public Friendship(int firstFriendId, int secondFriendId, double amount) {
        this.firstFriendId = firstFriendId;
        this.secondFriendId = secondFriendId;
        this.amount = amount;
    }
    // getters
    public double getAmountOwnedByFirstToSecond() { return this.amount; }
    public double getAmountOwnedBySecondToFirst() { return -(this.amount); }
    // setters
    public void setAmountOwnedByFirstToSecond(float amount){
        this.amount = amount;
    }
    public void setAmountOwnedBySecondToFirst(float amount){
        this.amount = -(amount);
    }
    // first friend pays
    public void giveMoneyForSecond(float amount){
        this.amount -= amount;
    }
    // second friend pays
    public void giveMoneyForFirst(float amount){
        this.amount += amount;
    }
}