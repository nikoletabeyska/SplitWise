package server.services;

import database.model.User;

public class ExpensesService {

    public String split(User user, Double amount, String username, String reason) {
        if (!UserManager.isValidString(username)) {
            return "Invalid input. Username is required.";
        }

        return "";
    }
}
