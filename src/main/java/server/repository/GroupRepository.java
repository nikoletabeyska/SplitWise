package server.repository;

import database.model.Group;
import database.model.User;
import jakarta.persistence.*;
import server.services.Logger;

import java.util.List;
import java.util.Map;

public class GroupRepository {

    private final EntityManager manager;
    private final Logger logger;



    public GroupRepository(String repoName) {
        this.manager = Persistence.createEntityManagerFactory(repoName).createEntityManager();
        logger = new Logger();

    }

    public void createGroup(Group group) {
        EntityTransaction transaction = null;

        try {
            transaction = manager.getTransaction();
            transaction.begin();

            manager.persist(group);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            logger.logError("An error occured while creating a group  ", e);
            e.printStackTrace();
        }
    }
    public List<Group> getAllGroups(User user) {
        EntityTransaction transaction = null;
        try {
            //TODO check hot auto-join or simply join

            Query query = manager.createQuery(
                    "SELECT g FROM Group g WHERE :username in g.members  ",
                    Group.class);
            query.setParameter("username", user.getUsername());
            return query.getResultList();
        } catch (Exception e) {
            logger.logError("An error occured while getting all of the groups  ", e);
            e.printStackTrace();
        }
        return null;
    }





}
