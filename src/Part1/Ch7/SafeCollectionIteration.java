package Part1.Ch7;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SafeCollectionIteration extends Object {
    public static void main(String[] args) {
//        to be safe. only keep a reference to the *synchronized* list so
//        that you are sure that all accesses are controlled

//        the collection *must* be synchronized (a list in this case)
        List <String> wordList = Collections.synchronizedList(new ArrayList<>());

        wordList.add("Iterators");
        wordList.add("require");
        wordList.add("special");
        wordList.add("handling");

//        all of this must be in a synchronized block to block
//        other threads from modifying wordList while the iterator is in progress
        synchronized (wordList) {
            Iterator <String> iter = wordList.iterator();
            while (iter.hasNext()) {
                String s = iter.next();
                System.out.println("found string: " + s + ", length=" + s.length());
            }
        }
    }
}
