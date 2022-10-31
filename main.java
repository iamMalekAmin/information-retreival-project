import java.io.*;
import java.util.*;

class DictEntry {

    public int decFreq = 0;
    public int termFreq = 0;
    public HashSet<Integer> postingList;

    DictEntry() {
        postingList = new HashSet<>();
    }
}

class Index2 {

    Map<Integer, String> sources;
    HashMap<String, DictEntry> index;

    Index2() {
        sources = new HashMap<>();
        index = new HashMap<>();
    }

    public int intersect(List<String> words,List<String> query) {
        int count =0;
        for (int i = 0; i < words.size(); i++) {
            if (query.contains(words.get(i))) {
                count++;
            }
        }
        return count;
    }

    public void postingList(String[] words, int i){

        for (String word : words) {
            word = word.toLowerCase();
            if (!index.containsKey(word)) {
                index.put(word, new DictEntry());
            }
            if (!index.get(word).postingList.contains(i)) {
                index.get(word).decFreq += 1;
                index.get(word).postingList.add(i);
            }
            index.get(word).termFreq += 1;
        }
    }

    public void buildIndex(String[] files,String query) {

        int i = 1; // to indicate file index
        float totalNumberWords = 0;

        HashMap<String, Float> hashMap = new HashMap<>();
        String[] queryChar = query.split("\\W+"); // split query into words

        List<String> queryWordsList = Arrays.asList(queryChar);
        int queryLen = queryWordsList.size(); // size of query = 3


        for (String fileName : files) {
            int fileLen = 0;
            float resIntersect = 0;

            try ( BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                sources.put(i, fileName);
                String ln;

                while ((ln = file.readLine()) != null) {
                    String[] words = ln.split("\\W+"); // posting list for words in each document
                    postingList(words, i);

                    List<String> fileWordsList = Arrays.asList(words); // FILES WORDS
                    fileLen = fileWordsList.size(); // doc1 len = 4
                    resIntersect =  intersect(fileWordsList, queryWordsList); // 1

                }
                totalNumberWords = fileLen + queryLen - resIntersect; //4 + 3 - (1) = 6

                float Jaccard = resIntersect / totalNumberWords; // 1/6 = 0.166777

                hashMap.put(fileName, Jaccard); // map each file with

            } catch (IOException e) {
                System.out.println("File " + fileName + " not found. Skip it");
            }

            i++;
        }
        System.out.println(hashMap);
    }


}


public class main {

    public static void main(String[] args) {
        Index2 index = new Index2();
        String query = "idea of March";
        index.buildIndex(new String[]{"doc1.txt", "doc2.txt"}, query);
    }

}