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

	private final JPanel pnlMain = new JPanel();
	private JProgressBar progressBar;
	private JLabel lblTop;
	private JButton btnExit;
	private JButton btnExport;
	private JLabel lblBottom;
	
	public ProgressDialog(File file, UserView view) {
		prepareGUI(file, view);
	}

	
	void updateProgress(int i){
		progressBar.setValue(i);
		lblTop.setText("Parsing through file..." + i + "% complete");
	}
	
	void doneParse(int numErrors){
		progressBar.setValue(100);
		lblTop.setText("Parsing through file... done!");
		lblBottom.setText("Number of errors found: " + numErrors); 
		btnExport.setVisible(true);
		btnExit.setVisible(true);
	}
	
	void prepareGUI(File file, UserView view){
		setBounds(200, 200, 300, 200);
		setLocationRelativeTo(null);
	
		getContentPane().add(pnlMain, BorderLayout.CENTER);
		pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
		pnlMain.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
		
		progressBar = new JProgressBar(0, 100);
		//progressBar = new JProgressBar(0, fileSize);
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
		   // FileNameExtensionFilter filter = new FileNameExtensionFilter("csv");
		    FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "CSV files (*csv)", "csv");
		    chooser.setFileFilter(filter);
		    chooser.setAcceptAllFileFilterUsed(false);
		    int returnVal = chooser.showSaveDialog(getParent());
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    	try {
					//generateCSVFile(chooser.getSelectedFile().getName(), view);
		    		CSVFileWriter CSVWriter = new CSVFileWriter(view);
		    		CSVWriter.writeTo(chooser.getSelectedFile().getName());
		    		
				} catch (Exception e1) {
					// TODO Auto-generated catch block
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
