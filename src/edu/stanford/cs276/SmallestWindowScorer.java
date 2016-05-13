package edu.stanford.cs276;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import edu.stanford.cs276.util.WordPosition;

/**
 * A skeleton for implementing the Smallest Window scorer in Task 3.
 * Note: The class provided in the skeleton code extends BM25Scorer in Task 2. However, you don't necessarily
 * have to use Task 2. (You could also use Task 1, in which case, you'd probably like to extend CosineSimilarityScorer instead.)
 * Also, feel free to modify or add helpers inside this class.
 */
public class SmallestWindowScorer extends CosineSimilarityScorer {
	
  	double urlweight = 0.4;
  	double titleweight  = 0.4;
  	double bodyweight = 0.5;
  	double headerweight = 0.4;
  	double anchorweight = 0.1;
  	double smoothingBodyLength = 50.0; 
	
	
	public SmallestWindowScorer(Map<String, Double> idfs, Map<Query,Map<String, Document>> queryDict) {
		super(idfs);
	}

	/**
	 * get smallest window of one document and query pair.
	 * @param d: document
	 * @param q: query
	 */	
	private int getSmallestWindow(Document d, Query q) {
		int smallest = Integer.MAX_VALUE;
		
		
		//URL
		System.out.println("url");
		List<WordPosition> urlPositions = this.createPositions(d.url.split("[/_\\-.]"), q);
		int checkSmallest = this.smallestWindow(urlPositions, q);
		smallest = Integer.min(smallest, checkSmallest);		
		
		//TITLE
		System.out.println("title");
		List<WordPosition> titlePositions = this.createPositions(d.title.split(" "), q);
		checkSmallest = this.smallestWindow(titlePositions, q);
		smallest = Integer.min(smallest, checkSmallest);
		
		//HEADERS
	
		if (d.headers != null) {
			System.out.println("headers");
			for (String headers : d.headers) {
				List<WordPosition> headerPositions = this.createPositions(headers.split(" "), q);
				checkSmallest = this.smallestWindow(headerPositions, q);
				smallest = Integer.min(smallest, checkSmallest);
			}
		}
		
		//BODY POSITIONS
		if (d.body_hits != null) {
			System.out.println("body");
			List<WordPosition> bodyPositions = this.createBodyPositions(d.body_hits, q);
			checkSmallest = this.smallestWindow(bodyPositions, q);
			smallest = Integer.min(smallest, checkSmallest);
		}
		
		//ANCHOR TEXTS
		if (d.anchors != null) {
			System.out.println("anchor");
			for (String anchorText : d.anchors.keySet()) {
				List<WordPosition> anchorPositions = this.createPositions(anchorText.split(" "), q);
				checkSmallest = this.smallestWindow(anchorPositions, q);
				smallest = Integer.min(smallest, checkSmallest);
			}
		}
		
		
		
		return smallest;
	}


	

	
//	private Map<String, List<Integer>> createPositions(String field, Query q) {
//		Map<String, List<Integer> > postitions = new HashMap<String, List<Integer> >();
//		Set<String> qSet = new HashSet<String>(q.queryWords);
//		
//		
//		String[] words = field.split(" ");
//		
//		if (words.length < q.queryWords.size() ) {
//			return null;
//		}
//		
//		for (int i = 0; i < words.length; i++) {
//			if (qSet.contains(words[i])) {
//				if (postitions.containsKey(words[i])) {
//					postitions.get(words[i]).add(i);
//				} else {
//					List<Integer> newPositionList = new ArrayList<Integer>();
//					newPositionList.add(i);
//					postitions.put(words[i], newPositionList);
//				}
//			}
//		}
//		return postitions;
//	}
	
	private List<WordPosition> createBodyPositions(Map<String, List<Integer> > bodyPositions, Query q) {
		List<WordPosition> positions = new ArrayList<WordPosition>();
		for (String word : bodyPositions.keySet()) {
			List<Integer> wordPositions = bodyPositions.get(word);
			for (Integer i : wordPositions) {
				WordPosition newPos = new WordPosition(word, i);
				positions.add(newPos);
			}
		}
		Collections.sort(positions);
		return positions;
	}
	
	private String arrToString(String[] fields) {
		String ret = "[";
		for (String i : fields) {
			ret += i + ",";
		}
		ret += "]";
		return ret;
	}
	
	private List<WordPosition> createPositions(String[] fields, Query q) {
		System.out.println("field: " + arrToString(fields));
		List<WordPosition> positions = new ArrayList<WordPosition>();
		String[] words = fields;
		Set<String> qWords = new HashSet<String>(q.queryWords);
		
		for (int i = 0; i < words.length; i++) {
			if (qWords.contains(words[i])) {
				WordPosition newPos = new WordPosition(words[i],i);
				positions.add(newPos);
			}
		}
		return positions;
	}
	
	private int smallestWindow (List<WordPosition> positions, Query q) {
		int smallest = Integer.MAX_VALUE;
		Set<String> qSet = new HashSet<String>(q.queryWords);
		Set<String> wordsFound = new HashSet<String>();
		List<WordPosition> window = new LinkedList<WordPosition>();
		
		
		for (WordPosition p : positions) {
			if (qSet.contains(p.word)) {
				window.add(p);
				wordsFound.add(p.word);
			}
			if (window.size() < 1) {
				continue;
			}
			if (window.get(0).word.equals(p.word) && wordsFound.equals(qSet)) {
				window.remove(0);
			}
			
			if (wordsFound.equals(qSet)) {
				int end = window.get(window.size() - 1).position;
				int begin = window.get(0).position;
				int windowLen = end - begin + 1;
				if (windowLen < smallest) {
					smallest = windowLen;
				}
			}
		}
		
		System.out.print("window: " + window.toString() + "\n");
		System.out.println("distance: " + smallest + "\n");
		
		return smallest;
	}
	
	/**
	 * get boost score of one document and query pair.
	 * @param d: document
	 * @param q: query
	 */	
	private double getBoostScore (Document d, Query q) {
		System.out.println("query: " + q.toString());
		System.out.println(d.toString());
		int smallestWindow = getSmallestWindow(d, q);
		double boostScore = 0;
		/*
		 * @//TODO : Your code here, calculate the boost score.
		 *
		 */
		return boostScore;
	}
	
	

	
	@Override
	public double getSimScore(Document d, Query q) {
		Map<String,Map<String, Double>> tfs = this.getDocTermFreqs(d,q);
		this.normalizeTFs(tfs, d, q);
		Map<String,Double> tfQuery = getQueryFreqs(q);
		double boost = getBoostScore(d, q);
		double rawScore = this.getNetScore(tfs, q, tfQuery, d);
		return boost * rawScore;
	}

}

//public Double getWindow(List<String> fields, Query q) {
//Double minWindow = Double.MAX_VALUE;
//Set<String> qSet = new HashSet<String>(q.queryWords);
//
//for (String field: fields) {
//	Set <String> needSet = new HashSet<String>(qSet);
//	Set <String> foundSet = new HashSet<String>();
//	Double minWindowLen = 0.0;
//	String[] words = field.split(" ");
//	if (words.length < q.queryWords.size()) {
//		continue;
//	}
//	
//	for (int start = 0, end = 0 ; end < words.length; end++) {
//		if (!needSet.contains(words[end])){
//			continue;
//		} else if (qSet.contains(words[end])){
//			foundSet.add(words[end]);
//			needSet.remove(words[end]);
//		}
//		
//		if (foundSet.equals(qSet) ) {
//			
//		}
//		
//	}
//	
//}
//if (minWindow == Double.MAX_VALUE) {
//	return -1.0;
//}
//return minWindow;
//}