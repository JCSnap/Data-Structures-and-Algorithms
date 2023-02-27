import java.util.ArrayList;

public class Trie {
    private TrieNode root;
    // Wildcards
    final char WILDCARD = '.';

    private class TrieNode {
        private boolean flag;
        private boolean[] presentChars;
        private boolean hasChildren;
        private TrieNode[] children;
        public TrieNode(boolean flag) {
            this.flag = flag;
            this.presentChars = new boolean[62];
            this.children = new TrieNode[62];
            this.hasChildren = false;
        }

        private int charToInt(char s) {
            if (Character.isDigit(s)) {
                return (int) s - 48;
            } else if (Character.isUpperCase(s)) {
                return (int) s - 55;
            } else {
                return (int) s - 61;
            }
        }

        private char intToChar(int i) {
            int index = i;
            if (i >= 0 && i <= 9) {
                index = i + 48;
            } else if (i < 36) {
                index = i + 55;
            } else {
                index = i + 61;
            }
            char ascii = (char) index;
            return ascii;
        }

        public boolean contains(char c) {
            return this.presentChars[charToInt(c)];
        }

        public TrieNode getChildNode(char c) {
            return this.children[charToInt(c)];
        }

        public void insert(char c) {
            if (!hasChildren) {
                this.hasChildren = true;
            }
            int pos = charToInt(c);
            this.presentChars[pos] = true;
            this.children[pos] = new TrieNode(false);
        }

        public boolean isFlagged() {
            return this.flag;
        }
        public void setRedFlag() {
            this.flag = true;
        }

        public boolean hasChildren() {
            return this.hasChildren;
        }
    }

    public Trie() {
        this.root = new TrieNode(false);
    }

    /**
     * Inserts string s into the Trie.
     *
     * @param s string to insert into the Trie
     */
    void insert(String s) {
        TrieNode curNode = root;
        int len = s.length();
        int cur = 0;
        while (true) {
            if (cur == len) { // end of string
                curNode.setRedFlag();
                break;
            } else { // not end of string
                char curChar = s.charAt(cur);
                if (curNode.contains(curChar)) {
                    // move to next node, move to next character
                    curNode = curNode.getChildNode(curChar);
                    cur++;
                } else { // character not found in current node
                    curNode.insert(curChar);
                    curNode = curNode.getChildNode(curChar);
                    cur++;
                }
            }
        }
    }

    /**
     * Checks whether string s exists inside the Trie or not.
     *
     * @param s string to check for
     * @return whether string s is inside the Trie
     */
    boolean contains(String s) {
        TrieNode curNode = root;
        int len = s.length();
        int cur = 0;
        while (true) {
            if (cur == len && curNode.isFlagged()) {
                return true;
            } else if (cur == len) {
                break;
            } else {
                char curChar = s.charAt(cur);
                if (!curNode.contains(curChar)) {
                    // does not contain a character
                    break;
                } else {
                    curNode = curNode.getChildNode(curChar);
                    cur++;
                }
            }
        }
        return false;
    }

    /**
     * Searches for strings with prefix matching the specified pattern sorted by lexicographical order. This inserts the
     * results into the specified ArrayList. Only returns at most the first limit results.
     *
     * @param s       pattern to match prefixes with
     * @param results array to add the results into
     * @param limit   max number of strings to add into results
     */
    void prefixSearch(String s, ArrayList<String> results, int limit) {
        int len = s.length();
        int[] count = new int[1];
        count[0] = 0;
        String stringInstance = "";
        recurse(s, stringInstance, root, results, limit, count);
    }

    private void recurse(
            String s,
            String stringInstance,
            TrieNode node,
            ArrayList<String> results,
            int limit,
            int[] count) {
        String curStringInstance = stringInstance;
        TrieNode curNode = node;
        int pointer = curStringInstance.length();
        int prefixLength = s.length();
        // no point adding anything if current instance < the prefix
        if (curStringInstance.length() < prefixLength) {
            char curChar = s.charAt(pointer);
            // if it is a . we can branch out
            if (curChar == this.WILDCARD) {
                branchOutNextChar(s, curStringInstance, curNode, results, limit, count);
            // if the current node contains curChar, we move on to the next char of the prefix
            } else if (node.contains(curChar)) {
                curStringInstance = curStringInstance + curChar;
                TrieNode nextNode = node.getChildNode(curChar);
                recurse(s, curStringInstance, nextNode, results, limit, count);
            // if there exist any chars in the prefix that cannot be found in the nodes, terminate
            } else {
                // do nothing
            }
        } else { // past the prefix, treat everything else like a wildcard
            // if it is flagged, we add then branch, regardless of whether the flag is the end of if it has children
            // we let branOutNextChar handle that
            if (node.isFlagged()) {
                int curCount = count[0];
                if (curCount < limit) {
                    results.add(curStringInstance);
                    count[0] = curCount + 1;
                    branchOutNextChar(s, curStringInstance, curNode, results, limit, count);
                } else {
                    // do nothing
                }
            } else {
                branchOutNextChar(s, curStringInstance, curNode, results, limit, count);
            }
        }
    }

    private void branchOutNextChar(
            String s,
            String stringInstance,
            TrieNode node,
            ArrayList<String> results,
            int limit,
            int[] count
    ) {
        if (node.hasChildren) {
            // we find all the characters that this node contain
            for (int i = 0; i < node.presentChars.length; i++) {
                boolean curBool = node.presentChars[i];
                if (curBool) {
                    char curChar = node.intToChar(i);
                    String newStringInstance = stringInstance + curChar;
                    // for each char, we recurse their respective nodes
                    TrieNode nextNode = node.getChildNode(curChar);
                    recurse(s, newStringInstance, nextNode, results, limit, count);
                }
            }
        } else {
            // if this is the end of the tree. ie a leaf where it has no children, we terminate
            // do nothing
        }

    }




    // Simplifies function call by initializing an empty array to store the results.
    // PLEASE DO NOT CHANGE the implementation for this function as it will be used
    // to run the test cases.
    String[] prefixSearch(String s, int limit) {
        ArrayList<String> results = new ArrayList<String>();
        prefixSearch(s, results, limit);
        return results.toArray(new String[0]);
    }


    public static void main(String[] args) {


        Trie t = new Trie();


        t.insert("peter");
        t.insert("piper");
        t.insert("picked");
        t.insert("a");
        t.insert("peck");
        t.insert("of");
        t.insert("pickled");
        t.insert("peppers");
        t.insert("pepppito");
        t.insert("pepi");
        t.insert("pik");
        //System.out.println(t.contains("peck"));
        String[] result1 = t.prefixSearch("pe", 10);
        /*
        String[] result2 = t.prefixSearch("pe.", 10);
        for (int i = 0; i < result2.length; i++) {
            System.out.println(result2[i]);
        }
        */

        // result1 should be:
        // ["peck", "pepi", "peppers", "pepppito", "peter"]
        // result2 should contain the same elements with result1 but may be ordered arbitrarily
    }
}
