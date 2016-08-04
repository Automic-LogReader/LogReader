package interfaceTest;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JComboBox;
import javax.swing.MutableComboBoxModel;

public class LogicalComboBox extends JComboBox<String> {
	
	LogicalComboBox(int option, UserView view){
		super();
		MutableComboBoxModel<String> model = (MutableComboBoxModel<String>)this.getModel();
		this.setOpaque(true);
		this.setBackground(Color.WHITE);
		switch (option){
		case 1:
			this.setPreferredSize(new Dimension(80, 20));
			model.addElement("AND");
			model.addElement("OR");
			model.addElement("AND NOT");
			break;
		case 2:
			this.setPreferredSize(new Dimension(100, 20));
			for (String s: view.comboBoxKeyWords){
				model.addElement(s);
			}
			break;
		case 3:
			this.setPreferredSize(new Dimension(80, 20));
			model.addElement("AND");
			model.addElement("AND NOT");
			break;
		}
	
		this.setSelectedIndex(-1);
	}
}
