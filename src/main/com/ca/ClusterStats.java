package main.com.ca;
import java.util.Collection;
import java.util.Iterator;
import org.apache.log4j.Logger;
import java.io.FileWriter;
import java.io.IOException;

import Jama.Matrix;

public class ClusterStats {
	private static Logger log = Logger.getLogger(ClusterStats.class);
	private CreateVectors createVectors = CreateVectors.getInstance(null, null, null,null);
	private double[][] vectors = null;
	private Matrix mat = null;
    private FileWriter filewrt = null;
	public void calcuateMean(int clusterNumber){
		log.info("Calculating mean for cluster number: " + clusterNumber);
		try {
			filewrt = new FileWriter("files/mean"+clusterNumber+".txt");
		} catch (IOException e) {
			log.error(e, e);
		}
		int runningSum = 0; 
		int start,end,norm;
		if(clusterNumber ==1){
			start=0;
			end=createVectors.getClusterOneSamples();
			norm = createVectors.getClusterOneSamples();
		}else if(clusterNumber ==2){
			start=createVectors.getClusterOneSamples();
			end=createVectors.getClusterOneSamples()+createVectors.getClusterTwoSamples();
			norm = createVectors.getClusterTwoSamples();
		}else{
			return;
		}
		int j;
		for(int i=0; i< mat.getRowDimension(); i++){
			for(j= start; j<end ; j++){
				runningSum += mat.get(i, j);
			}
			mat.set(i,createVectors.getClusterOneSamples()+
					createVectors.getClusterTwoSamples()+clusterNumber-1, runningSum/norm);
			try {
				filewrt.write(Double.toString(runningSum/norm));
				filewrt.flush();
				filewrt.write("\n");
				filewrt.flush();
			} catch (IOException e) {
				log.error(e, e);
			}
			runningSum=0;
		}

	}
	
	public void calculateDistance(int clusterNumber){
		log.info("Calculating distance of query vector from both clusters");
		double distance =0;
		int limit = createVectors.getClusterOneSamples() + createVectors.getClusterTwoSamples();
		
		
			for(int j=0; j< mat.getColumnDimension();j++){
				distance += Math.pow(mat.get(limit+clusterNumber-1,j)-mat.get(limit+2, j), 2);
			}
			log.info("Distance from cluster number " + clusterNumber + " is : " + distance);
		
	}

	public void normalizeVectors(){
		log.info("Normalizing all vectors by sum of dimensions");
		ClusterStats clusterStats = new ClusterStats();
		vectors = new double[CreateVectors.dictionary.size()][clusterStats.createVectors.getClusterOneSamples()+
		                     clusterStats.createVectors.getClusterTwoSamples()+
		                     3];
		Collection<double[]> coll = CreateVectors.dictionary.values();
	    Iterator<double[]> itr = coll.iterator();
	    int dimensions =0;
	    while(itr.hasNext()){
	    	vectors[dimensions]= itr.next();
	    	dimensions++;
	    }
	    
		
		mat = new Matrix(vectors);
		double norm = mat.norm1();
		log.info("Norm value is : " + norm);
		double val =0;
		for(int i =0; i < mat.getRowDimension(); i++){
			for(int j=0; j < mat.getColumnDimension(); j++){
				val = mat.get(i, j);
				mat.set(i, j, (val));
			}
		}
		
	}

}
