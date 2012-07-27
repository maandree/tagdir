/**
 *  tagdir — Tag files to add and remove the from directories
 *  Copyright © 2012  Mattias Andrée
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.kth.maandree.tagdir;

import java.sql.*;
import java.util.*;


/**
 * Database façade
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class Database
{
    /**
     * The SQL driver class
     */
    private static final String SQL_DRIVER = "org.postgresql.Driver";
    
    /**
     * The SQL dialect protocol
     */
    private static final String SQL_PROTOCOL = "postgresql";
    
    /**
     * The SQL host
     */
    private static final String SQL_HOST = "//localhost";
    
    /**
     * The SQL port
     */
    private static final String SQL_PORT = "5432";
    
    /**
     * The SQL database user's username
     */
    private static final String SQL_USERNAME = System.getProperty("user.name");
    
    /**
     * The SQL database
     */
    private static final String SQL_DATABASE = SQL_USERNAME;
    
    /**
     * The connection string for the database
     */
    private static  final String CONNECTION = "jdbc:" + SQL_PROTOCOL + ':' + SQL_HOST + ':' + SQL_PORT + '/' + SQL_DATABASE;
    
    
    
    /**
     * Non-constructor
     */
    private Database()
    {
        assert false : "You may not create instances of this class [Database].";
    }
    
    
    
    /**
     * Check whether the proper SQL driver is loadable
     * 
     * @return  Whether the proper SQL driver is loadable
     */
    public static boolean hasDriver()
    {
	try
	{   Class.forName(SQL_DRIVER);
	    return true;
	}
	catch (final ClassNotFoundException err)
	{   return false;
	}
    }
    
    
    /**
     * Query the database
     * 
     * @param   query     The query
     * @param   password  The user's password
     * @param   strings   String to add to the query when preparing
     * @return            A matrix of the result, where the first (index 0) row contains the columns' titles, {@code null} is returned on error
     * 
     * @throws  SQLException  On error
     */
    public static String[][] query(final String query, final String password, final String... strings) throws SQLException
    {
	Connection conn = null;
	ResultSet rs = null;
	ResultSetMetaData rsmd = null;
	PreparedStatement pstmt = null;
	int numColumns = 0;
	
	try
	{
	    conn = DriverManager.getConnection(CONNECTION, SQL_USERNAME, password);
	    pstmt = conn.prepareStatement(query);
	    for (int i = 0, n = strings.length; i < n; i++)
		pstmt.setString(i + 1, strings[i]);
	    rs = pstmt.executeQuery();
	    rsmd = rs.getMetaData();
	    numColumns = rsmd.getColumnCount();
	    
	    final ArrayList<String[]> rows = new ArrayList<String[]>();
	    String[] row = new String[numColumns];
	    for (int i = 0; i < numColumns;)
		row[i] = rsmd.getColumnName(++i);
	    rows.add(row);
	    
	    while (rs.next())
	    {   row = new String[numColumns];
		for (int i = 0; i < numColumns;)
		    row[i] = rs.getString(++i);
		rows.add(row);
	    }
	    
	    final String[][] rc = new String[rows.size()][];
	    int i = 0;
	    while ((row = rows.poll()) != null)
		rc[i++] = row;
	    
	    return rc;
	}
	finally
	{   if (conn != null)
		try
		{   conn.close();
		}
		catch (final Throwable ignore)
		{   //Ignore
        }       }
    }
    
    
    /**
     * Update the database
     * 
     * @param   update    The update command
     * @param   password  The user's password 
     * @param   strings   String to add to the update when preparing
     * @return            The number of updated rows, {@code -1} is returned on error
     * 
     * @throws  SQLException  On error
     */
    public static int update(final String update, final String password, final String... strings) throws SQLException
    {
	Connection conn = null;
	ResultSet rs = null;
	ResultSetMetaData rsmd = null;
	PreparedStatement pstmt = null;
	
	try
	{
	    conn = DriverManager.getConnection(CONNECTION, SQL_USERNAME, password);
	    
	    pstmt = conn.prepareStatement(update);
	    for (int i = 0, n = strings.length; i < n; i++)
		pstmt.setString(i + 1, strings[i]);
	    
	    return pstmt.executeUpdate();
	}
	finally
	{   if (conn != null)
		try
		{   conn.close();
		}
		catch (final Throwable ignore)
		{   //Ignore
        }       }
    }
    
}

