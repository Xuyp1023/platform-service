package com.betterjr.modules.notification;

import java.io.IOException;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.alibaba.rocketmq.remoting.annotation.CFNullable;

public class StreamTest {
    public static void main(String[] args) {
        String orginStr = "12,21,23,11,13,43";
  /*      List<Integer> seqs = Pattern.compile(",").splitAsStream(orginStr).map(Integer::valueOf).sorted().peek(System.out::println)
                .collect(Collectors.toList());*/
        StringBuilder sb = new StringBuilder();
        
        try {
            Appendable xx = appendTo(sb, "Frist", "Second", new String[] {"1", "2", "3", "4"});
            System.out.println(xx.toString());
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    public static <A extends Appendable> A appendTo(A appendable, Iterator parts) throws IOException {
        if (parts.hasNext()) {
            appendable.append(parts.next().toString());
            while (parts.hasNext()) {
                appendable.append(",");
                appendable.append(parts.next().toString());
            }
        }
        return appendable;
    }

    public static <A extends Appendable> A appendTo(A appendable, Object first, Object second, Object... rest) throws IOException {
        return appendTo(appendable, iterable(first, second, rest));
    }

    private static Iterator iterable(final Object first, final Object second, final Object[] rest) {
        return new AbstractList() {
            @Override
            public int size() {
                return rest.length + 2;
            }

            @Override
            public Object get(int index) {
                switch (index) {
                case 0:
                    return first;
                case 1:
                    return second;
                default:
                    return rest[index - 2];
                }
            }
        }.iterator();
    }
}
