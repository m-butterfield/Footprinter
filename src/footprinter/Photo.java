package footprinter;

import com.jhlabs.map.proj.*; // get it here: jhlabs.com/java/maps/proj/index.html
import java.awt.geom.Point2D;

public class Photo {
	private String[] dataFields = new String[7];
	private String photoID;
	private double groundHeight, xValue, yValue, zValue, oValue, pValue, kValue;
	// will need to convert o,p,k to radians
	private double radOValue, radPValue, radKValue;
	private double[][] cornerCoordinates = new double[4][2];
	private double r11, r12, r13, r21, r22, r23, r31, r32, r33; // rotation matrix elements
	private String EPSGcode;
	// UCX image coordinates and focal length
	private final double UCX_IMAGE_Y_FRONT = -33.912, UCX_IMAGE_Y_BACK = 33.912;
	private final double UCX_IMAGE_X_RIGHT = -51.948, UCX_IMAGE_X_LEFT = 51.948;
	private final double UCX_FOCAL_LENGTH = 100.5;

    // Constructor for a photo object
	public Photo(String nextLine, double groundHeight, String EPSGcode) {
		this.groundHeight = groundHeight;
		this.EPSGcode = EPSGcode;
		if (!nextLine.contains("#")) { // detect grouping separator
		dataFields = nextLine.split("[\\s,]+"); // file will be comma or space delimited
		photoID = dataFields[0];
		xValue = Double.parseDouble(dataFields[1]);
		yValue = Double.parseDouble(dataFields[2]);
		zValue = Double.parseDouble(dataFields[3]);
		oValue = Double.parseDouble(dataFields[4]);
		pValue = Double.parseDouble(dataFields[5]);
		kValue = Double.parseDouble(dataFields[6]);
		radOValue = Math.toRadians(oValue);
		radPValue = Math.toRadians(pValue);
		radKValue = Math.toRadians(kValue);
		computeRotationMatrix();
		}
		else dataFields[0] = "#";
	}

	private void computeRotationMatrix() {
		r11 = Math.cos(radKValue) * Math.cos(radPValue);
		r12 = Math.cos(radKValue) * Math.sin(radPValue) * Math.sin(radOValue) - Math.sin(radKValue) * Math.cos(radOValue);
		r13 = Math.cos(radKValue) * Math.sin(radPValue) * Math.cos(radOValue) + Math.sin(radKValue) * Math.sin(radOValue);
		r21 = Math.sin(radKValue) * Math.cos(radPValue);
		r22 = Math.sin(radKValue) * Math.sin(radPValue) * Math.sin(radOValue) + Math.cos(radKValue) * Math.cos(radOValue);
		r23 = Math.sin(radKValue) * Math.sin(radPValue) * Math.cos(radOValue) - Math.cos(radKValue) * Math.sin(radOValue);
		r31 = -Math.sin(radPValue);
		r32 = Math.cos(radPValue) * Math.sin(radOValue);
		r33 = Math.cos(radPValue) * Math.cos(radOValue);
	}
	
	public void computeCornerCoordinates() {
		// FrontRight X
		if (dataFields[0] != "#") {
		cornerCoordinates[0][0] = xValue + (groundHeight - zValue)
				* ((r11 * UCX_IMAGE_X_RIGHT + r12 * UCX_IMAGE_Y_FRONT - r13 * UCX_FOCAL_LENGTH)/
				   (r31 * UCX_IMAGE_X_RIGHT + r32 * UCX_IMAGE_Y_FRONT - r33 * UCX_FOCAL_LENGTH));
		
		// FrontRight Y
		cornerCoordinates[0][1] = yValue + (groundHeight - zValue)
				* ((r21 * UCX_IMAGE_X_RIGHT + r22 * UCX_IMAGE_Y_FRONT - r23 * UCX_FOCAL_LENGTH)/
				   (r31 * UCX_IMAGE_X_RIGHT + r32 * UCX_IMAGE_Y_FRONT - r33 * UCX_FOCAL_LENGTH));
		
		// FrontLeft X
		cornerCoordinates[1][0] = xValue + (groundHeight - zValue)
				* ((r11 * UCX_IMAGE_X_LEFT + r12 * UCX_IMAGE_Y_FRONT - r13 * UCX_FOCAL_LENGTH)/
				   (r31 * UCX_IMAGE_X_LEFT + r32 * UCX_IMAGE_Y_FRONT - r33 * UCX_FOCAL_LENGTH));
		
		// FrontLeft Y
		cornerCoordinates[1][1] = yValue + (groundHeight - zValue)
				* ((r21 * UCX_IMAGE_X_LEFT + r22 * UCX_IMAGE_Y_FRONT - r23 * UCX_FOCAL_LENGTH)/
				   (r31 * UCX_IMAGE_X_LEFT + r32 * UCX_IMAGE_Y_FRONT - r33 * UCX_FOCAL_LENGTH));
		
		// BackRight X
		cornerCoordinates[2][0] = xValue + (groundHeight - zValue)
				* ((r11 * UCX_IMAGE_X_RIGHT + r12 * UCX_IMAGE_Y_BACK - r13 * UCX_FOCAL_LENGTH)/
				   (r31 * UCX_IMAGE_X_RIGHT + r32 * UCX_IMAGE_Y_BACK - r33 * UCX_FOCAL_LENGTH));
		
		// BackRight Y
		cornerCoordinates[2][1] = yValue + (groundHeight - zValue)
				* ((r21 * UCX_IMAGE_X_RIGHT + r22 * UCX_IMAGE_Y_BACK - r23 * UCX_FOCAL_LENGTH)/
				   (r31 * UCX_IMAGE_X_RIGHT + r32 * UCX_IMAGE_Y_BACK - r33 * UCX_FOCAL_LENGTH));
		
		// BackLeft X
		cornerCoordinates[3][0] = xValue + (groundHeight - zValue)
				* ((r11 * UCX_IMAGE_X_LEFT + r12 * UCX_IMAGE_Y_BACK - r13 * UCX_FOCAL_LENGTH)/
				   (r31 * UCX_IMAGE_X_LEFT + r32 * UCX_IMAGE_Y_BACK - r33 * UCX_FOCAL_LENGTH));
		
		// BackLeft Y
		cornerCoordinates[3][1] = yValue + (groundHeight - zValue)
				* ((r21 * UCX_IMAGE_X_LEFT + r22 * UCX_IMAGE_Y_BACK - r23 * UCX_FOCAL_LENGTH)/
				   (r31 * UCX_IMAGE_X_LEFT + r32 * UCX_IMAGE_Y_BACK - r33 * UCX_FOCAL_LENGTH));
		
		transformToLatLong(cornerCoordinates);
		
		// Not using GDAL anymore...
//		SpatialReference crs1 = new SpatialReference();
//		crs1.ImportFromEPSG(EPSGcode);
//		SpatialReference crs2 = new SpatialReference();
//		crs2.ImportFromEPSG(WGS84_EPSG_CODE);
//		CoordinateTransformation transformer = new CoordinateTransformation(crs1, crs2);
//		transformer.TransformPoints(cornerCoordinates);
		}
	}

	private void transformToLatLong(double[][] points) {
		Projection projection = ProjectionFactory.getNamedPROJ4CoordinateSystem(this.EPSGcode);
		for (int i = 0; i < 4; i++) {
			Point2D.Double point = new Point2D.Double(points[i][0], points[i][1]);
			projection.inverseTransform(point, point);
			points[i][0] = point.getX();
			points[i][1] = point.getY();
		}	
	}

	public double[][] getCornerCoordinates() {
		return cornerCoordinates;
	}

	@Override
	public String toString() {
		// returns empty string if grouping separator is detected
		if (dataFields[0] != "#") {
		return  "	<Placemark>\n		<name>" + photoID + "</name>\n		<description>"
				+ "xValue=" + xValue
				+ "\nyValue=" + yValue
				+ "\nzValue=" + zValue
				+ "\noValue=" + oValue
				+ "\npValue=" + pValue 
				+ "\nkValue=" + kValue
				+ "\nGround Height=" + groundHeight 
				+ "\nCRS EPSG code=" + EPSGcode
				+ "</description>\n"
				+ "		<styleUrl>#msn_ylw-pushpin0</styleUrl>\n"
				+ "		<Polygon>\n"
				+ "			<tessellate>1</tessellate>\n"
				+ "			<outerBoundaryIs>\n"
				+ "				<LinearRing>\n"
				+ "					<coordinates>\n"
				+ cornerCoordinates[0][0] + "," + cornerCoordinates[0][1] + "," + groundHeight + " " 
				+ cornerCoordinates[1][0] + "," + cornerCoordinates[1][1] + "," + groundHeight + " " 
				+ cornerCoordinates[3][0] + "," + cornerCoordinates[3][1] + "," + groundHeight + " " 
				+ cornerCoordinates[2][0] + "," + cornerCoordinates[2][1] + "," + groundHeight + "\n"
				+ "					</coordinates>\n"
				+ "				</LinearRing>\n"
				+ "			</outerBoundaryIs>\n"
				+ "		</Polygon>\n	</Placemark>\n";
		}
		else return "";
	}
	
}

//  Here is an alternate rotation matrix that may work as well, may need some slight 
//  modifications to the coordinate computation formulas though
//  r11 = Math.cos(pValue) * Math.cos(kValue);
//  r12 = -Math.cos(pValue) * Math.sin(kValue);
//  r13 = Math.sin(pValue);
//  r21 = Math.cos(oValue) * Math.sin(kValue) + Math.sin(oValue) * Math.sin(pValue) * Math.cos(kValue);
//  r22 = Math.cos(oValue) * Math.cos(kValue) - Math.sin(oValue) * Math.sin(pValue) * Math.sin(kValue);
//  r23 = -Math.sin(oValue) * Math.cos(pValue);
//  r31 = Math.sin(oValue) * Math.sin(kValue) - Math.cos(oValue) * Math.sin(pValue) * Math.cos(kValue);
//  r32 = Math.sin(oValue) * Math.cos(kValue) + Math.cos(oValue) * Math.sin(pValue) * Math.sin(kValue);
//  r33 = Math.cos(oValue) * Math.cos(pValue);