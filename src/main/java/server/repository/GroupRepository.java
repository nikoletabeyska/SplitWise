package server.repository;

import com.mysql.cj.log.Log;
import database.model.Group;
import database.model.User;
import jakarta.persistence.*;
import server.Server;
import server.services.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroupRepository extends RepositoryBase {
    public GroupRepository(EntityManager manager) {
       super(manager);
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

            Logger.logError("An error occured while creating a group  ", e);
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
            Logger.logError("An error occured while getting all of the groups  ", e);
            e.printStackTrace();
        }
        List<Group> emptyList = new ArrayList<>();
        return emptyList;
    }





}
