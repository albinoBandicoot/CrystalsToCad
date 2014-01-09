import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;
public class Converter {

	/* This class handles the parsing of the .mol2 files and has the top-level routine for writing the SCAD file.
	 * It also provides the main method for the command-line utility */

	public static final int SPACE_FILLING = 0;
	public static final int BALL_AND_STICK = 1;

	public static int mode = SPACE_FILLING;

	/* Test whether a string contains only whitespace characters */
	public static boolean isBlank (String str){
		for (int i=0; i<str.length(); i++){
			char c = str.charAt(i);
			if (c == ' ' || c == '\t' || c == '\n'){
				continue;
			} else {
				return false;
			}
		}
		return true;
	}

	/* Construct a Molecule from a .mol2 file specified by 'infile' */
	public static Molecule readMolecule (File infile) throws IOException {
		Molecule mol = new Molecule ();
		Scanner sc = new Scanner (infile);
		String line = sc.nextLine ();
		int line_num = 1;
		while (!line.contains("ATOM")){	// search for an ATOM block
			line = sc.nextLine();
			line_num++;
			if (!sc.hasNextLine()){
				System.out.println("Error - no ATOM block found in .mol2 file; aborting.");
				System.exit(1);
			}
		}
		line = sc.nextLine();
		line_num++;
		while (!line.contains("@<TRIPOS>")){	// this would indicate we're on to another section
			if (!isBlank(line)){			// if the line actually has stuff on it
				try {
					Scanner ls = new Scanner(line);
					int id = ls.nextInt();	// the ID # of the atom (the bonds use these to say which atoms they connect)
					String name = ls.next();	// name of the atom
					double x, y, z;
					x = ls.nextDouble();		// the 3D cartesian coordinates of the atom's center
					y = ls.nextDouble();
					z = ls.nextDouble();

					String type = ls.next();	// the type of atom.
					if (type.contains(".")){
						type = type.substring(0, type.indexOf('.'));	// sometimes there is additional gunk after the element name
					}
					// we don't care about the rest of the (optional) parameters on the line.
					mol.atoms.add (new Atom (new Point (x,y,z), id, Atom.findNumberFromSymbol (type), name));
					System.out.println("Added atom " + mol.atoms.get(mol.atoms.size()-1));

				} catch (java.util.InputMismatchException e){
					System.out.println("Invalid input on line " + line_num + ".");
					e.printStackTrace ();
					System.exit(1);
				}
			}
			line = sc.nextLine();
			line_num ++;
		}
		while (!line.contains("BOND")){	// now skip until we hit the start of the BOND section, or the end of the file.
			if (sc.hasNextLine()){
				line = sc.nextLine();
				line_num ++;
			} else {	// we're at the end of the file and haven't found BOND data. This is only a problem if we wanted a ball and stick model.
				if (mode == BALL_AND_STICK){
					System.out.println("Error - bond data is required for ball and stick model generation, but none was provided.");
					System.exit(2);
				} else {
					return mol;	// if we had a space filling model, we have everything we need already, so we can return.
				}
			}
		}
		line = sc.nextLine();	// skip to the first line of bond data.
		while (!line.contains("@<TRIPOS>")){
			if (!isBlank(line)){
				try {
					Scanner ls = new Scanner(line);
					ls.next();	// skip over the bond number.
					int atom1 = ls.nextInt();
					int atom2 = ls.nextInt();
					String type = ls.next();
					mol.bonds.add (new Bond (mol.atoms.get(atom1-1), mol.atoms.get(atom2-1), type));
				} catch (java.util.InputMismatchException e){
					System.out.println ("Invalid input on line " + line_num + ".");
					e.printStackTrace ();
					System.exit (1);
				}
			}
			if (!sc.hasNextLine()){
				return mol;
			}
			line = sc.nextLine();
			line_num ++;
		}
		return mol;
	}

	/* Writes out the .scad file, given a Molecule. */
	public static void writeScadfile (File f, Molecule mol) throws IOException {
		PrintWriter out = new PrintWriter (f);
		/* First write out the definitions for all of the variables that control the model. They are initialized to their default
		 * values as specified in various places in the CrystalsToCAD source; they are provided to make tinkering with the model
		 * easier (changing bond sizes or atom resolutions, etc.) */
		out.println("single_bond_rad = " + Bond.BOND_RAD[0] + ";");
		out.println("double_bond_rad = " + Bond.BOND_RAD[1] + ";");
		out.println("tripple_bond_rad = " + Bond.BOND_RAD[2] + ";");
		out.println("aromatic_bond_rad = " + Bond.BOND_RAD[3] + ";");
		out.println("amine_bond_rad = " + Bond.BOND_RAD[4] + ";");
		out.println("bond_res = " + Bond.BOND_RES + ";");
		out.println("double_bond_space = " + Bond.DBSPACE + ";");
		out.println("tripple_bond_space = " + Bond.TBSPACE + ";");
		out.println("c_res = " + Atom.C_RES + ";");
		out.println("o_res = " + Atom.O_RES + ";");
		out.println("h_res = " + Atom.H_RES + ";");
		out.println("other_res = " + Atom.OTHER_RES + ";");
		out.println("bs_rad_scale = " + Atom.BS_RAD_SCALE + ";");

		mol.print(out);	// print out the SCAD source for the molecule 
		out.close();	// close the writer
	}

	/* Main method for the command-line utility */
	public static void main (String[] args){
		File infile = null;
		File scadfile = null;

		/* Parse the arguments */
		for (int i=0; i<args.length; i++){
			if (args[i].equals("-bs")){
				mode = BALL_AND_STICK;
			} else if (args[i].equals("-sf")){
				mode = SPACE_FILLING;
			} else if (args[i].equals("-o")){
				if (i+1 < args.length){
					scadfile = new File (args[i+1]);
					i++;
				} else {
					System.out.println("No ouput file specified after -o flag!");
					System.exit(1);
				}
			} else {	// assume it's the input file name
				infile = new File(args[i]);
			}
		}
		if (infile == null){
			System.out.println("No input file specified!");
			System.exit(1);
		}
		if (scadfile == null){	// no output file specified, so generate a decent filename based on the input file. 
			String infname = infile.getName();
			if (infname.contains(".mol2")){	// if the input file has a .mol2 extension, replace it with .scad
				scadfile = new File (infname.substring(0, infname.length()-5) + ".scad");
			} else {						// otherwise, just append .scad to it
				scadfile = new File (infname + ".scad");
			}
		}

		// now that everything's set up, actually read the molecule from the file and try to convert it.
		try {
			Molecule mol = readMolecule (infile);
			try {
				writeScadfile (scadfile, mol);
			} catch (IOException e){
				System.out.println ("IO problem when reading the input file");
				e.printStackTrace();
				System.exit(1);
			}
		} catch (IOException e){
			System.out.println ("IO problem when reading the input file");
			e.printStackTrace();
			System.exit(1);
		}
	}
}
