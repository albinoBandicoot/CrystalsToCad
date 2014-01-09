import java.util.ArrayList;
import java.io.*;
public class Molecule {

	/* This class represents a molecule. It consists of a list of atoms and bonds. Note that the graph of which atoms are connected
	 * to which others is never constructed, because it is not needed. However, if we want to investigate computing the ellipsoids 
	 * for the thermal ellipsoid model, constructing this graph will likely be necessary, and so this representation will either need
	 * to be changed or a new one will need to be added in addition */

	public ArrayList<Atom> atoms;
	public ArrayList<Bond> bonds;

	public Molecule (){
		atoms = new ArrayList<Atom>();
		bonds = new ArrayList<Bond>();
	}

	/* Write out the SCAD code for the molecule. Mostly just calls methods in Atom and Bond to do the real work */
	public void print (PrintWriter out){
		if (Converter.mode == Converter.SPACE_FILLING){
			// in the space filling model, we don't need any bonds
			out.println ("union() {");
			for (Atom a : atoms){
				a.print (out);
			}
			out.println ("}");
		} else if (Converter.mode == Converter.BALL_AND_STICK){
			out.println ("union() {");
			for (Atom a : atoms){
				a.print (out);
			}
			for (Bond b : bonds){
				b.print (out);
			}
			out.println ("}");
		} else {
			System.out.println ("Unsuported mode; valid options are SPACE_FILLING and BALL_AND_STICK.");
			System.exit (1);
		}
	}
}
