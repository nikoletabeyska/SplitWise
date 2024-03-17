## Description
This project is a console client-server application which is simplified version of the popular expense-tracking app Splitwise. 
It aims to simplify sharing expenses with friends, individually or in groups. The idea is taken from Advanced Java Technologies course in Sofia university.

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
   
