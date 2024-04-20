package server;

import database.model.Group;
import jakarta.persistence.EntityManager;
import server.repository.FriendshipRepository;
import server.repository.GroupRepository;
import server.repository.TransactionRepository;
import server.repository.UserRepository;
import server.services.ExpensesService;
import server.services.FriendshipService;
import server.services.GroupService;
import server.services.UserManager;

public class ClassesInitializer {

    private EntityManager manager;
    private ExpensesService expensesService;
    private FriendshipService friendshipService;
    private GroupService groupService;
    private UserManager userManager;
    private FriendshipRepository friendshipRepository;
    private GroupRepository groupRepository;
    private TransactionRepository transactionRepository;
    private UserRepository userRepository;
    private static ClassesInitializer instance = null;

    private ClassesInitializer(EntityManager manager) {
        this.manager = manager;
        this.userRepository = new UserRepository(manager);
        this.friendshipRepository = new FriendshipRepository(manager);
        this.groupRepository = new GroupRepository(manager);
        this.transactionRepository = new TransactionRepository(manager);
        this.userManager = new UserManager(this);
        this.friendshipService = new FriendshipService(this);
        this.groupService = new GroupService(this);
        this.expensesService = new ExpensesService(this);
    }

    public static ClassesInitializer getInstance(EntityManager manager) {
        if (instance == null) {
            instance = new ClassesInitializer(manager);
        }
        return instance;
    }

    public ExpensesService getExpensesService() {
        return expensesService;
    }

    public void setManager(EntityManager manager) {
        this.manager = manager;
    }

    public void setExpensesService(ExpensesService expensesService) {
        this.expensesService = expensesService;
    }

    public void setFriendshipService(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    public void setGroupService(GroupService groupService) {
        this.groupService = groupService;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public void setFriendshipRepository(FriendshipRepository friendshipRepository) {
        this.friendshipRepository = friendshipRepository;
    }

    public void setGroupRepository(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public void setTransactionRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public FriendshipService getFriendshipService() {
        return friendshipService;
    }

    public GroupService getGroupService() {
        return groupService;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public FriendshipRepository getFriendshipRepository() {
        return friendshipRepository;
    }

    public GroupRepository getGroupRepository() {
        return groupRepository;
    }

    public TransactionRepository getTransactionRepository() {
        return transactionRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }
}
