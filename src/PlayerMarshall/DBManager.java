package PlayerMarshall;

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
                disconnect (connection);
            }
            catch (Exception e)
            {
                String error = "Within DBManager.Execute, error executing SQL query: " + query + ": " + e;
                if (SystemState.DEBUG)
                    System.out.println(error);

                SystemState.Log("Within DBManager.Execute, error executing SQL query: " + query + ": " + e);
            }
        }

        SystemState.Log ("Successfully executed query within DBManager.Execute. " + query);
    }


    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * Executes the SQL and return the key of the affected row
     * Obviously .. the query needs to be one INSERT only.
     *
     * @param query the query to execute
     * @return the pri key of the newly created row
     */
    public static int ExecuteReturnKey (String query)
    {
        int res = -1;
        Connection connection = connect();

        if (connection != null)
        {
            try
            {
                PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                int affected_rows = statement.executeUpdate();

                if (affected_rows != 1)
                {
                    String error = "DBManager.ExecuteReturnKey - Insert into database failed. Affected rows: " + affected_rows + ": " + query;
                    SystemState.Log(error);

                    if (SystemState.DEBUG)
                        System.out.println (error);
                }
                else
                {
                    try (ResultSet generatedKeys = statement.getGeneratedKeys())
                    {
                        if (generatedKeys.next())
                        {
                            res = generatedKeys.getInt(1);
                        }
                        else
                        {
                            throw new SQLException("DBManager.ExecuteReturnKey - Creating user failed, no ID obtained.");
                        }
                    }
                }

                disconnect (connection);
            }
            catch (Exception e)
            {
                String error = "DBManager.ExecuteReturnKey - Error executing SQL query: " + query + ": " + e;
                SystemState.Log(error);

                if (SystemState.DEBUG)
                    System.out.println (error);
            }
        }

        SystemState.Log ("DBManager.ExecuteReturnKey - Insert query a success, returning new prikey " + res);
        return res;
    }


    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * Executes a query and returns the results in a resultSet
     * These queries are executed in three stages
     * connect -> executeQuery -> disconnect
     *
     * @param query - the SELECT query to execute
     * @return - the results of the query
     */
    public static ResultSet ExecuteQuery (String query, Connection connection)
    {
        ResultSet results = null;

        if (connection != null)
        {
            try
            {
                Statement statement = connection.createStatement();
                results = statement.executeQuery(query);

                SystemState.Log ("DBManager.ExecuteQuery - Execution of query successful, results obtained for " + query);
            }
            catch (Exception e)
            {
                String error = "DBManager.ExecuteQuery - Error executing SQL query. Query: " + query + " Exception: " + e;
                SystemState.Log(error);

                if (SystemState.DEBUG)
                    System.out.println (error);
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
    public static Connection connect()
    {
        Connection connection = null;
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/"
                    + db_database + "?user=" + db_username + "&password=" + db_password);

            SystemState.Log ("DBManager.Connect - Connection to database established.");
        }
        catch (Exception e)
        {
            String error = "DBManager.Connect - Exception connecting to tournament database: " + e;
            SystemState.Log(error);

            if (SystemState.DEBUG)
                System.out.println (error);
        }

        return connection;
    }


    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * A variety of methods for disconnecting from the database.
     *
     * @param connection - the connection to disconnect
     */
    public static void disconnect (Connection connection)
    {
        try
        {
            connection.close();

            SystemState.Log("DBManager.disconnect - Disconnected from database!");
        }
        catch (Exception e)
        {
            String error = "DBManager.disconnect - Exception disconnecting from database: " + e;
            SystemState.Log(error);

            if (SystemState.DEBUG)
                System.out.println (error);
        }
    }

    public static void disconnect (ResultSet results)
    {
        try {
            Statement statement = results.getStatement();
            Connection connection = statement.getConnection();

            results.close();
            statement.close();
            connection.close();

            SystemState.Log ("DBManager.disconnect - Disconnected from database!");
        }
        catch (Exception e)
        {
            String error = "DBManager.disconnect - Exception disconnecting from database: " + e;
            SystemState.Log(error);

            if (SystemState.DEBUG)
                System.out.println (error);
        }
    }


    /**
     * Nick Sifniotis u5809912
     * 31/08/2015
     *
     * A quick and dirty function for converting between java booleans and SQL tinyints
     *
     * @param r the bool to convert
     * @return
     */
    public static int BoolValue(boolean r)
    {
        return r ? 1 : 0;
    }
}
