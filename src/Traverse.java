import java.util.ArrayList;
import java.util.List;

public class Traverse {

    List<Vertex> vertexList = new ArrayList<>();
    /**
     * 前序遍历函数，返回所有结点的list集合，按照前序遍历的顺序排列
     * @param vertex
     */
    public void doTraverse(Vertex vertex){
        if (vertex != null){
            vertex.setMark((vertex.getMark() +1) %2);
            vertexList.add(vertex);
            if (vertex.getLow() != null){
                if (vertex.getMark() != vertex.getLow().getMark()){
                    doTraverse(vertex.getLow());
                }
            }
            if (vertex.getHigh() != null){
                if (vertex.getMark() != vertex.getHigh().getMark()){
                    doTraverse(vertex.getHigh());
                }
            }
        }
    }
    /**
     * 取反操作，将所有终端结点的val值取反
     * 前序遍历，改变所有终结点的val值
     * @param vertex
     * @return
     */
    public Vertex negationTraverse(Vertex vertex){
        if (vertex != null){
            if ((vertex.getLow() != null) && (vertex.getHigh() != null)){
                negationTraverse(vertex.getLow());
                negationTraverse(vertex.getHigh());
            }
            else{
                //判断标志位，如果已经改变过值，那么在本次操纵中就不需要再次改变
                if (!vertex.isChanged()) {
                    vertex.setVal((vertex.getVal() + 1) % 2);
                    vertex.setChanged(true);
                }
            }

        }
        return vertex;
    }
    /**
     * 将所有结点的isChanged标志位重置，方便下次取反操作
     * @param vertex
     * @return
     */
    public Vertex initIsChanged(Vertex vertex){
        if (vertex != null){
            vertex.setChanged(false);
            if ((vertex.getLow() != null) && (vertex.getHigh() != null)){
                initIsChanged(vertex.getLow());
                initIsChanged(vertex.getHigh());
            }
        }
        return vertex;
    }

}