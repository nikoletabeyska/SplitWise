package database.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name="groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;
    @Column(name="name", unique = true)
    private String name;

    @ManyToMany
    @JoinTable(
            name="user_group",
            joinColumns = @JoinColumn(name="group_id"),
            inverseJoinColumns = @JoinColumn(name="user_id")
    )

    private List<User> members;

    public Group(){
        this.members = new ArrayList<>();
    }
    public Group(String name){
        this.name = name;
        this.members = new ArrayList<>();
    }
    public Group(String name,List<User> members){
        this.name = name;
        this.members = new ArrayList<User>(members);
    }
    public String getName(){
        return this.name;
    }
    public List<User> getMembers(){
        return this.members;
    }

    public int getId() {
        return id;
    }

    public void setName(String name){
        this.name = name;
    }
    public void addMember(User newMember){
        this.members.add(newMember);
    }
    public void removeMember(User memberToBeRemoved){
        if(members.size()>=3){
            this.members.remove(memberToBeRemoved);
        }
        else{
            // TO DO: Delete group
            // Decide if two friends are a group or not, we have their money history in the friendship table
            ;
        }
    }
}
