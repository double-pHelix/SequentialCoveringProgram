

public class Proposition {
	private int attribute; //is a value
	private String value;
	private boolean negated;
	
	public Proposition() {
		super();
		this.attribute = 0;
		this.value = "";
		this.negated = false;
	}

	public Proposition(int attribute, String value) {
		super();
		this.attribute = attribute;
		this.value = value;
		this.negated = false;
	}
	
	public int getAttribute() {
		return attribute;
	}
	public void setAttribute(int attribute) {
		this.attribute = attribute;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public boolean isNegated() {
		return negated;
	}

	public void setNegated(boolean negated) {
		this.negated = negated;
	}

	//are the exact same
	public boolean isEqual(Proposition p){
		if(negated != p.isNegated()){
			return false; 
		}
		
		if(attribute == p.getAttribute() && value.matches(p.getValue())){
			return true;
		}
		
		return false;
		
	}
	

	//is satisfied if a proposition does not contradict
	//true of they are the same
	//or for case that this is a negated proposition, is not the same
	public boolean isSatisfied(Proposition p){
		
		//if they match and not negated, true!, if they match but is negated, then false!
		if(attribute == p.getAttribute() && value.matches(p.getValue())){
			if(negated){
				return false;
			} else {
				return true;
			}
		}
		
		//no matches, if negated true! 
		if(negated){
			return true;
		} else {
			return false;
		}
		
	}

	public boolean satisfies(Example e){
		
		//is the example's attribute the same value?
		if(value.matches(e.getAttributeVal(attribute))){
			if(negated){
				return false; 
			} else {
				return true;
			}
		}
		
		if(negated){
			return true; 
		} else {
			return false;
		}
		
	}
	
	public void printToConsole(){
		if(negated){
			System.out.print("[NOT("+ attribute + ")='"+ value + "']");
		} else {
			System.out.print("[("+ attribute + ")='"+ value + "']");
		}
	}
	
}
