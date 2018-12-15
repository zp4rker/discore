package com.zp4rker.core.discord.database;

import com.zp4rker.core.discord.exception.ExceptionHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ZP4RKER
 */
public class Database {

    private String host, username, password;
    private Connection con = null;
    private List<PreparedStatement> statements = new ArrayList<>();

    /**
     * Initialises the database class
     *
     * @param host the host/database address
     * @param username the username
     * @param password the password
     */
    public Database(String host, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
    }

    /**
     * Opens a connection to the database. (Required before queries can be made)
     *
     * @throws SQLException when unable to open a connection
     */
    public void openConnection() throws SQLException {
        if (con != null) return;
        if (username != null) con = DriverManager.getConnection("jdbc:mysql://" + host, username, password);
        else con = DriverManager.getConnection("jdbc:sqlite:" + host);
    }

    /**
     * Closes the connection(s) and statement(s) to the database.
     */
    public void closeConnection() {
        try {
            for (PreparedStatement statement : statements) if (statement != null) statement.close();
            statements.clear();
            if (con != null) {
                con.close();
                con = null;
            }
        } catch (SQLException e) {
            ExceptionHandler.handleException("closing database connection", e);
        }
    }

    /**
     * Executes a query.
     *
     * @param statement the statement to be executed
     * @param args any arguments required for the statement
     * @return the resultset of the executed query
     * @throws SQLException when unable to execute the query
     */
    public ResultSet query(String statement, Object... args) throws SQLException {
        PreparedStatement s = con.prepareStatement(statement);
        for (int i = 0; i < args.length; i++) s.setObject(i + 1, args[i]);
        ResultSet result = s.executeQuery();
        statements.add(s);
        return result;
    }

    /**
     * Executes an update query.
     *
     * @param statement the statement to be executed
     * @param args any arguments required for the statement
     * @return the completion code of the execution
     * @throws SQLException when unable to execute the query
     */
    public int update(String statement, Object... args) throws SQLException {
        PreparedStatement s = con.prepareStatement(statement);
        for (int i = 0; i < args.length; i++) s.setObject(i + 1, args[i]);
        int code = s.executeUpdate();
        statements.add(s);
        return code;
    }

}
