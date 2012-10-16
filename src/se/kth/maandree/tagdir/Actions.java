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
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOptions;


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
     * Gets the current working directory's file path
     * 
     * @return  The current working directory's file path
     */
    private static String getDirPath()
    {
	return "";
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
     * Sets the current working directory's ID
     * 
     * @param  id  The current working directory's ID
     */
    private static void setDirID(final int id)
    {
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
     * Creates a file hard link
     * 
     * @param  target  The target file
     * @param  source  The source file
     */
    private static void hardlink(final String target, final String source)
    {
    }
    
    
    
    /**
     * Creates the database
     * 
     * @throws  SQLException  On database error
     */
    public static void install() throws SQLException
    {
	System.out.println("Creating database...");
	Functions.install(fetchPassword());
	System.out.println("Finished.");
    }
    
    
    /**
     * Initialise the directory for use with the program
     * 
     * @throws  SQLException  On database error
     */
    public static void init() throws SQLException
    {
	System.out.println("Initialing catalogue...");
	setDirID(Functions.init(fetchPassword(), getDirPath()));
	System.out.println("Finished.");
    }
    
    
    /**
     * List all available files, and their tags
     * 
     * @throws  SQLException  On database error
     */
    public static void ls() throws SQLException
    {
	final String[][] pairs = Functions.ls(fetchPassword(), getDirPath());
	String last = null;
	for (final String[] pair : pairs)
	{
	    final String file = pair[0];
	    final String tag = pair[1];
	    
	    if ((last == null) || (last.equals(file) == false))
	    {
		if (last != null)
		    System.out.println("]");
		System.out.print("'" + file.replace("'", "'\\''") + "'");
		System.out.print(" [ " + tag.replace("'", "'\\''") + " ");
		last = file;
	    }
	    else
		System.out.print(tag.replace("'", "'\\''") + " ");
	}
	if (last != null)
	    System.out.println("]");
    }
    
    
    /**
     * List all files
     * 
     * @throws  SQLException  On database error
     */
    public static void all() throws SQLException
    {
	final String[] files = Functions.ls(fetchPassword(), getDirPath());
	for (final String file : files)
	    System.out.println("'" + file.replace("'", "'\\''") + "'");
    }
    
    
    /**
     * List all available tags
     * 
     * @throws  SQLException  On database error
     */
    public static void tags() throws SQLException
    {
	final String[] tags = Functions.tags(fetchPassword(), getDirPath());
	for (final String tag : tags)
	    System.out.println("'" + tag.replace("'", "'\\''") + "'");
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
	final String[][] pairs = Functions.tags(fetchPassword(), getDirPath(), files);
	String last = null;
	for (final String[] pair : pairs)
	{
	    final String file = pair[0];
	    final String tag = pair[1];
	    
	    if ((last == null) || (last.equals(file) == false))
	    {
		if (last != null)
		    System.out.println("]");
		System.out.print("'" + file.replace("'", "'\\''") + "'");
		System.out.print(" [ " + tag.replace("'", "'\\''") + " ");
		last = file;
	    }
	    else
		System.out.print(tag.replace("'", "'\\''") + " ");
	}
	if (last != null)
	    System.out.println("]");
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
	final String[] files = Functions.rm(fetchPassword(), getDirPath(), files);
	for (final String file : files)
	{
	    System.out.println("Removing '" + tag.replace("'", "'\\''") + "'");
	    try
	    {
		final boolean c = (new File("./" + file)).exists();
		final boolean a = (new File("./.tagdir/" + file)).exists();
		if (c)  (new File("./" + file)).delete();
		if (a)  (new File("./.tagdir/" + file)).delete();
		if (!c && !a)
		    System.out.println("No such file");
	    }
	    catch (final Throwable err)
	    {
		System.out.println(err.toString());
	    }
	}
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
	Functions.untag(fetchPassword(), getDirPath(), files);
	for (final String file : files)
	    try
	    {
		final boolean c = (new File("./" + file)).exists();
		final boolean a = (new File("./.tagdir/" + file)).exists();
		
		if (c && a)
		    (new File("./.tagdir/" + file)).delete();
		else if (c)
		{
		    // Do nothing
		}
		else if (a)
		    Files.move(Paths.get("./.tagdir/" + file), Paths.get("./" + file), StandardCopyOptions.ATOMIC_MOVE);
		else
		    System.out.println("No such file");
	    }
	    catch (final Throwable err)
	    {
		System.out.println(err.toString());
	    }
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
	final boolean cs = (new File("./" + src)).exists();
	final boolean as = (new File("./.tagdir/" + src)).exists();
	final boolean cd = (new File("./" + dest)).exists();
	final boolean ad = (new File("./.tagdir/" + dest)).exists();
	
	if (cd | ad)
	    System.out.println("Destination file already exists");
	else if (!cs && !as)
	    System.out.println("No such file");
	else
	{
	    Functions.mv(fetchPassword(), getDirPath(), src, dest);
	    if (cs)
		Files.move(Paths.get("./" + src), Paths.get("./" + dest), StandardCopyOptions.ATOMIC_MOVE);
	    if (as)
		Files.move(Paths.get("./.tagdir/" + src), Paths.get("./.tagdir/" + dest), StandardCopyOptions.ATOMIC_MOVE);
	}
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
	final String[] files = Functions.tagFilter(fetchPassword(), getDirPath(), as, ns, ms);
	for (final String file : (new File("./")).list())
	    if ((new File("./" + file)).isDirectory() == false)
		if ((new File("./.tagdir/" + file)).exists())
		    (new File("./" + file)).delete();
	for (final String file : files)
	    if ((new File("./.tagdir/" + file)).exists())
	    {
		System.out.println("File '" + file.replace("'", "'\\''") + "' does not exist is archive");
	        return;
	    }
	for (final String file : files)
	    if ((new File("./" + file)).exists() == false)
	        hardlink("./.tagdir/" + file, "./" + file);
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
	final String[] files = Functions.tagFilter(fetchPassword(), getDirPath(), as, ns, ms);
	for (final String file : files)
	    if ((new File("./.tagdir/" + file)).exists())
	    {
		System.out.println("File '" + file.replace("'", "'\\''") + "' does not exist is archive");
	        return;
	    }
	for (final String file : files)
	    if ((new File("./" + file)).exists() == false)
	        hardlink("./.tagdir/" + file, "./" + file);
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
	final String[] files = Functions.tagFilter(fetchPassword(), getDirPath(), as, ns, ms);
	for (final String file : files)
	    if ((new File("./.tagdir/" + file)).exists())
	    {
		System.out.println("File '" + file.replace("'", "'\\''") + "' does not exist is archive");
	        return;
	    }
	for (final String file : files)
	    if ((new File("./" + file)).exists())
		(new File("./" + file)).delete();
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
	int index = -1;
	for (int i = 0, n = params.length(); i < n; i++)
	    if ((params[i] == '=') || (params[i] == '+') || (params[i] == '-'))
	    {
		index = i;
		break;
	    }
	if ((index < 1) || (index == params.length - 1))
	{
	    System.out.println("Syntax error");
	    return;
	}
        final String[] files = new String[index];
        final String[] tags = new String[params.length - 1 - index];
	System.arraycopy(params, 0, files, 0, files.length);
	System.arraycopy(params, index + 1, tags, 0, tags.length);
	boolean stop = false;
	for (final String file : files)
	{
	    final boolean c = (new File("./" + file)).exists();
	    final boolean a = (new File("./.tagdir/" + file)).exists();
	    if (!c && !a)
	    {
		System.out.println("File '" + file.replace("'", "'\\''") + "' does not exist");
		break;
	    }
	}
	if (stop)
	{
	    System.out.println("Aborting");
	    return;
	}
	for (final String file : files)
	{
	    final boolean a = (new File("./.tagdir/" + file)).exists();
	    if (!a)
		hardlink("./" + file, "./.tagdir/" + file);
	}
	if (params[i] == '=')
	    Functions.tagSet(fetchPassword(), getDirPath(), files, tags);
	else if (params[i] == '+')
	    Functions.tagAdd(fetchPassword(), getDirPath(), files, tags);
	else if (params[i] == '-')
	    Functions.tagRemove(fetchPassword(), getDirPath(), files, tags);
    }
    
    
    /**
     * Export all data for the directory
     * 
     * @throws  SQLException  On database error
     */
    public static void export() throws SQLException
    {
	String[][] data = Functions.export(fetchPassword(), getDirPath());
	for (final String[] tuple : data)
	    System.out.println("( file: '"  + tuple[0].replace("'", "'\\''")
			       + "' dir: '" + tuple[1].replace("'", "'\\''")
			       + "' tag: '" + tuple[2].replace("'", "'\\''")
			       + "' )");
    }
    
    
    /**
     * Import exported directory data
     * 
     * @throws  SQLException  On database error
     */
    public static void importSave() throws SQLException
    {
    }
    
}

