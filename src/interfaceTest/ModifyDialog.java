package interfaceTest;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.Component;
import javax.swing.Box;
import java.awt.Dimension;

public class ModifyDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField keywordText;
	private JTextField errorText;
	private JTextField solutionText;
	private int selectedRow;

	/**
	 * Create the dialog.
	 */
	public ModifyDialog(String keyWord, String errorMessage, String solutionMessage, DataController dc, int row) {
		
		selectedRow = row;
		
		setBounds(200, 200, 450, 200);
		getContentPane().setLayout(new BorderLayout());
		{
			Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
			getContentPane().add(rigidArea, BorderLayout.WEST);
		}
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel);
			panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
			{
				JLabel lblNewLabel = new JLabel("Keyword:                 ");
				panel.add(lblNewLabel);
			}
			{
				Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
				panel.add(rigidArea);
			}
			{
				keywordText = new JTextField();
				panel.add(keywordText);
				keywordText.setColumns(10);
				keywordText.setText(keyWord);
			}
			{
				Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
				panel.add(rigidArea);
			}
		}
		{
			Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
			contentPanel.add(rigidArea);
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel);
			panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
			{
				JLabel lblNewLabel_1 = new JLabel("Error Message:      ");
				panel.add(lblNewLabel_1);
			}
			{
				Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
				panel.add(rigidArea);
			}
			{
				errorText = new JTextField();
				panel.add(errorText);
				errorText.setText(errorMessage);
				errorText.setColumns(10);
			}
			{
				Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
				panel.add(rigidArea);
			}
		}
		{
			Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
			contentPanel.add(rigidArea);
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel);
			panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
			{
				JLabel lblNewLabel_2 = new JLabel("Solution Message:");
				panel.add(lblNewLabel_2);
			}
			{
				Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
				panel.add(rigidArea);
			}
			{
				solutionText = new JTextField();
				panel.add(solutionText);
				solutionText.setColumns(10);
				solutionText.setText(solutionMessage);
			}
			{
				Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
				panel.add(rigidArea);
			}
		}
		{
			Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
			getContentPane().add(rigidArea, BorderLayout.NORTH);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Modify");
				okButton.addActionListener(e ->{
				if (keywordText.getText().equals("") || errorText.getText().equals("") || solutionText.getText().equals(""))
					JOptionPane.showMessageDialog(null, "All entries must be filled");
				else
				{
					try {
						dc.modifyData(keywordText.getText(), errorText.getText(), solutionText.getText(), "MODIFY", selectedRow);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					this.setVisible(false);
				}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}

		}
	}
}
