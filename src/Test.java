import java.io.*;
import java.util.*;

import javax.security.auth.kerberos.KerberosKey;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.dic.Dictionary;

public class Test {
	private static File[] lilydocs = new File[10];
	private static File[]lilydocsout = new File[10];
	private static File Chinese_stopwordFile = new File("src\\Chinese-stop-words.dic");
	private static File English_stopwordFile = new File("src\\stopword.dic");
	
	private static List<String[]> []instances = new List[10];
	private static Vector<String> dictionary = new Vector<String>();//all words stored here
	private static Map<Integer,Double>tf = null;
	private static Map<String, Double> idf = new HashMap<String, Double>(); 
	private static int []instancecount = new int[10];
	private static int allinstancecount = 0;
public static void main(String []args) throws FileNotFoundException{
	lilydocs[0] = new File("doc\\lily\\Basketball.txt");
	lilydocs[1] = new File("doc\\lily\\D_Computer.txt");
	lilydocs[2] = new File("doc\\lily\\FleaMarket.txt");
	lilydocs[3] = new File("doc\\lily\\Girls.txt");
	lilydocs[4] = new File("doc\\lily\\JobExpress.txt");
	lilydocs[5] = new File("doc\\lily\\Mobile.txt");
	lilydocs[6] = new File("doc\\lily\\Stock.txt");
	lilydocs[7] = new File("doc\\lily\\V_Suggestions.txt");
	lilydocs[8] = new File("doc\\lily\\WarAndPeace.txt");
	lilydocs[9] = new File("doc\\lily\\WorldFootball.txt");
	
	lilydocsout[0] = new File("out\\lily\\Basketball.txt");
	lilydocsout[1] = new File("out\\lily\\D_Computer.txt");
	lilydocsout[2] = new File("out\\lily\\FleaMarket.txt");
	lilydocsout[3] = new File("out\\lily\\Girls.txt");
	lilydocsout[4] = new File("out\\lily\\JobExpress.txt");
	lilydocsout[5] = new File("out\\lily\\Mobile.txt");
	lilydocsout[6] = new File("out\\lily\\Stock.txt");
	lilydocsout[7] = new File("out\\lily\\V_Suggestions.txt");
	lilydocsout[8] = new File("out\\lily\\WarAndPeace.txt");
	lilydocsout[9] = new File("out\\lily\\WordFootball.txt");
	try {
		//read Chinese stop word file
		List<String> stopword = new ArrayList<String>();
	
		InputStreamReader read1 = new InputStreamReader(new FileInputStream(Chinese_stopwordFile),"UTF-8");
		BufferedReader reader1 = new BufferedReader(read1);
		String line = null;
		while((line = reader1.readLine())!=null){
			stopword.add(line.trim());
		}
		reader1.close();
		
		//read English stop word file
		read1 = new InputStreamReader(new FileInputStream(English_stopwordFile),"UTF-8");
		reader1 = new BufferedReader(read1);
		line = null;
		while((line = reader1.readLine())!=null){
			stopword.add(line.trim());
		}
		reader1.close();

		InputStreamReader []read2 = new InputStreamReader[10];
		for(int i = 0;i<10;i++)
			read2[i] = new InputStreamReader(new FileInputStream(lilydocs[i]),"UTF-8");
		BufferedReader[] reader2 = new BufferedReader[10];
		for(int i = 0;i<10;i++)
			reader2[i] = new BufferedReader(read2[i]);
		
		System.out.println("Start process the data");
		System.out.println("Please wait...");
		IKSegmenter []ik = new IKSegmenter[10];
		for(int i = 0;i<10;i++){

			String instance = null;
			instancecount[i] = 0;
			instances[i] = new Vector<String[]>();
			
			//read a line in a file
			while((instance = reader2[i].readLine())!=null){
				instancecount[i]++;
				allinstancecount++;
				Vector<String>vtemp = new Vector<String>();
				
				ik[i] = new IKSegmenter(new StringReader(instance),true);
				
				
				Lexeme lexeme = null;
				String word = null;
				//count the number of words in a line
				int count_line = 0;
				while((lexeme=ik[i].next())!=null){
					count_line++;
				}
				ik[i].reset(new StringReader(instance));
				String []linewords = new String[count_line];
				
				int j = 0;
				while((lexeme = ik[i].next())!=null){
					word = lexeme.getLexemeText().trim();
					if(stopword.contains(word)==false)
						linewords[j++] = word;//store the word
					
						if(dictionary.contains(word)==false){//dictionary doesn't contain the word
							dictionary.add(word);
						}
				  
				  		if(vtemp.contains(word)==false){
				  			if(idf.containsKey(word)==false)
				  				idf.put(word, 1.0);
				  			else
				  				idf.put(word, idf.get(word)+1);
				  			vtemp.add(word);
				  		}
				}
				/*for(String a:linewords)System.out.print(a);
				System.out.println("");
				System.out.println("插入前大小:"+instances[i].size());
				 */
				instances[i].add(linewords);
				//System.out.println("插入后大小:"+instances[i].size());
			}
		}
		
		for(String a:idf.keySet())
			idf.replace(a, Math.log10((double)allinstancecount/idf.get(a)));
		System.out.println("Size of dictionary is:"+dictionary.size());
		System.out.println("Please wait...");
		//System.out.println(allinstancecount);
		
		FileOutputStream fileout = null;
		
		for(int i = 0;i<10;i++){
			fileout = new FileOutputStream(lilydocsout[i]);
			
			//every file
			//System.out.println(instances[i].size());
			for(int j = 0;j<instances[i].size();j++)
			{
				//every instance
				if(tf!=null){
					tf.clear();
				}
				tf = new HashMap<Integer,Double>();
				String []lineword = instances[i].get(j);
				//for(String a:lineword)System.out.print(a);
				int linewordcount = lineword.length;
				for(int k = 0;k<linewordcount;k++){
					//if(dictionary.contains(lineword[k])==false)System.out.println("不存在于单词表的单词");
					if(tf.containsKey(dictionary.indexOf(lineword[k]))==false){
						//System.out.println("charu "+dictionary.indexOf(lineword[k])+" "+lineword[k]);
						tf.put(dictionary.indexOf(lineword[k]), 1.0);
						//System.out.println(tf.get(dictionary.indexOf(lineword[k])));
					}
					else
						tf.replace(dictionary.indexOf(lineword[k]), tf.get(dictionary.indexOf(lineword[k]))+1);
				}
				Set<Integer> s = new TreeSet<Integer>(tf.keySet());
				for(Integer a:s){
					//System.out.println(tf.get(a));
					//System.out.println(idf.get(dictionary.get(a)));
					tf.replace(a, tf.get(a)/linewordcount*idf.get(dictionary.get(a)));
					String out = String.format("%d:%.4f ",a.intValue(),tf.get(a));
					fileout.write(out.getBytes());
				}
				if(i<instances[i].size()-1)
					fileout.write("\n".getBytes());
			}
			fileout.close();
		}
		/*
		for(int i =0;i<dictionary.size();i++){
			System.out.println(dictionary.get(i));
		}*/
		for(int i = 0;i<10;i++)
			reader2[i].close();
		System.out.println("Process finish!");
		System.out.println("Output file is stored in floder \"out\\lily\"");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
}
