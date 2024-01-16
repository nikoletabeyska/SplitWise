package server.repository;

import jakarta.persistence.EntityManager;
import server.services.Logger;

public class RepositoryBase {
    public EntityManager manager;
    RepositoryBase(EntityManager manager)
    {
        this.manager= manager;
    }
}
