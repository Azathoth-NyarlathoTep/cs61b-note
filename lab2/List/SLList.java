package List;

import jh61b.junit.In;

public class SLList {
//    private IntNode first;
    private IntNode sentinel;//建造一个哨兵节点，以防止有空链表的可能
    private int size;

    public SLList() {
        sentinel = new IntNode(-18,null);
        size = 0;
    }

    public SLList(int x){
        sentinel = new IntNode(-18,null);
        sentinel.next = new IntNode(x,null);
        size = 1;
    }

    public void addFirst(int x){
        sentinel.next = new IntNode(x,sentinel.next);//sentinel节点是始终作为一个"0"存在的，故位置不改变
        size++;
    }

    public int getFirst(){
        return sentinel.next.item;
    }

    public void addLast(int x){
        size++;
        IntNode p = sentinel;
        while(p.next != null){
            p = p.next;
        }
        p.next = new IntNode(x,null);
    }

    public int size(){
        return size;
    }

    public static void main(String[] args){
        SLList L = new SLList(4);
        L.addFirst(15);
    }
}
