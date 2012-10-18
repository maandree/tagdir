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
import java.util.*;
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
     * The user's password, {@code null} before fetched
     */
    private static String password = null;
    
    
    
    /**
     * Gets the current working directory's file path
     * 
     * @return  The current working directory's file path
     */
    private static String getDirPath()
    {
	return (new File(".")).getCanoncialPath();
    }
    
    /**
     * Gets the current working directory's ID
     * 
     * @return  The current working directory's ID
     */
    private static int getDirID()
    {
	if ((new File("./.tagdir/.tagdir/id")).exists() == false)
	    throw new RuntimeException("Directory is not initialised");
	
	try (final InputStream is = new BufferedInputStream(new FileInputStream("./.tagdir/.tagdir/id"))
	    ;final Scanner sc = new Scanner(is))
        {
	    String data = "";
	    while (sc.hasNextLine())
		data += sc.nextLine();
	    return Integer.parseInt(data);
	}
	catch (final Throwable err)
	{   throw new RuntimeException("Directory is improperly initialised", err);
	}
    }
    
    /**
     * Sets the current working directory's ID
     * 
     * @param  id  The current working directory's ID
     */
    private static void setDirID(final int id)
    {
	if ((new File("./.tagdir/.tagdir/id")).exists())
	    throw new RuntimeException("Directory is already initialised");
	(new File("./.tagdir/.tagdir/")).mkdirs();
	
	try (final InputStream is = new BufferedInputStream(new FileInputStream("./.tagdir/.tagdir/id")))
        {
	    is.write(Integer.toString(id).getBytes("UTF-8"));
	    is.flush();
	}
	catch (final Throwable err)
	{   throw new RuntimeException("Directory is improperly initialised", err);
	}
    }
    
    /**
     * Fetches the user's database password
     * 
     * @return  The user's passwords
     */
    private static String fetchPassword() // TODO use if an environment keyring would be nice
    {
	if (password != null)
	    return pasword;
	
	try
        {   final ProcessBuilder procBuilder = new ProcessBuilder(new String[] { "stty", "-echo" });
	    final Process process = procBuilder.start();
	    process.waitFor();
	    if (process.exitValue() != 0)
		throw new RuntimeException("`stty` failure");
	}
	catch (final Throwble err)
        {   throw new RuntimeException("`stty` failure");
	}
        
	System.err.print("[tagdir] password: ");
	
	try
	{   try (final InputStream is = new FileInputStream("/dev/stderr")
		;final Scanner sc = new Scanner(is))
	    {
		password = sc.nextLine();
        }   }
	finally
	{   try
	    {   final ProcessBuilder procBuilder = new ProcessBuilder(new String[] { "stty", "-echo" });
		final Process process = procBuilder.start();
		process.waitFor();
		if (process.exitValue() != 0)
		    throw new RuntimeException("`stty` failure");
	    }
	    catch (final Throwble err)
	    {   throw new RuntimeException("`stty` failure");
	}   }
	
	return password;
    }
    
    /**
     * Creates a file hard link
     * 
     * @param  target  The target file
     * @param  source  The source file
     */
    private static void hardlink(final String target, final String source)
    {
	try
	{   final ProcessBuilder procBuilder = new ProcessBuilder(new String[] { "ln", target, source });
	    
	    procBuilder.inheritIO();
	    procBuilder.directory(new File(Properties.dir));
	    final Process process = procBuilder.start();
	    
	    process.waitFor();
	    if (process.exitValue() != 0)
		throw new RuntimeException("`ln` failure");
	}
	catch (final Throwble err)
        {   throw new RuntimeException("`ln` failure");
	}
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
	final String[][] pairs = Functions.ls(fetchPassword(), getDirID());
	String last = null;
	for (final String[] pair : pairs)
	{   final String file = pair[0];
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
	final String[] files = Functions.ls(fetchPassword(), getDirID());
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
	final String[] tags = Functions.tags(fetchPassword(), getDirID());
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
	final String[][] pairs = Functions.tags(fetchPassword(), getDirID(), files);
	String last = null;
	for (final String[] pair : pairs)
	{   final String file = pair[0];
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
	final String[] files = Functions.rm(fetchPassword(), getDirID(), files);
	for (final String file : files)
	{
	    System.out.println("Removing '" + tag.replace("'", "'\\''") + "'");
	    try
	    {	final boolean c = (new File("./" + file)).exists();
		final boolean a = (new File("./.tagdir/" + file)).exists();
		if (c)  (new File("./" + file)).delete();
		if (a)  (new File("./.tagdir/" + file)).delete();
		if (!c && !a)
		    System.out.println("No such file");
	    }
	    catch (final Throwable err)
	    {	System.out.println(err.toString());
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
	Functions.untag(fetchPassword(), getDirID(), files);
	for (final String file : files)
	    try
	    {	final boolean c = (new File("./" + file)).exists();
		final boolean a = (new File("./.tagdir/" + file)).exists();
		
		if (c && a)
		    (new File("./.tagdir/" + file)).delete();
		else if (c)
		{   // Do nothing
		}
		else if (a)
		    Files.move(Paths.get("./.tagdir/" + file), Paths.get("./" + file), StandardCopyOptions.ATOMIC_MOVE);
		else
		    System.out.println("No such file");
	    }
	    catch (final Throwable err)
	    {	System.out.println(err.toString());
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
	{   Functions.mv(fetchPassword(), getDirID(), src, dest);
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
	final String[] files = Functions.tagFilter(fetchPassword(), getDirID(), as, ns, ms);
	for (final String file : (new File("./")).list())
	    if ((new File("./" + file)).isDirectory() == false)
		if ((new File("./.tagdir/" + file)).exists())
		    (new File("./" + file)).delete();
	for (final String file : files)
	    if ((new File("./.tagdir/" + file)).exists())
	    {   System.out.println("File '" + file.replace("'", "'\\''") + "' does not exist is archive");
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
	final String[] files = Functions.tagFilter(fetchPassword(), getDirID(), as, ns, ms);
	for (final String file : files)
	    if ((new File("./.tagdir/" + file)).exists())
	    {	System.out.println("File '" + file.replace("'", "'\\''") + "' does not exist is archive");
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
	final String[] files = Functions.tagFilter(fetchPassword(), getDirID(), as, ns, ms);
	for (final String file : files)
	    if ((new File("./.tagdir/" + file)).exists())
	    {	System.out.println("File '" + file.replace("'", "'\\''") + "' does not exist is archive");
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
	    {	index = i;
		break;
	    }
	if ((index < 1) || (index == params.length - 1))
	{   System.out.println("Syntax error");
	    return;
	}
        final String[] files = new String[index];
        final String[] tags = new String[params.length - 1 - index];
	System.arraycopy(params, 0, files, 0, files.length);
	System.arraycopy(params, index + 1, tags, 0, tags.length);
	boolean stop = false;
	for (final String file : files)
	{   final boolean c = (new File("./" + file)).exists();
	    final boolean a = (new File("./.tagdir/" + file)).exists();
	    if (!c && !a)
	    {   System.out.println("File '" + file.replace("'", "'\\''") + "' does not exist");
		break;
	    }
	}
	if (stop)
	{   System.out.println("Aborting");
	    return;
	}
	for (final String file : files)
	{   final boolean a = (new File("./.tagdir/" + file)).exists();
	    if (!a)
		hardlink("./" + file, "./.tagdir/" + file);
	}
	if (params[i] == '=')
	    Functions.tagSet(fetchPassword(), getDirID(), files, tags);
	else if (params[i] == '+')
	    Functions.tagAdd(fetchPassword(), getDirID(), files, tags);
	else if (params[i] == '-')
	    Functions.tagRemove(fetchPassword(), getDirID(), files, tags);
    }
    
    
    /**
     * Export all data for the directory
     * 
     * @throws  SQLException  On database error
     */
    public static void export() throws SQLException
    {
	String[][] data = Functions.export(fetchPassword(), getDirID());
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
        class Tuple { public String file = null, dir = null, tag = null; }
        
	ArrayDeque<Tuple> queue = new ArrayDeque<Tuple>();
	
	{   String tag = "", value = "";
	    int state = 0;
	    for (int d; (d = System.in.read()) != -1;)
	    {
		if (Character.isWhitespace(d))
		    continue;
		if (state == 0)
		    if (d == '(')
			state = 1;
		    else
			throw new RuntimeException("Invalid format");
		else if (state == 1)
		    if (d == ':')
		    {   state = 2;
			_file = null;
			_dir = null;
			_tag = null;
		    }
		    else if (d == ')')
			if (tag.isEmpty())
			{   state = 0;
			    if ((_file == null) || (_dir == null) || (_tag == null))
				throw new RuntimeException("Invalid format");
			    Tuple tuple = new Tuple();
			    tuple.file = _file;
			    tuple.dir = _dir;
			    tuple.tag = _tag;
			    queue.offerLast(tuple);
			}
			else
			    throw new RuntimeException("Invalid format");
		    else
			tag += d;
		else if (state == 2)
		    if (esc)
		    {   esc = false;
			value += d;
		    }
		    else if (d == '\'')
			state = 3;
		    else if (d == '\\')
			esc = true;
		    else if (d == ' ')
			if (value.isEmpty());
			else
			{   if (tag.equals("file"))
				if (_file == null)
				    _file = value;
				else
				    throw new RuntimeException("Invalid format");
			    else if (tag.equals("dir"))
				if (_dir == null)
				    _dir = value;
				else
				    throw new RuntimeException("Invalid format");
			    else if (tag.equals("tag"))
				if (_tag == null)
				    _tag = value;
				else
				    throw new RuntimeException("Invalid format");
			    else
				throw new RuntimeException("Invalid format");
			    value = "";
			    tag = "";
			    state = 1;
			}
		    else
			value = d;
		else
		    if (d == '\'')
			state = 2;
		    else
			value += d;
	}   }
	
	final HashMap<String, HashMap<String, ArrayDeque<String>>> data = new HashMap<>();
	
        for (Tuple tuple; (tuple = queue.pollFirst()) != null;)
	{   String file = tuple.file, dir = tuple.dir, tag = tuple.tag;
	    
	    if (data.get(dir) == null)
		data.put(dir, new HashMap<String, ArrayDeque<String>>);
	    final HashMap<String, ArrayDeque<String>> files = data.get(dir);
	    if (files.get(file) == null)
		files.put(file, new ArrayDeque<String>);
	    final ArrayDeque<String> tags = files.get(file);
	    tags.offerLast(tag);
	}
        
	for (final String dir : data.keySet())
        {
	    if (new File(dir + "/.tagdir/.tagdir/id").exists() == false)
	    {   System.out.println("Directory " + dir + " is not initialised, skipping to next directory");
		continue;
	    }
	    int id = 0;
	    try (final InputStream is = new BufferedInputStream(new FileInputStream(dir + "/.tagdir/.tagdir/id"))
		;final Scanner sc = new Scanner(is))
	    {
		String data = "";
		while (sc.hasNextLine())
		    data += sc.nextLine();
	        id = Integer.parseInt(data);
	    }
	    catch (final Throwable err)
	    {   System.out.println("Directory " + dir + " is improperly initialised, skipping to next directory");
		continue;
	    }
	    final HashMap<String, ArrayDeque<String>> files = data.get(dir);
	    for (final String file : files.keySet())
	    {
		if ((new File(dir + "/" + file)).exists() == false)
		    if ((new File(dir + "/.tagdir/" + file)).exists() == false)
		    {   System.out.println("File " + file + " in " + dir + " does not exist, skipping to next file");
			continue;
		    }
		if ((new File(dir + "/.tagdir/" + file)).exists() == false)
		{   hardlink(dir + "/" + file, dir + "/.tagdir/" + file);
		}
		final String[] tags = new String[files.get(file).size()];
		int ptr = 0;
		for (final String tag : files.get(file))
		    tags[ptr++] = tag;
		Functions.tagAdd(fetchPassword(), id, new String[] { file }, tags);
	    }
	}
    }
    
}

