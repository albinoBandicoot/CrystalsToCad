import java.io.*;
public class Atom {

	public Point pos;	// the atom's position
	public double rad;	// it's radius
	public int number;	// atomic number
	public int id;		// id number
	public String name;

	/* These variables define the default resolutions for the spheres. These will be assigned to similarly named variables in the 
	 * openSCAD output, and those variables will be referenced throughout the scad file. */
	public static int C_RES = 24;	// for carbon atoms
	public static int O_RES = 24;	// for oxygen atoms
	public static int H_RES = 16;	// for hydrogen atoms
	public static int OTHER_RES = 20;	// everything else

	public static double BS_RAD_SCALE = 0.19;	// scaling factor for atom radius in the ball & stick model

	public static final double[] radii = {1.20, 1.43, 2.12, 1.98, 1.91, 1.77, 1.66, 1.50, 1.46, 1.58, 2.50, 2.51, 2.25, 2.19, 1.90, 1.89, 1.82, 1.83, 2.73, 2.62, 2.58, 2.46, 2.42, 2.45, 2.45, 2.44, 2.40, 2.40, 2.38, 2.39, 2.32, 2.29, 1.88, 1.82, 1.86, 2.25, 3.21, 2.84, 2.75, 2.52, 2.56, 2.45, 2.44, 2.46, 2.44, 2.15, 2.53, 2.49, 2.43, 2.42, 2.47, 1.99, 2.04, 2.06, 3.48, 3.03, 2.98, 2.88, 2.92, 2.95, 2.90, 2.87, 2.83, 2.79, 2.87, 2.81, 2.83, 2.79, 2.80, 2.74, 2.63, 2.53, 2.57, 2.49, 2.48, 2.41, 2.29, 2.32, 2.45, 2.47, 2.60, 2.54, 2.8, 2.93, 2.88, 2.71, 2.82, 2.81, 2.83, 3.05, 3.4, 3.05, 2.7};	// this only goes up to Einsteinium (element 99).

	public static final String[] symbols = {"h", "he", 
											"li", "be", "b", "c", "n", "o", "f", "ne",
											"na", "mg", "al", "si", "p", "s", "cl", "ar",
											"k", "ca", "sc", "ti", "v", "cr", "mn", "fe", "co", "ni", "cu", "zn", "ga", "ge", "as", "se", "br", "kr",
											"rb", "sr", "y", "zr", "nb", "mo", "tc", "ru", "rh", "pd", "ag", "cd", "in", "sn", "sb", "te", "i", "xe", 
											"cs", "ba", "la", "ce", "nd", "pm", "sm", "eu", "gd", "tb", "dy", "ho", "er", "tm", "yb", "lu", "hf", "ta", "w", "re", "os", "ir", "pt", "au", "hg", "tl", "pb", "bi", "po", "at", "rn"};

	public Atom (Point p, int id, int num, String name){
		this.pos = p;
		this.id = id;
		this.number = num;
		this.rad = radii[num-1];
		this.name = name;
	}

	/* Given a string for the symbol, find the corresponding atomic number */
	public static int findNumberFromSymbol (String sym){
		for (int i=0; i<symbols.length; i++){
			if (sym.toLowerCase().equals(symbols[i])){
				return i+1;	// the 0th element holds the symbol for atomic # 1.
			}
		}
		System.out.println("Unrecognized atom symbol: " + sym);
		System.exit(1);
		return -1;
	}

	/* Get the correct SCAD variable name for the resolution of this atom */
	public String getResolution () {
		switch (number){
			case 6:	// carbon
				return "c_res";
			case 8:	// oxygen
				return "o_res";
			case 1:	// hydrogen
				return "h_res";
			default:
				return "other_res";
		}
	}

	/* Determine whether this atom has a predefined color */
	public boolean hasColor (){
		return number == 1 || number == 6 || number == 7 || number == 8;
	}

	/* Get the openSCAD color statement for this atom's predefined color, if it has one. Note that the close bracket must be added later */
	public String getColor (){
		switch (number){
			case 1:
				return "color([1.0,1.0,1.0]) {";	// hydrogen is white
			case 6:
				return "color([0.3,0.3,0.3]) {";	// carbon is dark grey
			case 7:
				return "color([0.2,0.9,0.3]) {";	// nitrogen is green
			case 8:
				return "color([1.0,0.0,0.0]) {";	// oxygen is red
		}
		return "";
	}

	/* Prints out the SCAD code for this atom */
	public void print (PrintWriter out){
		if (hasColor()){
			out.println("\t" + getColor());
		}

		out.println("\ttranslate([" + pos.x + ", " + pos.y + ", " + pos.z + "]) {");	// translate things to the right spot

		if (Converter.mode == Converter.SPACE_FILLING){
			out.println("\t\tsphere(r=" + rad + ", $fn=" + getResolution() + ");");		// make a sphere with the right size and resolution
		} else if (Converter.mode == Converter.BALL_AND_STICK){
			out.println("\t\tsphere(r=bs_rad_scale * " + rad + ", $fn=" + getResolution() + ");");
		} else {
			System.out.println("Bad model mode.");
			System.exit(1);
		}
		out.println("\t}");
		if (hasColor()){
			out.println("\t}");
		}
	}

	/* Produce a human-readable string */
	public String toString (){
		return symbols[number-1].toUpperCase() + " at " + pos + ", radius " + rad + ", id " + id + ", and name " + name;
	}

}
