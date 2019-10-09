import org.hamcrest.CoreMatchers;
import org.hibernate.criterion.Subqueries;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RollbackExercise {

    @Before
    public void setUp() {
        try(Connection connection = getConnection()) {
            createTables(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void rollback_exercise() {
        try(Connection connection = getConnection()) {
            connection.setAutoCommit(false);

            connection.createStatement().execute("insert into items(name) values('Windows 10')");
            connection.createStatement().execute("insert into bids(name, time, amount, currency) values(" +
                    "'Santhosh', now(), 1, 'EUR')");
            connection.createStatement().execute("insert into bids(name, time, amount, currency) values(" +
                    "'Kumar', now(), 1, 'EUR')");

            throw new RuntimeException("Test");

        } catch (Exception e ) {
                Assert.assertThat(getItemsCount(), CoreMatchers.equalTo(0));

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
