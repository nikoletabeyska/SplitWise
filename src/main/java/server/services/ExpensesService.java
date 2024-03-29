package server.services;

import database.model.Group;
import database.model.Moneyflow;
import database.model.User;
import server.ClassesInitializer;
import server.RepositoryImplementationMapping;
import server.Server;
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

    public ExpensesService(ClassesInitializer initializer) {
        this.userRepository = initializer.getUserRepository();
        this.transactionRepository = initializer.getTransactionRepository();
        this.groupRepository = initializer.getGroupRepository();
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
        if (taker == null) {
            return "Taker does not exist.";
        }
        transactionRepository.createTransaction(new Moneyflow(giver, taker, amount / 2.0, reason, true));
        Logger.log("Split " + amount + " with " + takerName + " for " + reason, giverName);

        return "Successfully split money ";
    }

    public String splitGroup(String giverName, String groupName, Double amount, String reason) {

        if (!UserManager.isValidString(giverName)) {
            return "Invalid input. Username is required.";
        }
        User giver = userRepository.getUserByUsername(giverName);

        List<User> membersOfGroup = transactionRepository.getWantedGroupMembers(groupName);
        membersOfGroup.remove(userRepository.getUserByUsername(giverName));
        Group ourGroup = transactionRepository.getWantedGroup(groupName);
        double splitAmount = amount / (membersOfGroup.size() + 1);
        //If all taker names are valid
        for (User member : membersOfGroup) {
            transactionRepository.createTransaction(new Moneyflow(giver, member, splitAmount, reason, true, ourGroup));
        }

        Logger.log("Split " + amount + " with group " + groupName + " for " + reason, giverName);
        return "Successfully split money.";
    }


    //Get owed money to and from another user from a GROUP or INDIVIDUALLY (if group is null)
    // and return the sum of the transcations.
    //E.g m owes n 10, and n owes m  -> so get-status for m yield m owes n 5 (10 -5)(the sum)
    public Map<User, Double> GetTotalAmountPerUser(User user, Group group) {
        Map<User, Double> owedFrom;
        Map<User, Double> owedTo;
        //Individual transactions
        if (group == null) {
            owedFrom = transactionRepository.getOwedMoneyFrom(user);
            owedTo = transactionRepository.getOwedMoneyTo(user);
        } else {
            owedFrom = transactionRepository.getOwedMoneyFrom(user, group);
            owedTo = transactionRepository.getOwedMoneyTo(user, group);
        }

        owedTo.forEach((muser, amount) -> owedTo.replace(muser, -amount));

        // Merge the maps by adding amounts for common users
        Map<User, Double> mergedMap = new HashMap<>(owedFrom);

        owedTo.forEach((muser, amount) ->
            mergedMap.merge(muser, amount, Double::sum));
        return mergedMap;
    }

    public String AppendTransactionCategory(String categoryName, Map<User, Double> map) {
        String result = "\n" + categoryName + "\n";
        for (Map.Entry<User, Double> entry : map.entrySet()) {
            User muser = entry.getKey();
            Double amount = entry.getValue();
            if (entry.getValue() < 0)
                result += "     " + "You owe    " + muser.getUsername() + ":    " + (-amount) + "\n";
            else
                result += "     " + muser.getUsername() + "    owes you:   " + amount + "\n";

        }
        return result;
    }

    public String getStatus(String userName) {
        if (!UserManager.isValidString(userName)) {
            return "Invalid input. Username is required.";
        }
        User user = userRepository.getUserByUsername(userName);
        //Get all transaction in which user is part of
        String result = "";
        Map<User, Double> mergedMap = GetTotalAmountPerUser(user, null);
        result += AppendTransactionCategory("Individual obligations:", mergedMap);
        List<Group> groups = groupRepository.getAllGroups(user);
        if (!groups.isEmpty()) result += "Your groups:";

        for (Group g : groups) {
            Map<User, Double> groupAmount = GetTotalAmountPerUser(user, g);
            result += AppendTransactionCategory(g.getName(), groupAmount);
        }

        Logger.log("Viewed status of all obligations  ", userName);

        return result;
    }

    public String pay(String giverName, Double givenAmount, String takerName) {
        if (!UserManager.isValidString(giverName)) {
            return "Invalid input. Username is required.";
        }
        if (!UserManager.isValidString(takerName)) {
            return "Invalid input. Taker username is required.";
        }
        User giver = userRepository.getUserByUsername(giverName);
        User taker = userRepository.getUserByUsername(takerName);

        if (taker == null) {
            return "Taker does not exist.";
        }
        transactionRepository.createTransaction(new Moneyflow(giver, taker, givenAmount, "payed debt", true));
        Logger.log("Payed " + givenAmount + " to " + takerName, giverName);

        return "Successful payment!";
    }

    public String payGroup(String giverName, Double givenAmount, String groupName) {
        if (!UserManager.isValidString(groupName)) {
            return "Invalid input. Group name is required.";
        }

        User giver = userRepository.getUserByUsername(giverName);
        List<Group> userGroups = groupRepository.getAllGroups(giver);
        for (Group g : userGroups) {
            if (g.getName().equals(groupName)) {

                List<User> membersOfGroup = transactionRepository.getWantedGroupMembers(groupName);
                membersOfGroup.remove(userRepository.getUserByUsername(giverName));

                Map<User, Double> map = GetTotalAmountPerUser(giver, g);
                List<User> membersToGetPayed = new ArrayList<>();
                for (Map.Entry<User, Double> entry : map.entrySet()) {
                    User muser = entry.getKey();
                    Double amount = entry.getValue();
                    if (entry.getValue() < 0)
                        membersToGetPayed.add(muser);
                }

                double splitAmount = givenAmount / (membersToGetPayed.size());
                //If all taker names are valid
                for (User member : membersToGetPayed ) {
                    transactionRepository.createTransaction(
                        new Moneyflow(giver, member , splitAmount, "payed", true, g));
                }
                Logger.log("Payed " + givenAmount + " to group " + groupName, giverName);
                return "Successfull payment!";

            }
        }

        return "You don't participate in such group.";
    }
}
