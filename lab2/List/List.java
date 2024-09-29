package List;

import java.util.Scanner;

public class List{
    public static class Node{
        int coefficient;
        int index;
        Node next;

        public Node(int coefficient, int index,Node next){
            this.coefficient = coefficient;
            this.index = index;
            this.next = next;
        }
    }

    public Node sentinel;

    public List(){
        sentinel = new Node(0, 0,null);
    }
    public List(int c,int i){
        sentinel = new Node(0,0,null);
        sentinel.next = new Node(c,i,null);
    }

    public void addLast(int c,int i){
        Node p = sentinel;
        while(p.next != null){
            p = p.next;
        }
        p.next = new Node(c,i,null);
    }

    public static List Add(List l,List r){
        List tmp = new List();
        Node pl = l.sentinel.next;
        Node pr = r.sentinel.next;
        while(pl!=null && pr!=null)
        {
            if(pl.index > pr.index ){
                tmp.addLast(pl.coefficient,pl.index);
                pl = pl.next;
            }
            else if(pr.index > pl.index ){
                tmp.addLast(pr.coefficient,pr.index);
                pr = pr.next;
            }
            else {
                int a = pl.coefficient + pr.coefficient;
                if(a!=0){
                    tmp.addLast(a,pr.index);
                    pl = pl.next;
                    pr = pr.next;
                }
            }
//            System.out.println("111");
        }
        while(pr!=null){
            tmp.addLast(pr.coefficient,pr.index);
            pr = pr.next;
        }
        while(pl!=null){
            tmp.addLast(pl.coefficient,pl.index);
            pl = pl.next;
        }
        return tmp;
    }

    public static List mul(List l,List r){
        List result = new List();
        Node pl = l.sentinel.next;
        Node pr = r.sentinel.next;

        while(pl!=null){
            while(pr!=null){
                int coef = pl.coefficient * pr.coefficient;
                int index = pl.index + pr.index;
                result.addLast(coef,index);
                pr = pr.next;
            }
            pl = pl.next;
            pr = r.sentinel.next;
        }
//合并同类项
        Node cur = result.sentinel.next;
        Node pre = result.sentinel;
        while(cur!=null){
            Node nextNode = cur.next;
            while(nextNode!=null){
                if(nextNode.index == cur.index){
                    cur.coefficient = nextNode.coefficient + cur.coefficient;
                }
                nextNode = nextNode.next;
            }
            if(cur.coefficient == 0) pre.next = cur.next;
            cur = cur.next;
        }
        return result;
    }

    public void print(){
        Node p = sentinel.next;
        while(p!=null){
            System.out.print(p.coefficient + " ");
            System.out.print(p.index + " ");
            p = p.next;
        }
        System.out.println();
    }

    public static void main(String[] args){
        List List_1 = new List();
        List List_2 = new List();

        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        for(int i = 0; i < n; i++){
            int coefficient = scanner.nextInt();
            int index = scanner.nextInt();
            List_1.addLast(coefficient,index);
        }
        n = scanner.nextInt();
        for(int i = 0; i < n; i++){
            int coefficient = scanner.nextInt();
            int index = scanner.nextInt();
            List_2.addLast(coefficient,index);
        }
        List theResultofAdd = Add(List_1,List_2);
        List theResultofMul = mul(List_1,List_2);
        theResultofMul.print();
        theResultofAdd.print();
    }
}