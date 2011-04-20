package epics.archiveviewer.xal.controller.print;

import epics.archiveviewer.base.AVBaseConstants;

import java.awt.*;
import java.awt.print.*;
import javax.swing.*;
import javax.print.*;

/** A simple utility class that lets you very simply print
 *  an arbitrary component. Just pass the component to the
 *  PrintController.printComponent. The component you want to
 *  print doesn't need a print method and doesn't have to
 *  implement any interface or do anything special at all.
 *  <P>
 *  If you are going to be printing many times, it is marginally more 
 *  efficient to first do the following:
 *  <PRE>
 *    PrintController printHelper = new PrintContoller(theComponent);
 *  </PRE>
 *  then later do printHelper.print(). But this is a very tiny
 *  difference, so in most cases just do the simpler
 *  PrintController.printComponent(componentToBePrinted).
 *
 *  Bob Hall, 3/4/2010 Adapted from Marty Hall, http://www.apl.jhu.edu/~hall/java/
 *      PrintUtilities.java.
 *      See "Printing Swing Components in Java 1.2" tutorial:
 *      http://www.apl.jhu.edu/~hall/java/Swing-Tutorial/Swing-Tutorial-Printing.html
 *
 *      Created so that the Archive Viewer default printer can be set to
 *      a specified value (e.g., physics-lclslog).  Used instead of the
 *      XAL source PrintManager.java.
 *
 *  Mods:
 *      08-Feb-2011, Bob Hall
 *         Modified to set the specified default print service to the Java
 *         system property "defaultprinter" if it was set.  This allows
 *         different shell startup scripts (e.g., lclsarch, facetarch)
 *         to supply different default printers. 
 */

public class PrintController implements Printable {
  private Component componentToBePrinted;

  public static void printComponent(Component c) {
    new PrintController(c).print();
  }
  
  public PrintController(Component componentToBePrinted) {
    this.componentToBePrinted = componentToBePrinted;
  }

  public void print() {
    PrinterJob printJob = PrinterJob.getPrinterJob();

    /*
     * Set the default printer to the Java system property
     * "defaultprinter" if it was set.  Otherwise, set the
     * default printer to the constant value
     * AVBaseConstants.DEFAULT_PRINTER. 
     */
    String defaultPrinter = System.getProperty("defaultprinter",
        AVBaseConstants.DEFAULT_PRINTER);

    /*
     * Loop through all of the print services.  If a specified
     * default print service is found (e.g., "physics-lclslog"),
     * set the print service to this print service.  If the
     * default print service is not found, the print service
     * is not set and the default print service remains the
     * first print service in /etc/printcap for a CUPS system.
     */
    PrintService[] services = printJob.lookupPrintServices();
    for (int i = 0; i < services.length; i++)
    {
        if (services[i].getName().equals(defaultPrinter))
        {
            System.out.println("setting print service");

            try
            {
                printJob.setPrintService(services[i]);
            }
            catch (PrinterException exception)
            {
                System.err.println("Printing error: " + exception);
            }

            break;
        }
    }

    printJob.setPrintable(this);
    if (printJob.printDialog())
      try {
        printJob.print();
      } catch(PrinterException pe) {
        System.out.println("Error printing: " + pe);
      }
  }

  public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
    if (pageIndex > 0) {
      return(NO_SUCH_PAGE);
    } else {
      Graphics2D g2d = (Graphics2D)g;
      g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

/*
 *    The following scaling method was obtained from the print method
 *    of the Printable getPrintable interface found in
 *    /afs/slac/g/lcls/physics/xal4lcls/gov/sns/application/XalAbstractDocument.java.
 *    It is needed, for example, to shrink the Archive Viewer print image for
 *    proper display in a lclselog Physics E-log image (which otherwise would
 *    be truncated and rotated because it would be too large).
 */
      final Dimension viewSize = componentToBePrinted.getSize();
      final double pageWidth = pageFormat.getImageableWidth();
      final double pageHeight = pageFormat.getImageableHeight();
      final double xScale = pageWidth / viewSize.width;
      final double yScale = pageHeight / viewSize.height;
      final double scale = Math.min(xScale, yScale);
      g2d.scale( scale, scale );

      disableDoubleBuffering(componentToBePrinted);
      componentToBePrinted.paint(g2d);
      enableDoubleBuffering(componentToBePrinted);
      return(PAGE_EXISTS);
    }
  }

  /** The speed and quality of printing suffers dramatically if
   *  any of the containers have double buffering turned on.
   *  So this turns if off globally.
   *  @see enableDoubleBuffering
   */
  public static void disableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(false);
  }

  /** Re-enables double buffering globally. */
  
  public static void enableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(true);
  }
}
