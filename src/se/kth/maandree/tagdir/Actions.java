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


/**
 * Real actions
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class Actions
{
    /**
     * Non-constructor
     */
    private Actions()
    {
        assert false : "You may not create instances of this class [Actions].";
    }
    
    
    
    /**
     * Gets the current working directory's ID
     * 
     * @return  The current working directory's ID
     */
    private static int getDirID()
    {
	return 0;
    }
    
    /**
     * Fetches the user's database password
     * 
     * @return  The user's passwords
     */
    private static String fetchPassword()
    {
	return "";
    }
    
    
    
    /**
     * Creates the database
     * 
     * @throws  SQLException  On database error
     */
    public static void install() throws SQLException
    {
    }
    
    
    /**
     * Initialise the directory for use with the program
     * 
     * @throws  SQLException  On database error
     */
    public static void init() throws SQLException
    {
    }
    
    
    /**
     * List all available files, and their tags
     * 
     * @throws  SQLException  On database error
     */
    public static void ls() throws SQLException
    {
    }
    
    
    /**
     * List all files
     * 
     * @throws  SQLException  On database error
     */
    public static void all() throws SQLException
    {
    }
    
    
    /**
     * List all available tags
     * 
     * @throws  SQLException  On database error
     */
    public static void tags() throws SQLException
    {
    }
    
    
    /**
     * List tags for file
     * 
     * @param  files  The files of interest
     * 
     * @throws  SQLException  On database error
     */
    public static void tags(final String[] files) throws SQLException
    {
    }
    
    
    /**
     * Remove file from the system
     * 
     * @param  files  The files of interest
     * 
     * @throws  SQLException  On database error
     */
    public static void rm(final String[] files) throws SQLException
    {
    }
    
    
    /**
     * Remove all tags from file
     * 
     * @param  files  The files of interest
     * 
     * @throws  SQLException  On database error
     */
    public static void untag(final String[] files) throws SQLException
    {
    }
    
    
    /**
     * Move a file int the system
     * 
     * @param  src   The file to move
     * @param  dest  The new file name
     * 
     * @throws  SQLException  On database error
     */
    public static void mv(final String src, final String dest) throws SQLException
    {
    }
    
    
    /**
     * Show (only) files depending on tags
     * 
     * @param  as  Set of tags from which one must be used
     * @param  ns  Set of tags from whihc none may be used
     * @param  ms  Set of tags from which all must be used
     * 
     * @throws  SQLException  On database error
     */
    public static void show(final String[] as, final String[] ns, final String[] ms) throws SQLException
    {
    }
    
    
    /**
     * Show (also) files depending on tags
     * 
     * @param  as  Set of tags from which one must be used
     * @param  ns  Set of tags from whihc none may be used
     * @param  ms  Set of tags from which all must be used
     * 
     * @throws  SQLException  On database error
     */
    public static void also(final String[] as, final String[] ns, final String[] ms) throws SQLException
    {
    }
    
    
    /**
     * Hide (also) files depending on tags
     * 
     * @param  as  Set of tags from which one must be used
     * @param  ns  Set of tags from whihc none may be used
     * @param  ms  Set of tags from which all must be used
     * 
     * @throws  SQLException  On database error
     */
    public static void hide(final String[] as, final String[] ns, final String[] ms) throws SQLException
    {
    }
    
    
    /**
     * Export all data for the directory
     * 
     * @throws  SQLException  On database error
     */
    public static void export() throws SQLException
    {
    }
    
    
    /**
     * Import exported directory data
     * 
     * @throws  SQLException  On database error
     */
    public static void importSave() throws SQLException
    {
    }
    
    
    /**
     * Set, add or remove tags for file
     * 
     * @param  params  The files of interest, =/+/-, the tags
     * 
     * @throws  SQLException  On database error
     */
    public static void tag(final String[] params) throws SQLException
    {
    }
    
    
    /**
     * Set tags for file
     * 
     * @param  files  The files of interest
     * @param  tag    The tags
     * 
     * @throws  SQLException  On database error
     */
    private static void tagSet(final String[] files, final String[] tags) throws SQLException
    {
    }
    
    
    /**
     * Add tag to file
     * 
     * @param  files  The files of interest
     * @param  tag    The tags
     * 
     * @throws  SQLException  On database error
     */
    private static void tagAdd(final String[] files, final String[] tags) throws SQLException
    {
    }
    
    
    /**
     * Remove tag from file
     * 
     * @param  files  The files of interest
     * @param  tag    The tags
     * 
     * @throws  SQLException  On database error
     */
    private static void tagRemove(final String[] files, final String[] tags) throws SQLException
    {
    }
    
}

