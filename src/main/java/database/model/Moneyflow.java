package database.model;

import jakarta.persistence.*;

@Entity
@Table(name="moneyflow")
public class Moneyflow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int moneyflow_id;

    @ManyToOne
    @JoinColumn(name = "user_id_1")
    private User giver;
    @ManyToOne
    @JoinColumn(name = "user_id_2")
    private User taker;

    @Column(name="amountgiven")
    private double amount;

    @Column(name="reason")
    private String reason;

    @Column(name="activeness")
    private boolean isActive;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    public Moneyflow(){
        this.amount=0;
        this.isActive=true;
    }

    public Moneyflow(User giver, User taker, double amount, String reason){
        this.giver =giver;
        this.taker =taker;
        this.amount=amount;
        this.reason=reason;
        this.isActive = true;
    }
    public Moneyflow(User giver, User taker, double amount, String reason, boolean isActive){
        this(giver,taker,amount,reason);
        this.isActive=isActive;
    }
    public Moneyflow(User giver, User taker, double amount, String reason, boolean isActive, Group group){
        this(giver,taker,amount,reason,isActive);
        this.group = group;
    }

    public double getAmount(){
        return this.amount;
    }
    public String getReason(){
        return this.reason;
    }
    public boolean getIsActive(){
        return this.isActive;
    }

    public void setAmount(double amount){
        this.amount=amount;
    }
    public void setReason(String reason){
        this.reason=reason;
    }
    public void setIsActive(boolean isActive){
        this.isActive=isActive;
    }
}
