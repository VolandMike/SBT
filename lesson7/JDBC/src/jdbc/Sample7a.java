package jdbc;

import java.sql.*;

public class Sample7a {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        Class.forName("org.hsqldb.jdbc.JDBCDriver");
        Connection conn = DriverManager.getConnection("jdbc:hsqldb:file:H:\\temp\\db/TEST", "sa", "");

        Statement stat = conn.createStatement();
        stat.executeUpdate("CREATE TABLE TEMP (id INT, NAME VARCHAR (20))");

        PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO TEMP VALUES (?, ?)");

        conn.setAutoCommit(false);
        for (int i = 0; i < 10000; i++) {
            preparedStatement.setInt(1, i);
            preparedStatement.setString(2, "zzz" + (i * i));
            preparedStatement.addBatch();

            if (i % 1000 == 0) {
                preparedStatement.executeBatch();
                conn.commit();
                System.out.println("Batch at i == " + i);
            }
        }
        preparedStatement.executeBatch();
        conn.commit();

        preparedStatement.close();

        preparedStatement = conn.prepareStatement("SELECT id, name FROM TEMP WHERE id > ?");
        preparedStatement.setInt(1, 9990);
        ResultSet res = preparedStatement.executeQuery();

        ResultSetMetaData meta = res.getMetaData();
        for (int i = 1; i <= meta.getColumnCount(); i ++) {
            System.out.print(meta.getColumnLabel(i));
            System.out.print(" | ");
        }
        System.out.println("\n==============================");

        while (res.next()) {
            System.out.print(res.getString(1));
            System.out.print(" | ");
            System.out.println(res.getString(2));
        }
        res.close();
        preparedStatement.close();
        stat.executeUpdate("DROP TABLE TEMP");
        stat.close();
        if (conn != null) {
            conn.close();
        }
    }
}
