package interfaceTest;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class CheckBoxList {	
	
	static class CheckBoxListItem {
		   private String label;
		   private boolean isSelected = false;
		 
		   /**
		    * Constructs a CheckBoxListItem given a specific label
		 * @param label
		 */
		public CheckBoxListItem(String label) {
		      this.label = label;
		   }
		 
		   /**
		    * Returns the boolean isSelected value of the CheckBoxListItem
		 * @return
		 */
		public boolean isSelected() {
		      return isSelected;
		   }
		 
		   /**
		    * Sets the boolean isSelected value of the CheckBoxListItem
		 * @param isSelected
		 */
		public void setSelected(boolean isSelected) {
		      this.isSelected = isSelected;
		   }
		 
		   /* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
		      return label;
		   }
		}
	
	@SuppressWarnings("serial")
	static class CheckBoxListRenderer extends JCheckBox implements
    ListCellRenderer<CheckBoxListItem> {

		 @Override
		 public Component getListCellRendererComponent(
		       JList<? extends CheckBoxListItem> list, CheckBoxListItem value,
		       int index, boolean isSelected, boolean cellHasFocus) {
		    setEnabled(list.isEnabled());
		    setSelected(value.isSelected());
		    setFont(list.getFont());
		    setBackground(list.getBackground());
		    setForeground(list.getForeground());
		    setText(value.toString());
		    return this;
		 }
	}
}
