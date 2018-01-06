import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class Apply {

    //扫描出最大的index，因为一个布尔表达式里index值是唯一的
    int maxIndex = -1;
    //根结点的栈
    Stack<Vertex> stack = new Stack<>();

    /**
     * 扫描字符串的主函数
     * @param profix
     * 返回最终的根结点
     */
    public Vertex scanString(String profix){

        char[] strings = profix.toCharArray();

        List<Character> charList = new ArrayList<>();
        for (int i = 0; i < strings.length; i++) {
            charList.add(strings[i]);
        }

        //找到表达式中最大的index,返回全局变量
        Iterator itemp = charList.iterator();
        while (itemp.hasNext()){
            String s = itemp.next().toString();
            if (!isOperator(s)){
                char curchar = s.charAt(0);
                int currindex = (int)curchar - 64;
                if (currindex > maxIndex){
                    maxIndex = currindex;
                }
            }
        }
        //第二遍遍历布尔表达式
        Iterator it = charList.iterator();
        while (it.hasNext()){
            String s = it.next().toString();
            //扫描字符串出现了非操作符，则new一个基本的变量结点
            if (!isOperator(s)){
                Vertex newVertex = createSimpleVertex(s);
                stack.push(newVertex);
            }
            //扫描到了操作符，那么对栈内的变量操作，并返回apply后的根结点再次进栈
            else{
                //遇到取反符号则将栈中DAG图的终结点的val值改变
                if (s.equals("!")){
                    Vertex negationVertex = stack.pop();
                    Traverse traverse = new Traverse();
                    //终端结点的val值取反
                    Vertex vertex = traverse.negationTraverse(negationVertex);
                    //重置所有结点的isChanged标志位
                    //vertex = traverse.initIsChanged(vertex);
                    stack.push(vertex);

                }
                if (s.equals("+") || s.equals("*")){
                    Vertex sec = stack.pop();
                    Vertex fir = stack.pop();
                    Vertex rootVertex = doApply(fir,s,sec);
                    stack.push(rootVertex);
                }

            }
        }

        //全部结束后，栈内仅有唯一的结点，就是化简规约后的根结点
        Vertex root = stack.peek();
        return root;
    }

    private Vertex doApply(Vertex fir,String operator,Vertex sec){
        //生成两个DAG图的所有结点
        Traverse traverse1 = new Traverse();
        traverse1.doTraverse(fir);
        List<Vertex> firVertex = traverse1.vertexList;
        int numOfFir = firVertex.size();
        //Vertex firroot = firVertex.get(0);

        Traverse traverse2 = new Traverse();
        traverse2.doTraverse(sec);
        List<Vertex> secVertex = traverse2.vertexList;
        int numOfSec = secVertex.size();
        //Vertex secroot = secVertex.get(0);

        //对两个子图的所有结点编唯一的ID
        for (int i = 0; i < firVertex.size(); i++) {
            Vertex tmp = firVertex.get(i);
            tmp.setId(i);
            firVertex.set(i,tmp);
        }
        for (int i = 0; i < secVertex.size(); i++) {
            Vertex tmp = secVertex.get(i);
            tmp.setId(i);
            secVertex.set(i,tmp);
        }

        Vertex firroot = firVertex.get(0);
        Vertex secroot = secVertex.get(0);

        //设置二维数组，用于判断两个结点是否已经合并过
        /**
         * 该函数为主进程函数
         */
         Vertex[][] metrix = new Vertex[numOfFir][numOfSec];
         Vertex vertex = applyStep(firroot,secroot,operator,metrix);
         Reduce reduce = new Reduce();
         Vertex redVertex =  reduce.doReduce(vertex);

        return redVertex;
    }

    /**
     * 递归函数，合并两个DAG图
     * @param v1
     * @param v2
     * @param operator
     * @param metrix 辅助二维数组，判定两个非终结点是否已经合并过，若合并过就不再操作，减少时间开销
     * @return
     */
    private Vertex applyStep(Vertex v1,Vertex v2,String operator,Vertex[][] metrix){
        Vertex u = metrix[v1.getId()][v2.getId()];
        if (u != null){
            return u;
        }
        u = new Vertex();
        u.setMark(v1.getMark());
        metrix[v1.getId()][v2.getId()] = u;
        u.setVal(compute(v1.getVal(),operator,v2.getVal()));
        if (u.getVal() != 2){
            u.setIndex(maxIndex + 1);
            u.setLow(null);
            u.setHigh(null);
        }
        else {
            u.setIndex(min(v1.getIndex(),v2.getIndex()));
            Vertex vlow1,vhigh1,vlow2,vhigh2;
            if (v1.getIndex() == u.getIndex()){
                vlow1 = v1.getLow();
                vhigh1 = v1.getHigh();

            }
            else {
                vlow1 = v1;
                vhigh1 = v1;
            }

            if (v2.getIndex() == u.getIndex()){
                vlow2 = v2.getLow();
                vhigh2 = v2.getHigh();
            }
            else {
                vlow2 = v2;
                vhigh2 = v2;
            }
            u.setLow(applyStep(vlow1,vlow2,operator,metrix));
            u.setHigh((applyStep(vhigh1,vhigh2,operator,metrix)));
        }

        return u;
    }

    private int min(int num1,int num2){
        if (num1 > num2){
            return num2;
        }
        else {
            return num1;
        }
    }

    /**
     * 计算两个结点的val函数
     * @param val1
     * @param operator
     * @param val2
     * @return 返回结点val值
     */
    private int compute(int val1,String operator,int val2){
        if (operator.equals("+")){
            if (val1 == 1 || val2 == 1){
                return 1;
            }
            else if (val1 ==0 && val2 == 0){
                return 0;
            }
            else return 2;
        }

        if (operator.equals("*")){
            if (val1 == 0 || val2 == 0){
                return 0;
            }
            else if (val1 == 1 && val2 == 1){
                return 1;
            }
            else return 2;
        }
        return 0;
    }

    /**
     * 判断操作符函数
     * @param oper
     * @return 返回boolean值
     */
    private static boolean isOperator(String oper){
        if (oper.equals("+")||oper.equals("-")||oper.equals("/")||oper.equals("*")
                ||oper.equals("(")||oper.equals(")")||oper.equals("!")) {
            return true;
        }
        return false;
    }

    /**
     * 生成初始的单个结点
     * @param s
     * @return 返回初始结点，有两个分支，分别是0和1
     */
    private Vertex createSimpleVertex(String s){
        char[] c = s.toCharArray();
        Vertex vertex = new Vertex();
        vertex.setMark(0);
        vertex.setIndex((int)c[0] - 64);
        vertex.setVal(2);

        Vertex vertexLeft = new Vertex();
        vertexLeft.setMark(0);
        vertexLeft.setIndex(maxIndex + 1);
        vertexLeft.setVal(0);
        vertexLeft.setLow(null);
        vertexLeft.setHigh(null);

        Vertex vertexRight = new Vertex();
        vertexRight.setMark(0);
        vertexRight.setIndex(maxIndex + 1);
        vertexRight.setVal(1);
        vertexRight.setLow(null);
        vertexRight.setHigh(null);

        vertex.setLow(vertexLeft);
        vertex.setHigh(vertexRight);
        return vertex;
    }
}
