import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        LinkedList<String> list=new LinkedList<>();
        //Scanner scanner=new Scanner("A * B #");
        System.out.println("其中运算符只包括与（*）、或（+）、非（！）。\n" +
                " 用A代表 x1 ,B代表x2，C 代表 x3 , D 代表 x4，且每一个字符都用空格隔开，最后字符串结束标志用#。\n"
                +"如：A * B #"+"\n"+"如：A + B #"+"\n"+"如：A * B + C * D #"+"\n输入字符串:");

        Scanner scanner = new Scanner(System.in);
        String s;
        //#号结束输入，输入的字符间要有空格，方便处理
        while (!(s=scanner.next()).equals("#")) {
            list.add(s);
        }
        Postfix postfix = new Postfix();
        String string =  postfix.transferToPostfix(list);
        string.replaceAll(" ","");
        System.out.print(string);
        Apply apply = new Apply();
        Vertex root = apply.scanString(string);
        System.out.println();
        Traverse traverse = new Traverse();
        traverse.doTraverse(root);
        List<Vertex> vertexList = traverse.vertexList;
        Iterator<Vertex> iterator = vertexList.iterator();
        BufferedWriter simu=null;
        try {
            simu=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("bdd.dot", false)));

            simu.write("digraph G{");
            simu.newLine();
            while (iterator.hasNext()){
                Vertex v = iterator.next();
                //System.out.println("输出该结点id"+"输出index"+"输出val" + "输出标志位");
                //System.out.println(v.getId() + " " + v.getIndex() + " "+ v.getVal() + v.isChanged());

                String first=null;
                String symbol=null;
                String next1=null;
                String next2=null;
                String action1=null;
                String action2=null;
                if ((v.getLow() != null) && (v.getHigh() != null)){
                    System.out.println();
                    System.out.println("输出该结点" +v.getIndex() + "左右孩子的index");
                    System.out.println(v.getLow().getIndex() + " "+ v.getHigh().getIndex());
                    first = "{\""+v.getIndex()+"\"}";
                    symbol = " -> ";
                    if ((v.getLow().getLow() != null) && (v.getLow().getHigh() != null)) {
                        next1 = "{\""+v.getLow().getIndex()+"\"}";
                    }else {
                        next1 = "{\""+"False"+"\"}";
                    }
                    if ((v.getHigh().getLow() != null) && (v.getHigh().getHigh() != null)) {
                        next2 = "{\""+v.getHigh().getIndex()+"\"}";
                    }else {
                        next2 = "{\""+"True"+"\"}";
                    }
                    action1 = " [label = \"" + "0" + "\"]";
                    action2 = " [label = \"" + "1" + "\"]";
                    simu.write(first+symbol+next1+action1);
                    simu.newLine();
                    simu.write(first+symbol+next2+action2);
                    simu.newLine();
                }else {
                    System.out.println();
                    System.out.println("该结点" + v.getIndex() + "为终端结点");


                }
            }
            simu.write("}");
            scanner.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }finally {
            try {
                simu.flush();
                simu.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec("dot "+"bdd.dot" + " -T png -o "+"bdd"+".png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

