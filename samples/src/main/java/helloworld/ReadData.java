package helloworld;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.csvreader.CsvReader;

public class ReadData {
	private CsvReader csvreader;
	private ArrayList<Integer> IDlist;
	private ArrayList<Integer> values;
	private int offset;
	
	public ReadData(String filename){
		try {
			csvreader = new CsvReader(filename, ',');
			if(filename == "/samples/uwmp_table_2_1_r_conv_to_af.csv"){
				offset = 9;
			}
			if(filename == "/samples/uwmp_table_3_1_r.csv"){
				offset = 6;
			}
			else{
				System.err.println("Not sure how to format this file");
			}
			for(int q = 1; q < (csvreader.getValues().length+1)/10; q++){
				IDlist.add(Integer.parseInt(csvreader.get((q-1)*10))); 
				values.add(Integer.parseInt(csvreader.get(offset + ((q-1)*10)))); 
			}//Gets first and every 9th column value of the table
			//Double check this logic in case of an error
			
			combineLists();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<Integer> combineLists(){
		for(int q = 0; q < IDlist.size(); q++){
			if(q > 0 && IDlist.get(q) == IDlist.get(q-1)){
				IDlist.remove(q);
				values.set(q-1, values.get(q-1)+values.get(q));
				values.remove(q);
				q--; //Because I remove an element, I have to fix the indexing
			}
		}
		return values;
	}
	
	
}
