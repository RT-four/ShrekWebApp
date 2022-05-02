package com.example.LoginRegister.UsersTableCreate;

import com.example.Constants;

import java.sql.*;

public class CreateUsersTable {
    public static void createUsersTable(){
        try {
            Connection connection = DriverManager.getConnection(Constants.url, Constants.user, Constants.password);
            Statement statement = connection.createStatement();

            //Check if table exists in database.
            try {
                DatabaseMetaData dbm = connection.getMetaData();
                ResultSet table = dbm.getTables(null, null, "spbexchange_parser", null);
                if (!table.next()) {
                    statement.executeUpdate("CREATE TABLE registered_users(email TEXT PRIMARY KEY NOT NULL, password TEXT NOT NULL, user_name TEXT NOT NULL)");
                    statement.executeUpdate("CREATE EXTENSION pgcrypto");  // Create encryption extension.
                }
                table.close();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }

            statement.close();
            connection.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}
