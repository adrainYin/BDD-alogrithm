import java.util.*;

public class Main {

    public static void main(String[] args) {

        LinkedList<String> list=new LinkedList<>();
        //Scanner scanner=new Scanner("! ( A * C ) + B * C #");
        System.out.println("输入字符串，以空格分开每个字符，并以 # 结束");
        System.out.println("示例： ( A + B ) * ! C #");
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
        while (iterator.hasNext()){
            Vertex v = iterator.next();
            System.out.println("输出该结点id"+"输出index"+"输出val" + "输出标志位");
            System.out.println(v.getId() + " " + v.getIndex() + " "+ v.getVal() + v.isChanged());
            if ((v.getLow() != null) && (v.getHigh() != null)){
                System.out.println("输出该结点" +v.getId() + "左右孩子的ID");
                System.out.println(v.getLow().getId() + " "+ v.getHigh().getId());

            }
            else {
                System.out.println("该结点" + v.getId() + "为终端结点");
            }
        }
        scanner.close();

    }
}
