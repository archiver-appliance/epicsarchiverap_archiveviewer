package org.jfree.chart;

/**************************************************************

 Abs:     Archive Viewer Choose Print Service

 Class:   ChoosePrintService

 Name:    ChoosePrintService.java 

 Package: org.jfree.chart 

 Rem:     This class displays a dialog box with a pull-down
          menu that allows the selection of a print service
          from the list of local printers associated with
          Java system property "localprinters".  This Java
          system property was set through an environment
          variable when the application was invoked.  For
          example:

             java -Dlocalprinters=$EPICS_PR_LIST

          This class is invoked from class ChartPanel.  The
          ChoosePrintService class was created at SLAC for
          the LCLS project to obtain a selected print service
          because the standard java.awt.print package PrinterJob
          method printDialog shows all of the available printers
          when run on a Linux system.  Instead of displaying
          the long list of available printers at SLAC, it is
          desired to display the much shorter list of printers
          that are used for the SLAC LCLS project, which are
          specified in the environment variable string
          EPICS_PR_LIST.

 Auth:    29-Jan-2007, Bob Hall (rdh)
 Rev:     dd-mmm-yyyy, Reviewer's Name (user)

 -----------------------------------------------------------
 Mod:  (latest to oldest)


*************************************************************/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.print.*;

public class ChoosePrintService
{
    // Contains the list of valid printer choices available to the user.
    private String[] printerChoices;

    public ChoosePrintService()
    {
    }

    /**
     * Get the print service associated with the selected printer.
     *
     * @return a print service 
     */
    public PrintService getPrintService()
    {
        int i;
        boolean found;

        String curPrinterChoice;

        // 
        // Parse the Java system property "localprinters", which contains
        // a string containing the local printer names separated by colons.
        // 
        String[] localPrinters = parseLocalPrintersString();

        //
        // Get the PrintService objects associated with the valid print
        // services in the parsed Java system property "localprinters" string.
        //
        PrintService[] printerChoicesServices = getPrinterChoicesServices(localPrinters);

        //
        // Get the user selected print service name choosen by the user in
        // a dialog box pull-down menu.
        //
        String userSelection = getUserSelection();
 
        if ((userSelection != null) && (userSelection.length() > 0))
        {
            // System.out.println("selection = " + userSelection);

            //
            // The user selected a print service.  Find the element
            // in the array of valid printer choices that matches
            // this selected print service name and return the
            // associated element in the array of valid printer
            // choice PrintService objects.
            //
            found = false;
            i = 0;
            while ((i < printerChoices.length) && (!found))
            {
                curPrinterChoice = printerChoices[i];
                if (curPrinterChoice.equals(userSelection))
                {
                    found = true;
                }
                else
                {
                    i++;
                }
            }

            if (found)
            {
                return printerChoicesServices[i]; 
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    //
    // Private method that parses the Java system property "localprinters"
    // string, which contains the list of local printers (separated by
    // colons).
    //
    private String[] parseLocalPrintersString()
    {
        String localPrintersStr = System.getProperty("localprinters");

        String patternStr = ":";

        String[] fields = localPrintersStr.split(patternStr);

        return fields;
    }

    //
    // Private method that returns an array of PrintService objects
    // associated with the valid print services contained in the parsed
    // list of local printers.  It also sets the instance variable
    // array "printerChoices", which contains the array of valid
    // print service names associated with the returned array of
    // PrintService objects.
    //
    private PrintService[] getPrinterChoicesServices(String[] localPrinters)
    {
        int i;
        int j;
        int numLocalPrinters;
        int numPrinterChoices;

        boolean found;

        String curLocalPrinterName;
        String curServiceName;

        PrintService[] services = null;

        PrintService curPrinterService = null;

        //
        // Get the list of all printer services.
        //
        DocFlavor theDocFlavor = DocFlavor.INPUT_STREAM.POSTSCRIPT;

        javax.print.attribute.PrintRequestAttributeSet jobAttrs =
            new javax.print.attribute.HashPrintRequestAttributeSet();

        services = PrintServiceLookup.lookupPrintServices(theDocFlavor, jobAttrs);

        numLocalPrinters = localPrinters.length; 

        // System.out.println("numLocalPrinters = " + numLocalPrinters);

        //
        // Create an array of PrintService objects for the local printers
        // specified in the Java system property "localprinters".  It is
        // possible that one or more of the names of these print services
        // are not valid print services, in which case the associated
        // PrintService object for each invalid print service will be set
        // to null.
        //
        PrintService[] localPrinterServices = new PrintService[numLocalPrinters];

        //
        // Loop through all of the local printer names.  For each local
        // printer name, attempt to find a PrintService object with the
        // same name.  If found, set the corresponding localPrinterServices
        // element to the associated PrintService object.  If not found,
        // then the local print service name is invalid so set the
        // correspond localPrinterServices element to null.
        //
        numPrinterChoices = 0;
        for (i = 0; i < numLocalPrinters; i++)
        {
            curLocalPrinterName = localPrinters[i];

            found = false;
            j = 0;
            while ((j < services.length) && (!found))
            {
                curServiceName = services[j].getName();

                if (curServiceName.equals(curLocalPrinterName))
                {
                    found = true;
                }
                else
                {
                    j++;
                }
            }

            if (found)
            {
                numPrinterChoices++;
                localPrinterServices[i] = services[j];
            }
            else
            {
                localPrinterServices[i] = null;
            }
        }

        //
        //  Create an array of valid local printer service PrintService objects
        //  and an array of corresponding print service names.  Loop through
        //  all local printer service PrintService objects and set the
        //  elements of the valid local print service PrintService objects
        //  array and the elements of the corresponding print service names
        //  to those for the valid local printer services.
        //  
        PrintService[] printerChoicesServices = new PrintService[numPrinterChoices];

        printerChoices = new String[numPrinterChoices];

        j = 0;
        for (i = 0; i < numLocalPrinters; i++)
        {
            if (localPrinterServices[i] != null)
            {
                printerChoicesServices[j] = localPrinterServices[i];
                printerChoices[j] = localPrinters[i];  
                j++;
            }
        }

        return printerChoicesServices;
    }

    //
    //  This private method displays a dialog box containing a pull-down
    //  menu of all valid local printer service names and returns the
    //  selected name.  If the user selected the "Cancel" button or
    //  the dialog box window was closed, a null String will be returned.
    //
    private String getUserSelection()
    {
        String userSelection = (String)JOptionPane.showInputDialog(
            null,
            "Select Print Service\n"
            + "and click OK to print.",
            "Print",
            JOptionPane.PLAIN_MESSAGE,
            null,
            printerChoices,
            printerChoices[0]);

        return userSelection;
    }
}
