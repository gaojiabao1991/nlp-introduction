package com.sheeva;

import com.hankcs.hanlp.collection.trie.DoubleArrayTrie;

import java.util.Arrays;
import java.util.Collection;
import java.util.TreeMap;

/**
 * Created by sheeva on 2020/5/14.
 */
public class DATWrapper {
    private static String EMPTY_STRING = "";
    final DoubleArrayTrie<String> dat;
    final int[] base;
    final int[] check;
    final int size;

    public DATWrapper(Collection<String> texts) {
        TreeMap<String, String> m = new TreeMap<>();
        for (String text : texts) {
            m.put(text, EMPTY_STRING);
        }
        dat = new DoubleArrayTrie<>(m);
        base = dat.getBase();
        check = dat.getCheck();
        size = base.length;
    }

    public boolean contains(String text) {
        return dat.containsKey(text);
    }

    public int maxMatchLen(String s, int start) {
        int b = 0;
        int maxMatch = 0;
        for (int i = start; i < s.length(); i++) {
            int p = base[b] + code(s, i);
            if (p >= size || base[p] == 0 || check[p] != b) return maxMatch;
            b = p;
            //判断是否是叶子节点
            int leaf = base[b];
            if (leaf < size && base[leaf] != 0 && check[leaf] == b) maxMatch = i + 1;
        }
        return maxMatch;
    }

    private static int code(String text, int i) {
        if (i == text.length()) return 0;
        return text.charAt(i) + 1;
    }

    public static void main(String[] args) {
        String[] values = {"ab", "abc", "def"};
        DATWrapper trie = new DATWrapper(Arrays.asList(values));

        System.out.println(trie.contains("ab"));
        System.out.println(trie.contains("abc"));
        System.out.println(trie.contains("def"));
        System.out.println(trie.contains("a"));
        System.out.println(trie.contains("b"));
        System.out.println(trie.contains("c"));
        System.out.println(trie.contains("d"));

        System.out.println(trie.maxMatchLen("abcdef", 0));
        System.out.println(trie.maxMatchLen("de", 0));
    }
}
