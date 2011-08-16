package main.com.ca;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.log4j.Logger;

public class CreateVectors {
	private static Logger log = Logger.getLogger(CreateVectors.class);
	private String oldCluster = null;
	private String newCluster = null;
	private int clusterOneSamples = 0;
	private int clusterTwoSamples = 0;
	public  static Map<String,double[]> dictionary= new HashMap<String,double[]>();
	private static CreateVectors createVectors = null;

	private CreateVectors(String oldCluster, String newCluster, String clusterOneSamples,
			String clusterTwoSamples){

		this.oldCluster = oldCluster;
		this.newCluster = newCluster;
		this.clusterOneSamples = Integer.parseInt(clusterOneSamples);
		this.clusterTwoSamples = Integer.parseInt(clusterTwoSamples);
	}

	public static CreateVectors getInstance(String oldCluster, String newCluster, String clusterOneSamples,
			String clusterTwoSamples){
		if(createVectors == null){
			createVectors = new CreateVectors(oldCluster, newCluster, clusterOneSamples,
					clusterTwoSamples);
			return createVectors;
		}else{
			return createVectors;
		}
	}

	public void formDictionary(){

		log.info("Reading files to create dictionary");
		log.info("Reading cluster one");
		readFile(this.clusterOneSamples,this.oldCluster);
		log.info("Reading cluster two");
		readFile(this.clusterTwoSamples,this.newCluster);
		log.info("Dictionary formed with size : " + CreateVectors.dictionary.size());
	}

	public void formVector(String str,int sampleNumber,String path){
		Pattern pattern = Pattern.compile("[`:'></;.?,\\[\\]()\\s]+");
		String[] tokens = null;
		String token = null;
		if(str == null){
			try{
				BufferedReader buffRd = null;
				StringBuffer line = new StringBuffer();
				if(sampleNumber < this.clusterOneSamples)
					buffRd = new BufferedReader(new InputStreamReader(new FileInputStream
							(new File(path+Integer.toString(sampleNumber)+".txt"))));
				else
					buffRd = new BufferedReader(new InputStreamReader(new FileInputStream
							(new File(path+Integer.toString(sampleNumber-this.clusterOneSamples)+".txt"))));
				while(buffRd.ready()){
					line.append(buffRd.readLine());
				}
				str = line.toString();
			}catch(Exception e){
				log.error(e, e);
			}
		}
		tokens = pattern.split(str);
		log.info("Forming vector for sample number : "+ sampleNumber);
		for(int i =0; i < tokens.length; i++){
			token = tokens[i].toLowerCase();
			double[] list = CreateVectors.dictionary.get(token);
			list[sampleNumber]++;
			CreateVectors.dictionary.put(token, list);
		}
	}

	public void readFile(int samples, String path){
		Pattern pattern = Pattern.compile("[`:'></;.?,\\[\\]()\\s]+");
		BufferedReader buffRd = null;
		String line = null;
		String[] tokens = null;
		String token = null;
		FileWriter filewrt = null;
		try {
			filewrt = new FileWriter("files/dictionary.txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while(samples > 0){
			try{
				buffRd = new BufferedReader(new InputStreamReader(new FileInputStream
						(new File(path+Integer.toString(--samples)+".txt"))));

				while(buffRd.ready()){
					line = buffRd.readLine();
					tokens = pattern.split(line);
					for(int i =0; i < tokens.length; i++){
						token = tokens[i].toLowerCase();
						if(!CreateVectors.dictionary.containsKey(token)){
							CreateVectors.dictionary.put(token, new double[this.clusterOneSamples+this.clusterTwoSamples+3]);
							filewrt.write(token);
							filewrt.flush();
							filewrt.write("\n");
							filewrt.flush();
						}
					}

				}
			}catch(Exception e){
				log.error(e, e);
			}
		}
	}

	public int getClusterOneSamples(){
		return this.clusterOneSamples;
	}
	public int getClusterTwoSamples(){
		return this.clusterTwoSamples;
	}
	public String getOldClusterPath(){
		return this.oldCluster;
	}
	public String getNewClusterPath(){
		return this.newCluster;
	}


}
