package server.repository;

import database.model.Friendship;
import database.model.User;
import jakarta.persistence.*;

import java.util.List;

public class FriendshipRepository {
    private static final String PERSISTENCE_UNIT_NAME = "SplitWisePersistenceUnit";

    private final EntityManagerFactory entityManagerFactory;

    public FriendshipRepository() {
        this.entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        //Seed database
        Friendship f = new Friendship(new User("Gosho", "asd"),new User("Tosho", "123"));

    }
    public Friendship createNewFriendship(User from, User to)
    {
        return new Friendship(from, to);
    }

    public void addNewFriendship(Friendship friendship) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;


        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            entityManager.persist(friendship);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }

    public List<Friendship> getAllFriendships(User user) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        List<Friendship> friendships = null;

        try {
            Query query = entityManager.createQuery("SELECT f FROM Friendship f WHERE f.firstFriend = :user OR f.secondFriend = :user", Friendship.class);
            query.setParameter("user", user);

            List<Friendship> resultList = query.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }

        return friendships;
    }
}
