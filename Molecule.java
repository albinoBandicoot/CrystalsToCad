import java.util.ArrayList;
import java.io.*;
public class Molecule {

	public ArrayList<Atom> atoms;
	public ArrayList<Bond> bonds;

	public Molecule (){
		atoms = new ArrayList<Atom>();
		bonds = new ArrayList<Bond>();
	}

	public void print (PrintWriter out){
		if (Converter.mode == Converter.SPACE_FILLING){
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
