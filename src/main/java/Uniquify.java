//import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;

/*
Input:
<span><b> This </b> is very <i>funny</i></span>
Tree:
              1<span>
       /      |          \
   2<b>     " is very"   3<i>
     |        5            |
   "This"              " funny"
     4                     6

To Solve:
* Get the tree from the given DOM construct
* Given the DOM construct search for specific value within sub-strings
 */
enum Type {
    VALUE,
    START_TAG,
    END_TAG
};
class MyStack<T> {
    T[] entries;
    int front, end, size;
    public MyStack(Class<T> cls, int size) {
        entries = (T[]) Array.newInstance(cls, size);
        front = end = 0;
        this.size = size;
    }
    public boolean isEmpty() {
        if(front == end) return true;
        else return false;
    }
    public boolean isFull() {
        if(end == size) return true;
        else return false;
    }
    public boolean push(T entry) {
        if (!isFull()) {
            entries[front] = entry;
            front = (front + 1) % size;
            return true;
        } else  return false;
    }
    public T pop() {
        if(! isEmpty()) {
            front--;
            T entry = entries[front];
            entries[front] = null;
            return entry;
        } else return null;
    }
    public final int getCapacity() {
        return size;
    }
    public final int getSize() {
        return front - end;
    }
}

//TODO: Get timing functions embedded to fine tune performance
class HtmlNodeProcessor {
    String[] keywords = {"b", "i", "span"};
    char[] startKeyMarkers = new char[]{'<'};
    char[] endKeyMarkers = new char[]{'>'};
    boolean startKeyTag, endKeyTag;
    String keyword;
    Tree tree;
    public final String[] getKeywords() {
        return keywords;
    }

    private Node checkAndGetNode(String t_key) {
        Matcher m = Pattern.compile("<[a-z/]+>").matcher(t_key);
        if (m.find()) {
            startKeyTag = false;
            String key = t_key.substring(m.start() + 1, m.end() - 1); //eliminate starting and end angular bkt
            if(key.charAt(0) == '/') { //check if it is end tag
                key = key.substring(1, key.length());
                return new Node(key, Type.END_TAG);
            } else {
                return new Node(key, Type.START_TAG);
            }
        }
        return null;
    }
    public Node detectHTMLKeyword(char c) {
        if(c == '<') {
            String temp;
            startKeyTag = true;
            temp = keyword;
            keyword = "<";
            if(temp == "") return null;
            else return new Node(temp, Type.VALUE); //whatever is stored so far in keyword
        }
        keyword = keyword + c;
        if(startKeyTag) {
            Node n =  checkAndGetNode(keyword);
            if(n != null) keyword = "";
            return n;
        }
        return null;
    }
    private void updateTreeWithTag(MyStack<Node> myStack, Node n) {
        Node t_node = myStack.pop(); //value for the node
        //collapse tree till you get the start tag same as n
        while(!(t_node.type == Type.START_TAG && t_node.value.compareToIgnoreCase(n.value) == 0)) {
            n.addToStart(t_node);
            t_node = myStack.pop();
        }
        myStack.push(n); //put this compressed node back on stack
    }
    public Tree run(String html) {
        MyStack<Node> myStack = new MyStack<Node>(Node.class, 100);
        startKeyTag = true;
        keyword = "";
        Node n = null;
        for(char c : html.toCharArray()) {
            n = detectHTMLKeyword(c);
            if(n != null) {
                switch(n.type) {
                    case START_TAG:
                    case VALUE:
                        myStack.push(n);
                        break;
                    case END_TAG:
                        updateTreeWithTag(myStack, n);
                        break;
                    default:
                        break;
                }
            }
        }
        n = myStack.pop();
        tree.add(n);
        return tree;
    }
    public HtmlNodeProcessor() {
        //printKeywords();
        tree = new Tree();
    }
}
class Node {
    public Node(String v, Type t) {
        this.value = v;
        this.type = t;
    }
    //TODO: Make them private with getters and setters
    String value;
    Type type;
    int id;
    //TODO: replace LinkedList with own implementation
    LinkedList<Node> children;
    public int childCount() {
        return this.children.size();
    }
    public void addToStart(Node e) {
        if(this.children == null)
            this.children = new LinkedList<Node>();
        this.children.addFirst(e);
    }
    public String toString() {
        String t_str = "{" + id + ":" + "[" + type + ":" + value + "]:(";
        if (children != null) {
            for (Node c : children) {
                t_str += c.id + ",";
            }
        }
        t_str += ")}";
        return t_str;
    }
}
class Tree {
    Node head;
    int id_counter = 0;
    public Tree(Node... args) {
        if(args.length > 0) {
            head = args[0];
        } else {
            head = null;
        }
    }
    interface WalkFunction {
        public boolean walk(String p);
    }
    //TODO: better way to avoid two function authoring here
    public void add(Node node) {
        add(node, null);
    }
    public void add(Node node, Node parent) {
        if(head == null) {
            head = node; //ignore parent
        } else {
            //TODO: locate parent for error scenarios??
            parent.children.add(node);
        }
    }

    private void enumerateNode(Node node) {
        if(node.children == null) return;
        for(Node c:node.children) {
            if(c.type == Type.END_TAG && c.id  != 0) {
                c.id = ++id_counter;
            }
        }
        //enum value only nodes
        for(Node c:node.children) {
            if(c.type == Type.END_TAG) {
                if(c.children.size() == 1 && c.id != 0){ // value only
                    c.children.get(0).id = ++id_counter;
                }
            } else if(c.type == Type.VALUE && c.id != 0) {
                c.id = ++id_counter;
            }
        }
    }

    //TODO: better way to avoid two function authoring here
    //TODO: enumerate can be separate class
    public void enumerate() {
        if(head != null) enumerate(head);
    }
    public void enumerate(Node t_node) {
        if(t_node.id == 0)
            t_node.id = ++id_counter;
        enumerateNode(t_node);
        if(t_node.children != null) {
            for(Node c: t_node.children) {
                enumerate(c);
            }
        }
    }
    //TODO: how to beautify the print to represent tree
    public void print(Node me) {
        if(me != null) {
            System.out.println(me);
            if(me.children == null) return;
            for(Node child:me.children) {
                print(child);
            }
        }
    }
    //TODO: better way to avoid two function authoring here
    //TODO: this operation can be moved to SearchTree
    public List<Node> collectNodes(WalkFunction fn, List<Node> collector) {
        return collectNodes(fn, collector, head);
    }
    public List<Node> collectNodes(WalkFunction fn, List<Node> collector, Node t_node) {
        if(t_node.type == Type.VALUE && fn.walk(t_node.value) == true){
            collector.add(t_node);
        }
        if(t_node.children != null) {
            for(Node c: t_node.children)
                collectNodes(fn, collector, c);
        }
        return collector;
    }
    public void print() {
        print(head);
    }
}


class SearchTree{
    private Tree tree;
    public SearchTree(Tree tree){this.tree = tree;}
    public List<Node> getNodesContaining(String text) {
        List<Node> list = tree.collectNodes((node_value) -> {
            int ret_val = node_value.indexOf(text);
            if(ret_val >= 0)
                return true;
            return false;
        }, new ArrayList<>());
        return list;
    }
}
public class Uniquify {
    public static void main(String[] args) {
        HtmlNodeProcessor processor = new HtmlNodeProcessor();
        Tree tree = processor.run("<div><span><b>This</b> is the <i>message</i></span><div><p>What is the message</p></div></div>");
        tree.enumerate();
        tree.print();
        System.out.println("Nodes containing: message");
        long time_start = System.currentTimeMillis();
        List<Node> nodes = new SearchTree(tree).getNodesContaining("message");
        long time_end = System.currentTimeMillis();
        System.out.println("Elapsed time: " + (time_end - time_start) + " ms");
        for(Node c: nodes) {
            System.out.println(c.id + ", ");
        }
    }
}


