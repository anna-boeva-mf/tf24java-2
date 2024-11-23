package ru.tbank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        // 1
        CustomLinkedList<Integer> list0 = new CustomLinkedList<>();
        list0.add(1);
        System.out.println(list0);
        System.out.println(list0.get(0));
        System.out.println(list0.contains(1));

        list0.remove(0);
        System.out.println(list0);
        System.out.println(list0.contains(1));

        Integer[] array = {1, 2, 3, 4, 5};
        ArrayList<Integer> arrList = new ArrayList<>(Arrays.asList(array));
        list0.addAll(arrList);
        System.out.println(list0);

        // 2
        Stream<Integer> stream = Stream.of(6, 7, 8, 9, 10);
        CustomLinkedList<Integer> list = stream.reduce(new CustomLinkedList<>(), (acc, element) -> {
            acc.add(element);
            return acc;
        }, (cll, col) -> {
            cll.addAll((Collection<? extends Integer>) col);
            return cll;
        });
        System.out.println(list);

        // ДЗ-11
        CustomLinkedList<String> list11 = new CustomLinkedList<>();
        list11.add("apple");
        list11.add("banana");
        list11.add("cherry");

        CustomIterator<String> iterator = list11.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }

        // Использование forEachRemaining
        iterator = list11.iterator(); // Сброс итератора
        iterator.forEachRemaining(System.out::println);
    }
}
