import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AutoCommitExericse {

    @Before
    public void setUp() {
        try(Connection connection = getConnection()) {
            createTables(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void autocommit_true() {
        try(Connection connection = getConnection()) {
            connection.isValid(0);
            connection.createStatement().execute("insert into items (name)"
                    + " values ('Windows 10 Premium Edition')");

            connection.createStatement().execute("insert into bids(name, time, amount, currency) values('Santhosh', now(), 1, 'PHP')");

            connection.createStatement().execute("insert into bidz(name, time, amount, currency) values('Kumar', now(), 1, 'PHP')");


        } catch (SQLException e) {
            try (ResultSet resultSet = getConnection().createStatement().executeQuery("select * from bids")) {
                while (resultSet.next()) {
                    System.out.println(resultSet.getString("name"));
                    System.out.println(resultSet.getTimestamp("time"));
                    System.out.println(resultSet.getInt("amount"));
                    System.out.println(resultSet.getString("currency"));

                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }

            e.printStackTrace();
        }
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
