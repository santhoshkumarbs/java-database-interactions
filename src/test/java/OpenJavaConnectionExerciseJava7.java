import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OpenJavaConnectionExerciseJava7 {

    private static final int TIMEOUT = 0;

    @Test
    public void open_jdbc_connection_java7() {
        try(Connection connection = DriverManager.getConnection("jdbc:h2:mem:exercise_db;DB_CLOSE_DELAY=-1")) {
            System.out.println(connection.isValid(TIMEOUT));

            connection.createStatement().execute("create table bids(id IDENTITY, user VARCHAR, time TIMESTAMP, amount NUMBER, currency VARCHAR)");
            connection.createStatement().execute("create table items(id IDENTITY, name VARCHAR)");
            System.out.println("Tables are created");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
