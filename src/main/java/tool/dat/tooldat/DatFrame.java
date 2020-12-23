/**
 *
 */
package tool.dat.tooldat;

import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import tool.dat.tooldat.exception.FilePathException;

/**
 * @author balabala1
 *
 */
public class DatFrame extends JFrame {

	Panel panOne;
	JButton showFileDialogButton;
	JLabel lblOne;

	Panel titlePan;
	JLabel titleLab;
	static File directory;

	Panel areaPanel;

	JFrame f;

	/**
	 *
	 */
	public DatFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setResizable(false);
		setSize(500, 300);
		setLocationRelativeTo(null);
		setLayout(new GridLayout(3,1));
		//setDefaultLookAndFeelDecorated(true);

		panOne = new Panel();
		titlePan = new Panel();
		areaPanel = new Panel();

		titleLab = new JLabel("DAT Check List Execution");
		lblOne = new JLabel("");

		showFileDialogButton = new JButton("Select Dat Directory");
//		showFileDialogButton.addActionListener(openAction());

		titlePan.add(titleLab);
		panOne.add(showFileDialogButton);
		panOne.add(lblOne);
		add(titlePan);
		add(panOne);
		add(areaPanel);
		showFileChooser();
	}

	private void showFileChooser(){

		final JFileChooser  fileDialog = new JFileChooser();
		fileDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		ApplicationContext ctx =
		         new AnnotationConfigApplicationContext(DatConfig.class);

		showFileDialogButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = fileDialog.showOpenDialog(f);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					directory = fileDialog.getSelectedFile();
//					String path = reader.readLine();
					File file = new File(directory.getAbsolutePath());
					DatReader datRead = ctx.getBean(DatReader.class);
					try {
						datRead.setFilePath(file);
					} catch (FilePathException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					try {
						//lblOne.setText("Executing...");
						datRead.readFile();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					datRead.generateLog();
					//lblOne.setText("Finished..");
				}
				else{
				}
			}
		});
		setVisible(true);
	}

	public static void main(String a[]){
		new DatFrame();
	}

}
