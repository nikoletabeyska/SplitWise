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

            //TODO write an SQL query to cover this case and not filter groups here
            Query query = manager.createQuery(
                    "SELECT g FROM Group g",
                    Group.class);
            List<Group> groups = query.getResultList();
            groups.removeIf(x -> !x.getMembers().contains(user));
            return groups;



           // query.setParameter("userid", user.getId());
            //return query.getResultList();
        } catch (Exception e) {
            Logger.logError("An error occured while getting all of the groups  ", e);
            e.printStackTrace();
        }
        List<Group> emptyList = new ArrayList<>();
        return emptyList;
    }





}
