/**
 * @file ProgressDialog.java
 * @authors Leah Talkov, Jerry Tsui
 * @date 8/4/2016
 * Creates a JDialog that contains a progress bar, as well as text
 * to show the percentage the bar is currently at. The progress bar
 * serves to show the user how far along the parsing progress is.
 */

package interfaceTest;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.BoxLayout;
import javax.swing.JProgressBar;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;

@SuppressWarnings("serial")
public class ProgressDialog extends JDialog {

	/**The main panel that contains the progress bar and text*/
	private final JPanel pnlMain = new JPanel();
	/**Simple progressbar that progresses from 0% to 100%*/
	private JProgressBar progressBar;
	private JLabel lblTop;
	private JButton btnExit;
	/**A button that allows the user to export the results to a csv file*/
	private JButton btnExport;
	private JLabel lblBottom;
	
	/**
	 * Contains a progress bar that allows the user to follow the parsing progress
	 * @param file The file that is being parsed
	 * @param view The UserView that is associated with this object
	 */
	public ProgressDialog(File file, UserView view) {
		prepareGUI(file, view);
	}

	/**
	 * Updates the progress of the ProgressDialog to reflect
	 * how far the parsing progress is. 
	 * @param i A new value to set the progress bar to
	 */
	void updateProgress(int i){
		progressBar.setValue(i);
		lblTop.setText("Parsing through file..." + i + "% complete");
	}
	
	/**
	 * Called when the parsing process has been completed. The progress 
	 * bar value is set to 100, the JDialog displays that the parsing 
	 * process has finished, and also displays the number of errors that
	 * were found. The export button also becomes visible so that
	 * the user can export the results to a CSV file. 
	 * @param numErrors The number of errors found during the parsing process
	 */
	void doneParse(int numErrors){
		progressBar.setValue(100);
		lblTop.setText("Parsing through file... done!");
		lblBottom.setText("Number of errors found: " + numErrors); 
		btnExport.setVisible(true);
		btnExit.setVisible(true);
	}
	
	/**
	 * Prepares the GUI for the ProgressDialog class.
	 * @param file The filepath for the file being parsed
	 * @param view The UserView object associated with this ProgressDialog
	 */
	void prepareGUI(File file, UserView view){
		//this.setModal(true);
		setBounds(200, 200, 300, 200);
		setLocationRelativeTo(null);
	
		getContentPane().add(pnlMain, BorderLayout.CENTER);
		pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
		pnlMain.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
		
		progressBar = new JProgressBar(0, 100);
		progressBar.setBorder(new EmptyBorder(10, 10, 10, 10));
		progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
		pnlMain.add(progressBar);
		
		lblTop = new JLabel("");
		lblTop.setBorder(new EmptyBorder(5, 0, 5, 0));
		lblTop.setAlignmentX(Component.CENTER_ALIGNMENT);
		pnlMain.add(lblTop);
		
		lblBottom = new JLabel("");
		lblBottom.setBorder(new EmptyBorder(0, 0, 5, 0));
		lblBottom.setAlignmentX(Component.CENTER_ALIGNMENT);
		pnlMain.add(lblBottom); 
		
		
		btnExit = new JButton("Close");
		btnExit.setVisible(false);
		btnExit.addActionListener(e -> {
			view.btnSubmit.setEnabled(true);
			this.setVisible(false);
		});
		btnExit.setAlignmentX(Component.CENTER_ALIGNMENT);
		pnlMain.add(btnExit);
		
		pnlMain.add(Box.createRigidArea(new Dimension(0, 10)));
		
		btnExport = new JButton("Export Results");
		btnExport.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser(){
			    @Override
			    public void approveSelection(){
			        File f = getSelectedFile();
			        if(f.exists() && getDialogType() == SAVE_DIALOG){
			            int result = JOptionPane.showConfirmDialog(this,"The file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
			            switch(result){
			                case JOptionPane.YES_OPTION:
			                    super.approveSelection();
			                    return;
			                case JOptionPane.NO_OPTION:
			                    return;
			                case JOptionPane.CLOSED_OPTION:
			                    return;
			                case JOptionPane.CANCEL_OPTION:
			                    cancelSelection();
			                    return;
			            }
			        }
			        super.approveSelection();
			    }
			};
			chooser.setDialogTitle("Export To");
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
			chooser.setSelectedFile(new File("Error_Log_" + timeStamp + ".csv"));
		    FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "CSV files (*csv)", "csv");
		    chooser.setFileFilter(filter);
		    chooser.setAcceptAllFileFilterUsed(false);
		    int returnVal = chooser.showSaveDialog(getParent());
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    	try {
		    		CSVFileWriter CSVWriter = new CSVFileWriter(view);
		    		CSVWriter.writeTo(chooser.getSelectedFile().getName());
		    		
				} catch (Exception e1) {
					e1.printStackTrace();
				}
		    }
		});
		btnExport.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnExport.setVisible(false);
		pnlMain.add(btnExport);
		
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				view.btnSubmit.setEnabled(true);
			}
		});
	}
}
