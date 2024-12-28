package org.telegram.ui.Stars;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BagRandomizer {
    private final List bag;
    private int currentIndex;
    private Object next;
    private final Random random;
    private final List shuffledBag;

    public BagRandomizer(List list) {
        ArrayList arrayList = new ArrayList(list == null ? new ArrayList() : list);
        this.bag = arrayList;
        this.shuffledBag = new ArrayList(arrayList);
        this.currentIndex = 0;
        this.random = new Random();
        reshuffle();
        next();
    }

    private void reshuffle() {
        Collections.shuffle(this.shuffledBag, this.random);
        this.currentIndex = 0;
    }

    public Object getNext() {
        return this.next;
    }

    public Object next() {
        if (this.bag.isEmpty()) {
            return null;
        }
        Object obj = this.next;
        if (this.currentIndex >= this.shuffledBag.size()) {
            reshuffle();
        }
        List list = this.shuffledBag;
        int i = this.currentIndex;
        this.currentIndex = i + 1;
        this.next = list.get(i);
        return obj;
    }
}
