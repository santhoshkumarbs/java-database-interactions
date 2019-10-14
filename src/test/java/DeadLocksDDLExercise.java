import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeadLocksDDLExercise {
    @Before
    public void setUp() {
        try(Connection connection = getConnection()) {
            createTables(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private int getItemsCount() {
        int count = 0;
        try(Connection connection = getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery("select count(*) as count from items");
            boolean next = resultSet.next();
            count = resultSet.getInt("count");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    @Test
    public void deadlocks_ddl() {
        System.out.println("Will reach end of the method?");
        try(Connection connectionFromSanthosh = getConnection()) {
            connectionFromSanthosh.setAutoCommit(false);
            connectionFromSanthosh.createStatement().execute("insert into items(name) values('Windows')");
            try(Connection connectionFromKumar = getConnection()) {
                connectionFromKumar.setAutoCommit(false);
                connectionFromKumar.createStatement().execute("alter table items add column (release_date date null)");
                connectionFromKumar.commit();
            }
            connectionFromSanthosh.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Yes");
    }


    private void createTables(Connection connection) {
        try {
            connection.createStatement().execute("create table items (id" +
                    " identity, name VARCHAR)");
            connection.createStatement().execute("create table bids(id IDENTITY, name VARCHAR, time TIMESTAMP, amount NUMBER, currency VARCHAR)");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:mem:exercise_db;DB_CLOSE_DELAY=-1");
    }
}
