import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.io.*;
public class CrystalsToCAD extends JFrame implements ActionListener {

	public JFileChooser fchooser = new JFileChooser();

	public File inf, outf;

	public JComboBox mode;
	public JLabel modeL;
	public JLabel infileL;
	public JLabel outfileL;
	public JButton infile;
	public JButton outfile;
	public JButton convert;

	public CrystalsToCAD () {
		super ("Crystals to CAD");
		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

		JPanel content = new JPanel ();
		content.setOpaque (true);
		content.setLayout (null);

		modeL = new JLabel ("Model type: ");
		modeL.setBounds (10, 10, 100, 25);

		String[] modes = {"Space filling", "Ball and Stick"};
		mode = new JComboBox (modes);
		mode.setBounds (110, 10, 150, 25);
		
		infileL = new JLabel ("Input file: ");
		infileL.setBounds(10, 45, 500, 25);

		infile = new JButton ("Browse...");
		infile.setBounds(510, 45, 100, 25);
		infile.addActionListener (this);
		infile.setActionCommand ("choose_infile");

		outfileL = new JLabel ("Output file: ");
		outfileL.setBounds(10, 80, 500, 25);

		outfile = new JButton ("Browse...");
		outfile.setBounds(510, 80, 100, 25);
		outfile.addActionListener (this);
		outfile.setActionCommand ("choose_outfile");

		convert = new JButton ("CONVERT");
		convert.setBounds(10, 130, 600, 45);
		convert.addActionListener (this);
		convert.setActionCommand ("convert");

		content.add (mode);
		content.add (modeL);
		content.add (infileL);
		content.add (outfileL);
		content.add (infile);
		content.add (outfile);
		content.add (convert);

		setContentPane (content);
		pack ();
		setSize (620, 200);
		setVisible(true);
	}

	public static void main (String[] args){
		CrystalsToCAD c = new CrystalsToCAD ();
	}

	public void actionPerformed (ActionEvent e){
		String c = e.getActionCommand ();
		if (c.equals ("choose_infile")){
			fchooser.showOpenDialog (this);
			inf = fchooser.getSelectedFile();
			infileL.setText ("Input file: " + inf.getAbsolutePath());
			String infname = inf.getAbsolutePath();
			if (infname.contains(".mol2")){
				outf = new File(infname.substring(0, infname.length()-5) + ".scad");
			} else {
				outf = new File(infname + ".scad");
			}
			outfileL.setText("Output file: " + outf.getAbsolutePath());
		} else if (c.equals("choose_outfile")){
			fchooser.showSaveDialog(this);
			outf = fchooser.getSelectedFile();
			outfileL.setText("Output file: " + outf.getAbsolutePath());
		} else if (c.equals("convert")){
			try {
				int m = mode.getSelectedIndex();
				if (m == 0) {
					Converter.mode = Converter.SPACE_FILLING;
				} else if (m == 1){
					Converter.mode = Converter.BALL_AND_STICK;
				}
				Molecule mol = Converter.readMolecule (inf);
				Converter.writeScadfile (outf, mol);
			} catch (IOException ex){
				JOptionPane.showMessageDialog (this, "IO Exception when converting the files! See console for stack backtrace.", "IO Problem!", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			} catch (Exception ex){
				JOptionPane.showMessageDialog (this, "General unspecified error occurred! Check console.", "Error!", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}
}
