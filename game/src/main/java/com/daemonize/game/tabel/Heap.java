package com.daemonize.game.tabel;

public class Heap< T extends     IHeapItem <T> > {
    T[] items;
    int currentItemCount;

    public Heap(T[] items) {
        this.items = items;
        this.currentItemCount = 0;
    }

    public void add(T item) {
        item.setHeapIndex(currentItemCount);
        items[currentItemCount] = item;
        sortUp(item);
        currentItemCount++;


    }

    public T removeFirst(){
        T firstItem = items[0];
        currentItemCount--;
        items[0] = items[currentItemCount];
        items[0].setHeapIndex(0);
        sortDown(items[0]);
        return firstItem;

    }
    public  void updateItem(T item) {
        sortUp(item);
    }

    public int size() {
        return currentItemCount;
    }

    public boolean contains (T item) {
        return  item.equals(items[item.getHeapIndex()]);
    }

    void  sortDown(T item) {
        while (true) {
            int childIndexLeft = item.getHeapIndex() * 2 + 1;
            int childIndexRight = item.getHeapIndex() * 2 + 2;
            int swapIndex = 0;

            if (childIndexLeft < currentItemCount) {
                swapIndex = childIndexLeft;
                if (childIndexRight < currentItemCount) {
                    if (items[childIndexLeft].compareTo(items[childIndexRight]) > 0) {
                        swapIndex = childIndexRight;
                    }
                }

                if (item.compareTo(items[swapIndex]) > 0){
                    swap(item,items[swapIndex]);
                } else {
                    return;
                }

            } else {
                return;
            }

        }
    }

    void sortUp(T item) {

        while (true) {
            int parentIndex = (item.getHeapIndex() - 1) / 2;
            T parentItem = items[parentIndex];
            if (item.compareTo(parentItem) < 0){
                swap(item, parentItem);
            } else {
                break;
            }
        }
    }

    void swap(T itemA, T itemB ){
        items[itemA.getHeapIndex()] = itemB;
        items[itemB.getHeapIndex()] = itemA;
        int itemAIndex = itemA.getHeapIndex();
        itemA.setHeapIndex(itemB.getHeapIndex());
        itemB.setHeapIndex(itemAIndex);
    }

}

interface IHeapItem<T> extends Comparable<T> {

    int getHeapIndex();
    int setHeapIndex(int index);

}
