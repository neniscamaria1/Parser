import java.util.ArrayList;
import java.util.List;
class HashNode extends SymbolTable {
    public String info;
    HashNode next;

    public HashNode(String info) {
        this.info = info;
    }
}

public class SymbolTable {
    private List<HashNode> elements;
    private int noBuckets = 0;
    private int size;

    public SymbolTable() {
        elements = new ArrayList<>();
        noBuckets = 5;
        size = 0;
        for(int i=0; i< noBuckets; i++)
            elements.add(null);
    }

    public int getSize(){
        return size;
    }

    public void add(String element){
        //we find the bucket in which the element might be
        int index = hash(element);
        HashNode head = elements.get(index);
        //check if the element already exists
        while(head!=null){
            if(head.info.equals(element))
                return;
            head = head.next;
        }
        //insert value in chain
        size+=1;
        head = elements.get(index); //reinitialize the head
        HashNode nodeToAdd = new HashNode(element);
        nodeToAdd.next=head;//we add the new node in the head of the linked list
        elements.set(index,nodeToAdd);

        //if the load factor gets too big => resize
        if((1.0 * size/noBuckets) >= 0.75){
            List<HashNode> copy = elements;
            elements = new ArrayList<>();
            noBuckets *= 2;
            size = 0;
            //initialize new table
            for(int i=0;i<noBuckets; i++)
                elements.add(null);
            for (HashNode node : copy)
                while (node != null) {
                    add(node.info);
                    node = node.next;
                }
        }
    }

    public int findElement(String element){
        //we find the bucket in which the element might be
        int index = hash(element);
        HashNode head = elements.get(index);
        while(head!=null){
            if(head.info.equals(element))
               return index;
            head = head.next;
        }
        return -1;
    }
    private int hash(String element){
        int hashCode = element.hashCode();
        if(hashCode<0)
            hashCode = -1 * hashCode;
        int index = hashCode % noBuckets;
        return index;
    }
}
