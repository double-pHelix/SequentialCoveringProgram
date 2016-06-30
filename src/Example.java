
import java.util.ArrayList;
import java.util.List;


public class Example {
	private List<String> attributeVals = new ArrayList<String>();

	public Example(List<String> attributeVals) {
		super();
		this.attributeVals = attributeVals;
	}

	public List<String> getAttributeVals() {
		return attributeVals;
	}

	public void setAttributeVals(List<String> attributeVals) {
		this.attributeVals = attributeVals;
	}
	
	public void addAttributeVal(String attVal){
		attributeVals.add(attVal);
	}
	
	public void setAttributeVal(int index, String attVal){
		attributeVals.set(index, attVal);
	}
	
	public String getAttributeVal(int index){
		return attributeVals.get(index);
	}
	
	//assumes the last attribute is the target class
	public String getTargetClass(){
		return attributeVals.get(attributeVals.size()-1);
	}
	//returns num of attribute values
	public int getSize(){
		return attributeVals.size();
	}
	
	public void printToConsole(){
		for(String currVal : attributeVals){
			System.out.print(currVal + ",");
		}
		System.out.println();
	}
	
}
