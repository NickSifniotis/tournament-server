import java.sql.*;

/**
 * Created by nsifniotis on 31/08/15.
 *
 * Wrapper class for interfacing with the database.
 * No special functionality is supplied other than abstracting away the connection deets
 *
 */
public class DBManager {

    private static String db_username = "nick";
    private static String db_password = "b64094bf";
    private static String db_database = "tournament";


    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * Executes the given query
     * No safety checks whatsoever are conducted on the query string. Use with caution!
     *
     * @param query - the query to execute
     */
    public static void Execute (String query)
    {
        Connection connection = connect();

        if (connection != null)
        {
            try
            {
                Statement statement = connection.createStatement();
                statement.execute(query);
                connection.close();
            }
            catch (Exception e)
            {
                System.out.println ("Error executing SQL query: " + query + ": " + e);
            }
        }
    }


    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * This needs to be refactored somehow so that either the calling function handles the connections,
     * or the ResultSet is dumped into some sort of object and then closed.
     * Ideally the latter because the very point of this class is to abstract away database-y details
     * @TODO refactor me bitch
     * @param query - the SELECT query to execute
     * @return - the results of the query
     */
    public static ResultSet ExecuteQuery (String query)
    {
        Connection connection = connect();
        ResultSet results = null;

        if (connection != null)
        {
            try
            {
                Statement statement = connection.createStatement();
                statement.execute(query);
                connection.close();
            }
            catch (Exception e)
            {
                System.out.println ("Error executing SQL query: " + query + ": " + e);
            }
        }

        return results;
    }

    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * Connects to the tournament database.
     * Returns a connection object that can be used to process SQL and so forth.
     *
     * @return a connection object that is connected to the database. Remember to close when finished!
     */
    private static Connection connect()
    {
        Connection connection = null;
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/"
                    + db_database + "?user=" + db_username + "&password=" + db_password);
        }
        catch (Exception e)
        {
            System.out.println ("Error connecting to tournament database: " + e);
        }

        return connection;
    }
}
