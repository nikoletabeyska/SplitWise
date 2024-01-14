package server.services;

import database.model.Moneyflow;
import database.model.User;
import server.repository.FriendshipRepository;
import server.repository.TransactionRepository;
import server.repository.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExpensesService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public ExpensesService() {
        userRepository = new UserRepository();
        transactionRepository = new TransactionRepository();
    }
    public ExpensesService(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    };

    public String split(String giverName,  String takerName,Double amount, String reason) {
        if (!UserManager.isValidString(giverName)) {
            return "Invalid input. Username is required.";
        }
        if (!UserManager.isValidString(takerName)) {
            return "Invalid input. Taker username is required.";
        }
        User giver = userRepository.getUserByUsername(giverName);
        User taker = userRepository.getUserByUsername(takerName);
        transactionRepository.createTransaction(new Moneyflow(giver, taker, amount/2.0, reason, true));
        return "";
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
        return "Successfully split money ";
    }
    public String getStatus(String userName) {
        if (!UserManager.isValidString(userName)) {
            return "Invalid input. Username is required.";
        }
        User user = userRepository.getUserByUsername(userName);
        //Get all transaction in which user is part of
        List<Moneyflow> transactions =transactionRepository.getAllTransactions(user);
        String result = "";
        for (Moneyflow t:transactions) {
            //Takew

        return "";
        }

        return "";
    }
}
