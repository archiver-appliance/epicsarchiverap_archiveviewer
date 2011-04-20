package epics.archiveviewer.xal.view.timeinput;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


import epics.archiveviewer.base.AVBaseConstants;
import epics.archiveviewer.xal.view.components.AVAbstractPanel;

/**
 * This class is a panel that uses a slider to help user to select an absolute time
 * The slider uses a base 10 log scale. The range of the time is from -1 day to -1000 days
 * Last modified on: 7/25/06
 * @author John Lee
 *
 */
public class SliderTimePanel extends AVAbstractPanel{
	// the date that shows on the panel and used to output to the time axes
    private RequestDate date;
	// used to tell whether changes are from text, combo, or slider
	private boolean fromText = false;
	private boolean fromCombo = false;
	private boolean fromSlider = false;
	
    // shows the absolute time format defined by Archieve viewer
    private JTextField req_time;
	
    private JLabel validation;
    // the hours combo box
	private JComboBox hourBox;
	
	// the minutes combo box
	private JComboBox minBox;

	// the seocnds combo box
	private JComboBox secBox;
	
	private ZoomSlider zoom; 
	//model uses in pair with zoommodel to provide the natural log scale
    private AxisModel model;
    
    //total seconds in one day
    private static final int daysToSeconds = 0x15180;
    
    // used to convert seconds to values that takes in by zoom slider and zoom model
    private static int valueForSeconds(double seconds) {
        return valueForDays(seconds / daysToSeconds);
    }
    // used to convert days to values that takes in by zoom slider and zoom model
    private static int valueForDays(double days) {
        return (int)Math.round((1000 * Math.log(days)));
    }
    // used to convert values from zoom model to seconds that takes in by axis model 
    private static int secondsForValue(double value){
        return daysToSeconds * daysForValue(value);
    }
    // used to convert values from zoom model to days that takes in by axis model
    private static int daysForValue(double value) {
        return (int)Math.round(Math.exp((double)value / 1000));
    }
    /**
     * Creates a new instance of <CODE>AbsoluteTimePanel</CODE>
     * @param initialDate the date the calendar for which is initially displayed
     */
    public SliderTimePanel(Date initialDate) {
    	if (initialDate != null)
    		date = new RequestDate (initialDate);
    	else
    		date = new RequestDate ();
        init();
    }
	/**
	 * Returns the String representation of the time this
	 * TimePanel shows (returns "failed" if <CODE>Exception
	 * </CODE> s occur)
	 * 
	 * @return the String representation of the time this <CODE>
	 *         AbsoluteTimePanel</CODE> shows
	 */
	public String getSelectedTime(){
		try{
			return AVBaseConstants.MAIN_DATE_FORMAT.format(date.getDate());
		}catch (Exception e){
			return "Failed";
		}
	}
	/**
	 * Lays out the components, called by init()
	 */
	protected void addComponents() {
		setLayout(new BorderLayout());
		JPanel plotPanel = new JPanel();
		plotPanel.setLayout(new BoxLayout (plotPanel, BoxLayout.X_AXIS));
        add(plotPanel, "Center");
        
        plotPanel.add(req_time);
        plotPanel.add(validation);
        validation.setAlignmentX(Component.CENTER_ALIGNMENT);
        
		JPanel hourPanel = new JPanel(new GridLayout(0, 1));
		hourPanel.add(new JLabel("Hour"));
		hourPanel.add(this.hourBox);

		JPanel minPanel = new JPanel(new GridLayout(0, 1));
		minPanel.add(new JLabel("Min"));
		minPanel.add(this.minBox);

		JPanel secPanel = new JPanel(new GridLayout(0, 1));
		secPanel.add(new JLabel("Sec"));
		secPanel.add(this.secBox);

		JPanel timePanel = new JPanel(new GridLayout(1, 4));
		timePanel.add(hourPanel);
		timePanel.add(minPanel);
		timePanel.add(secPanel);
		timePanel.setBorder(BorderFactory.createTitledBorder("HourMinSec Panel"));
        add(timePanel, "South");

        add(zoom, "East");   
	}
	/**
	 * Builds components, called by init()
	 */
	protected void createComponents(){

        //create label to show the Archieve Viewer main date format
        req_time = new JTextField (AVBaseConstants.MAIN_DATE_FORMAT.format(date.getDate()));
        req_time.setMaximumSize(new Dimension (250,40));
        req_time.setPreferredSize(new Dimension (250,40));
        req_time.setBorder(BorderFactory.createTitledBorder("Date"));
        req_time.setFont(new Font("Times-Roman", Font.BOLD, 15));
        req_time.setEditable(true);
        
        // create validation
        validation = new JLabel ("Valid", JLabel.RIGHT);
        validation.setForeground(Color.GREEN);
        validation.setMaximumSize(new Dimension (50,40));
        validation.setPreferredSize(new Dimension (50,40));
        
        // create hour, min, sec box
		this.hourBox = new JComboBox();
		this.minBox = new JComboBox();
		this.secBox = new JComboBox();

		for (int i = 0; i < 24; i++){
			this.hourBox.addItem(""+i);
		}
		for (int i = 0; i < 60; i++){
			this.minBox.addItem(""+i);
			this.secBox.addItem(""+i);
		}
		
		Calendar c = Calendar.getInstance();
		c.setTime(date.getDate());
		this.hourBox.setSelectedIndex(c.get(Calendar.HOUR_OF_DAY));
		this.minBox.setSelectedIndex(c.get(Calendar.MINUTE));
		this.secBox.setSelectedIndex(c.get(Calendar.SECOND));
		
		hourBox.setEditable(true);
		minBox.setEditable(true);
		secBox.setEditable(true);

		// create axis model
        Calendar temp = Calendar.getInstance();
        temp.add(Calendar.DATE, -1000);
        int min = (int)(temp.getTimeInMillis()/1000);
        int max = (int)(Calendar.getInstance().getTimeInMillis()/1000);
        model = new AxisModel(min, max);
        model.setExtent((int)(max-date.getSec()));

        zoom = new ZoomSlider(model);
        
        addListeners();
	}
	/**
	 * adds listeners for this class to update internal values
	 *
	 */
	private void addListeners (){
		ActionListener al = new ActionListener () { 
			public void actionPerformed(ActionEvent arg0) {
				if (arg0.getSource() instanceof JComboBox){
					fromCombo = true;

					if (!fromText){
						date.valuesFromPanel(
								((String)hourBox.getSelectedItem()).matches("[0-9]{1,}?") ? 
										(Integer.parseInt(((String)hourBox.getSelectedItem())) <= 24 ? Integer.parseInt(((String)hourBox.getSelectedItem())) : 0) : 0,
								((String)minBox.getSelectedItem()).matches("[0-9]{1,}?") ? 
										(Integer.parseInt(((String)minBox.getSelectedItem())) <= 60 ? Integer.parseInt(((String)minBox.getSelectedItem())) : 0) : 0,
								((String)secBox.getSelectedItem()).matches("[0-9]{1,}?") ? 
										(Integer.parseInt(((String)secBox.getSelectedItem())) <= 60 ? Integer.parseInt(((String)secBox.getSelectedItem())) : 0) : 0);
						req_time.setText(date.getDateString());
					}
					fromCombo = false;
				}
			}
		};
		
		hourBox.addActionListener(al);
		minBox.addActionListener(al);
		secBox.addActionListener(al);
		req_time.getDocument().addDocumentListener(new DocumentListener (){
			public void changedUpdate(DocumentEvent arg0) {}
			public void insertUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
				
				//if the date parsed wasn't the same as the input time... then
				//input time has problem might be typo, so reject
				
				Date d = AVBaseConstants.MAIN_DATE_FORMAT.parse(req_time.getText(), new ParsePosition (0));
				if (d != null){
					Calendar c = Calendar.getInstance();
					c.setTime(d);
					Calendar cb = Calendar.getInstance();
					cb.add(Calendar.YEAR, -16);
					if (c.after(Calendar.getInstance()) || c.before(cb)){
						validation.setText("Invalid");
						validation.setForeground(Color.RED);
						return;
					}
						
					date.valuesFromPanel(d);
//					System.err.println("Date: "+date.getDateString());
					fromText = true;
					if (!fromCombo){
						hourBox.setSelectedItem(""+c.get(Calendar.HOUR_OF_DAY));
						minBox.setSelectedItem(""+c.get(Calendar.MINUTE));
						secBox.setSelectedItem(""+c.get(Calendar.SECOND));
					}
					if (!fromSlider)
						model.setExtent((int)(Calendar.getInstance().getTimeInMillis()/1000-date.getSec())); 
					fromText = false;
					validation.setText("Valid");
					validation.setForeground(Color.GREEN);
				}
				else{
					validation.setText("Invalid");
					validation.setForeground(Color.RED);
				}
			}

			public void removeUpdate(DocumentEvent arg0) {
				insertUpdate (arg0);
			}
		});
        model.addChangeListener(new ChangeListener (){
    	    public void stateChanged(ChangeEvent e){
    	    	fromSlider = true;
    	    	if (!fromText){
                    date.set_req_date(model.getExtent());
                    req_time.setText(date.getDateString());
    	    	}
    	    	fromSlider = false;
    	    }
        });
        zoom.addKeyListener(new KeyListener (){
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
				zoom.setFromKey(true);
			}

			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				zoom.setFromKey(false);
			}

			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
        	
        });
	}
	/**
	 * the slider class that uses axisModel and zoomModel together
	 * to provide the natural log scale
	 * Last modified on: 7/25/06
	 * @author John Lee
	 *
	 */
	private class ZoomSlider extends JSlider{
		/**
		 * Constructor to create a vertical new slider which has 
		 * the label from 1 day to 10 years
		 * @param axisModel
		 */
		ZoomModel zm;
	    public ZoomSlider(AxisModel axisModel) {
	        zm = new ZoomModel(axisModel);
	        this.setModel(zm);
	        Dictionary dict = new Hashtable();
	        
	        // to initialize the label of slider
	        Calendar m1 = Calendar.getInstance();
	        m1.add(Calendar.MONTH, -1);
	        
	        Calendar m3 = Calendar.getInstance();
	        m3.add(Calendar.MONTH, -3);
	        
	        Calendar y1 = Calendar.getInstance();
	        y1.add(Calendar.YEAR, -1);
	        
	        Calendar y3 = Calendar.getInstance();
	        y3.add(Calendar.DATE, -1000);

	        double cur_sec = Calendar.getInstance().getTimeInMillis()/1000.0;
	        dict.put(new Integer(valueForSeconds(cur_sec - y3.getTimeInMillis()/1000.0)), new JLabel("1000 Days"));
	        dict.put(new Integer(valueForSeconds(cur_sec - y1.getTimeInMillis()/1000.0)), new JLabel("1 Year"));
	        dict.put(new Integer(valueForSeconds(cur_sec - m3.getTimeInMillis()/1000.0)), new JLabel("3 Months"));
	        dict.put(new Integer(valueForSeconds(cur_sec - m1.getTimeInMillis()/1000.0)), new JLabel("1 Month"));
	        dict.put(new Integer(valueForDays(7)), new JLabel("1 Week"));
	        dict.put(new Integer(valueForDays(1)), new JLabel("1 Day"));
	        setLabelTable(dict);
	        
	        //set other property of this slider
	        setOrientation(1);
	        setPaintLabels(true);
	        setBorder(BorderFactory.createTitledBorder("DateSlider"));
	    }
	    public void setFromKey (boolean mouse_drag){
	    	zm.setFromKey(mouse_drag);
	    }
	}
	/**
	 * A Model used by zoomslider to maintain the location of the knob and 
	 * provides values which will be converted to seconds used by axismodel  
	 * 
	 * Last modified on: 7/25/06
	 * @author John Lee
	 *
	 */
	private class ZoomModel extends DefaultBoundedRangeModel implements ChangeListener{
		//the model used to notify the timepanel to update the display time and
		//update the location of zoomslider knob
	    private AxisModel axisModel;
	    //a boolean to ensure no concurrency
	    private boolean waggingDog;
	    private boolean from_key;
	    //it was used to assist locating the value which will changes the extent of axisModel
	    private int saved_val;
	    
	    /**
	     * used to tell if the knob position was modify by keyboard
	     * @param isKey
	     */
	    public void setFromKey (boolean isKey){
	    	from_key = isKey;
	    }
	    /**
	     * used to update the location of the knob after updating the extent of the axisModel
	     */
	    public void stateChanged(ChangeEvent event) {
	        waggingDog = true;
	        setValue((int)valueForSeconds(axisModel.getExtent()));
	        waggingDog = false;
	    }	
	    /**
	     * used to update the extent of the axismodel
	     */
	    protected void fireStateChanged() {
	    	int temp = getValue();
	        if(!waggingDog){
	        	if (temp > saved_val){
	        		if (daysForValue(temp) == daysForValue(saved_val))
	        			temp = valueForDays(daysForValue(temp)+1);
	        	}
	        	else if (temp < saved_val){
	        		if (daysForValue(temp) == daysForValue(saved_val))
	        			temp = valueForDays(daysForValue(temp)-1);
	        	}
		    	if (from_key)
		    		saved_val = temp;
		    	else
		    		saved_val = this.getValue();
		    		
	            axisModel.setExtent((int)secondsForValue(temp)); 
	        }
	        
	        super.fireStateChanged();
	    }
	    /**
	     * Constructor to initialize the range of the slider and the listener to axismodel
	     * @param axisModel 
	     */
	    public ZoomModel(AxisModel axisModel) {
	        waggingDog = true;
	        this.axisModel = axisModel;
	        axisModel.addChangeListener(this);
	        setMinimum((int)valueForDays(1));
	        setMaximum((int)valueForSeconds(axisModel.getMaximum() - axisModel.getMinimum()));
	        setValue((int)valueForSeconds(axisModel.getExtent()));
	        waggingDog = false;
	    }
	}
	/**
	 * A Model used to communicate between date shown on panel and the slider
	 * it was used to notify both the slider and panel when there is an update of its extent
	 * so that the knob can be in the correct location and the time is correctly displayed
	 * 
	 * Last modified on: 7/25/06
	 * @author John Lee
	 *
	 */
	private class AxisModel extends DefaultBoundedRangeModel{
		/**
		 * Constructor to initialize the range of this AxisModel
		 * that it is respoinsible for
		 * @param min
		 * @param max
		 */
	    public AxisModel(int min, int max) {
	        super(min, max - min, min, max);
	    }
	    /**
	     * used to fire changes of this model to notify 
	     * the listeners to update their values
	     * 
	     */
	    public void setExtent(int param) {
	        int max = getMaximum();
	        int min = getMinimum();
	        if(param > max - min)
	            param = max - min;
	        int value = getValue();
	        if(value + param > max){
	            boolean changing = getValueIsAdjusting();
	            if(!changing)
	                setValueIsAdjusting(true);
	            super.setExtent(param);
	            super.setValue(max - param);
	            if(!changing)
	                setValueIsAdjusting(false);
	        } else {
	            super.setExtent(param);
	        }
	    }
	}
	/**
	 * A class that was used by TimePanel to display the selected time
	 * and to output the absolute time format specified by AchieveViewer
	 * Last modified on: 7/25/06
	 * @author John Lee
	 *
	 */
	private class RequestDate {
		private Calendar cur_date;
//		private boolean enable_time_input = false;
		private int p_hour, p_min, p_sec;
		
		/**
		 * Constructor to initialize the date to parameter date
		 * @param d
		 */
		public RequestDate (Date d){
			cur_date = Calendar.getInstance();
			cur_date.setTime(d);
			p_hour = cur_date.get(Calendar.HOUR_OF_DAY);
			p_min = cur_date.get(Calendar.MINUTE);
			p_sec = cur_date.get(Calendar.SECOND);
		}
		/**
		 * Constructor to initialize the date to yesterday
		 *
		 */
		public RequestDate (){
			cur_date = Calendar.getInstance();
			cur_date.add(Calendar.DATE, -1);
			p_hour = cur_date.get(Calendar.HOUR_OF_DAY);
			p_min = cur_date.get(Calendar.MINUTE);
			p_sec = cur_date.get(Calendar.SECOND);
		}
		/**
		 * get the default Java Date class toString format
		 * @return
		 */
		public String getDateString (){
			return AVBaseConstants.MAIN_DATE_FORMAT.format(cur_date.getTime());
		}
		/**
		 * get current date
		 * @return
		 */
		public Date getDate (){
			return cur_date.getTime();
		}
		/**
		 * get current date in second
		 * @return
		 */
		public double getSec (){
			return cur_date.getTimeInMillis()/1000.0;
		}
		/**
		 * set the date to today subtract number of date this sec representing
		 * @param sec
		 */
		public void set_req_date (int sec){
			cur_date = Calendar.getInstance();
			cur_date.add(Calendar.SECOND, -sec);
			cur_date.set(Calendar.HOUR_OF_DAY, p_hour);
			cur_date.set(Calendar.MINUTE, p_min);
			cur_date.set(Calendar.SECOND, p_sec);
			cur_date.set(Calendar.MILLISECOND, 0);
		}
		/**
		 * set the hour, mins, sec of date be the values from parameter
		 * @param hour
		 * @param min
		 * @param sec
		 */
		public void valuesFromPanel (int hour, int min, int sec){
			cur_date.set(Calendar.HOUR_OF_DAY, hour);
			cur_date.set(Calendar.MINUTE, min);
			cur_date.set(Calendar.SECOND, sec);
			cur_date.set(Calendar.MILLISECOND,0);
			p_hour = hour;
			p_min = min;
			p_sec = sec;
		}
		/**
		 * set the date be the date from parameter
		 * @param d
		 */
		public void valuesFromPanel (Date d){
			cur_date.setTime(d);
			p_hour = cur_date.get(Calendar.HOUR_OF_DAY);
			p_min = cur_date.get(Calendar.MINUTE);
			p_sec = cur_date.get(Calendar.SECOND);
		}
	}
}
