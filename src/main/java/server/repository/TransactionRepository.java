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


public class TransactionRepository extends RepositoryBase{

    public TransactionRepository(EntityManager manager) {
        super(manager);
    }


    //Get all active transactions to or from  another user SUMMED in
    //Moneyflow object that is not in the database
    // that are NOT from a group and sums their amounts
    //Positive amounts means that money is being received, negative means money is owed
    public Map<User,Double> getOwedMoneyTo(User user, Group group)
    {
        EntityTransaction transaction = null;
        try {

            Query query = manager.createQuery(
                    "SELECT t.giver, sum(t.amount) FROM Moneyflow t WHERE t.taker.username = :username " +
                            "AND  (:group IS NULL OR t.group = :group) AND isActive=true  group by t.giver",
                    Object[].class);
            query.setParameter("username", user.getUsername());
            query.setParameter("group", group);

            Map<User,Double> toReturn = new HashMap<>();
            for (Object[] o : (List<Object[]>)query.getResultList())
            {
                toReturn.put((User)o[0], (Double)o[1]);
            }
            return toReturn;
        } catch (Exception e) {
            Server.logger.logError("An error occured  ", e);
            e.printStackTrace();
        }
        return null;
    }
    //Get all active transactions to or from  another user SUMMED in
    //moneyflow object that is not in the database
    // that are NOT from a group and sums their amounts
    //Positive amounts means that money is being received, negative means money is owed
    public  Map<User, Double> getOwedMoneyFrom(User user,Group group)
    {
        EntityTransaction transaction = null;

        try {

            Query query = manager.createQuery(
                    "SELECT t.taker, sum(t.amount) FROM Moneyflow t WHERE t.giver.username = :username " +
                            "AND (:group IS NULL OR t.group = :group) AND isActive=true  group by t.taker",
                    Object[].class);
            query.setParameter("username", user.getUsername());
            query.setParameter("group", group);

            Map<User,Double> toReturn = new HashMap<>();
            for (Object[] o : (List<Object[]>)query.getResultList())
            {
                toReturn.put((User)o[0], (Double)o[1]);
            }
            return toReturn;
        } catch (Exception e) {
            Server.logger.logError("An error occured  ", e);
            e.printStackTrace();
        }
        return null;
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
            Server.logger.logError("An error occured  ", e);
            e.printStackTrace();
        }
    }
    public void closeEntityManagerFactory() {
        if (manager != null && manager.isOpen()) {
            manager.close();
        }
    }

    public List<User> getWantedGroupMembers(String groupName){

        Query query = manager.createQuery(
                "SELECT g.members FROM Group g WHERE g.name=:groupName",
                User.class);
        query.setParameter("groupName",groupName);
        List<User> members = query.getResultList();
        return members;
    }
}
