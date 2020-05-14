package com.sheeva;

import java.util.*;

/**
 * Created by sheeva on 2020/5/2.
 * V1
 */
public class DAT {
    private int size = 100;

    String[] states = new String[size];
    int[] base = new int[size];
    int[] check = new int[size];

    private void check_n_grow(int i) {
        if (i < size) return;
        while (i >= size) {
            size *= 2;
        }
        states = Arrays.copyOf(states, size);
        base = Arrays.copyOf(base, size);
        check = Arrays.copyOf(check, size);
    }

    public DAT() {
        base[0] = 1;
        check[0] = -1;
    }

//    public DAT(String[] texts) {
//        this();
//        for (String text : texts) {
//            for (int i = 0; i < text.length() + 1; i++) {
//                addOne(text, i);
//            }
//        }
//    }

    public DAT(Collection<String> texts) {
        this();
//        for (String text : texts) {
//            for (int i = 0; i < text.length() + 1; i++) {
//                addOne(text, i);
//            }
//        }

        int maxLen = 0;
        for (String text : texts) {
            maxLen = Math.max(maxLen, text.length());
        }

        for (int i = 0; i < maxLen + 1; i++) {
            System.out.println(i);
            for (String text : texts) {
                if (i <= text.length()) addOne(text, i);
            }
        }
        System.out.println("dat build complete, size: " + size);
    }

    public void add(String text) {
        for (int i = 0; i < text.length() + 1; i++) {
            addOne(text, i);
        }
    }

    public boolean contains(String text) {
        int b = 0;
        for (int i = 0; i < text.length() + 1; i++) {
            int p = base[b] + code(text, i);
            if (p >= size || base[p] == 0 || check[p] != b) return false;
            b = p;
        }
        return true;
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

    public void addOne(String text, int pos) {
        int b = 0;
        for (int i = 0; i < pos; i++) {
            int p = base[b] + code(text, i);
            if (p >= size || check[p] != b) throw new RuntimeException("error");
            b = p;
        }

        int nextIndex = base[b] + code(text, pos);
        check_n_grow(nextIndex); //使用更大的索引前检查是否溢出

        //冲突了, 该位置已被占用, 修改base[b]
        if (base[nextIndex] != 0) {
            if (check[nextIndex] == b) return; //已经加入过了
            moveBase(b, code(text, pos));
            nextIndex = base[b] + code(text, pos);
        }
        check[nextIndex] = b;
        base[nextIndex] = base[b];
//        states[nextIndex] = text.substring(0, pos + 1);
    }

    //修改base[index]的值, 使得newCode可以无冲突的插入(所有已经存在的基于index状态的后继状态将被移动)
    private void moveBase(int index, int newCode) {
        int curBase = base[index];
        List<Integer> oldSubCodes = new ArrayList<>();
        int maxCode = newCode;
        for (int i = 0; i < check.length; i++) {
            if (base[i] != 0 && check[i] == index) {
                oldSubCodes.add(i - curBase);
                maxCode = Math.max(maxCode, i - curBase);
            }
        }

        int newBase = curBase + 1;
        a:
        while (true) {
            if (newBase + newCode < size && base[newBase + newCode] != 0) {
                newBase++;
                continue;
            }
            for (Integer subCode : oldSubCodes) {
                if (newBase + subCode < size && base[newBase + subCode] != 0) {
                    newBase++;
                    continue a;
                }
            }
            break; //当前newBase可以存放包含新节点在内的所有节点
        }

        check_n_grow(newBase + maxCode); //使用更大的索引前检查是否溢出

        for (Integer subCode : oldSubCodes) {
            base[newBase + subCode] = base[curBase + subCode];
            check[newBase + subCode] = check[curBase + subCode];
            states[newBase + subCode] = states[curBase + subCode];

            //对于那些check指向当前state的后继节点, 修改check为新的state
            for (int i = 0; i < check.length; i++) {
                if (check[i] == curBase + subCode) check[i] = newBase + subCode;
            }

            base[curBase + subCode] = 0;
            check[curBase + subCode] = 0;
            states[curBase + subCode] = null;
        }
        base[index] = newBase;
    }


//    private static char[] chars = {'清', '华', '大', '学', '新', '中', '人'};

    private static int code(String text, int i) {
        if (i == text.length()) return 0;
        return text.charAt(i) + 1;
    }

    public static void main(String[] args) {
//        DAT trie = new DAT();
//        trie.add("ab");
//        trie.add("abc");
//        trie.add("def");
//
//        System.out.println(trie.contains("ab"));
//        System.out.println(trie.contains("abc"));
//        System.out.println(trie.contains("def"));
//        System.out.println(trie.contains("a"));
//        System.out.println(trie.contains("b"));
//        System.out.println(trie.contains("c"));
//        System.out.println(trie.contains("d"));
//
//        System.out.println(trie.maxMatchLen("abcdef", 0));
//        System.out.println(trie.maxMatchLen("de", 0));


    }
}
