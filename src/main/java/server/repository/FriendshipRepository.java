package server.repository;

import database.model.Friendship;
import database.model.User;
import jakarta.persistence.*;
import server.Server;
import server.services.Logger;

import java.util.List;

public class FriendshipRepository extends  RepositoryBase  {

    public FriendshipRepository(EntityManager manager) {
        super(manager);
    }
    public Friendship createNewFriendship(User from, User to)
    {
        return new Friendship(from, to);
    }

    public void addNewFriendship(Friendship friendship) {
        EntityTransaction transaction = null;
        try {
            transaction = manager.getTransaction();
            transaction.begin();

            manager.persist(friendship);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            Logger.logError("An error occured while adding new friend ", e);
            e.printStackTrace();
        }
    }

    public List<Friendship> getAllFriendships(User user) {
        List<Friendship> friendships = null;

        try {
            Query query = manager.createQuery("SELECT f FROM Friendship f WHERE f.firstFriend = :user OR f.secondFriend = :user", Friendship.class);
            query.setParameter("user", user);
            friendships = query.getResultList();
        } catch (Exception e) {
            Logger.logError("An error occured  ", e);
            e.printStackTrace();
        }
        return friendships;
    }


}
