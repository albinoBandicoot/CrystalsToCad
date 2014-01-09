import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;
public class Converter {

	public static final int SPACE_FILLING = 0;
	public static final int BALL_AND_STICK = 1;


	public static int mode = SPACE_FILLING;

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

	public static Molecule readMolecule (File infile) throws IOException {
		Molecule mol = new Molecule ();
		Scanner sc = new Scanner (infile);
		String line = sc.nextLine ();
		int line_num = 1;
		while (!line.contains("ATOM")){
			line = sc.nextLine();
			line_num++;
			if (!sc.hasNextLine()){
				System.out.println("Error - no ATOM block found in .mol2 file; aborting.");
				System.exit(1);
			}
		}
		line = sc.nextLine();
		line_num++;
		while (!line.contains("@<TRIPOS>")){
			if (!isBlank(line)){
				try {
					Scanner ls = new Scanner(line);
					int id = ls.nextInt();
					String name = ls.next();
					double x, y, z;
					x = ls.nextDouble();
					y = ls.nextDouble();
					z = ls.nextDouble();
					String type = ls.next();
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
		while (!line.contains("BOND")){
			if (sc.hasNextLine()){
				line = sc.nextLine();
				line_num ++;
			} else {
				if (mode == BALL_AND_STICK){
					System.out.println("Error - bond data is required for ball and stick model generation, but none was provided.");
					System.exit(2);
				} else {
					return mol;
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

	public static void writeScadfile (File f, Molecule mol) throws IOException {
		PrintWriter out = new PrintWriter (f);
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
		out.println("//ball_rad = " + Atom.BS_RAD + ";");
		out.println("//ball_h_rad = " + Atom.BS_H_RAD + ";");

		mol.print(out);
		out.close();
	}

	public static void main (String[] args){
		File infile = null;
		File scadfile = null;
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
		if (scadfile == null){
			String infname = infile.getName();
			if (infname.contains(".mol2")){
				scadfile = new File (infname.substring(0, infname.length()-5) + ".scad");
			} else {
				scadfile = new File (infname + ".scad");
			}
		}

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

