/**
 * @file CBTree.java
 * @authors Leah Talkov, Jerry Tsui
 * @date 8/15/2016
 * Creates a Checkbox tree for the UserView interface. Every unique folder is shown 
 * in a tree format, and within each folder are the associated keywords. 
 * Tree automatically updates whenever an administrator makes a change to the data.
 */

package interfaceTest;
import javax.swing.AbstractCellEditor;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.EventObject;


/**
 * CheckBox Tree that displays a checkbox with an associated label
 * in a "tree file explorer" format 
 */
@SuppressWarnings("serial")
class CBTree extends JTree {
	   
	/**
	 * Creates a CheckBoxTree object
	 */
	public CBTree() {
	      setCellRenderer(new CheckBoxTreeNodeRenderer());
	      setCellEditor(new CheckBoxTreeNodeEditor(this));
	      setEditable(true);
	}
	
	/**
	 * Recursively expands the CheckBoxTree 
	 * @param parent The TreePath of the root node of the CheckBoxTree
	 */
	public void expandAll(TreePath parent) {
	    TreeNode node = (TreeNode) parent.getLastPathComponent();
	    if (node.getChildCount() >= 0) {
	      for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
	        TreeNode n = (TreeNode) e.nextElement();
	        TreePath path = parent.pathByAddingChild(n);
	        expandAll(path);
	      }
	    }
	    this.expandPath(parent);
	} 
}
	
	 
/**
 * 
 * TreeNodeCheckBox object inserted into a CheckBoxTree
 * You can add TreeNodeCheckBoxes to DefaultTreeModels just like
 * any other Tree Node. 
 */
@SuppressWarnings("serial")
class TreeNodeCheckBox extends JCheckBox {
 
   /**
   * Generates a TreeNodeCheckBox
   */
   public TreeNodeCheckBox() {
      this("", false);
   }
 
   /**
    *  Generates a TreeNodeCheckBox
	 * @param text Label associated with the TreeNodeCheckBox
	 * @param selected boolean value determining whether checkbox is selected or not
	 */
	public TreeNodeCheckBox(final String text, final boolean selected) {
      this(text, null, selected);
   }
 
   /**
    *  Generates a TreeNodeCheckBox
	 * @param text Label associated with the TreeNodeCheckBox
	 * @param icon Icon associated with TreeNodeCheckBox
	 * @param selected boolean value determining whether checkbox is selected or not
	 */
	public TreeNodeCheckBox(final String text, final Icon icon, final boolean selected) {
      super(text, icon, selected);
      setMargin(new Insets(1, 1, 1, 1));
   }
}
 
/** Renderer for the CheckBoxTree */
class CheckBoxTreeNodeRenderer implements TreeCellRenderer {
   Color selectionBorderColor, selectionForeground, selectionBackground, textForeground, textBackground;
   private TreeNodeCheckBox checkBoxRenderer = new TreeNodeCheckBox();
   private DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();
 
   /** Creates a CheckBoxTreeNodeRenderer object */
   public CheckBoxTreeNodeRenderer() {
      Font fontValue;
      fontValue = UIManager.getFont("Tree.font");
      if (fontValue != null) {
         checkBoxRenderer.setFont(fontValue);
      }
      Boolean booleanValue = (Boolean) UIManager.get("Tree.drawsFocusBorderAroundIcon");
      checkBoxRenderer.setFocusPainted((booleanValue != null) && (booleanValue.booleanValue()));
 
      selectionBorderColor = UIManager.getColor("Tree.selectionBorderColor");
      selectionForeground = UIManager.getColor("Tree.selectionForeground");
      selectionBackground = UIManager.getColor("Tree.selectionBackground");
      textForeground = UIManager.getColor("Tree.textForeground");
      textBackground = UIManager.getColor("Tree.textBackground");
   }
  
   /**
    * Gets the CheckBoxRenderer
    * @return the TreeNodeCheckBox in the checkBoxRenderer field
    */
   public TreeNodeCheckBox getCheckBoxRenderer() {
      return checkBoxRenderer;
   }
 
   /* (non-Javadoc)
 * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
 */
public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
      Component component;
      if (leaf) {
         String stringValue = tree.convertValueToText(value, selected, expanded, leaf, row, false);
         checkBoxRenderer.setText(stringValue);
         checkBoxRenderer.setSelected(false);
         checkBoxRenderer.setEnabled(tree.isEnabled());
         if (selected) {
            checkBoxRenderer.setBorder(new LineBorder(selectionBorderColor));
            checkBoxRenderer.setForeground(selectionForeground);
            checkBoxRenderer.setBackground(selectionBackground);
         } else {
            checkBoxRenderer.setBorder(null);
            checkBoxRenderer.setForeground(textForeground);
            checkBoxRenderer.setBackground(textBackground);
         }
         if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
            if (userObject instanceof TreeNodeCheckBox) {
               TreeNodeCheckBox node = (TreeNodeCheckBox) userObject;
               checkBoxRenderer.setText(node.getText());
               checkBoxRenderer.setSelected(node.isSelected());
            }
         }
         component = checkBoxRenderer;
      } else {
         component = defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
      }
      return component;
   }
}
 
/** Editor for the CheckBoxTree */
@SuppressWarnings("serial")
class CheckBoxTreeNodeEditor extends AbstractCellEditor implements TreeCellEditor {
   CheckBoxTreeNodeRenderer renderer = new CheckBoxTreeNodeRenderer();
   JTree tree;
 
   /**
    * Creates a CheckBoxTreeNodeEditor
    * @param tree JTree to create an editor for
    */
   public CheckBoxTreeNodeEditor(JTree tree) {
      this.tree = tree;
   }
 
	   /* (non-Javadoc)
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
   public Object getCellEditorValue() {
      TreeNodeCheckBox checkBox = renderer.getCheckBoxRenderer();
      TreeNodeCheckBox checkBoxNode = new TreeNodeCheckBox(checkBox.getText(), checkBox.isSelected());
      System.out.println(checkBoxNode.isSelected() + " " + checkBoxNode.getText());
      return checkBoxNode;
   }
 
	   /* (non-Javadoc)
	 * @see javax.swing.AbstractCellEditor#isCellEditable(java.util.EventObject)
	 */
	public boolean isCellEditable(EventObject event) {
      boolean editable = false;
      if (event instanceof MouseEvent) {
         MouseEvent mouseEvent = (MouseEvent) event;
         TreePath path = tree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
         if (path != null) {
            Object node = path.getLastPathComponent();
            if ((node != null) && (node instanceof DefaultMutableTreeNode)) {
               DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
               editable = treeNode.isLeaf();
            }
         }
      }
      return editable;
   }
 
   /* (non-Javadoc)
 * @see javax.swing.tree.TreeCellEditor#getTreeCellEditorComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int)
 */
	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row) {
	      Component editor = renderer.getTreeCellRendererComponent(tree, value, true, expanded, leaf, row, true);
	      if (editor instanceof TreeNodeCheckBox) {
	         ((TreeNodeCheckBox) editor).addItemListener(new ItemListener() {
	            public void itemStateChanged(ItemEvent itemEvent) {
	               if (stopCellEditing()) {
	                  fireEditingStopped();
	               }
	            }
	         });
	      }
	      return editor;
	   }
}
