public class Point {

	/* Class for working with 3D points */

	public double x, y, z;

	public Point (double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Point add (Point b){
		return new Point (x + b.x, y + b.y, z + b.z);
	}

	public Point sub (Point b){
		return new Point (x - b.x, y - b.y, z - b.z);
	}

	public Point mul (double d){
		return new Point (x*d, y*d, z*d);
	}

	public double length (){
		return Math.sqrt (x*x + y*y + z*z);
	}

	public Point normalize () {
		return mul(1/length());
	}

	public String toString (){
		return "[" + x + ", " + y + ", " + z + "]";
	}

}
