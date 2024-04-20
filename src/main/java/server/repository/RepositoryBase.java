package server.repository;

import jakarta.persistence.EntityManager;
import server.services.Logger;

//Used to ensure that entity manager is accessible from all repository derived classes
public class RepositoryBase {

    public EntityManager manager;

    RepositoryBase(EntityManager manager) {
        this.manager = manager;
    }
}
