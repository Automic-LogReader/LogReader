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
    
    public TableMouseListener(JTable table) {
        this.table = table;
    }
    
    public static int getCurrentRow(){
    	return TableMouseListener.currentRow;
    }
   
     
    @Override
    public void mousePressed(MouseEvent event) {
        // selects the row at which point the mouse is clicked
        Point point = event.getPoint();
        currentRow = table.rowAtPoint(point);
        currentColumn = table.columnAtPoint(point);
        table.setRowSelectionInterval(currentRow, currentRow);
        table.setColumnSelectionInterval(currentColumn, currentColumn);
    }

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
    
    public static void openURI() throws URISyntaxException{
    	Object valueInCell = table.getValueAt(currentRow, currentColumn);
    	if (valueInCell instanceof String && currentColumn == 4){
    		String uriString = (String) valueInCell;
    		System.out.println(uriString);
    		URI uri = new URI("http://google.com");
    		openURIHelper(uri);
    	}
    }
    
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
