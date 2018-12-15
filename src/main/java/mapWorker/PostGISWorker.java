package mapWorker;

import database.GameDataSet;
import java.sql.*;
import java.util.Objects;

public class PostGISWorker {
    private Connection c;

    public void connect() {
        try {
            String dbUrl = System.getenv("JDBC_COORD_DATABASE_URL");
            c = DriverManager.getConnection(dbUrl);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void reconnect() {
        try {
            c.close();
            connect();
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void initDatabase() {
        try {
            if (c.isClosed())
                reconnect();

            Statement stmt = c.createStatement();
            var sql = "CREATE EXTENSION IF NOT EXISTS postgis; \n";
            stmt.executeUpdate(sql);
            stmt.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}