
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Random;

/**
 * Sequential Covering Algorithm with FOIL Structure and Information Gain for candidate rule selection
 * 
 * To make sense of how the program works, go to main() at the bottom and observe function calls (hopefully they are clear)
 * 
 * @author Felix
 *
 */
public class SequentialCovering {

	private GraphicsOutputDisplayer dataDisplayer;
	private List<Example> allData;
	private List<Example> trainingExamples;
	private List<Example> testingExamples;
	
	private List<Example> positiveTrainingExamples;
	private List<Example> negativeTrainingExamples;
	private List<Variable> variables;
	private Variable targetVariable;
	private List<Rule> ruleSet; 
	private boolean debugPrints = false;
	
	public SequentialCovering(){
		this.dataDisplayer = new GraphicsOutputDisplayer();
		this.allData = new ArrayList<Example>();
		this.trainingExamples = new ArrayList<Example>();
		this.testingExamples = new ArrayList<Example>();
		this.positiveTrainingExamples = new ArrayList<Example>();
		this.negativeTrainingExamples = new ArrayList<Example>();
		this.variables = new ArrayList<Variable>(); 
		this.ruleSet = new ArrayList<Rule>(); 
		
	}
	
	/* 
	 * 
	 * input and output 
	 * 
	 */
	
	public String getChosenClassTarget(){
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				
			String input;

			System.out.println("Enter target class value (e.g. N or EI_IE, or e or p, else default will be the first value)");
			while((input=br.readLine())==null){
				System.out.println("Enter directory location of data");
			}
			
			return input;
				
		}catch(IOException io){
			io.printStackTrace();
		}	
		
		return null;
	}
	
	public String getFileDirInput(){
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				
			String input;

			System.out.println("Enter directory location of data");
			while((input=br.readLine())==null){
				System.out.println("Enter directory location of data");
			}
			
			return input;
				
		}catch(IOException io){
			io.printStackTrace();
		}	
		
		return null;
	}
	
	public String getPercentageInput(){
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				
			String input;

			System.out.println("Enter percentage of data to be training data");
			while((input=br.readLine())==null){
				System.out.println("Enter percentage of data to be training data");
			}
			
			Integer.parseInt(input);
			
			return input;
				
		} catch(Exception io){
			return null;
		}	
		
	}
	
	public String setUpDirectoryWithExistingFile(String dir){

		int endSubStrIndex = dir.lastIndexOf('/');
		return dir.substring(0, endSubStrIndex);
	}
	
	
	/* 
	 * 
	 * Prints
	 * 
	 */
	
	public void printExampleListToConsole(List<Example> toPrint){
		for(Example printThis : toPrint){
			printThis.printToConsole();
		}
	}
	
	public void printVariableListToConsole(List<Variable> toPrint){
		for(Variable printThis : toPrint){
			printThis.printToConsole();
		}
	}
	public void printPropositionListToConsole(List<Proposition> toPrint){
		for(Proposition printThis : toPrint){
			printThis.printToConsole();
		}
		System.out.println();
	}
	public void printRuleListToConsole(List<Rule> toPrint){
		System.out.println("------------------------------------");
		System.out.println("Learned Rule Set");
		System.out.println();
		for(Rule printThis : toPrint){
			printThis.printToConsole();
			System.out.println();
		}
	}
	
	
	/* 
	 * 
	 * Auxillary Functions 
	 * 
	 */
	
	//disjunctive of all the rules (if or or or or therefore in class, else it is not in the set)
	public boolean satisfiedByRuleSet(List<Rule> ruleList, Example example){
		for(Rule r : ruleList){
			if(r.satisfies(example)){
				return true;
			}
		}
		return false;
	}
	
	
	public boolean ruleExistsInList(List<Rule> ruleList, Rule rule){
		if(rule == null){
			return false;
		}
		
		for(Rule r : ruleList){
			if(r.isEqual(rule)){
				return true;
			}
		}
		return false;
	}
	
	public List<Example> createListCopyExamples(List<Example> toCopy){
		
		List<Example> newList = new ArrayList<Example>();
		for(Example copy : toCopy){
			newList.add(copy);
		}
		
		return newList;
	}
	
	public List<Proposition> createListCopyPropositions(List<Proposition> toCopy){
		
		List<Proposition> newList = new ArrayList<Proposition>();
		for(Proposition copy : toCopy){
			newList.add(copy);
		}
		
		return newList;
	}
                
	
	//given this rule we do not contradict (add values to existing attributes)
	public List<Proposition> getCandidatePropositions(Rule excludeRule){
		//return list of all possible propositions
		
		//list of attribute value pairs
		List<Proposition> candidatePropositions = new ArrayList<Proposition>();
		
		for(Variable variable : variables){
			int variableAttribute = variable.getAttributeNum();
			
			if(excludeRule.attributeExistsInAntecedent(variableAttribute) 
					|| variable.getAttributeNum() == targetVariable.getAttributeNum()){
				//attribute already has value in the exclude rule
				continue;
			}
			
			List<String> variableValues =  variable.getValueList();
			for(String value : variableValues){
				Proposition newProposition = new Proposition(variableAttribute,value);
				//add to our list of candidates
				candidatePropositions.add(newProposition);
				
			}
		}

		return candidatePropositions;
	}
	
	/* 
	 * 
	 * Set up functions
	 * 
	 */    
	private void prepareData(String file, String outputDir, double percentageSplitData){
		   
	    //reading   
	    try{
	        InputStream ips=new FileInputStream(file); 
	        InputStreamReader ipsr=new InputStreamReader(ips);
	        BufferedReader br=new BufferedReader(ipsr);
	        String line;

	        
	        int currVarNum = 0;
	        while ((line=br.readLine())!=null){
	        	//skip empty lines
		        if(line.length() == 0){
		        	continue;
		        }

	        	//data does not start with % or @
	        	if(line.indexOf('%') == 0){
	        		//comments
	        		continue;
	        	} else if(line.indexOf('@') == 0){
	        		
	        		//Variable is (attribute => listof(values))
	        		Variable newVariable = null;
	        		StringTokenizer tokenizerForName = new StringTokenizer(line, " ");
	        		tokenizerForName.nextElement();
	        		if(tokenizerForName.hasMoreElements()){
		        		String variableName = (String) tokenizerForName.nextElement();
		        		newVariable = new Variable(variableName, currVarNum);
	        		} else {
	        			newVariable = new Variable(currVarNum);
	        		}
	        		
		        	line = line.replaceAll("\\s+","");
	        		
		        	//need to load attributes
		        	//@attribute 'cap-shape' { 'b', 'c', 'f', 'k', 's', 'x'}
		        	//get { ELEMENTS }
	        		int setStartIndex = line.indexOf('{')+1;
	        		int setEndIndex = line.indexOf('}');
	        		if(setStartIndex < setEndIndex){
		        		String valuesOnl = line.substring(setStartIndex, setEndIndex);

		        		StringTokenizer tokenizer = new StringTokenizer(valuesOnl, ",");
		        		int count = 0;
		        		while (tokenizer.hasMoreElements()) {
		        			
		        			if(count%2 == 0){ //every even element is data!
		        				String nextElem = (String) tokenizer.nextElement();
		        				nextElem = nextElem.replaceAll("'","");
		        				
		        				//add a new value to the variable
		        				newVariable.addAttributeValue(nextElem);
		        			}
		        		}
		        		
		        		currVarNum++;
		        		variables.add(newVariable);
		        		continue;
	        		}
	        		

	        	} else {
	        		//DATA
	        		line = line.replaceAll("\\s+","");
		        	//tokenize
		        	StringTokenizer tokenizer = new StringTokenizer(line, ",");
		        	
		        	Example newExample = new Example(new ArrayList<String>(tokenizer.countTokens()));
		    		while (tokenizer.hasMoreElements()) {
		    			String attrVal = (String) tokenizer.nextElement();
		    			
	    				attrVal = attrVal.replaceAll("'","");
		    			if(attrVal.matches("\\?")){
		    				attrVal = attrVal.replaceAll("\\?","#");
		    			} 
		    			newExample.addAttributeVal(attrVal);
		    		}
		    		allData.add(newExample);
		    		
	        	}
	        	
	        }
	        br.close(); 

	    }       
	    catch (Exception e){
	        System.out.println(e.toString());
	    } 
	    
	    //quick reference for targetVariable
	    this.targetVariable = variables.get(variables.size()-1);
	    
	    List<String> variableNames = new ArrayList<String>();
	    for(Variable v : variables){
	    	variableNames.add(v.getName());
	    }
	    dataDisplayer.setVariableNames(variableNames);
	    
	    if(!outputDir.matches("")){
	    	dataDisplayer.setHtmlLocation(outputDir);
	    }
	    
	    splitTrainingAndTestData(percentageSplitData);
		dataDisplayer.setTrainingSetSize(trainingExamples.size());
		dataDisplayer.setTestingSetSize(testingExamples.size());
		dataDisplayer.setFileName(file);
	}
	
	public void splitTrainingAndTestData(double percentTrainingData){
		
		double ratio = percentTrainingData/100;
		int sizeOfData = allData.size();
		int numTrainingData = (int) Math.ceil(sizeOfData * ratio);
		Random rand = new Random();
		List<Example> copyAllData = createListCopyExamples(allData);

		System.out.println("------------------------------------");
		System.out.println("Splitting data into training and testing sets");
		System.out.println();
		System.out.println("Size of all data:" + sizeOfData);		
		System.out.println("No. of Training Examples:" + numTrainingData);
		
		int[] randomValues = new int[numTrainingData];
        for (int i = 0; i < numTrainingData; ++i){
                randomValues[i]=i;
        }
        for(int i = numTrainingData ; i < sizeOfData; ++i) {
        	int v = rand.nextInt(i+1);
        	if(v < numTrainingData) {
        		randomValues[v] = i;
        	}
        }
		for(int i = 0; i < numTrainingData; i++){
			trainingExamples.add(copyAllData.get(randomValues[i]));
			copyAllData.set(randomValues[i], null);
		}
		
		for(Example e : copyAllData){
			if(e != null){
				testingExamples.add(e);
			}
			
		}

	}
	

	public void splitPositiveAndNegativeTrainingData(Proposition targetClass){
		
		for(Example e : trainingExamples){
			if(targetClass.satisfies(e)){
				positiveTrainingExamples.add(e);
			} else {
				negativeTrainingExamples.add(e);
			}
		}		
	}
	
	/* 
	 * 
	 * Calculation and List manipulation functions
	 * 
	 */  
	
	public void addAllPropositionsFromRule(List<Proposition> listOfPropositions, Rule rule){
		for(Proposition p : rule.getAntecedent()){
			listOfPropositions.add(p);
		}
	}
	
	
	//returns the amount returned
	public int removeCoveredByRuleAndTargetClass(Rule rule, Proposition targetClass, List<Example> examples){
		
		int count = 0;
		for (Iterator<Example> iter = examples.listIterator(); iter.hasNext(); ) {
			Example example = iter.next();
			
			if(rule.satisfies(example) && targetClass.satisfies(example)){
				//example.printToConsole();
				iter.remove();
				count++;
			}
		}
		
		return count;
	}	//returns the amount returned
	
	//remove exact proposition from the list 
	public int removePropositionFromList(List<Proposition> list, Proposition toRemove){
		
		int count = 0;
		for (Iterator<Proposition> iter = list.listIterator(); iter.hasNext(); ) {
			Proposition p = iter.next();
			
			if(toRemove.isEqual(p)){
				//example.printToConsole();
				iter.remove();
				count++;
			}
		}
		
		return count;
	}
	//returns the amount returned
	public int removeCoveredByRule(Rule rule, List<Example> examples){
		
		int count = 0;
		for (Iterator<Example> iter = examples.listIterator(); iter.hasNext(); ) {
			Example example = iter.next();
			
			if(rule.satisfies(example)){
				//example.printToConsole();
				iter.remove();
				count++;
			}
		}
		
		return count;
	}
	
	public void removeVariable(Variable toRemove){

		for (Iterator<Variable> iter = variables.listIterator(); iter.hasNext(); ) {
			Variable candidateToRemove = iter.next();
			
			if(toRemove.isEqual(candidateToRemove)){
				//example.printToConsole();
				iter.remove();
			}
		}
		
	}
	//returns the amount returned
	public void removeVariable(String toRemove){

		for (Iterator<Variable> iter = variables.listIterator(); iter.hasNext(); ) {
			Variable candidateToRemove = iter.next();
			
			if(toRemove.matches(candidateToRemove.getName())){
				//example.printToConsole();
				iter.remove();
			}
		}
		
	}
	
	//returns the num removed
	public int retainCoveredByRule(Rule rule, List<Example> examples){
		int count = 0;
		for (Iterator<Example> iter = examples.listIterator(); iter.hasNext(); ) {
			Example example = iter.next();
			
			if(!rule.satisfies(example)){
				count++;
				iter.remove();
			}
		}
		return count;
	}
	
	
	public List<Example> getNotCoveredOfRule(Rule rule, List<Example> examples){
		
		List<Example> coveredEamples = new ArrayList<Example>();
		for (Iterator<Example> iter = examples.listIterator(); iter.hasNext(); ) {
			Example example = iter.next();
			
			if(!rule.satisfies(example)){
				coveredEamples.add(example);
			}
		}
		
		return coveredEamples;
				
	}
	
	public List<Example> getCoverOfRule(Rule rule, List<Example> examples){
		
		List<Example> coveredEamples = new ArrayList<Example>();
		for (Iterator<Example> iter = examples.listIterator(); iter.hasNext(); ) {
			Example example = iter.next();
			
			if(rule.satisfies(example)){
				coveredEamples.add(example);
			}
		}
		
		return coveredEamples;
				
	}

	public int getSupportOfRule(Rule rule, List<Example> space){
		int count = 0;
		
		for(Example e : space){
			if(rule.satisfies(e)){
				count++;
			}
		}
		
		return count;

	}
	public double calculateAccuracy(Rule rule, List<Example> sampleSpace){
		List<Example> classifiedPositive = new ArrayList<Example>();
		List<Example> classifiedNegative = new ArrayList<Example>();
		
		//classify
		for(Example e : sampleSpace){
			if(rule.satisfies(e)){
				classifiedPositive.add(e);
			} else {
				classifiedNegative.add(e);
			}
		}
		
		//measure how well we did classifying it based on targetVariable
		double total = classifiedPositive.size() + classifiedNegative.size();
		double truePositives = numCorrectlyClassified(rule.getConsequent(), classifiedPositive);
		rule.getConsequent().setNegated(true);
		double trueNegatives = numCorrectlyClassified(rule.getConsequent(), classifiedNegative);
		rule.getConsequent().setNegated(false);
		double accuracy = ((truePositives+trueNegatives)/total) * 100;
		
		return accuracy;
	}
	
	
	public void calculateInformationRuleOnData(Proposition targetClass, Rule rule, List<Rule> currRuleSet, List<Example> uncoveredData){
		//testdata 	testingExamples;
		//rules ruleSet;
		
		List<Example> classifiedPositive = new ArrayList<Example>();
		List<Example> classifiedNegative = new ArrayList<Example>();
		
		//classify
		for(Example e : uncoveredData){
			if(rule.satisfies(e)){
				classifiedPositive.add(e);
			} else {
				classifiedNegative.add(e);
			}
		}
		
		//double total = classifiedPositive.size() + classifiedNegative.size();
		double truePositives = numCorrectlyClassified(targetClass, classifiedPositive);
		
		double ruleCoverage = classifiedPositive.size();
		double ruleAccuracy = (truePositives/(ruleCoverage)) * 100;
		double ruleCoveragePercent = (ruleCoverage/uncoveredData.size()) * 100;
		
		dataDisplayer.appendToRuleSet(rule);
		Statistic newStat = new Statistic(Double.toString(ruleCoveragePercent), Double.toString(ruleAccuracy));
		dataDisplayer.appendToRuleCoverStat(newStat);
		
		classifiedPositive = new ArrayList<Example>();
		classifiedNegative = new ArrayList<Example>();
		
		//classify
		for(Example e : testingExamples){
			if(satisfiedByRuleSet(currRuleSet, e)){
				classifiedPositive.add(e);
			} else {
				classifiedNegative.add(e);
			}
		}
		
		//measure how well we did classifying it based on targetVariable
		double total = classifiedPositive.size() + classifiedNegative.size();
		double coverage = (classifiedPositive.size()/total)*100;
		truePositives = numCorrectlyClassified(targetClass, classifiedPositive);
		targetClass.setNegated(true);
		double trueNegatives = numCorrectlyClassified(targetClass, classifiedNegative);
		targetClass.setNegated(false);
		
		double accuracy = ((truePositives+trueNegatives)/total) * 100;
		//give data to html display
		Statistic newStat2 = new Statistic(Double.toString(coverage), Double.toString(accuracy));
		dataDisplayer.appendToRuleSetStat(newStat2);
	}
	
	
	public double calculateEntropyOnRule(Rule rule, Proposition candidateProposition, List<Example> sampleSpace, Statistic candidateStat){
		List<Example> coveredExamples = getCoverOfRule(rule, sampleSpace);
		//List<Example> notCoveredExamples  = getNotCoveredOfRule(rule, sampleSpace);
		
		double ruleCoverageSupport = coveredExamples.size();
		if(ruleCoverageSupport == 0){
			//we can't calculate entropy over empty set! 
			//return failure
			candidateProposition.setNegated(true);
		}
				
		double coveredEntropy = calculateEntropy(rule.getConsequent(), candidateProposition, coveredExamples);

		//accuracy is 
		if(candidateStat != null && !candidateProposition.isNegated()){
			candidateStat.setCoverage(Double.toString((ruleCoverageSupport/sampleSpace.size())*100));
			candidateStat.setAccuracy(Double.toString(calculateAccuracy(rule, sampleSpace)));
		}
						
		return coveredEntropy; 
	}

	public double calculateEntropy(Proposition target, Proposition candidateProposition, List<Example> sampleSpace){
		if(sampleSpace.size() == 0)
			return 0;
		
		//calculate impurity
		//satisfied by the rule
		double numSat = 0;
		//not satisfied by ruke
		double numUnsat = 0;
		for (Example example : sampleSpace){
			
			if(target.satisfies(example)){
				numSat++;
			} else {
				numUnsat++;
			}
		}
		
		if(numSat == 0 || numUnsat == 0){
			if(numUnsat > numSat){
				//make it NOT proposition
				if(candidateProposition != null){
					candidateProposition.setNegated(true);
				}
			}
			
			return 0;
		}
		
		double total = sampleSpace.size();
		
		double entropy = (-1) *(numSat/total) * Math.log((numSat/total))/Math.log(2) + 
				      (-1) * (numUnsat/total) * Math.log((numUnsat/total))/Math.log(2) ; 
	
		//we have provided a candidate proposition to switch to negative if this is the scenario of covered examples and we classify the NOT target more
		if(numUnsat > numSat){
			//make it NOT proposition
			if(candidateProposition != null){
				candidateProposition.setNegated(true);
			}
		}
		return entropy;
	}
	
	public double numCorrectlyClassified(Proposition targetClass, List<Example> set){
		
		double numCorrect = 0;
		for(Example e : set){
			
			if(targetClass.satisfies(e)){
				numCorrect++;
			} 
		}
		
		return numCorrect;
	}


	public double calculateInformationGain(Rule currRule, Proposition candidateProposition, List<Example> sampleSpace, Statistic candidateStat){
		Rule newRule = currRule.clone();
		Proposition copyCandidate = new Proposition (candidateProposition.getAttribute(), candidateProposition.getValue());
		
		newRule.addAntecedentProposition(candidateProposition);
		
		double previousEntropy = calculateEntropyOnRule(currRule, null, sampleSpace, null);
		double afterEntropy = calculateEntropyOnRule(newRule, copyCandidate, sampleSpace, candidateStat);
		
		if(debugPrints){
			currRule.printToConsole(); System.out.println();
			newRule.printToConsole(); System.out.println();
			System.out.println("     Sample size:" + sampleSpace.size());
			System.out.println("     Prev entropy=" + previousEntropy);
			System.out.println("     After entropy=" + afterEntropy);
		}
		
		//if it is negated..
		//then we do not consider this candidateProposition as it classifieds the non-target class
		if(copyCandidate.isNegated()){
			return 0; //we avoid adding it
		}

		//return the decrease of entropy
		return previousEntropy-afterEntropy;
	}

	//use information gain to select next proposition
	//idea: given a list of possible propositions
	//search for the proposition that when added to the current rule 
	//classifies the remaining examples providing the most information gain
	public Proposition selectNextProposition(Rule currentRule, List<Proposition> candidatePropositions, List<Example> currentExampleSpace, RuleStatistics currRuleStatistics){
		Proposition nextProposition = null;
		
		//add each proposition to the rule, and then calculate the information gain
		double currMaxInfoGain = 0;
		Proposition bestPerfProposition = null;
		Statistic statForBest = new Statistic(null, null);
		for(Proposition candidateProposition : candidatePropositions){
			
			//pass the new candidate into calculateINformationGain
			double candidatesInfoGain = calculateInformationGain(currentRule, candidateProposition, currentExampleSpace, null);
			
			if(debugPrints){
				System.out.println("      Propositon:" + candidateProposition.getAttribute() + "=" + candidateProposition.getValue() + " | infogain:" + candidatesInfoGain);
				if(candidateProposition.isNegated()){
					System.out.println("Negated!");
				}
			}
			
			if(candidatesInfoGain > currMaxInfoGain || (currMaxInfoGain == 0 && candidatesInfoGain > 0)){
				//new best performer
				currMaxInfoGain = candidatesInfoGain;
				bestPerfProposition = candidateProposition;
			}
			
		}
		
		//crown the winner!
		nextProposition = bestPerfProposition;
		
		//we calculate the statistics to be printed out by HTML
		if(nextProposition != null){
			calculateInformationGain(currentRule, nextProposition, currentExampleSpace, statForBest);
			
			currRuleStatistics.appendStatistic(statForBest);
		}
		
		return nextProposition;
	}
	
	//general to specific
	public Rule learnOneRule(Proposition targetClass, List<Example> currentExampleSpace){
		//create a copy of the negative examples
		
		Rule newRule = new Rule(targetClass); //rule that predicts the target class with no preconditions (most general)
		List<Example> copyNeg = createListCopyExamples(negativeTrainingExamples);

		RuleStatistics learnedRuleStats = new RuleStatistics();
		learnedRuleStats.appendStatistic(new Statistic("100",Double.toString(calculateAccuracy(newRule, currentExampleSpace))));
		learnedRuleStats.setSampleSize(Integer.toString(currentExampleSpace.size()));
		
		while(!copyNeg.isEmpty()){
			//specialise newRule by choosing the next proposition for the rule
			//information gain...
			List<Proposition> candidatePropositions = getCandidatePropositions(newRule);
			Proposition newProposition = selectNextProposition(newRule, candidatePropositions, currentExampleSpace, learnedRuleStats);
			
			if(newProposition == null){
				if(newRule.getAntecedent().size() == 0){
					newRule = null; //can't add the true->target rule... 
				}
				//System.out.println("impure");
				//unable to remain pure, some negative examples remain that are covered by our rule (and contradict it!)
				break;
			}
			
			newRule.addAntecedentProposition(newProposition);
			
			//remove negative examples NOT covered, or keep the ones still covered by newRule
			retainCoveredByRule(newRule, copyNeg);
			
		}
		//display coverage and accuracy of new rule
		if(newRule != null){
			dataDisplayer.appendToRuleStatsList(learnedRuleStats);
			
		}
		
		return newRule;
	}

	
	public void runSequentialCoveringAlgorithm(Proposition targetClass){
		dataDisplayer.setTrainingSetSize(trainingExamples.size());
		
		//split data into positive and negatives
		splitPositiveAndNegativeTrainingData(targetClass);
		
		List<Example> copyPos = createListCopyExamples(positiveTrainingExamples);
		List<Example> copyTraining = createListCopyExamples(trainingExamples);
				
		List<Rule> learnedRules = new ArrayList<Rule>();
		
		double coveredExamplesCount = 0;
		
		while(!copyPos.isEmpty()){
			//learn a rule
			Rule newRule = learnOneRule(targetClass, copyTraining);

			if(newRule == null || ruleExistsInList(learnedRules, newRule)){
				//we've run out of rules to add or 
				//if there is no reduction in copyPos, we go into a loop
				break;
			}

			learnedRules.add(newRule);
		
			if(debugPrints){
				newRule.printToConsole();
				System.out.println();
				printExampleListToConsole(copyTraining);
			}
			
			
			//get data and add to displayer for when we generate HTML
			calculateInformationRuleOnData(targetClass, newRule, learnedRules, copyTraining);
		
			removeCoveredByRule(newRule, copyPos);
			
			//get data and add to displayer for when we generate HTML
			coveredExamplesCount += removeCoveredByRuleAndTargetClass(newRule, targetClass, copyTraining);
			double coverPercentage = (coveredExamplesCount/trainingExamples.size())*100;
			dataDisplayer.appendToRuleSetCoverage(Double.toString(coverPercentage));
		}
		
		ruleSet = learnedRules;
	}

	public void runModelOnData(Proposition targetClass, List<Example> testData){
		//testdata 	testingExamples;
		//rules ruleSet;
		
		List<Example> classifiedPositive = new ArrayList<Example>();
		List<Example> classifiedNegative = new ArrayList<Example>();
		
		//classify
		for(Example e : testData){
			if(satisfiedByRuleSet(ruleSet, e)){
				classifiedPositive.add(e);
			} else {
				classifiedNegative.add(e);
			}
		}
		
		//measure how well we did classifying it based on targetVariable
		displayPerformanceResults(targetClass, classifiedPositive, classifiedNegative);
	}
	
	public void displayPerformanceResults(Proposition targetClass, List<Example> classifiedPositive, List<Example> classifiedNegative){
		//performances
		double total = classifiedPositive.size() + classifiedNegative.size();
		double truePositives = numCorrectlyClassified(targetClass, classifiedPositive);
		double falsePositives = classifiedPositive.size() - truePositives;
		targetClass.setNegated(true);
		double trueNegatives = numCorrectlyClassified(targetClass, classifiedNegative);
		double falseNegatives = classifiedNegative.size() - trueNegatives;
		targetClass.setNegated(false);
		
		NumberFormat formatter = new DecimalFormat("#0.0000");  
		System.out.println("------------------------------------");
		System.out.println("Performance on Testing Data");
		System.out.println();
		System.out.println("True Positives:" + truePositives);
		System.out.println("False Positives:" + falsePositives);
		System.out.println("True Negatives:" + trueNegatives);
		System.out.println("False Negatives:" + falseNegatives);
		String accuracy = formatter.format((truePositives+trueNegatives)/total);
		String errorRate = formatter.format((falsePositives+falseNegatives)/total);
		String precision = formatter.format((truePositives)/(truePositives + falsePositives));
		String recall = formatter.format((truePositives)/(truePositives + falseNegatives));
		String sensitivity = formatter.format((truePositives)/(trueNegatives + falseNegatives));
		String specificity = formatter.format((trueNegatives)/(trueNegatives + falsePositives));
		String tPRate = formatter.format((truePositives)/(truePositives + falseNegatives));
		String fPRate = formatter.format((falsePositives)/(falsePositives + trueNegatives));
		String negativePredictiveVal = formatter.format((trueNegatives)/(trueNegatives + falseNegatives));
		String coverage = formatter.format((truePositives + falsePositives)/(total));

		dataDisplayer.setAccuracy(accuracy);
		dataDisplayer.setErrorRate(errorRate);
		dataDisplayer.setPrecision(precision);
		dataDisplayer.setRecall(recall);
		dataDisplayer.setSensitivity(sensitivity);
		dataDisplayer.setSpecificity(specificity);
		dataDisplayer.settPRate(tPRate);
		dataDisplayer.setfPRate(fPRate);
		dataDisplayer.setNegativePredictiveVal(negativePredictiveVal);
		dataDisplayer.setCoverage(coverage);
		
		System.out.println("Accuracy:" + accuracy);
		System.out.println("Error Rate:" + errorRate);
		System.out.println("Precision:" + precision);
		System.out.println("Recall:" + recall);
		System.out.println("Sensitivity:" + sensitivity);
		System.out.println("Specificity:" + specificity);
		System.out.println("True Positive Rate:" + tPRate);
		System.out.println("False Positive Rate:" + fPRate);
		System.out.println("Negative Predictive Value:" + negativePredictiveVal);
		System.out.println("Coverage:" + coverage);
		System.out.println("Total Examples:" + total);
				
	}


	public void buildHTMLFileDisplayDirectory(){

		System.out.println("------------------------------------");
		String htmlDir = dataDisplayer.buildHTML();
		System.out.println("View learning details and performance summary at: " + htmlDir);
		
	}
	
	public void run(String className){
		System.out.println("------------------------------------");
		System.out.println("Running...");
		System.out.println();
		//split data into training and test data
		//build model and learn rules on training data for a target class		
		
		Proposition targetClass = targetVariable.getProposition(className);
		if(targetClass == null){
			targetClass = targetVariable.getProposition(0);//0. e = edible, 1. p = poisonous // 0. EI_IE= 1. N=
		}

		runSequentialCoveringAlgorithm(targetClass);
		
		printRuleListToConsole(ruleSet);

		runModelOnData(targetClass, testingExamples);

		buildHTMLFileDisplayDirectory();
	}
	
	public static void main(String[] args) {
		String file = null;
		String valueName = null;
		String percentageInput = null;
		String outputDir = null;
		SequentialCovering model = new SequentialCovering();
		
		if(args.length < 1){
			file = model.getFileDirInput();
		} else {
			file = args[0];
		}
		if(args.length < 2){
			valueName = model.getChosenClassTarget();
		} else {
			valueName = args[1];
		}		
		if(args.length < 3){
			percentageInput = model.getPercentageInput();
		} else {
			percentageInput = args[2];
		}
		if(args.length < 4){
			outputDir = System.getProperty("user.dir");
		} else {
			outputDir = args[3];
		}
		System.out.println("------------------------------------");
		System.out.println("Input");
		System.out.println();
		System.out.println("Data File:" + file);
		System.out.println("Target Class:" + valueName);
		System.out.println("Percentage Training Data:" + percentageInput);
		System.out.println("HTML Output Directory:" + outputDir);
		
		double percentTrainingData = Double.parseDouble(percentageInput);

		model.prepareData(file, outputDir, percentTrainingData);
		model.removeVariable("Instance_name");
		model.run(valueName);
	}

}
