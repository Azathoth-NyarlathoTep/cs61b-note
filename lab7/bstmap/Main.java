package bstmap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Main {
    static int cnt = 0,n;
    static int[] arr = new int[100];
    static int k;
    static Queue<BT> q = new LinkedList<>();

    public static class BT {
        public int value;
        public BT left;
        public BT right;

        public BT(int value, BT left, BT right) {
            this.value = value;
            this.left = left;
            this.right = right;
        }
    }

    public static BT Build(int x) {
        if(x>k) return null;
        BT tmp = new BT(0, null, null);
        tmp.left = Build(2*x);
        tmp.right = Build(2*x+1);
        return tmp;
    }

    public static void init(BT x) {
        if(x==null) return;
        x.value = arr[--k];
        init(x.right);
        init(x.left);
    }

    public static void print() {
        while(!q.isEmpty()) {
            BT x = q.poll();
            if(x.left != null) q.add(x.left);
            if(x.right != null) q.add(x.right);
            cnt++;
            if(cnt == n) System.out.println(x.value);
            else System.out.print(x.value + " ");
        }
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        n = in.nextInt();
        k = n;

        for(int i = 0; i < n; i++) {
            int x = in.nextInt();
            arr[i] = x;
        }

        BT ans = Build(1);
        init(ans);
        q.add(ans);
        print();
    }
}