/**
 * @file TableMouseListener.java
 * @authors Leah Talkov, Jerry Tsui
 * @date 8/3/2016
 * TableMoustListner extends from MouseAdapter and is intended to help
 * the JTable in UserView interact with the popup menu in UserView.
 */
package interfaceTest;

import java.awt.Desktop;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JTable;

public class TableMouseListener extends MouseAdapter {
	private static JTable table;
	private static int currentRow;
	private static int currentColumn;
    
    /**
     * Constructor
     * @param table JTable to attach to the TableMouseListener
     */
    public TableMouseListener(JTable table) {
        this.table = table;
    }
    /**
     * Returns the table's currentRow index
     * @return currentRow index
     */
    public static int getCurrentRow(){
    	return TableMouseListener.currentRow;
    }
   
     
    /* (non-Javadoc)
     * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent event) {
        // selects the row at which point the mouse is clicked
        Point point = event.getPoint();
        currentRow = table.rowAtPoint(point);
        currentColumn = table.columnAtPoint(point);
        table.setRowSelectionInterval(currentRow, currentRow);
        table.setColumnSelectionInterval(currentColumn, currentColumn);
    }
    
    /**
     * Copies the value of the selected cell within the JTable to the clipboard
     */
    static void copyCellValueToClipBoard(){
    	Object valueInCell = table.getValueAt(currentRow, currentColumn);
    	
    	if (valueInCell instanceof String){
    		String myString = (String) valueInCell;
    		StringSelection stringSelection = new StringSelection(myString);
        	Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        	clpbrd.setContents(stringSelection, null);
    	}
    	else return;
    }
    
    /**
     * Opens the URI that is associated with the solution.
     * @throws URISyntaxException if URI is not formatted properly
     */
    public static void openURI() throws URISyntaxException{
    	Object valueInCell = table.getValueAt(currentRow, currentColumn);
    	if (valueInCell instanceof String && currentColumn == 4){
    		String uriString = (String) valueInCell;
    		System.out.println(uriString);
    		URI uri = new URI("http://google.com");
    		openURIHelper(uri);
    	}
    }
    
    /**
     * Helper function for openURI() 
     * @param uri URI to open
     */
    private static void openURIHelper(URI uri){
    	if (Desktop.isDesktopSupported()) {
    		try {
    			Desktop.getDesktop().browse(uri);
    		} catch (IOException e){
    			e.printStackTrace();
    		}
    	}
    }
}
