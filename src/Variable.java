
import java.util.ArrayList;
import java.util.List;


public class Variable {
	private String name;
	private int attributeNum;
	private List<String> valueList;
	
	
	public Variable(int attributeNum) {
		super();
		this.name = "";
		this.attributeNum = attributeNum;
		this.valueList = new ArrayList<String>();
	}
	
	public Variable(String name, int attributeNum) {
		super();
		this.name = name;
		this.attributeNum = attributeNum;
		this.valueList = new ArrayList<String>();
	}
	
	public Variable(String name, int attributeNum, List<String> valueList) {
		super();
		this.name = name;
		this.attributeNum = attributeNum;
		this.valueList = valueList;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAttributeNum() {
		return attributeNum;
	}
	public void setAttributeNum(int attributeNum) {
		this.attributeNum = attributeNum;
	}
	public List<String> getValueList() {
		return valueList;
	}
	public void setValueList(List<String> valueList) {
		this.valueList = valueList;
	}
	public void addAttributeValue(String value) {
		this.valueList.add(value);
	}
	public boolean isEqual(Variable var) {
		if(name.matches(var.getName())){
			return false;
		}
		
		
		if(attributeNum != var.getAttributeNum()){
			return false;
		}
		
		if(var.getValueList().size() != valueList.size()){
			return false;
		}
		
		//compare value lists
		for(String v1 : valueList){
			
			boolean found = false;
			for(String v2 : var.getValueList()){
				
				if(v1.matches(v2)){
					found = true;
				}
			}
			
			if(!found){
				return false;
			}
		}
		
		return true;
	}
	public void printToConsole() {
		System.out.print(attributeNum + "->");
		for(String val : valueList){
			System.out.print(val + ",");
		}
		System.out.println();
	}
	
	public Proposition getProposition(int valueIndex){
		Proposition p = new Proposition(attributeNum, valueList.get(valueIndex));
		return p;
	}
	
	public Proposition getProposition(String className){
		for(String v : valueList){
			if(className.matches(v)){
				Proposition p = new Proposition(attributeNum, v);
				return p;
			}
		}
		return null;
	}
}
