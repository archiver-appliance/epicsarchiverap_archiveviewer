package epics.archiveviewer.base.fundamental;



/**
 * An abstract class, encapsulating the common methods and attributes of <CODE>
 * RangeAxes</CODE> and <CODE>TimeAxes</CODE>.
 * 
 * @see RangeAxis
 * @see TimeAxis
 * @author sergei chevtsov
 */
public abstract class Axis extends Object
{
	private final String name;
	
	public Axis(String _name) throws Exception
	{
		if(_name == null || _name.trim().equals(""))
			throw new Exception("No empty axis names allowed!");
		this.name = _name;
	}
	
	public String getName() {
		return this.name;
	}
}