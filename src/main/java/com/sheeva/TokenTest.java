package com.sheeva;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.collection.trie.DoubleArrayTrie;
import com.hankcs.hanlp.corpus.io.IOUtil;
import com.hankcs.hanlp.dictionary.CoreDictionary;

import java.io.IOException;
import java.util.*;

/**
 * Created by sheeva on 2020/5/2.
 */
public class TokenTest {
    public static void main(String[] args) throws IOException {
//        System.out.println(HanLP.segment("欢迎使用hanlp汉语处理包"));

//        for (String line : Files.readAllLines(Paths.get("//wsl$/Ubuntu-18.04/usr/local/lib/python3.8/dist-packages/pyhanlp/static/hanlp.properties"))) {
//            System.out.println(line);
//        }
        String root = "//wsl$/Ubuntu-18.04/usr/local/lib/python3.8/dist-packages/pyhanlp/static/";
        TreeMap<String, CoreDictionary.Attribute> dict = IOUtil.loadDictionary(root + "data/dictionary/CoreNatureDictionary.mini.txt");

        Trie trie = new Trie();
        for (String term : dict.keySet()) {
            trie.add(term);
        }

        Map<String, CoreDictionary.Attribute> trieDict = new HashMap<String, CoreDictionary.Attribute>() {
            @Override
            public boolean containsKey(Object key) {
                return trie.contains((String) key);
            }
        };

        System.out.println("build dat");
//        DAT dat = new DAT(dict.keySet());
//        Map<String, CoreDictionary.Attribute> datDict = new HashMap<String, CoreDictionary.Attribute>() {
//            @Override
//            public boolean containsKey(Object key) {
//                return dat.contains((String) key);
//            }
//        };

        DATWrapper dat = new DATWrapper(dict.keySet());
        System.out.println("build dat complete");


        String[] texts = {"项目的研究", "商品和服务", "研究生命起源", "当下雨天地面积水", "结婚的和尚未结婚的", "欢迎新老师生前来就餐"};
        int num = 0;
        for (String text : texts) {
            num++;
            List<String> forward = segmentForward(text, trieDict);
            List<String> backward = segmentBackward(text, trieDict);
            List<String> biward = segmentBiward(text, trieDict);

            List<String> trieAdvanceForward = trieAdvanceForward(text, trie);
            List<String> datAdvanceForward = datAdvanceForward(text, dat);

            System.out.printf("==========%s\t %s==========\n", num, text);
            System.out.printf("forward: %s\n", forward);
            System.out.printf("backward: %s\n", backward);
            System.out.printf("biward: %s\n", biward);

            System.out.printf("trieAdvanceForward: %s\n", trieAdvanceForward);
            System.out.printf("datAdvanceForward: %s\n", datAdvanceForward);
        }


        String text = "江西鄱阳湖干枯, 中国最大淡水湖变成大草原";

        long start;
        double costTime;
        final int pressure = 10000;

        //warm up
        for (int i = 0; i < pressure; i++) {
            segmentForward(text, dict);
        }

        start = System.currentTimeMillis();
        for (int i = 0; i < pressure; i++) {
            segmentForward(text, dict);
        }
        costTime = (System.currentTimeMillis() - start) / (double) 1000;
        System.out.printf("forward: %.2f万字/秒\n", text.length() * pressure / 10000 / costTime);

        start = System.currentTimeMillis();
        for (int i = 0; i < pressure; i++) {
            segmentBackward(text, dict);
        }
        costTime = (System.currentTimeMillis() - start) / (double) 1000;
        System.out.printf("backward: %.2f万字/秒\n", text.length() * pressure / 10000 / costTime);

        start = System.currentTimeMillis();
        for (int i = 0; i < pressure; i++) {
            segmentBiward(text, dict);
        }
        costTime = (System.currentTimeMillis() - start) / (double) 1000;
        System.out.printf("biward: %.2f万字/秒\n", text.length() * pressure / 10000 / costTime);


        //warm up
        for (int i = 0; i < pressure; i++) {
            segmentForward(text, trieDict);
        }

        start = System.currentTimeMillis();
        for (int i = 0; i < pressure; i++) {
            segmentForward(text, trieDict);
        }
        costTime = (System.currentTimeMillis() - start) / (double) 1000;
        System.out.printf("trie forward: %.2f万字/秒\n", text.length() * pressure / 10000 / costTime);

        start = System.currentTimeMillis();
        for (int i = 0; i < pressure; i++) {
            segmentBackward(text, trieDict);
        }
        costTime = (System.currentTimeMillis() - start) / (double) 1000;
        System.out.printf("trie backward: %.2f万字/秒\n", text.length() * pressure / 10000 / costTime);

        start = System.currentTimeMillis();
        for (int i = 0; i < pressure; i++) {
            segmentBiward(text, trieDict);
        }
        costTime = (System.currentTimeMillis() - start) / (double) 1000;
        System.out.printf("trie biward: %.2f万字/秒\n", text.length() * pressure / 10000 / costTime);

        start = System.currentTimeMillis();
        for (int i = 0; i < pressure; i++) {
            trieAdvanceForward(text, trie);
        }
        costTime = (System.currentTimeMillis() - start) / (double) 1000;
        System.out.printf("trie advance forward: %.2f万字/秒\n", text.length() * pressure / 10000 / costTime);

        //warm up
        for (int i = 0; i < pressure; i++) {
            datAdvanceForward(text, dat);
        }

        start = System.currentTimeMillis();
        for (int i = 0; i < pressure; i++) {
            datAdvanceForward(text, dat);
        }
        costTime = (System.currentTimeMillis() - start) / (double) 1000;
        System.out.printf("dat advance forward: %.2f万字/秒\n", text.length() * pressure / 10000 / costTime);

    }

    private static List<String> segmentFully(String text, Map<String, CoreDictionary.Attribute> dict) {
        List<String> r = new LinkedList<>();
        for (int i = 0; i < text.length(); i++) {
            for (int j = i + 1; j < text.length() + 1; j++) {
                String token = text.substring(i, j);
                if (dict.containsKey(token)) r.add(token);
            }
        }
        return r;
    }

    private static List<String> segmentForward(String text, Map<String, CoreDictionary.Attribute> dict) {
        List<String> r = new LinkedList<>();
        for (int i = 0; i < text.length(); i++) {
            int end = i + 1;
            for (int j = i + 2; j < text.length() + 1; j++) {
                String token = text.substring(i, j);
                if (dict.containsKey(token)) {
                    end = j;
                }
            }
            r.add(text.substring(i, end));
            i = end - 1;
        }
        return r;
    }

    private static List<String> segmentBackward(String text, Map<String, CoreDictionary.Attribute> dict) {
        Deque<String> r = new LinkedList<>();
        for (int i = text.length(); i > 0; i--) {
            int start = i - 1;
            for (int j = i - 2; j >= 0; j--) {
                String token = text.substring(j, i);
                if (dict.containsKey(token)) {
                    start = j;
                }
            }
            r.addFirst(text.substring(start, i));
            i = start + 1;
        }
        return new LinkedList<>(r);
    }

    private static List<String> segmentBiward(String text, Map<String, CoreDictionary.Attribute> dict) {
        List<String> forward = segmentForward(text, dict);
        List<String> backward = segmentBackward(text, dict);
        if (forward.size() > backward.size()) {
            return backward;
        } else if (forward.size() < backward.size()) {
            return forward;
        } else {
            if (countChar(forward) < countChar(backward)) {
                return forward;
            } else {
                return backward;
            }
        }
    }

    private static List<String> trieAdvanceForward(String text, Trie dict) {
        List<String> r = new LinkedList<>();
        int start = 0;
        while (start < text.length()) {
            int matchLen = Math.max(dict.maxMatchLen(text, start), 1);
            r.add(text.substring(start, start + matchLen));
            start += matchLen;
        }
        return r;
    }

    private static List<String> datAdvanceForward(String text, DATWrapper dat) {
        List<String> r = new LinkedList<>();
        int start = 0;
        while (start < text.length()) {
            int matchLen = Math.max(dat.maxMatchLen(text, start), 1);
            r.add(text.substring(start, start + matchLen));
            start += matchLen;
        }
        return r;
    }

    private static int countChar(List<String> l) {
        return (int) l.stream().filter(e -> e.length() == 1).count();
    }
}
