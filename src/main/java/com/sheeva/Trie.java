package com.sheeva;

/**
 * Created by sheeva on 2020/5/2.
 */
public class Trie {
    private static final int hashLevel = 1;
    Node root = new Node((char) 0);

    private static class Node {
        public Node(char ch) {
            this.ch = ch;
        }

        final char ch;
        Node[] child = new Node[0];
        boolean leaf = false;
    }

    public void add(String s) {
        Node node = root;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (i < hashLevel) {
                if (node.child.length == 0) {
                    node.child = new Node[65535 + 1];
                }
                if (node.child[(int) c] == null) node.child[(int) c] = new Node(c);
                node = node.child[(int) c];
            } else {
                int index = binarySearch(node.child, c);
                if (index >= 0) {
                    node = node.child[index];
                } else {
                    int insert = -(index + 1);
                    Node[] newChild = new Node[node.child.length + 1];
                    System.arraycopy(node.child, 0, newChild, 0, insert);
                    System.arraycopy(node.child, insert, newChild, insert + 1, node.child.length - insert);
                    newChild[insert] = new Node(c);
                    node.child = newChild;
                    node = newChild[insert];
                }
            }
            if (i == s.length() - 1) node.leaf = true;
        }
    }

    public boolean contains(String s) {
        Node node = root;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (i < hashLevel) {
                Node byHash = node.child[(int) c];
                if (byHash == null) return false;
                node = byHash;
            } else {
                int index = binarySearch(node.child, c);
                if (index < 0) return false;
                node = node.child[index];
            }
        }
        return node.leaf;
    }

    /**
     * @param start 从第start位开始匹配
     * @return 最长匹配长度
     */
    public int maxMatchLen(String s, int start) {
        Node node = root;
        int maxMatch = 0;
        for (int i = 0; i+start < s.length(); i++) {
            char c = s.charAt(i+start);
            if (i < hashLevel) {
                Node byHash = node.child[(int) c];
                if (byHash == null) return maxMatch;
                node = byHash;
            } else {
                int index = binarySearch(node.child, c);
                if (index < 0) return maxMatch;
                node = node.child[index];
            }
            if (node.leaf) maxMatch = i + 1;
        }
        return maxMatch;
    }

    private static int binarySearch(Node[] array, int c) {
        int low = 0;
        int high = array.length - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            int cmp = array[mid].ch - c;
            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return mid;
        }
        return -(low + 1);
    }

    public static void main(String[] args) {
        Trie trie = new Trie();
        trie.add("ab");
        trie.add("abc");
        trie.add("def");

        System.out.println(trie.contains("ab"));
        System.out.println(trie.contains("abc"));
        System.out.println(trie.contains("def"));
        System.out.println(trie.contains("a"));
        System.out.println(trie.contains("b"));
        System.out.println(trie.contains("c"));
        System.out.println(trie.contains("d"));

        System.out.println(trie.maxMatchLen("abcdef",0));
    }
}
