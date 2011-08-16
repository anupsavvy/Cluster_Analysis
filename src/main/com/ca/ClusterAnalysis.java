package main.com.ca;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.io.FileReader;
import org.apache.log4j.Logger;

public class ClusterAnalysis {
    private static Logger log = Logger.getLogger(ClusterAnalysis.class);
	public static void main(String args[]) throws ClusterAnalysisException{

		Properties properties = new Properties();
		try{
			log.info("Loading properties");
			properties.load(new FileReader("properties/clusterAnalysis.properties"));
		}catch(Exception e){
			log.error(e, e);
			throw new ClusterAnalysisException(e.getMessage());
		}
		
		CreateVectors createVectors = CreateVectors.getInstance(properties.getProperty("oldCluster")
				,properties.getProperty("newCluster"),
				properties.getProperty("clusterOneSamples"),
				properties.getProperty("clusterTwoSamples"));
		createVectors.formDictionary();
		
		for(int i =0; i< createVectors.getClusterOneSamples(); i++){
			createVectors.formVector(null, i, createVectors.getOldClusterPath());
		}
		for(int i=createVectors.getClusterOneSamples(); i< createVectors.getClusterOneSamples()
				+createVectors.getClusterTwoSamples();i++){
			createVectors.formVector(null, i, createVectors.getNewClusterPath());
		}
		createVectors.formVector(properties.getProperty("query"), createVectors.getClusterOneSamples()+
				createVectors.getClusterTwoSamples()+2, null);
		
		ClusterStats clusterStats = new ClusterStats();
		clusterStats.normalizeVectors();
		clusterStats.calcuateMean(1);
		clusterStats.calcuateMean(2);
		clusterStats.calculateDistance(1);
		clusterStats.calculateDistance(2);
	}

}

class ClusterAnalysisException extends RuntimeException{
	public ClusterAnalysisException(String message){
		super(message);
	}
}

