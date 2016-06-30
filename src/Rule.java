
import java.util.ArrayList;
import java.util.List;


public class Rule {

	//if antecedent size is 0, it is always true
	private List<Proposition> antecedent;
	private Proposition consequent;

	
	public Rule() {
		super();
		this.antecedent = new ArrayList<Proposition>();
		this.consequent = new Proposition();
	}

	public Rule(Proposition consequent) {
		super();
		this.antecedent = new ArrayList<Proposition>();
		this.consequent = consequent;
	}
	
	public Rule(List<Proposition> antecedent, Proposition consequent) {
		super();
		this.antecedent = antecedent;
		this.consequent = consequent;
	}
	
	public List<Proposition> getAntecedent() {
		return antecedent;
	}
	
	public void setAntecedent(List<Proposition> antecedent) {
		this.antecedent = antecedent;
	}
	
	public Proposition getConsequent() {
		return consequent;
	}
	public boolean attributeExistsInAntecedent(int attribute){
		
		for(Proposition proposition : antecedent){
			if(proposition.getAttribute() == attribute){
				return true;
			}
		}
		
		return false;
	}
	
	public void setConsequent(Proposition consequent) {
		this.consequent = consequent;
	}
	public void addAntecedentProposition(Proposition p){
		antecedent.add(p);
	}
	
	//rules can be satisfied by an example e
	//for a rule A->C, A(e) and C(e) <-Positive Examples help us withg generalising (covering what is in the set: what we know to be true)
	//rules can be contradicted
	//A(e) and NOT(A(e)) <- Negative Examples help us with specialising (not covering what is not in the set: what we know to be false)
	
	//true for all examples to be satisfied
	//if antecedent is empty return true for all cases
	public boolean satisfies(Example e){
		if(antecedent.size() == 0){
			return true;
		}
		
		//is the example's attribute the same value?
		for(Proposition proposition : antecedent){
			
			if(!proposition.satisfies(e)){
				return false;
			}
		}
		return true;
		
	}
	
	//if it's Propositions and Consequent are exactly the same (not non-contradictory)
	public boolean isEqual(Rule compareThis){
		if(compareThis.getAntecedent().size() != antecedent.size()){
			return false;
			
		}
		//compare consequent
		if(!compareThis.getConsequent().isEqual(compareThis.getConsequent())){
			return false;
		}
		
		//compare antecedents
		for(Proposition p1 : antecedent){
			
			boolean found = false;
			for(Proposition p2 : compareThis.getAntecedent()){
				
				if(p1.isEqual(p2)){
					found = true;
				}
			}
			
			if(!found){
				return false;
			}
		}
		
		return true;
	}
	
	public void printToConsole(){
		int count = antecedent.size();
		for (Proposition pre : antecedent){
			pre.printToConsole();
			if((--count) > 0)
				System.out.print("^");
		}
		System.out.print("--->");

		consequent.printToConsole();
	}
	
	@Override
	public Rule clone(){
		Rule clonedRule = new Rule();
		
		List<Proposition> clonedAntecedents = new ArrayList<Proposition>();
		for(Proposition copyProposition : this.antecedent){
			clonedAntecedents.add(new Proposition(copyProposition.getAttribute(), copyProposition.getValue()));
		}
		Proposition clonedConsequent = new Proposition(this.consequent.getAttribute(), this.consequent.getValue());
		
		clonedRule.setAntecedent(clonedAntecedents);
		clonedRule.setConsequent(clonedConsequent);
		
		return clonedRule;
	}
	
}
