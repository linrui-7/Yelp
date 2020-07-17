package hw5;
import java.io.*;
import java.util.*;

public class YelpAnalysis{
    private static FileReader in;
    private static BufferedReader bin;
    //meaningless words
    private static String[] commonwords = {"a","an","the","is","was","are","were","i","me","my",
        "mine","you","your","yours","he","him","his","she","her","hers","we","our","us",
        "it","its","they","their","theirs","and","but","this","that","to","of","for","with","in","on"};
    private static HashSet<String> meaningless;   
    private static HashSet<String> corpusCheck = new HashSet<String>();
    private static HashMap<String,Integer> corpusDFCount = new HashMap<String,Integer>();
    public static void main(String[] args) throws IOException{
        //map to store duplicated words and their counts
        
        //priorityqueue to store top 10 business
        PriorityQueue<Business> businessQueue = new PriorityQueue<Business>();

        //set contains all meaningless words
        meaningless = new HashSet<String>(Arrays.asList(commonwords));
        
            in = new FileReader("yelpDatasetParsed_short.txt");//open the file
            bin = new BufferedReader(in);//read the file

            readBusiness(businessQueue);

            // close streams
            bin.close();
            in.close();
        //print out needed info
        for(int i=0;i<10;i++)
        {   //codes from assignment instructions
            Business currB = businessQueue.remove();
            Map<String,Double> tfidfScoreMap = getTfidfScore(currB, 5);
            List<Map.Entry<String,Double>> tfidfScoreList = new ArrayList <>(tfidfScoreMap.entrySet());
            getTfidfScore(currB,5);
            sortByTfidf(tfidfScoreList);
            System.out.println(currB);
            printTopWords(tfidfScoreList,30);
        }
    }

   
    private static void readBusiness(PriorityQueue<Business> businessQueue) throws IOException {//read file method
        StringBuilder str = new StringBuilder();//use stringbuilder to store info

        String[] info = new String[4]; // record the 4 parts for the business object
    
        int count = 0;
        while(true){//stop when read the end of the file
            int check = bin.read();
            if(check == -1) {
                break;
            }

            char c = (char) check;//cast charcode to char

            if(c != '{' && c != '\n' && c!= '\r'){ //skip empty char and "{"
                if(c == ',' || c == '}'){ //when reacing the end of one part
                    info[count] = str.toString();//store info to different parts(ID,Name,Address)
                    count++;
                    if(count >= 4){//review part
                        
                        // add words to corpus
                        addDocumentCount(info[3]);

                        // add one if the queue is not full 
                        businessQueue.add(new Business(info));
                        if (businessQueue.size()>10){
                            businessQueue.remove(); //else, remove the first one
                        }
                    
                        count = 0;
                    }
                    // delete previous info
                    str.delete(0, str.length());
                }
                else{
                    // add char to the stringbuilder otherwise
                    str.append(c);
                }
                    
            }
        }
    }

    private static void addDocumentCount(String review){
        // use scanner to read from the file
        String word = "";
        Scanner add = new Scanner(review);
    
        while(add.hasNext()){
            word = add.next();//store to word
            if(!meaningless.contains(word)){ // if the word is not in meaningless list
            
                // if the word is new
                if(!corpusCheck.contains(word)){
                    if(corpusDFCount.containsKey(word)){
                        corpusDFCount.put(word,corpusDFCount.get(word)+1);
                    }    // if corpus contains the word, increase count by 1
                    else {
                        corpusDFCount.put(word, 1);
                    } // if not, add it to the map and put 1 for count

                    corpusCheck.add(word);//add to check list in case of duplicates
                }
            }
        }
        //clear check list and close stream
        corpusCheck.clear();
        add.close();
    }
    public static <K,V> void printTopWords(List<Map.Entry<K,V>> businessQueue, int n){
        for(int i = 0; i < n; i++){
            K key = businessQueue.get(i).getKey();
            V value = businessQueue.get(i).getValue();
            // formatting the output - two digits after the decimal point
            System.out.format("("+key.toString()+",%.2f)",value);

        }
        System.out.println();
       
    }

    public static <K,V extends Comparable<V>> void sortByTfidf(List<Map.Entry<K,V>> businessQueue){//use comparable to compare and then sort
        Collections.sort(businessQueue, new Comparator<Map.Entry<K,V>>(){
        @Override //override the compare method for this case
        public int compare(Map.Entry<K,V> score1, Map.Entry<K,V> score2)
        {
            return score1.getValue().compareTo(score2.getValue());
        }
        });
    }
    public static Map<String,Double> getTfidfScore(Business b, int x){
        // get the current map
        HashMap<String,Integer> currentDFCount = b.getCurrentDFCount(meaningless);
        HashMap<String,Double> tfidfScoreMap = new HashMap<String,Double>();
        for(String key: currentDFCount.keySet()){//interating through the map 

            
            if(corpusDFCount.get(key) >= x){//when it is larger than x, calculate the score
            
                double value = (double)(currentDFCount.get(key))/(double)(corpusDFCount.get(key));

                tfidfScoreMap.put(key,value);//add new key and value to new map entry
            }
            else {
              tfidfScoreMap.put(key,0.0);// if it is less, put 0 for the score
            }
        }
        return tfidfScoreMap;
    }
}