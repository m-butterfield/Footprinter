package footprinter;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;



/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {
	
	protected Shell shell;
	private Text text_1;
	private Text text_2;
	private Text text_3;
	private Text text_4;
	private Button btnBrowse2;
	private Label lblOutputKML;
	private Label lblGroundHeight;

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	public Object start(IApplicationContext context) throws Exception {
		try {
			Application window = new Application();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return context;
		
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		Image icon = new Image(display, Application.class.getResourceAsStream("/icons/MontyPythonFoot128.gif"));
		shell.setImage(icon); 
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(498, 278);
		shell.setText("Footprinter");
		
		text_1 = new Text(shell, SWT.BORDER);
		text_1.setBounds(107, 40, 276, 22);
		text_2 = new Text(shell, SWT.BORDER);
		text_2.setBounds(107, 70, 276, 22);
		
		final Button btnBrowse = new Button(shell, SWT.PUSH);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
				public void widgetSelected(SelectionEvent e) {
					FileDialog dlg = new FileDialog(btnBrowse.getShell(), SWT.OPEN);
					dlg.setText("Open");
					dlg.setFilterPath("C:/");
					String[] filterExt = { "*.txt", "*.csv", "*.prn", "*.*" };
					dlg.setFilterExtensions(filterExt);
					String path = dlg.open();
					if (path == null) return;
					text_1.setText(path);
			}
		});
		btnBrowse.setBounds(389, 38, 70, 27);
		btnBrowse.setText("Browse");
		
		Label lblInputFile = new Label(shell, SWT.HORIZONTAL | SWT.CENTER);
		lblInputFile.setBounds(31, 43, 70, 17);
		lblInputFile.setText("Input File:");
		
		btnBrowse2 = new Button(shell, SWT.NONE);
		btnBrowse2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dlg = new FileDialog(btnBrowse2.getShell(), SWT.SAVE);
				dlg.setText("Open");
				String[] filterExt = { "*.kml", "*.*" };
				dlg.setFilterExtensions(filterExt);
				String path = dlg.open();
				if (path == null) return;
				text_2.setText(path);
			}
		});
		btnBrowse2.setBounds(389, 68, 70, 27);
		btnBrowse2.setText("Browse");
		
		lblOutputKML = new Label(shell, SWT.NONE);
		lblOutputKML.setBounds(24, 73, 84, 17);
		lblOutputKML.setText("Output KML:");
		
		Button btnWriteFile = new Button(shell, SWT.NONE);
		btnWriteFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				String EPSGcode = text_4.getText();
				double groundHeight = Double.parseDouble(text_3.getText());
				File outputFile = new File(text_2.getText());
				File inputFile = new File(text_1.getText());
				try {
					Scanner input = new Scanner(inputFile);
				PrintWriter output;
				try {
					output = new PrintWriter(outputFile);
					output.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
								 "\n<kml xmlns=\"http://www.opengis.net/kml/2.2\"" +
								 " xmlns:gx=\"http://www.google.com/kml/ext/2.2\"" + 
								 " xmlns:kml=\"http://www.opengis.net/kml/2.2\"" +
								 " xmlns:atom=\"http://www.w3.org/2005/Atom\">\n" +
								 "<Document>\n" +
								 "	<name>FOOTPRINTS</name>\n" + 
								 "	<open>1</open>\n" +  
								 "	<Style id=\"sh_ylw-pushpin\">\n" + 
								 "		<IconStyle>\n" +
								 "			<scale>1.3</scale>\n" + 
								 "			<Icon>\n"+
								 "				<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>\n" + 
								 "			</Icon>\n" + 
								 "			<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>\n" + 
								 "		</IconStyle>\n" + 
								 "		<LineStyle>\n" + 
								 "			<color>87000000</color>\n" + 
								 "			<width>1.9</width>\n" + 
								 "		</LineStyle>\n" + 
								 "		<PolyStyle>\n" + 
								 "			<color>7300aa00</color>\n" + 
								 "			<outline>0</outline>\n" + 
								 "		</PolyStyle>\n" + 
								 "	</Style>\n" + 
								 "	<Style id=\"sn_ylw-pushpin\">\n" + 
								 "		<IconStyle>\n" + 
								 "			<scale>1.1</scale>\n" + 
								 "			<Icon>\n" + 
								 "				<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>\n" + 
								 "			</Icon>\n" + 
								 "			<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>\n" + 
								 "		</IconStyle>\n" + 
								 "		<LineStyle>\n" + 
								 "			<color>87000000</color>\n" + 
								 "			<width>1.9</width>\n" + 
								 "		</LineStyle>\n" + 
								 "		<PolyStyle>\n" + 
								 "			<color>7300aa00</color>\n" + 
								 "			<outline>0</outline>\n" + 
								 "		</PolyStyle>\n" + 
								 "	</Style>\n" + 
								 "	<StyleMap id=\"msn_ylw-pushpin0\">\n" + 
								 "		<Pair>\n" + 
								 "			<key>normal</key>\n" + 
								 "			<styleUrl>#sn_ylw-pushpin</styleUrl>\n" + 
								 "		</Pair>\n" + 
								 "		<Pair>\n" + 
								 "			<key>highlight</key>\n" + 
								 "			<styleUrl>#sh_ylw-pushpin</styleUrl>\n" + 
								 "		</Pair>\n" + 
								 "	</StyleMap>)\n");
					while (input.hasNext()) {
						Photo photo = new Photo(input.nextLine(), groundHeight, EPSGcode);
						photo.computeCornerCoordinates();
						output.print(photo.toString());
					}
					input.close();
					output.print("</Document>\n</kml>");
					output.close();
					MessageBox success = new MessageBox(shell);
					success.setMessage("Success!");
					success.open();
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnWriteFile.setBounds(201, 179, 82, 27);
		btnWriteFile.setText("Write File");
		
		text_3 = new Text(shell, SWT.BORDER);
		text_3.setBounds(136, 130, 84, 21);
		
		Label lblCrsEpsgCode = new Label(shell, SWT.NONE);
		lblCrsEpsgCode.setBounds(246, 133, 84, 15);
		lblCrsEpsgCode.setText("CRS EPSG Code:");
		
		text_4 = new Text(shell, SWT.BORDER);
		text_4.setBounds(336, 129, 76, 22);
		
		lblGroundHeight = new Label(shell, SWT.NONE);
		lblGroundHeight.setBounds(46, 133, 84, 15);
		lblGroundHeight.setText("Ground Height: ");
		
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		if (!PlatformUI.isWorkbenchRunning())
			return;
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}
}
