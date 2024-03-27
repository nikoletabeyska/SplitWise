## Description
This project is a console client-server application which is simplified version of the popular expense-tracking app Splitwise. 
It aims to simplify sharing expenses with friends, individually or in groups. The idea is taken from Advanced Java Technologies course in Sofia university.

The functionalities include:

- adding friends and creating groups of friends
- spliting expense with a friend or a group
- paying off debts to friends and groups
- seeing all of your obligations to groups or friends (get-status)
  

## Commands
```
- login <username> <password>
- register <name> <username> <password>
- add-friend <username>
- create-group <group_name> <username> <username> ... <username>
- split <amount> <username> <reason_for_payment>
- split-group <amount> <group_name> <reason_for_payment>
- get-status
- pay <value> <username>
- pay-group <value> <group-name>
```

## Technologies 
 - Java 19
 - JUnit
 - Mockito
 - Jakarta persistance API
 - Maven
   
