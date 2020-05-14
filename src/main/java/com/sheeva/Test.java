package com.sheeva;

import com.hankcs.hanlp.corpus.io.IOUtil;
import com.hankcs.hanlp.dictionary.CoreDictionary;

import java.io.IOException;
import java.util.TreeMap;

/**
 * Created by sheeva on 2020/5/14.
 */
public class Test {
    public static void main(String[] args) throws IOException {
        String root = "//wsl$/Ubuntu-18.04/usr/local/lib/python3.8/dist-packages/pyhanlp/static/";
        TreeMap<String, CoreDictionary.Attribute> dict = IOUtil.loadDictionary(root + "data/dictionary/CoreNatureDictionary.mini.txt");


        System.out.println("build dat");
        DAT dat = new DAT(dict.keySet());
        System.out.println("build dat complete");

//        System.out.println(dat.contains("新老"));
//        System.out.println(dat.maxMatchLen("新老",0));
    }


}
