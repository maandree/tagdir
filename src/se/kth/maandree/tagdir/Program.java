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
	final String cmd = args.length == 0 ? "" : args[0];
	
	if (Database.hasDriver())
	{   System.err.println("Can not find PostgreSQL driver, have you installed postgresql-jdbc?");
	    return;
	}
	
	if (cmd.equals("init"))
	    ;
	else if (cmd.equals("ls"))
	    ;
	else if (cmd.equals("all"))
	    ;
	else if (cmd.equals("tags")) // tags | tags FILE...
	    if (args.length == 1)
		;
	    else
		;
	else if (cmd.equals("rm"))
	    ;
	else if (cmd.equals("show"))
	    ;
	else if (cmd.equals("also"))
	    ;
	else if (cmd.equals("hide"))
	    ;
	else if (cmd.equals("untag"))
	    ;
	else if (cmd.equals("tag")) // tag FILE... = TAG... | tag FILE... + TAG... | tag FILE... - TAG...
	    ;
	else
	{
	    System.out.println("tagdir — Tag files to add and remove the from directories");
	    System.out.println();
	    System.out.println("USAGE:  tagdir init                    Initialise folder");
	    System.out.println("   or:  tagdir ls                      List all available files, and their tags");
	    System.out.println("   or:  tagdir all                     Show all files");
	    System.out.println("   or:  tagdir tags                    List all available tags");
	    System.out.println("   or:  tagdir tags FILE...            List tags for file");
	    System.out.println("   or:  tagdir rm FILE...              Remove file from system");
	    System.out.println("   or:  tagdir show A - N + M          Show file satisifing A - N + M, hide rest");
	    System.out.println("   or:  tagdir also A - N + M          Show file satisifing A - N + M");
	    System.out.println("   or:  tagdir hide A - N + M          Hide file satisifing A - N + M");
	    System.out.println("   or:  tagdir untag                   Remove all tags from file");
	    System.out.println("   or:  tagdir tag FILE... = TAG...    Set tags for file");
	    System.out.println("   or:  tagdir tag FILE... + TAG...    Add tag to file");
	    System.out.println("   or:  tagdir tag FILE... - TAG...    Remove tag from file");
	    System.out.println("   or:  tagdir export                  Print all system data");
	    System.out.println("   or:  tagdir import                  Initialise folder from exported data");
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
    
}

