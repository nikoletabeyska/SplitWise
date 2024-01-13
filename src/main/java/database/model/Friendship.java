package database.model;

import jakarta.persistence.*;

@Entity
@Table(name="friendship")
public class Friendship{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id_1", referencedColumnName = "id")
    private User firstFriend;
    @ManyToOne
    @JoinColumn(name = "user_id_2",referencedColumnName = "id")
    private User secondFriend;
    @Column(name="amountOwnedToSecondFriend")
    private double amount;

    // it demands a no-arguments constructor
    public Friendship(){
        this.amount = 0;
    }
    // Constructors, getters, and setters
    public Friendship(User firstFriend, User secondFriend) {
        this.firstFriend = firstFriend;
        this.secondFriend = secondFriend;
    }
    public Friendship(User firstFriend, User secondFriend, double amount) {
        this.firstFriend = firstFriend;
        this.secondFriend = secondFriend;
        this.amount = amount;
    }
    // getters
    public User getFirstFriend() {
        return this.firstFriend;
    }

    public User getSecondFriend() {
        return secondFriend;
    }

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