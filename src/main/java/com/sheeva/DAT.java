package com.sheeva;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sheeva on 2020/5/2.
 */
public class DAT {
    private int size = 5;

    String[] states = new String[size];
    int[] base = new int[size];
    int[] check = new int[size];

    private void check_n_grow(int i) {
        if (i < size) return;
        size *= 2;
        states = Arrays.copyOf(states, size);
        base = Arrays.copyOf(base, size);
        check = Arrays.copyOf(check, size);
    }

    public DAT(String[] texts) {
        base[0] = 1;
        check[0] = -1;

        int maxLen = 0;
        for (int i = 0; i < texts.length; i++) {
            maxLen = Math.max(maxLen, texts[i].length());
        }

//        for (int i = 0; i < maxLen; i++) {
//            for (int j = 0; j < texts.length; j++) {
//                if (i >= texts[j].length()) continue;
//                addOne(texts[j], i, texts[j].charAt(i));
//            }
//        }

        for (String text : texts) {
            for (int i = 0; i < text.length(); i++) {
                addOne(text, i, text.charAt(i), i == text.length() - 1);
                System.out.println();
            }
        }
    }

    public boolean containsKey(String text) {
        int b = 0;
        for (int i = 0; i < text.length(); i++) {
            int p = base[b] + code(text.charAt(i));
            if (base[p] == 0 || check[p] != b) return false;
            b = p;
        }
        return true;
    }

    public void addOne(String text, int insert, char c, boolean leaf) {
        int b = 0;
        for (int i = 0; i < insert; i++) {
            int p = base[b] + code(text.charAt(i));
            if (check[p] != b) throw new RuntimeException("error");
            b = p;
        }

        int nextIndex = base[b] + code(text.charAt(insert));
        check_n_grow(nextIndex); //使用更大的索引前检查是否溢出

        //冲突了, 该位置已被占用, 修改base[b]
        if (base[nextIndex] != 0) {
            if (check[nextIndex] == b) return; //已经加入过了
            moveBase(b, code(text.charAt(insert)));
            nextIndex = base[b] + code(text.charAt(insert));
        }
        check[nextIndex] = b;
        base[nextIndex] = base[b];
        states[nextIndex] = text.substring(0, insert + 1);
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


    private static char[] chars = {'清', '华', '大', '学', '新', '中', '人'};

    private int getBase(int index) {
        return Math.abs(base[index]);
    }

    private static int code(char c) {
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == c) return i + 1;
        }
        throw new RuntimeException("error");
    }

    public static void main(String[] args) {
        String[] texts = {"清华", "清华大学", "清新", "中华", "华人"};
        DAT dat = new DAT(texts);

        for (int i = 0; i < texts.length; i++) {
            System.out.println(dat.containsKey(texts[i]));
        }

        System.out.println(dat.containsKey("新"));

    }
}
