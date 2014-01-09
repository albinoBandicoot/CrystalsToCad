import java.io.*;
public class Atom {

	public Point pos;
	public double rad;
	public int number;	// atomic number
	public int id;		// id number
	public String name;

	public static int C_RES = 24;
	public static int O_RES = 24;
	public static int H_RES = 16;
	public static int OTHER_RES = 20;

	public static double BS_RAD_SCALE = 0.19;
	public static double BS_RAD = 0.35;	// ball and stick radius
	public static double BS_H_RAD = 0.2;	// hydrogen radius for ball and stick

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

	public boolean hasColor (){
		return number == 1 || number == 6 || number == 7 || number == 8;
	}

	public String getColor (){
		switch (number){
			case 1:
				return "color([1.0,1.0,1.0]) {";
			case 6:
				return "color([0.3,0.3,0.3]) {";
			case 7:
				return "color([0.2,0.9,0.3]) {";
			case 8:
				return "color([1.0,0.0,0.0]) {";
		}
		return "";
	}

	public void print (PrintWriter out){
		if (hasColor()){
			out.println("\t" + getColor());
		}
		out.println("\ttranslate([" + pos.x + ", " + pos.y + ", " + pos.z + "]) {");
		if (Converter.mode == Converter.SPACE_FILLING){
			out.println("\t\tsphere(r=" + rad + ", $fn=" + getResolution() + ");");
		} else if (Converter.mode == Converter.BALL_AND_STICK){
//			out.println("\t\tsphere(r=" + ((number==1) ? "ball_h_rad" : "ball_rad") + ", $fn=" + getResolution() + ");");
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

	public String toString (){
		return symbols[number-1].toUpperCase() + " at " + pos + ", radius " + rad + ", id " + id + ", and name " + name;
	}

}
