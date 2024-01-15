package server.services;

import database.model.Group;
import database.model.Moneyflow;
import database.model.User;
import server.repository.FriendshipRepository;
import server.repository.GroupRepository;
import server.repository.TransactionRepository;
import server.repository.UserRepository;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ExpensesService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final TransactionRepository transactionRepository;
    private final Logger logger;

    public ExpensesService() {
        userRepository = new UserRepository();
        groupRepository = new GroupRepository("TestPersistenceUnit");
        transactionRepository = new TransactionRepository("TestPersistenceUnit");
        logger = new Logger();

    }
    public ExpensesService(UserRepository u, GroupRepository g, TransactionRepository t) {
        userRepository = u;
        groupRepository = g;
        transactionRepository = t;
        logger = new Logger();
    }

    public String split(String giverName, String takerName, Double amount, String reason) {
        if (!UserManager.isValidString(giverName)) {
            return "Invalid input. Username is required.";
        }
        if (!UserManager.isValidString(takerName)) {
            return "Invalid input. Taker username is required.";
        }
        User giver = userRepository.getUserByUsername(giverName);
        User taker = userRepository.getUserByUsername(takerName);
        transactionRepository.createTransaction(new Moneyflow(giver, taker, amount/2.0, reason, true));
        logger.log("Split " + amount + " with " + takerName + " for " + reason, giverName);

        return "Successfully split money ";
    }
    public String splitGroup(String giverName,  String[] takersNames,Double amount, String reason) {
        if (!UserManager.isValidString(giverName)) {
            return "Invalid input. Username is required.";
        }
        User giver = userRepository.getUserByUsername(giverName);
        ArrayList<User> takers = new ArrayList<User>();
        for (String takerName : takersNames)
        {
            if (!UserManager.isValidString(takerName)) {
                return  "Invalid input. Takername is invalid." + takerName;
            }
            takers.add(userRepository.getUserByUsername(takerName));
        }


        double splitAmount = amount / takers.size();
        //If all taker names are valid
        for  ( User taker : takers)
        {
            transactionRepository.createTransaction(new Moneyflow(giver, taker, splitAmount, reason, true));
        }

        //to fix
        logger.log("Split " + amount + " with group " + " for " + reason, giverName);
        return "Successfully split money ";
    }


    public Map<User,Double> GetTotalAmountPerUser(User user, Group group)
    {
        //Individual transactions
        Map<User,Double> owedFrom = transactionRepository.getOwedMoneyFrom(user,group);
        Map<User,Double> owedTo =transactionRepository.getOwedMoneyTo(user,group);

//        //Make owed money to be negative
//        for ( User current : owedTo.keySet())
//        {
//            owedTo.replace(current, )
//
//        }
        owedTo.forEach((muser, amount) -> owedTo.replace(muser,-amount));

        // Merge the maps by adding amounts for common users
        Map<User, Double> mergedMap = new HashMap<>(owedFrom);

        owedTo.forEach((muser, amount) ->
                mergedMap.merge(muser, amount, Double::sum));
        return  mergedMap;
    }
    public String AppendTransactionCategory(String result, String categoryName, Map<User,Double> map)
    {
        result += "To/From" + categoryName + "\n";
        for (Map.Entry<User, Double> entry : map.entrySet()) {
            User muser = entry.getKey();
            Double amount = entry.getValue();
            if(entry.getValue()<0)
                result += "     " + "You owe " + muser.getUsername() + ": " + (-amount)+ "\n";
            else
                result += "     "  + muser.getUsername() + " owes you: " + amount+ "\n";

        };
        return result;
    }
    public String getStatus(String userName) {
        if (!UserManager.isValidString(userName)) {
            return "Invalid input. Username is required.";
        }
        User user = userRepository.getUserByUsername(userName);
        //Get all transaction in which user is part of
        String result = "";
        Map<User,Double> mergedMap = GetTotalAmountPerUser(user,null);
        result += AppendTransactionCategory(result, "Individuals",mergedMap);
        result += "Groups";
        List<Group> groups = groupRepository.getAllGroups(user);
        for(Group g : groups)
        {
            Map<User,Double> groupAmount = GetTotalAmountPerUser(user, g);
            result += AppendTransactionCategory(result, g.getName(), groupAmount);
        }

        logger.log("Viewed status of all obligations  ", userName);

        return result;
    }
}
