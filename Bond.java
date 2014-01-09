import java.io.*;
public class Bond {

	/* This class represents a bond between atoms. There are several types of bonds that can be specified
	 * in a mol2 file. I give a code to each type:
	 *
	 * 1 - single bond
	 * 2 - double bond
	 * 3 - triple bond
	 * 4 - aromatic bond
	 * 5 - amide bond
	 *
	 * Currently aromatic and amide bonds are treated no differently from single bonds. */

	public static double[] BOND_RAD = {0.05, 0.04, 0.033, 0.05, 0.05};	// default radii of the various kinds of bond
	public static int BOND_RES = 10;	// default bond resolution
	public static double DBSPACE = 0.07;	// default amount of space between the two cylinders in a double bond
	public static double TBSPACE = 0.05;	// default amount of space between each adjacent pair of cylinders in a triple bond

	public Atom a1;
	public Atom a2;
	public int type;	// 1 = single, 2 = double, 3 = triple, 4 = aromatic, 5 = amide.
						// for now, aromatic and amide bonds (and any other unrecognized bond type) are constructed identically to single bonds.

	public Bond (Atom a1, Atom a2, int type){
		this.a1 = a1;
		this.a2 = a2;
		this.type = type;
	}

	public Bond (Atom a1, Atom a2, String type){
		this.a1 = a1;
		this.a2 = a2;
		try {
			this.type = Integer.parseInt(type);
		} catch (NumberFormatException e){
			if (type.equals("ar")){
				this.type = 4;
			} else if (type.equals("am")){
				this.type = 5;
			} else {
				System.out.println("Warning - unrecognized bond type (" + type +"); treating it as a single bond");
				this.type = 1;
			}
		}
	}

	/* Print out the openSCAD code for this bond */
	public void print (PrintWriter out){
		Point center = a1.pos.add(a2.pos).mul(0.5);	// this point is the center of the bond
		Point vec = a2.pos.sub(a1.pos);				// this is a vector along the bond axis
		double len = vec.length();					// the distance between the two atom centers
		vec = vec.normalize();						// normalize the bond axis vector

		// now we want to get the spherical coordinate angles for the bond axis vector:
		double phi = -Math.acos (vec.z) * (180.0/Math.PI);
		vec.z = 0;
		vec = vec.normalize();	// this has projected the vector into the xy-plane and has normalized it, so it's on the unit circle.
		double theta = -Math.atan2 (vec.x, vec.y) * (180.0/Math.PI);

		out.println("\ttranslate(" + center + ") {");	// first translate the bond to the right spot
		out.println("\t\trotate([" + phi + ", 0, " + theta + "]){");	// now apply the rotations

		// now generate the actual bond geometry
		if (type == 2){	// double bond.
			out.println("\t\t\ttranslate([double_bond_space,0,0]){");	// first cylinder gets translated double_bond_space units in the +x direction
			out.println("\t\t\t\tcylinder(r=double_bond_rad, h=" + len + ", center=true, $fn=bond_res);");
			out.println("\t\t\t}");

			out.println("\t\t\ttranslate([-double_bond_space,0,0]){");	// second cylinder gets translated double_bond_space units in the -x direction
			out.println("\t\t\t\tcylinder(r=double_bond_rad, h=" + len + ", center=true, $fn=bond_res);");
			out.println("\t\t\t}");

		} else if (type == 3){	// triple bonds
			out.println("\t\t\ttranslate([tripple_bond_space,0,0]){");	
			out.println("\t\t\t\tcylinder(r=tripple_bond_rad, h=" + len + ", center=true, $fn=bond_res);");
			out.println("\t\t\t}");

			out.println("\t\t\t\tcylinder(r=tripple_bond_rad, h=" + len + ", center=true, $fn=bond_res);");	// the middle cylinder doesn't need a translation

			out.println("\t\t\ttranslate([-tripple_bond_space,0,0]){");
			out.println("\t\t\t\tcylinder(r=tripple_bond_rad, h=" + len + ", center=true, $fn=bond_res);");
			out.println("\t\t\t}");

		} else {	// types 1, 4, 5 => just a single cylinder
			out.println("\t\t\tcylinder(r=single_bond_rad, h=" + len + ", center=true, $fn=bond_res);");
		}
		out.println("\t\t}");
		out.println("\t}");
	}

}
