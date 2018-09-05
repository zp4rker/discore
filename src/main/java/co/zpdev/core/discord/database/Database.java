package co.zpdev.core.discord.database;

import co.zpdev.core.discord.exception.ExceptionHandler;

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

    public Database(String host, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
    }

    public void openConnection() throws SQLException {
        if (con != null) return;
        if (username != null) con = DriverManager.getConnection("jdbc:mysql://" + host, username, password);
        else con = DriverManager.getConnection("jdbc:sqlite:" + host);
    }

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

    public ResultSet query(String query, Object... args) throws SQLException {
        PreparedStatement statement = con.prepareStatement(query);
        for (int i = 0; i < args.length; i++) statement.setObject(i + 1, args[i]);
        ResultSet result = statement.executeQuery();
        statements.add(statement);
        return result;
    }

    public int update(String query, Object... args) throws SQLException {
        PreparedStatement statement = con.prepareStatement(query);
        for (int i = 0; i < args.length; i++) statement.setObject(i + 1, args[i]);
        int code = statement.executeUpdate();
        statements.add(statement);
        return code;
    }

}
