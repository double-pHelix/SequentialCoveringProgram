

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class GraphicsOutputDisplayer {
	private String fileName;
	private List<Rule> ruleSetToPrint; 

	private List<Statistic> ruleStatsOverRemainingToBeCoveredByRuleSet;
	private List<String> rulesetCoverOverTrainingData;
	private List<Statistic> ruleSetStatsOverTestingData;
	private List<RuleStatistics> ruleStatistics;
	
	private String htmlLocation = new String(); 
	private List<String> variableNames; 
	private int trainingSetSize; 
	private int testingSetSize;
	private String accuracy;
	private String errorRate;
	private String precision;
	private String recall;
	private String sensitivity;
	private String specificity;
	private String tPRate;
	private String fPRate;
	private String negativePredictiveVal;
	private String coverage;
	
	public GraphicsOutputDisplayer(){
		this.ruleSetToPrint = new ArrayList<Rule>();
		this.ruleStatsOverRemainingToBeCoveredByRuleSet = new ArrayList<Statistic>();
		
		this.rulesetCoverOverTrainingData = new ArrayList<String>();
		
		this.ruleSetStatsOverTestingData = new ArrayList<Statistic>();
		
		this.ruleStatistics = new ArrayList<RuleStatistics>();
		
		this.variableNames = new ArrayList<String>();
		this.htmlLocation = System.getProperty("user.dir");
	}
	public String buildHTML(){		
		//create file
		String HTMLfileDir = htmlLocation;
		HTMLfileDir+= "/display.html";
		
		System.out.println("Storing html at " + HTMLfileDir);
		//generate HTML file
		//how it works, it displays the labels for the graph and then loads the data that corresponds to label

	    try {
			//create a new file and prepare to write to it
			FileWriter newFile = new FileWriter(HTMLfileDir, false);
    		BufferedWriter bw = new BufferedWriter(newFile);
    	    PrintWriter newFileOut = new PrintWriter(bw);
	
    	    StringBuilder sbf = new StringBuilder();

			String htmlData = ""
			+ "<!doctype html>\n"
			+ "<html>\n"
				+ "<head>\n"
					+ "<title>Learning and Performance Summary</title>\n"
					+ "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/Chart.js/1.0.2/Chart.js\"></script>\n"
				+ "</head>\n"
				+ "<body><center>\n";
			
			String title = "<h1>Learning and Performance Graphical Summary</h1><h3>Dataset: " + fileName + "</h3>"
					+ "<div style=\"width:60%\">\n";
					
			String canvases = "<div>\n"
							+ "<hr><a name=\"ruleTraining\" href=\"#top\">Go to top</a><h2>Learning Over Training Set</h2>"
							+ "<p>Training Set Size: " + trainingSetSize + "</p>"
							+ "<h3>Rule Set Coverage (% of Training Set)</h3>"
							+ "<canvas id=\"canvas2\" height=\"300\" width=\"600\"></canvas><br>\n"
							+ "<h3>Rule Coverage and Coverage Accuracy Over Training Set Uncovered by Rule Set</h3>"
							+ "<h5>(the training set reduces in size after positive examples covered by growing rule set are removed)</h5>"
							+ "<canvas id=\"canvas\" height=\"300\" width=\"600\"></canvas><br>\n"
							
							+ "<hr><a name=\"ruleSetTesting\" href=\"#top\">Go to top</a><h2>Learning Over Testing Set</h2>"
							+ "<p>Testing Set Size: " + testingSetSize + "</p>"
							+ "<h3>Rule Set Coverage and Accuracy Over Testing Set</h3>"
							+ "<h5>(How our rule set performs against the testing set as it grows)</h5>"
							+ "<canvas id=\"canvas3\" height=\"300\" width=\"600\"></canvas>\n"
							
							+ "<hr><a name=\"ruleTesting\" href=\"#top\">Go to top</a><h2>Rule Learning Statistics</h2>\n"
							+ "<p>Cover and accuracy of the rule in progress as it is being learned. Note the \"Current Training Set Size\" reduces as positive rule-set-covered examples are removed.</p>";
			//add the required number of canvases for each rule's learning stats
			int canvasId = 4;
			int ruleNum = 0;
			for(Rule r : ruleSetToPrint){
				canvases+= "<h3>Rule " + (ruleNum+1) +  ": (";
				int count = r.getAntecedent().size();
				for (Proposition a : r.getAntecedent()){
					//grab the String
					canvases += "[("+ variableNames.get(a.getAttribute()) + ")=\'"+ a.getValue() + "\']";
					if((--count) > 0){
						canvases+= " AND ";
					}
				}
				canvases += " THEREFORE ";
				//r.getConsequent().printToConsole();
				Proposition c = r.getConsequent();
				canvases += "[("+ variableNames.get(c.getAttribute()) + ")='"+ c.getValue() + "'])";
				
				canvases+="</h3>";
				
				canvases+= "<p>Current Training Set Size: " + ruleStatistics.get(ruleNum).getSampleSize() + "</p>";
				canvases+= 	"<canvas id=\"canvas" + canvasId+ "\" height=\"300\" width=\"600\"></canvas>\n";
				canvasId++;
				ruleNum++;
			}
			
			canvases += "</div>\n"
					+ "</div>\n";
			
			String tableData = "<hr><table style=\"width:100%\" border=\"1\"><tr><th colspan=\"2\"><h4> <a name=\"ruleSet\">Learned Rule Set (in order of learning)</a></h4</th></tr><tr><th>Rule Number</th><th>Rule</th></tr>";
			int count0 = 1;
			for(Rule r : ruleSetToPrint){
				tableData += "<tr><td><center>";
				tableData += count0;
				tableData +="</center></td>";
				//labels+= "\""+ r.getAntecedent()+ "\"";
				int count = r.getAntecedent().size();
				tableData += "<td>";
				//tableData += "\"";
				for (Proposition a : r.getAntecedent()){
					//grab the String
					tableData += "[("+ variableNames.get(a.getAttribute()) + ")=\'"+ a.getValue() + "\']";
					if((--count) > 0){
						tableData+= " AND ";
					}
				}
				tableData += " THEREFORE ";
				//r.getConsequent().printToConsole();
				Proposition c = r.getConsequent();
				tableData += "[("+ variableNames.get(c.getAttribute()) + ")='"+ c.getValue() + "']";
				tableData += "</td></tr>";
				count0++;
				
			}
			tableData += "</table>";	
			
			String startBod = "<script>\n"
						//+ "var randomScalingFactor = function(){ return Math.round(Math.random()*100)};\n"
						+ "var lineChartData = {\n";
			
			String labels = "labels : [\n";

			for(int i = 1; i < ruleSetToPrint.size()+1; i++){
				labels+= i;
						
				if(i+1 < ruleSetToPrint.size()+1){
					labels+= ",";
				}
			}

			labels += "],\n";
						
			String startData = "\ndatasets : [\n";
			String dataset1= "{"
						+ "		label: \"Accuracy (%)\", \n"
						+ "		fillColor : \"rgba(254,0,18,0.05)\", \n"
						+ "		strokeColor : \"rgba(254,0,18,0.29)\", \n"
						+ "		pointColor : \"rgba(254,0,18,0.29)\", \n"
						+ "		pointStrokeColor : \"#fff\", \n"
						+ "		pointHighlightFill : \"#fff\", \n"
						+ "		pointHighlightStroke : \"rgba(220,220,220,1)\", \n"
						+ "		data : [";


			count0 = ruleStatsOverRemainingToBeCoveredByRuleSet.size();
			for (Statistic s :ruleStatsOverRemainingToBeCoveredByRuleSet){
				dataset1 += s.getAccuracy();
				if((--count0) > 0){
					dataset1+= ",";
				}
			}
			
			dataset1 += "]}"; //NOTE WE NEED TO REMOVE THE EXTRA ] if we want to add more data
		
			String dataset2 = ",{\n"
							+ "		label: \"Coverage (%)\", \n"
							+ "		fillColor : \"rgba(151,187,205,0.2)\", \n"
							+ "		strokeColor : \"rgba(151,187,205,1)\", \n"
							+ "		pointColor : \"rgba(151,187,205,1)\", \n"
							+ "		pointStrokeColor : \"#fff\", \n"
							+ "	    pointHighlightFill : \"#fff\", \n"
							+ "	    pointHighlightStroke : \"rgba(151,187,205,1)\", \n"
							+ "     data : [\n";
			
			count0 = ruleStatsOverRemainingToBeCoveredByRuleSet.size();
			for (Statistic s : ruleStatsOverRemainingToBeCoveredByRuleSet){
				dataset2 += s.getCoverage();
				if((--count0) > 0){
					dataset2+= ",";
				}
			}
			dataset2 += "]}\n ] \n";
			
			String dataset3= "{"
					+ "		label: \"Rule Set Coverage (%)\", \n"
					+ "		fillColor : \"rgba(220,220,220,0.2)\", \n"
					+ "		strokeColor : \"rgba(220,220,220,1)\", \n"
					+ "		pointColor : \"rgba(220,220,220,1)\", \n"
					+ "		pointStrokeColor : \"#fff\", \n"
					+ "		pointHighlightFill : \"#fff\", \n"
					+ "		pointHighlightStroke : \"rgba(220,220,220,1)\", \n"
					+ "		data : [";

			count0 = rulesetCoverOverTrainingData.size();
			for (String a : rulesetCoverOverTrainingData){
				dataset3 += a;
				if((--count0) > 0){
					dataset3+= ",";
				}
			}
			
			dataset3 += "]}]"; //NOTE WE NEED TO REMOVE THE EXTRA ] if we want to add more data
			
			
			String dataset4= "{"
						+ "		label: \"Accuracy (%)\", \n"
						+ "		fillColor : \"rgba(254,0,18,0.05)\", \n"
						+ "		strokeColor : \"rgba(254,0,18,0.29)\", \n"
						+ "		pointColor : \"rgba(254,0,18,0.29)\", \n"
						+ "		pointStrokeColor : \"#fff\", \n"
						+ "		pointHighlightFill : \"#fff\", \n"
						+ "		pointHighlightStroke : \"rgba(220,220,220,1)\", \n"
						+ "		data : [";
			
			count0 = ruleSetStatsOverTestingData.size();
			for (Statistic s : ruleSetStatsOverTestingData){
				dataset4 += s.getAccuracy();
				if((--count0) > 0){
					dataset4+= ",";
				}
			}
			
			dataset4 += "]}"; //NOTE WE NEED TO REMOVE THE EXTRA ] if we want to add more data
		
			String dataset5 = ",{\n"
							+ "		label: \"Coverage (%)\", \n"
							+ "		fillColor : \"rgba(151,187,205,0.2)\", \n"
							+ "		strokeColor : \"rgba(151,187,205,1)\", \n"
							+ "		pointColor : \"rgba(151,187,205,1)\", \n"
							+ "		pointStrokeColor : \"#fff\", \n"
							+ "	    pointHighlightFill : \"#fff\", \n"
							+ "	    pointHighlightStroke : \"rgba(151,187,205,1)\", \n"
							+ "     data : [\n";

			
			count0 = ruleSetStatsOverTestingData.size();
			for (Statistic s : ruleSetStatsOverTestingData){
				dataset5 += s.getCoverage();
				if((--count0) > 0){
					dataset5+= ",";
				}
			}
			dataset5 += "]}\n ] \n";
			
			String dataset1End = "}; \n"
							+ "	var ctx = document.getElementById(\"canvas\").getContext(\"2d\"); \n"
							+ "	window.myLine = new Chart(ctx).Line(lineChartData, { \n"
							+ "		responsive: true, \n"
							+ "		multiTooltipTemplate: \"<%= datasetLabel %> - <%= value %>\""
							+ "	}); \n"
							+ "</script> \n";

			String dataset2End = "}; \n"
							+ "	var ctx = document.getElementById(\"canvas2\").getContext(\"2d\"); \n"
							+ "	window.myLine = new Chart(ctx).Line(lineChartData, { \n"
							+ "		responsive: true, \n"
							+ "		multiTooltipTemplate: \"<%= datasetLabel %> - <%= value %>\""
							+ "	}); \n"
							+ "</script> \n";			
							/*
							+ "Grey: "+ lateSec1 + " (EventSetID: " +  eventSetID1 + ")<br>\n"
							+ "Blue: "+ lateSec2 + " (EventSetID: " +  eventSetID2 + ")<br>\n"
							+ "Currency: "+ lastCurrencyCode + "<br>\n"
							*/
			
			String dataset3End = "}; \n"
							+ "	var ctx = document.getElementById(\"canvas3\").getContext(\"2d\"); \n"
							+ "	window.myLine = new Chart(ctx).Line(lineChartData, { \n"
							+ "		responsive: true, \n"
							+ "		multiTooltipTemplate: \"<%= datasetLabel %> - <%= value %>\""
							+ "	}); \n"
							+ "</script> \n";			

			
			String lastPart = "</center></body> \n"
							+ "</html> \n";
			
//			String allLogData = "</center></body> \n";
//			allLogData+= "</html> \n";
			
			String performanceTable = "<br><br>"
					+ "<div style=\"width:60%\">\n<hr><a name=\"performanceResults\" href=\"#top\">Go to top</a><h2>Rule Set Performance On Testing Set</h2>"
					+ "<table style=\"width:100%\" border=\"1\"><tr><th colspan=\"2\"><h4>Performance Results</h4></th></tr><tr><th>Metric</th><th>Value</th></tr>";
			performanceTable += "<tr><td><center>";
			performanceTable += "Accuracy";
			performanceTable +="</center></td>";
	
			performanceTable += "<td><center>";
			performanceTable += accuracy;
			performanceTable += "</center></td></tr>";
			
			performanceTable += "<tr><td><center>";
			performanceTable += "Error Rate";
			performanceTable +="</center></td>";
	
			performanceTable += "<td><center>";
			performanceTable += errorRate;
			performanceTable += "</center></td></tr>";
			
			performanceTable += "<tr><td><center>";
			performanceTable += "Precision";
			performanceTable +="</center></td>";
	
			performanceTable += "<td><center>";
			performanceTable +=  precision;
			performanceTable += "</center></td></tr>";
			
			performanceTable += "<tr><td><center>";
			performanceTable += "Recall";
			performanceTable +="</center></td>";
	
			performanceTable += "<td><center>";
			performanceTable += recall;
			performanceTable += "</center></td></tr>";
			
			performanceTable += "<tr><td><center>";
			performanceTable += "Sensitivity";
			performanceTable +="</center></td>";
	
			performanceTable += "<td><center>";
			performanceTable += sensitivity;
			performanceTable += "</center></td></tr>";
			
			performanceTable += "<tr><td><center>";
			performanceTable += "Specificity";
			performanceTable +="</center></td>";
	
			performanceTable += "<td><center>";
			performanceTable += specificity;
			performanceTable += "</center></td></tr>";
			
			performanceTable += "<tr><td><center>";
			performanceTable += "True Positive Rate";
			performanceTable +="</center></td>";
	
			performanceTable += "<td><center>";
			performanceTable += tPRate;
			performanceTable += "</center></td></tr>";
			
			performanceTable += "<tr><td><center>";
			performanceTable += "False Positive Rate";
			performanceTable +="</center></td>";
	
			performanceTable += "<td><center>";
			performanceTable += fPRate;
			performanceTable += "</center></td></tr>";
			
			performanceTable += "<tr><td><center>";
			performanceTable += "Negative Predictive Value";
			performanceTable +="</center></td>";
	
			performanceTable += "<td><center>";
			performanceTable += negativePredictiveVal;
			performanceTable += "</center></td></tr>";
			
			performanceTable += "<tr><td><center>";
			performanceTable += "Coverage";
			performanceTable +="</center></td>";
	
			performanceTable += "<td><center>";
			performanceTable += coverage;
			performanceTable += "</center></td></tr>";
			performanceTable += "</table>\n</div>\n";	
			
			List<String> dataSetData = new ArrayList<String>();
			//create datasets for every ruleStatistics
			int dataSetId = 4;
			int ruleNo = 0;
			for(RuleStatistics r : ruleStatistics){
				
				String datalabels = "labels : [\n";

				
				datalabels+= "\"[TRUE]\",";
				
				String currAntecedent = new String();
				//print out the x-axis...
				Rule currRule = ruleSetToPrint.get(ruleNo);

				int count = currRule.getAntecedent().size();
				for(Proposition p :  currRule.getAntecedent()){
					currAntecedent += "[("+ variableNames.get(p.getAttribute()) + ")=\'"+ p.getValue() + "\']";
					datalabels += "\"" + currAntecedent + "\"";

					if((--count) > 0){
						datalabels+= ",";
						currAntecedent+= " AND ";
					}
					
				}
				

				datalabels += "],\n";
				
				String accuracyData= "{"
							+ "		label: \"Accuracy (%)\", \n"
							+ "		fillColor : \"rgba(254,0,18,0.05)\", \n"
							+ "		strokeColor : \"rgba(254,0,18,0.29)\", \n"
							+ "		pointColor : \"rgba(254,0,18,0.29)\", \n"
							+ "		pointStrokeColor : \"#fff\", \n"
							+ "		pointHighlightFill : \"#fff\", \n"
							+ "		pointHighlightStroke : \"rgba(220,220,220,1)\", \n"
							+ "		data : [";
				
				count0 = r.getStatistics().size();
				for (Statistic s : r.getStatistics()){
					accuracyData += s.getAccuracy();
					if((--count0) > 0){
						accuracyData+= ",";
					}
				}
				
				accuracyData += "]}"; //NOTE WE NEED TO REMOVE THE EXTRA ] if we want to add more data
			
				String coverageData = ",{\n"
								+ "		label: \"Coverage (%)\", \n"
								+ "		fillColor : \"rgba(151,187,205,0.2)\", \n"
								+ "		strokeColor : \"rgba(151,187,205,1)\", \n"
								+ "		pointColor : \"rgba(151,187,205,1)\", \n"
								+ "		pointStrokeColor : \"#fff\", \n"
								+ "	    pointHighlightFill : \"#fff\", \n"
								+ "	    pointHighlightStroke : \"rgba(151,187,205,1)\", \n"
								+ "     data : [\n";
				
				
				count0 = r.getStatistics().size();
				for (Statistic s : r.getStatistics()){
					coverageData += s.getCoverage();
					if((--count0) > 0){
						coverageData+= ",";
					}
				}
				coverageData += "]}\n ] \n";
			
			
			
				
				String dataEnd = "}; \n"
								+ "	var ctx = document.getElementById(\"canvas"+ dataSetId + "\").getContext(\"2d\"); \n"
								+ "	window.myLine = new Chart(ctx).Line(lineChartData, { \n"
								+ "		responsive: true, \n"
								+ "		multiTooltipTemplate: \"<%= datasetLabel %> - <%= value %>\""
								+ "	}); \n"
								+ "</script> \n";
				dataSetId++;
				ruleNo++;
				
				dataSetData.add(datalabels);
				dataSetData.add(accuracyData);
				dataSetData.add(coverageData);
				dataSetData.add(dataEnd);
				
				
			}
			
			String contentDir = "<hr><ol>"
								+"<h4 id=\"top\">Contents</h4>"
								+ "<li><a href=\"#ruleSet\">Rule Set</a></li>"
								+ "<li><a href=\"#ruleTraining\">Learning Over Training Set</a></li>"
								+ "<li><a href=\"#ruleSetTesting\">Learning Over Testing Set</a></li>"
								+ "<li><a href=\"#ruleTesting\">Rule Learning Statistics</a></li>"
								+ "<li><a href=\"#performanceResults\">Performance Results</a></li>";
			contentDir+= 		"</ol>";
			//combine these pieces together...
			sbf.append(htmlData);
			sbf.append(title);			
			//sbf.append(trainingSetData);	
			sbf.append(contentDir);	
			sbf.append(tableData);	
			sbf.append(canvases);		
			sbf.append(startBod);
			sbf.append(labels);
			sbf.append(startData);
			sbf.append(dataset1);
			sbf.append(dataset2);
			sbf.append(dataset1End);
			sbf.append(startBod);
			sbf.append(labels);
			sbf.append(startData);
			sbf.append(dataset3);
			sbf.append(dataset2End);
			
			sbf.append(startBod);
			sbf.append(labels);
			sbf.append(startData);
			sbf.append(dataset4);
			sbf.append(dataset5);
			sbf.append(dataset3End);
			
			for(int i = 0; i < ruleStatistics.size(); i++){
				int dataIndex = i*4;
				sbf.append(startBod);
				sbf.append(dataSetData.get(dataIndex));
				sbf.append(startData);
				sbf.append(dataSetData.get(dataIndex+1));
				sbf.append(dataSetData.get(dataIndex+2));
				sbf.append(dataSetData.get(dataIndex+3));
			}
			sbf.append(performanceTable);
			sbf.append(lastPart);
				
			//put out to file
			//add to file
	    	newFileOut.println(sbf.toString());
	    	newFileOut.close();
    
	    } catch (IOException e2){ 

			System.out.println(e2);
	    }
		    	
	
		return HTMLfileDir;

	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public List<Rule> getRuleSetToPrint() {
		return ruleSetToPrint;
	}
	public void setRuleSetToPrint(List<Rule> ruleSetToPrint) {
		this.ruleSetToPrint = ruleSetToPrint;
	}
	public List<Statistic> getRuleStatsOverRemainingToBeCoveredByRuleSet() {
		return ruleStatsOverRemainingToBeCoveredByRuleSet;
	}
	public void setRuleStatsOverRemainingToBeCoveredByRuleSet(
			List<Statistic> ruleStatsOverRemainingToBeCoveredByRuleSet) {
		this.ruleStatsOverRemainingToBeCoveredByRuleSet = ruleStatsOverRemainingToBeCoveredByRuleSet;
	}
	public String getHtmlLocation() {
		return htmlLocation;
	}
	public void setHtmlLocation(String htmlLocation) {
		this.htmlLocation = htmlLocation;
	}
	public List<String> getRulesetCoverOverTrainingData() {
		return rulesetCoverOverTrainingData;
	}
	public void setRulesetCoverOverTrainingData(
			List<String> rulesetCoverOverTrainingData) {
		this.rulesetCoverOverTrainingData = rulesetCoverOverTrainingData;
	}
	public List<Statistic> getRuleSetStatsOverTestingData() {
		return ruleSetStatsOverTestingData;
	}
	public void setRuleSetStatsOverTestingData(
			List<Statistic> ruleSetStatsOverTestingData) {
		this.ruleSetStatsOverTestingData = ruleSetStatsOverTestingData;
	}
	public List<String> getVariableNames() {
		return variableNames;
	}
	public void setVariableNames(List<String> variableSet) {
		this.variableNames = variableSet;
	}
	public int getTrainingSetSize() {
		return trainingSetSize;
	}
	public void setTrainingSetSize(int trainingSetSize) {
		this.trainingSetSize = trainingSetSize;
	}
	public int getTestingSetSize() {
		return testingSetSize;
	}
	public void setTestingSetSize(int testingSetSize) {
		this.testingSetSize = testingSetSize;
	}
	public String getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(String accuracy) {
		this.accuracy = accuracy;
	}
	public String getErrorRate() {
		return errorRate;
	}
	public void setErrorRate(String errorRate) {
		this.errorRate = errorRate;
	}
	public String getPrecision() {
		return precision;
	}
	public void setPrecision(String precision) {
		this.precision = precision;
	}
	public String getRecall() {
		return recall;
	}
	public void setRecall(String recall) {
		this.recall = recall;
	}
	public String getSensitivity() {
		return sensitivity;
	}
	public void setSensitivity(String sensitivity) {
		this.sensitivity = sensitivity;
	}
	public String getSpecificity() {
		return specificity;
	}
	public void setSpecificity(String specificity) {
		this.specificity = specificity;
	}
	public String gettPRate() {
		return tPRate;
	}
	public void settPRate(String tPRate) {
		this.tPRate = tPRate;
	}
	public String getfPRate() {
		return fPRate;
	}
	public void setfPRate(String fPRate) {
		this.fPRate = fPRate;
	}
	public String getNegativePredictiveVal() {
		return negativePredictiveVal;
	}
	public void setNegativePredictiveVal(String negativePredictiveVal) {
		this.negativePredictiveVal = negativePredictiveVal;
	}
	public String getCoverage() {
		return coverage;
	}
	public void setCoverage(String coverage) {
		this.coverage = coverage;
	}
	public void appendToRuleSet(Rule newRule){
		ruleSetToPrint.add(newRule);
	}
	public void appendToRuleCoverStat(Statistic newStat){
		ruleStatsOverRemainingToBeCoveredByRuleSet.add(newStat);
	}
	public void appendToRuleSetCoverage(String newStat){
		rulesetCoverOverTrainingData.add(newStat);
	}
	public void appendToRuleSetStat(Statistic newStat){
		ruleSetStatsOverTestingData.add(newStat);
	}
	public void appendToRuleStatsList(RuleStatistics newStats){
		ruleStatistics.add(newStats);
	}
}
