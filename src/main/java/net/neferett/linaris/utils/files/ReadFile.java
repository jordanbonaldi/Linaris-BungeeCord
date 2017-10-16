package net.neferett.linaris.utils.files;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadFile{
	
	protected String				file;
	protected ArrayList<String>		allFile = new ArrayList<>();
	protected BufferedReader 		br;
	protected String				name;
	
	public ReadFile(String path) throws IOException{
		this.file = path;
		this.br = this.Open();
		this.Read();
	}
	
	public BufferedReader Open() throws FileNotFoundException{
		return (new BufferedReader(new FileReader(this.file)));
	}
	
	public void Close() throws IOException{
		br.close();
	}
	
	public void Read() throws IOException{
		String str;

		while ((str = br.readLine()) != null){
			allFile.add(str);
		}
	}
	
	public ArrayList<String> getFile(){
		return (this.allFile);
	}

}