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

    public int intersect(List<String> words,List<String> query)
    {
        int count =0;
        for (int i = 0; i < words.size(); i++) {
            if (query.contains(words.get(i))) {
                count++;
            }
        }
        return count;
    }

    public void buildIndex(String[] files,String query) {

        int i = 1;
        HashMap<String, Float> hashMap = new HashMap<>();
        float totalNumberWords = 0;
        String[] queryChar = query.split("\\W+");
        List<String> q = Arrays.asList(queryChar);
        HashSet<String> queryUnion = new HashSet<>(q);
        int queryLen =queryUnion.size();

        for (String fileName : files) {
            int fileLen = 0;
            float resIntersect = 0;

            try ( BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                sources.put(i, fileName);
                String ln;

                while ((ln = file.readLine()) != null) {
                    String[] words = ln.split("\\W+");

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

                    List<String> wordList = Arrays.asList(words);
                    HashSet<String> wordUnion = new HashSet<String>(wordList);
                    fileLen = fileLen + wordUnion.size();
                    resIntersect = resIntersect + intersect(wordList,q);

                }
                totalNumberWords = fileLen + queryLen-resIntersect;

                float Jaccard = resIntersect / totalNumberWords;

                hashMap.put(fileName,Jaccard);

            } catch (IOException e) {
                System.out.println("File " + fileName + " not found. Skip it");
            }

            i++;
        }
        Map<String, Float> reverseSortedMap = new TreeMap<>(Collections.reverseOrder());

        reverseSortedMap.putAll(hashMap);
        System.out.println(reverseSortedMap);
    }

    HashSet<Integer> intersect(HashSet<Integer> pL1, HashSet<Integer> pL2) {
        HashSet<Integer> answer = new HashSet<Integer>();;

        int docId1 = 0, docId2 = 0;
        /*if (itP1.hasNext())
            docId1 = itP1.next();
        if (itP2.hasNext())
            docId2 = itP2.next();*/

        while (!pL1.isEmpty() &&!pL2.isEmpty() ) { // l1 = [1,2,5]        1,
            Iterator<Integer> itP1 = pL1.iterator();
            Iterator<Integer> itP2 = pL2.iterator();
            docId1 = itP1.next();
            docId2 = itP2.next();// l2 = [1,3,5]
//          3 do if docID ( p 1 ) = docID ( p2 )
            if (docId1 == docId2) {

                answer.add(docId1);
                pL1.remove(docId1);
                pL2.remove(docId2);
            } //          7   else if docID ( p1 ) < docID ( p2 )
            //          8        then p1 ← next ( p1 )
            else if (docId1 < docId2) {
                pL1.remove(docId1);

            } else {
//          9        else p2 ← next ( p2 )
                pL2.remove(docId2);

            }

        }
        if (docId1 == docId2) {
            answer.add(docId1);
        }

//          10 return answer
        return answer;
    }
}


public class main {

    public static void main(String[] args) {
        Index2 index = new Index2();
        String query = "idea of March";
        index.buildIndex(new String[]{"doc1.txt", "doc2.txt"},query);
    }

}