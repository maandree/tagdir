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
 * This is the main class of the program
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class Program
{
    /**
     * Non-constructor
     */
    private Program()
    {
        assert false : "You may not create instances of this class [Program].";
    }
    
    
    
    /**
     * This is the main entry point of the program
     * 
     * @param  args  Startup arguments, unused
     */
    public static void main(final String... args)
    {
	try
	{
	    final String cmd = args.length == 0 ? "" : args[0];
	    final String[] params = new String[args.length - 1];
	    System.arraycopy(args, 1, params, 0, params.length);
	    
	    if (Database.hasDriver())
		System.err.println("tagdir: error: Cannot find PostgreSQL driver, have you installed postgresql-jdbc?");
	    else if ((args.length == 1) && cmd.equals("install"))  Actions.install();
	    else if ((args.length == 1) && cmd.equals("init"))     Actions.init();
	    else if ((args.length == 1) && cmd.equals("ls"))       Actions.ls();
	    else if ((args.length == 1) && cmd.equals("all"))      Actions.all();
	    else if ((args.length == 1) && cmd.equals("tags"))     Actions.tags();
	    else if ((args.length >  1) && cmd.equals("tags"))     Actions.tags(params);
	    else if ((args.length >  1) && cmd.equals("rm"))       Actions.rm(params);
	    else if ((args.length == 3) && cmd.equals("mv"))       Actions.mv(args[1], args[2]);
	    else if ((args.length >  1) && cmd.equals("show"))     Actions.show(getA(params), getN(params), getM(params));
	    else if ((args.length >  1) && cmd.equals("also"))     Actions.also(getA(params), getN(params), getM(params));
	    else if ((args.length >  1) && cmd.equals("hide"))     Actions.hide(getA(params), getN(params), getM(params));
	    else if ((args.length >  1) && cmd.equals("untag"))    Actions.untag(params);
	    else if ((args.length >  3) && cmd.equals("tag"))      Actions.tag(params);
	    else if ((args.length == 2) && cmd.equals("export"))   Actions.export();
	    else if ((args.length == 2) && cmd.equals("import"))   Actions.importSave();
	    else
	    {   System.out.println("tagdir — Tag files to add and remove the from directories");
		System.out.println();
		System.out.println("USAGE:  tagdir install                 Creates the database for the user");
		System.out.println("   or:  tagdir init                    Initialise folder");
		System.out.println("   or:  tagdir ls                      List all available files, and their tags");
		System.out.println("   or:  tagdir all                     Show all files");
		System.out.println("   or:  tagdir tags                    List all available tags");
		System.out.println("   or:  tagdir tags FILE...            List tags for file");
		System.out.println("   or:  tagdir rm FILE...              Remove file from the system");
		System.out.println("   or:  tagdir mv SRC DEST             Move a file int the system");
		System.out.println("   or:  tagdir show A - N + M          Show file satisifing A - N + M, hide rest");
		System.out.println("   or:  tagdir also A - N + M          Show file satisifing A - N + M");
		System.out.println("   or:  tagdir hide A - N + M          Hide file satisifing A - N + M");
		System.out.println("   or:  tagdir untag FILE...           Remove all tags from file");
		System.out.println("   or:  tagdir tag FILE... = TAG...    Set tags for file");
		System.out.println("   or:  tagdir tag FILE... + TAG...    Add tag to file");
		System.out.println("   or:  tagdir tag FILE... - TAG...    Remove tag from file");
		System.out.println("   or:  tagdir export EXPORT_FILE      Print all system data");
		System.out.println("   or:  tagdir import IMPORT_FILE      Initialise folder from exported data");
		System.out.println();
		System.out.println("   A:  Set of tags from which one must be used");
		System.out.println("   M:  Set of tags from which all must be used");
		System.out.println("   N:  Set of tags from whihc none may be used");
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("Copyright © 2012  Mattias Andrée");
		System.out.println();
		System.out.println("This program is free software: you can redistribute it and/or modify");
		System.out.println("it under the terms of the GNU General Public License as published by");
		System.out.println("the Free Software Foundation, either version 3 of the License, or");
		System.out.println("(at your option) any later version.");
		System.out.println();
		System.out.println("This program is distributed in the hope that it will be useful,");
		System.out.println("but WITHOUT ANY WARRANTY; without even the implied warranty of");
		System.out.println("MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the");
		System.out.println("GNU General Public License for more details.");
		System.out.println();
		System.out.println("You should have received a copy of the GNU General Public License");
		System.out.println("along with this program.  If not, see <http://www.gnu.org/licenses/>.");
		System.out.println();
	    }
	}
	catch (final Throwable err)
	{   System.err.println("tagdir: error: " + err.toString());
	}
    }
    
    
    
    /**
     * Gets the tag in the A set
     * 
     * @param   args  Arguments, excluding method
     * @return        The tags in the set
     */
    private static String[] getA(final String[] args)
    {
        int i = 0;
	for (int n = args.length; i < n; i++)
	    if (args[i].equals("+") || args[i].equals("-"))
		break;
	final String[] rc = new String[i];
	System.arraycopy(args, 0, rc, 0, i);
	return rc;
    }
    
    
    /**
     * Gets the tag in the N set
     * 
     * @param   args  Arguments, excluding method
     * @return        The tags in the set
     */
    private static String[] getN(final String[] args)
    {
        int i = 0;
	for (int n = args.length; i < n; i++)
	    if (args[i].equals("-"))
		break;
	int j = i;
	for (int n = args.length; j < n; j++)
	    if (args[j].equals("+"))
		break;
	final String[] rc = new String[j - i];
	System.arraycopy(args, i, rc, 0, j - i);
	return rc;
    }
    
    
    /**
     * Gets the tag in the M set
     * 
     * @param   args  Arguments, excluding method
     * @return        The tags in the set
     */
    private static String[] getM(final String[] args)
    {
        int i = 0;
	for (int n = args.length; i < n; i++)
	    if (args[i].equals("+"))
		break;
	int j = i;
	for (int n = args.length; j < n; j++)
	    if (args[j].equals("-"))
		break;
	final String[] rc = new String[j - i];
	System.arraycopy(args, i, rc, 0, j - i);
	return rc;
    }
    
}

