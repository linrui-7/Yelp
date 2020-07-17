package hw5;
import java.util.*;
public class Business implements Comparable<Business>{
    public String businessID;
    public String businessName;
    public String businessAddress;
    public String review;
    public int charactercount;

    public Business(String[] info){
        businessID = info[0];
        businessName = info[1];
        businessAddress = info[2];
        review = info[3];
        charactercount = review.length();
    }

    public String toString(){
        return "-------------------------------------------------------------------------------\n"
                + "Business ID: " + businessID + "\n"
                + "Business Name: " + businessName + "\n"
                + "Business Address: " + businessAddress + "\n"
                + "Character Count: " + charactercount + "\n";
    }

    public int getLength() {
      return charactercount;
    }

    
    @Override//override the compareto method in comparable 
    public int compareTo(Business next){
        if(this.charactercount > next.charactercount) {
          return 1;
        }
        else if(this.charactercount == next.charactercount) {
          return 0;
        }
        return -1;
    }
    // obtain a Frequency map from the review
    public HashMap<String,Integer> getCurrentDFCount(HashSet<String> meaningless){
        HashMap<String,Integer> currentDFCount = new HashMap<String,Integer>();
        String word = "";
        // use scanner to read from review
        Scanner add = new Scanner(review);

        while(add.hasNext()){
            word = add.next();
            if(!meaningless.contains(word)){ //if the word is in meaningless list
            
                if(currentDFCount.containsKey(word)){
                    currentDFCount.put(word,currentDFCount.get(word)+1);
                } 
                    // if the map contains the word, add 1 to count
                else {
                  currentDFCount.put(word, 1);
                } // otherwise, put the word in the map with count of 1
            }
        }
        add.close();
        return currentDFCount;
    }
    
}