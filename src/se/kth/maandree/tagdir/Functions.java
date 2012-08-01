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


/**
 * Database functions
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class Functions
{
    /**
     * Non-constructor
     */
    private Functions()
    {
        assert false : "You may not create instances of this class [Functions].";
    }
    
    
    
    /**
     * Make a list out of the first column in a matrix
     * 
     * @param   matrix  The matrix
     * @return          The first column in the matrix
     */
    private static String[] toList(final String[][] matrix)
    {
	int n;
	final String[] rc = new String[n = matrix.length];
	for (int i = 0; i < n; i++)
	    rc[i] = matrix[i][0];
	return rc;
    }
    
    
    /**
     * Initialise the directory for use with the program
     * 
     * @param   password  The user's password for the database
     * @param   cwd       The currect working directory
     * @return            The ID assigned to the currect working directory
     * 
     * @throws  SQLException  On database error
     */
    public static int init(final String password, final String cwd) throws SQLException
    {
	final String COMMAND = " INSERT INTO  tagdir__dir (name)"
	                     + " VALUES       (%cwd)"
	                     + " RETURNING    dir;";
	
	return Integer.parseInt(Database.query(COMMAND.replace("%cwd", System.getProperty("user.dir")), password, pwd)[0][0]);
    }
    
    
    /**
     * List all available files, and their tags
     * 
     * @param   password  The user's password for the database
     * @param   dir       The currect working directory's ID in the database
     * @return            List of file–tag pairs (one file, one tag, per pair), sorted by (file, tag)
     * 
     * @throws  SQLException  On database error
     */
    public static String[][] ls(final String password, final int dir) throws SQLException
    {
	final String COMMAND = " SELECT    file.name, tag.name"
	                     + " FROM            tagdir__table AS table"
	                     + "           JOIN  tagdir__file  AS file   USING  file"
	                     + "           JOIN  tagdir__tag   AS tag    USING  tag"
	                     + " WHERE     table.dir = %dir"
	                     + " ORDER BY  tag.name, file.name;";
	
	return Database.query(COMMAND.replace("%dir", Integer.toString(dir)), password);
    }
    
    
    /**
     * List all files
     * 
     * @param   password  The user's password for the database
     * @param   dir       The currect working directory's ID in the database
     * @return            Array of all files
     * 
     * @throws  SQLException  On database error
     */
    public static String[] all(final String password, final int dir) throws SQLException
    {
	final String COMMAND = " SELECT  name"
	                     + " FROM    tagdir__file"
	                     + " WHERE   dir = %dir;";
	
	return toList(Database.query(COMMAND.replace("%dir", Integer.toString(dir)), password));
    }
    
    
    /**
     * List all available tags
     * 
     * @param   password  The user's password for the database
     * @param   dir       The currect working directory's ID in the database
     * @return            Array of all tags
     * 
     * @throws  SQLException  On database error
     */
    public static String[] tags(final String password, final int dir) throws SQLException
    {
	final String COMMAND = " SELECT    name"
	                     + " FROM      tagdir__tag"
	                     + "           NATURAL JOIN ( SELECT  DISTINCT tag"
	                     + "                          FROM    tagdir__table"
	                     + "                          WHERE   dir = %dir"
	                     + "                        ) AS dummy"
	                     + " ORDER BY  name;";
	
	return toList(Database.query(COMMAND.replace("%dir", Integer.toString(dir)), password));
    }
    
    
    /**
     * List tags for file
     * 
     * @param   password  The user's password for the database
     * @param   dir       The currect working directory's ID in the database
     * @param   files     The files of interest
     * @return            List of file–tag pairs (one file, one tag, per pair), sorted by (file, tag)
     * 
     * @throws  SQLException  On database error
     */
    public static String[][] tags(final String password, final int dir, final String[] files) throws SQLException
    {
	final String COMMAND = " SELECT    file.name, tag.name"
	                     + " FROM            tagdir__table AS table"
	                     + "           JOIN  tagdir__file  AS file   USING  file"
	                     + "           JOIN  tagdir__tag   AS tag    USING  tag"
	                     + " WHERE     table.dir = %dir"
	                     + "      AND  table.file IN (%files)"
	                     + " ORDER BY  tag.name, file.name;";
	
	final char[] qs = new char[files.length * 3];
	for (int i = 0, n = files.length; i < n; i++)
	{
	    qs[i * 3]     = '?';
	    qs[i * 3 + 1] = ',';
	    qs[i * 3 + 2] = ' ';
	}
	
	return Database.query(COMMAND.replace("%dir", Integer.toString(dir)).replace("%files", new String(qs, 0, qs.length - 2)), password, files);
    }
    
    
    /**
     * Remove file from the system
     * 
     * @param   password  The user's password for the database
     * @param   dir       The currect working directory's ID in the database
     * @param   files     The files of interest
     * @return            Array of the newly removed files
     * 
     * @throws  SQLException  On database error
     */
    public static String[] rm(final String password, final int dir, final String[] files) throws SQLException
    {
	final String COMMAND = " WITH  del AS ( DELETE FROM  tagdir__file"
	                     + "                WHERE        dir = %dir"
	                     + "                        AND  name IN (%files)"
	                     + "                RETURNING    file, name"
	                     + "              )"
	                     + " DELETE FROM  tagdir__table"
	                     + " WHERE        dir = %dir"
	                     + "         AND  file IN (SELECT file FROM del)"
	                     + " RETURNING    del.name;";
	
	final char[] qs = new char[files.length * 3];
	for (int i = 0, n = files.length; i < n; i++)
	{
	    qs[i * 3]     = '?';
	    qs[i * 3 + 1] = ',';
	    qs[i * 3 + 2] = ' ';
	}
	
	return toList(Database.query(COMMAND.replace("%dir", Integer.toString(dir)).replace("%files", new String(qs, 0, qs.length - 2)), password, files));
    }
    
    
    /**
     * Remove all tags from file
     * 
     * @param  password  The user's password for the database
     * @param  dir       The currect working directory's ID in the database
     * @param  files     The files of interest
     * 
     * @throws  SQLException  On database error
     */
    public static void untag(final String password, final int dir, final String[] files) throws SQLException
    {
	final String COMMAND = " DELETE FROM  tagdir__table"
	                     + " WHERE        dir = %dir"
	                     + "         AND  file IN"
	                     + "              ( DELETE FROM  tagdir__file"
	                     + "                WHERE        dir = %dir"
	                     + "                        AND  name IN (%files)"
	                     + "                RETURNING    file"
	                     + "              );";
	
	final char[] qs = new char[files.length * 3];
	for (int i = 0, n = files.length; i < n; i++)
	{
	    qs[i * 3]     = '?';
	    qs[i * 3 + 1] = ',';
	    qs[i * 3 + 2] = ' ';
	}
	
	Database.update(COMMAND.replace("%dir", Integer.toString(dir)).replace("%files", new String(qs, 0, qs.length - 2)), password, files);
    }
    
    
    /**
     * Move a file int the system
     * 
     * @param  password  The user's password for the database
     * @param  dir       The currect working directory's ID in the database
     * @param  src       The file to move
     * @param  dest      The new file name
     * 
     * @throws  SQLException  On database error
     */
    public static void mv(final String password, final int dir, final String src, final String dest) throws SQLException
    {
	final String COMMAND = " UPDATE  tagdir__file"
	                     + " SET     name = %dest"
	                     + " WHERE   name = %src"
	                     + "    AND  dir = %dir;";
	
	final char[] qs = new char[files.length * 3];
	for (int i = 0, n = files.length; i < n; i++)
	{
	    qs[i * 3]     = '?';
	    qs[i * 3 + 1] = ',';
	    qs[i * 3 + 2] = ' ';
	}
	
	Database.update(COMMAND.replace("%dir", Integer.toString(dir)).replace("%dest", "?").replace("%src", "?"), password, dest, src);
    }
    
    
    /**
     * List files depending on tags
     * 
     * @param   password  The user's password for the database
     * @param   dir       The currect working directory's ID in the database
     * @param   as        Set of tags from which one must be used
     * @param   ns        Set of tags from whihc none may be used
     * @param   ms        Set of tags from which all must be used
     * @return            Array of the found files
     * 
     * @throws  SQLException  On database error
     */
    public static String[] tagFilter(final String password, final int dir, final String[] as, final String[] ns, final String[] ms)
    {
	final String COMMAND = " WITH  a AS ( SELECT  tag"
	                     + "              FROM    tagdir__tag"
	                     + "              WHERE   name IN (%as)"
	                     + "            ),"
	                     + "       n AS ( SELECT  tag"
	                     + "              FROM    tagdir__tag"
	                     + "              WHERE   name IN (%ns)"
	                     + "            ),"
	                     + "       m AS ( SELECT  tag"
	                     + "              FROM    tagdir__tag"
	                     + "              WHERE   name IN (%ms)"
	                     + "            )"
	                     + " SELECT  name"
	                     + " FROM    tagdir__file AS f"
	                     + " WHERE   dir = %dir"
	                     + "    AND  ( ( SELECT  count(*)"
	                     + "             FROM    tagdir__table AS t"
	                     + "             WHERE   t.file = f.file"
	                     + "                AND  t.dir = %dir"
	                     + "                AND  t.tag IN a"
	                     + "           ) > 1"
	                     + "         )"
	                     + "    AND  ( ( SELECT  count(*)"
	                     + "             FROM    tagdir__table AS t"
	                     + "             WHERE   t.file = f.file"
	                     + "                AND  t.dir = %dir"
	                     + "                AND  t.tag IN n"
	                     + "           ) = 0"
	                     + "         )"
	                     + "    AND  ( ( SELECT  count(*)"
	                     + "             FROM    tagdir__table AS t"
	                     + "             WHERE   t.file = f.file"
	                     + "                AND  t.dir = %dir"
	                     + "                AND  t.tag IN m"
	                     + "           ) = %len(ms)"
	                     + "         );";
	
	final String[] params = new String[as.length + ns.length + ms.length];
	int ptr = 0;
	
	final char[] qa = new char[as.length * 3];
	for (int i = 0, n = as.length; i < n; i++)
	{
	    qa[i * 3]     = '?';
	    qa[i * 3 + 1] = ',';
	    qa[i * 3 + 2] = ' ';
	    params[ptr++] = as[i];
	}
	final char[] qn = new char[ns.length * 3];
	for (int i = 0, n = ns.length; i < n; i++)
	{
	    qn[i * 3]     = '?';
	    qn[i * 3 + 1] = ',';
	    qn[i * 3 + 2] = ' ';
	    params[ptr++] = ns[i];
	}
	final char[] qm = new char[ms.length * 3];
	for (int i = 0, n = ms.length; i < n; i++)
	{
	    qm[i * 3]     = '?';
	    qm[i * 3 + 1] = ',';
	    qm[i * 3 + 2] = ' ';
	    params[ptr++] = ms[i];
	}
	
	final String qas = new String(qa, 0, qa.length);
	final String qns = new String(qa, 0, qn.length);
	final String qms = new String(qa, 0, qm.length);
	
	Database.update(COMMAND.replace("%dir", Integer.toString(dir))
			       .replace("%len(ms)", Integer.toString(ms.length))
			       .replace("%as", qas)
			       .replace("%ns", qns)
			       .replace("%ms", qms),
			password, params);
    }
    
    
    /**
     * Export all data for the directory
     * 
     * @param   password  The user's password for the database
     * @param   dir       The currect working directory's ID in the database
     * @return            
     * 
     * @throws  SQLException  On database error
     */
    public static String[][] export(final String password, final int dir) throws SQLException
    {
	final String COMMAND = " SELECT  file.name, dir.name, tag.name"
	                     + " FROM          tagdir__table"
	                     + "         JOIN  tagdir__file  AS file  USING  files"
	                     + "         JOIN  tagdir__dir   AS dir   USING  dir"
	                     + "         JOIN  tagdir__tag   AS tag   USING  tag"
	                     + " WHERE   dir = %dir;";
	
	return Database.query(COMMAND.replace("%dir", Integer.toString(dir)), password);
    }
    
    
    /**
     * Set tags for file
     * 
     * @param  password  The user's password for the database
     * @param  dir       The currect working directory's ID in the database
     * @param  files     The files of interest
     * @param  tag       The tags
     * 
     * @throws  SQLException  On database error
     */
    public static void tagSet(final String password, final int dir, final String[] files, final String[] tags) throws SQLException
    {
	final String COMMAND_0 = "(%tags) EXCEPT (SELECT name FROM tagdir__tag);";
	
	final String COMMAND_1 = "INSERT INTO  tagdir__tag (name)  %VALUES;";
	
	final String COMMAND_2 = " SELECT  tag"
                               + " FROM    tagdir__tag"
                               + " WHERE   name IN (%tags)";
	
	final String COMMAND_3 = " DELETE FROM  tagdir__table"
                               + " WHERE        dir = %dir"
                               + "         AND  file IN"
                               + "              ( SELECT FROM  tagdir__file"
                               + "                WHERE        dir = %dir "
                               + "                        AND  name IN (%files)"
                               + "              );";
	
	final String COMMAND_4 = "INSERT INTO  tagdir__table (file, dir, tag)  %VALUES;";
	
	final char[] qt = new char[tags.length * 3];
	for (int i = 0, n = tags.length; i < n; i++)
	{
	    qt[i * 3]     = '?';
	    qt[i * 3 + 1] = ',';
	    qt[i * 3 + 2] = ' ';
	}
	final char[] qf = new char[files.length * 3];
	for (int i = 0, n = files.length; i < n; i++)
	{
	    qf[i * 3]     = '?';
	    qf[i * 3 + 1] = ',';
	    qf[i * 3 + 2] = ' ';
	}
	
	final String qts = new String(qt, 0, qt.length);
	final String qfs = new String(qf, 0, qf.length);
	
	final String[] missing = toList(Database.query(COMMAND_0.replace("%tags", qts), password, tags));
	
        final StringBuilder qm = new StringBuilder();
	for (int i = 0, n = missing.length; i < n; i++)
	    qm.append(i == 0 ? "VALUES (?)" : ", (?)");
	
        Database.update(COMMAND_1.replace("%VALUES", qm.toString()), password, missing);
	
	final String[] itags = toList(Database.query(COMMAND_2.replace("%tags", qts), password, tags));
        
	Database.update(COMMAND_3.replace("%dir", Integer.toString(dir)).replace("%files", qfs), password, files);
        
	final StringBuilder values = new StringBuilder();
	for (int i = 0, n = itags.length; i < n; i++)
	    values.append((i == 0 ? "VALUES (?, %dir, " : ", (?, %dir, ") + itags[i] + ")");
	
	Database.update(COMMAND_4.replace("%VALUES", values.toString()).replace("%dir", Integer.toString(dir)), password, files);
    }
    
    
    /**
     * Add tag to file
     * 
     * @param  password  The user's password for the database
     * @param  dir       The currect working directory's ID in the database
     * @param  files     The files of interest
     * @param  tag       The tags
     * 
     * @throws  SQLException  On database error
     */
    public static void tagAdd(final String password, final int dir, final String[] files, final String[] tags) throws SQLException
    {
	final String COMMAND_0 = "(%tags) EXCEPT (SELECT name FROM tagdir__tag);";
	
	final String COMMAND_1 = "INSERT INTO  tagdir__tag (name)  %VALUES;";
	
	final String COMMAND_2 = " SELECT  tag"
                               + " FROM    tagdir__tag"
                               + " WHERE   name IN (%tags)";
	
	final String COMMAND_3 = "INSERT INTO  tagdir__table (file, dir, tag)  %VALUES;";
	
	final char[] qt = new char[tags.length * 3];
	for (int i = 0, n = tags.length; i < n; i++)
	{
	    qt[i * 3]     = '?';
	    qt[i * 3 + 1] = ',';
	    qt[i * 3 + 2] = ' ';
	}
	final char[] qf = new char[files.length * 3];
	for (int i = 0, n = files.length; i < n; i++)
	{
	    qf[i * 3]     = '?';
	    qf[i * 3 + 1] = ',';
	    qf[i * 3 + 2] = ' ';
	}
	
	final String qts = new String(qt, 0, qt.length);
	final String qfs = new String(qf, 0, qf.length);
	
	final String[] missing = toList(Database.query(COMMAND_0.replace("%tags", qts), password, tags));
	
        final StringBuilder qm = new StringBuilder();
	for (int i = 0, n = missing.length; i < n; i++)
	    qm.append(i == 0 ? "VALUES (?)" : ", (?)");
	
        Database.update(COMMAND_1.replace("%VALUES", qm.toString()), password, missing);
	
	final String[] itags = toList(Database.query(COMMAND_2.replace("%tags", qts), password, tags));
        
	final StringBuilder values = new StringBuilder();
	for (int i = 0, n = itags.length; i < n; i++)
	    values.append((i == 0 ? "VALUES (?, %dir, " : ", (?, %dir, ") + itags[i] + ")");
	
	Database.update(COMMAND_3.replace("%VALUES", values.toString()).replace("%dir", Integer.toString(dir)), password, files);
    }
    
    
    /**
     * Remove tag from file
     * 
     * @param  password  The user's password for the database
     * @param  dir       The currect working directory's ID in the database
     * @param  files     The files of interest
     * @param  tag       The tags
     * 
     * @throws  SQLException  On database error
     */
    public static void tagRemove(final String password, final int dir, final String[] files, final String[] tags) throws SQLException
    {
	final String COMMAND = " WITH  tags AS ( SELECT  tag"
	                     + "                 FROM    tagdir__tag"
	                     + "                 WHERE   name IN (%tags)"
	                     + "               )"
	                     + " DELETE FROM  tagdir__table"
	                     + " WHERE        dir = %dir"
	                     + "         AND  file IN"
	                     + "              ( SELECT FROM  tagdir__file"
	                     + "                WHERE        dir = %dir"
	                     + "                        AND  name IN (%files)"
	                     + "              )"
	                     + "         AND  tag IN tags;";
	
	final String[] params = new String[tags.length + files.length];
	int ptr = 0;
	
	final char[] qt = new char[tags.length * 3];
	for (int i = 0, n = tags.length; i < n; i++)
	{
	    qt[i * 3]     = '?';
	    qt[i * 3 + 1] = ',';
	    qt[i * 3 + 2] = ' ';
	    params[ptr++] = tags[i];
	}
	final char[] qf = new char[files.length * 3];
	for (int i = 0, n = files.length; i < n; i++)
	{
	    qf[i * 3]     = '?';
	    qf[i * 3 + 1] = ',';
	    qf[i * 3 + 2] = ' ';
	    params[ptr++] = files[i];
	}
	
	final String qts = new String(qt, 0, qt.length);
	final String qfs = new String(qf, 0, qf.length);
	
        Database.update(COMMAND.replace("%dir", Integer.toString(dir)).replace("%tags", qts).replace("%files", qfs), password, params);
    }
    
}

