import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeadlocksInsert1Exercise {

    @Before
    public void setUp() {
        try(Connection connection = getConnection()) {
            createTables(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deadlocks_insert1() {
        System.out.println("Do we go to end of the method");
        try(Connection connectionFromSanthosh = getConnection()) {
            connectionFromSanthosh.setAutoCommit(false);
            connectionFromSanthosh.createStatement().execute("insert into items(name) values('Windows')");

            try(Connection connectionFromKumar = getConnection()) {
                connectionFromKumar.setAutoCommit(false);
                //connectionFromKumar.createStatement().execute("update items set name= 'Door' where name= 'Windows'");
                connectionFromKumar.createStatement().execute("insert into items(name) values('Windows')");
                //connectionFromKumar.createStatement().execute("delete items where name= 'Windows'");
                connectionFromKumar.commit();
            }
            connectionFromSanthosh.commit();
            /*try(Connection connectionFromThird = getConnection()) {
                connectionFromThird.setAutoCommit(false);
                connectionFromThird.createStatement().execute("insert into items(name) values('Windows')");
                //connectionFromKumar.commit();
            }*/


        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(getItemsCount());
        System.out.println("Yes");
    }


    private int getItemsCount() {
        int count = 0;
        try(Connection connection = getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery("select count(*) as count from items");
            boolean next = resultSet.next();
            count = resultSet.getInt("count");

            ResultSet resultSet1 = connection.createStatement().executeQuery("select * from items");
            while (resultSet1.next()) {
                System.out.println(resultSet1.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }


    private void createTables(Connection connection) {
        try {
            connection.createStatement().execute("create table items (id" +
                    " identity, name VARCHAR)");
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
