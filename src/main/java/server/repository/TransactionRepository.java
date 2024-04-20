package server.repository;

import database.model.Group;
import database.model.Moneyflow;
import database.model.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import jakarta.persistence.*;
import server.Server;
import server.services.Logger;

import java.util.*;

public class TransactionRepository extends RepositoryBase {

    public TransactionRepository(EntityManager manager) {
        super(manager);
    }

    private Map<User, Double> totalAmountByUser(List<Object[]> objectList) {
        Map<User, Double> toReturn = new HashMap<>();
        for (Object[] o : (List<Object[]>)objectList) {
            toReturn.put((User)o[0], (Double)o[1]);
        }
        return  toReturn;
    }

    //Get owed money to an individual outside of what is owed in a group
    public Map<User, Double> getOwedMoneyTo(User user) {
        Query query = null;
        query = manager.createQuery(
                "SELECT t.giver, sum(t.amount) FROM Moneyflow t WHERE t.taker.username = :username " +
                        "AND  t.group IS NULL AND isActive=true  group by t.giver",
                Object[].class)
                .setParameter("username", user.getUsername());
        return totalAmountByUser(query.getResultList());
    }
    //Get owed money to a user in specified group
    //NULL group return and empty map

    public Map<User,Double> getOwedMoneyTo(User user, Group group) {
        //Group must not be null
        if(group == null)
            return  new HashMap<>();
        Query query = manager.createQuery(
                "SELECT t.giver, sum(t.amount) FROM Moneyflow t WHERE t.taker.username = :username " +
                        "AND   t.group = :group AND isActive=true  group by t.giver",
                Object[].class);
        query.setParameter("username", user.getUsername());
        query.setParameter("group", group);
        return totalAmountByUser(query.getResultList());
    }

    //Get owed money from an individual outside of what is owed in a group
    public  Map<User, Double> getOwedMoneyFrom(User user) {
        Query query = manager.createQuery(
                "SELECT t.taker, sum(t.amount) FROM Moneyflow t WHERE t.giver.username = :username " +
                        "AND t.group IS NULL AND isActive=true  group by t.taker",
                Object[].class);
        query.setParameter("username", user.getUsername());
        return totalAmountByUser(query.getResultList());
    }

    //Get owed money from a user in specified group
    //NULL group return and empty map
    public  Map<User, Double> getOwedMoneyFrom(User user,Group group) {
        if (group == null) {
            return new HashMap<>();
        }
        Query query = manager.createQuery(
                "SELECT t.taker, sum(t.amount) FROM Moneyflow t WHERE t.giver.username = :username " +
                        "AND  t.group = :group AND isActive=true  group by t.taker",
                Object[].class);
        query.setParameter("username", user.getUsername());
        query.setParameter("group", group);
        return totalAmountByUser(query.getResultList());
    }

    public void createTransaction(Moneyflow moneyflowTransaction) {
        EntityTransaction transaction = null;
        try {
            transaction = manager.getTransaction();
            transaction.begin();
            manager.persist(moneyflowTransaction);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            Logger.logError("An error occured  ", e);
            e.printStackTrace();
        }
    }

    public void closeEntityManagerFactory() {
        if (manager != null && manager.isOpen()) {
            manager.close();
        }
    }

    public List<User> getWantedGroupMembers(String groupName) {
        Query query = manager.createQuery(
                "SELECT g.members FROM Group g WHERE g.name=:groupName",
                User.class);
        query.setParameter("groupName", groupName);
        List<User> members = query.getResultList();
        return members;
    }

    public Group getWantedGroup(String groupName) {
        Query query = manager.createQuery(
                "SELECT g FROM Group g WHERE g.name=:groupName",
                Group.class);
        query.setParameter("groupName", groupName);
        Group wantedGroup = (Group)query.getSingleResult();
        return wantedGroup;
    }
}
