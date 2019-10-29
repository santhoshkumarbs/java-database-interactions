import org.junit.Before;
import org.junit.Test;

import java.sql.*;

public class PessimisticLockingExercise {

    @Before
    public void setUp() {
        try(Connection connection = getConnection()) {
            createTables(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void pessimistic_locking_exercise() throws SQLException {
        try(Connection connectionFromSanthosh = getConnection()) {
            connectionFromSanthosh.setAutoCommit(false);
            boolean execute = connectionFromSanthosh.createStatement().execute("insert into items(name,release_date) values('CTU Report'," +
                    "current_date()-100)", Statement.RETURN_GENERATED_KEYS);

            connectionFromSanthosh.commit();
            System.out.println("First insert completed");
        }

        try(Connection connectionFromSanthosh = getConnection()) {
            connectionFromSanthosh.setAutoCommit(false);
            //need to update and make sure no one access it at the same time
            connectionFromSanthosh.createStatement().execute("select * from items where name='CTU Report' for update");
            System.out.println("Santhosh locked the row for any other update");

            try(Connection connectionFromKumar = getConnection()) {
                System.out.println("Kumar is beginning to lock the row");
                connectionFromKumar.setAutoCommit(false);
                connectionFromKumar.createStatement().execute("update items set release_date = current_date() where" +
                        " name = 'CTU Report'");
                connectionFromKumar.commit();
                System.out.println("Kumar completed update");
            }
            connectionFromSanthosh.createStatement().execute("update items set release_date = current_date()-1 where" +
                    " name = 'CTU Report'");
            connectionFromSanthosh.commit();
            System.out.println("santhosh completed update");
        }
    }

    private void createTables(Connection connection) {
        try {
            connection.createStatement().execute("create table items (id" +
                    " identity, release_date date, name VARCHAR)");
            connection.createStatement().execute("alter table items add constraint test_unique unique(name)");
            connection.createStatement().execute("create table bids(id IDENTITY, name VARCHAR, time TIMESTAMP, amount NUMBER, currency VARCHAR)");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:mem:exercise_db;DB_CLOSE_DELAY=-1");
    }
}
