import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionExercise {

    @Before
    public void setUp() {
        try(Connection connection = getConnection()) {
            createTables(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void transaction_exercise() {
        try(Connection connection = getConnection()) {
            //transaction starts
            System.out.println("transaction starts");
            connection.setAutoCommit(false);

            connection.createStatement().execute("insert into items(name) values('Windows 10')");
            connection.createStatement().execute("insert into bids(name, time, amount, currency) values(" +
                    "'Santhosh', now(), 1, 'EUR')");
            connection.createStatement().execute("insert into bids(name, time, amount, currency) values(" +
                    "'Kumar', now(), 1, 'EUR')");
            connection.createStatement().execute("insert into bidz(name, time, amount, currency) values(" +
                    "'Kumar', now(), 1, 'EUR')");


            //transaction commits
            connection.commit();
            System.out.println("transaction has been committed");
        } catch (SQLException e) {
            try(Connection connection = getConnection()) {
                System.out.println("retrieving records");
                ResultSet resultSet = connection.createStatement().executeQuery("select * from bids");
                while (resultSet.next()) {
                    System.out.println("Name:" + resultSet.getString("name"));
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
