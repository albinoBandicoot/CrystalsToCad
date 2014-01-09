import java.io.*;
public class Bond {

	public static double[] BOND_RAD = {0.05, 0.04, 0.033, 0.05, 0.05};
	public static int BOND_RES = 10;
	public static double DBSPACE = 0.07;
	public static double TBSPACE = 0.05;

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

	public void print (PrintWriter out){
		Point center = a1.pos.add(a2.pos).mul(0.5);
		Point vec = a2.pos.sub(a1.pos);
		double len = vec.length();
		vec = vec.normalize();
		double phi = -Math.acos (vec.z) * (180.0/Math.PI);
		vec.z = 0;
		vec = vec.normalize();
		double theta = -Math.atan2 (vec.x, vec.y) * (180.0/Math.PI);
		out.println("\ttranslate(" + center + ") {");
		out.println("\t\trotate([" + phi + ", 0, " + theta + "]){");
		if (type == 2){
			out.println("\t\t\ttranslate([double_bond_space,0,0]){");
			out.println("\t\t\t\tcylinder(r=double_bond_rad, h=" + len + ", center=true, $fn=bond_res);");
			out.println("\t\t\t}");

			out.println("\t\t\ttranslate([-double_bond_space,0,0]){");
			out.println("\t\t\t\tcylinder(r=double_bond_rad, h=" + len + ", center=true, $fn=bond_res);");
			out.println("\t\t\t}");
		} else if (type == 3){
			out.println("\t\t\ttranslate([tripple_bond_space,0,0]){");
			out.println("\t\t\t\tcylinder(r=tripple_bond_rad, h=" + len + ", center=true, $fn=bond_res);");
			out.println("\t\t\t}");

			out.println("\t\t\t\tcylinder(r=tripple_bond_rad, h=" + len + ", center=true, $fn=bond_res);");

			out.println("\t\t\ttranslate([-tripple_bond_space,0,0]){");
			out.println("\t\t\t\tcylinder(r=tripple_bond_rad, h=" + len + ", center=true, $fn=bond_res);");
			out.println("\t\t\t}");
		} else {	// types 1, 4, 5
			out.println("\t\t\tcylinder(r=single_bond_rad, h=" + len + ", center=true, $fn=bond_res);");
		}
		out.println("\t\t}");
		out.println("\t}");
	}

}
