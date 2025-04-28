package Main.TSAs.Assignment4.TestFlyWeight.WithoutFlyWeight;

import java.math.BigDecimal;
import java.util.*;
import org.openjdk.jol.info.*;

/**
 * Main class for directory walking application.
 * Reads input commands and manages directory structure.
 */
public class DirectoryWalkerWithoutFlyWeight{
    /**
     * Main entry point for the application.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Finder finder = new Finder();
        int n = input.nextInt();
        for (int i = 0; i < n; i++) {
            String command = input.next();
            switch (command) {
                case "DIR":
                    finder.addDir(input.nextLine());
                    break;
                case "FILE":
                    finder.addFile(input.nextInt(), input.next(), input.next(), input.next(), input.nextDouble(), input.next());
                    break;
                default:
                    System.out.println("Invalid command");
            }
        }
        System.out.println(finder.displayMemory(0));
        System.out.println(finder.displayTree(0));
        // Check size without fly weight pattern
        System.out.println("Total size: " + GraphLayout.parseInstance(finder).totalSize() + " bytes");
    }
}

/**
 * Manages the directory structure and provides operations on it.
 */
class Finder {
    private final HashMap<Integer, Directory> dirs;
    /**
     * Initializes the Finder with a root directory.
     */
    public Finder() {
        Directory head = new Directory(".");
        dirs = new HashMap<>();
        dirs.put(0, head);
    }

    /**
     * Adds a new directory to the structure.
     *
     * @param line Input line containing directory information
     */
    public void addDir(String line) {
        String[] arr = line.split(" ");
        int parent;
        int id;
        String name;
        if (arr.length == 3) {
            parent = 0;
            id = Integer.parseInt(arr[1]);
            name = arr[2];
        } else {
            id = Integer.parseInt(arr[1]);
            parent = Integer.parseInt(arr[2]);
            name = arr[3];
        }
        Directory dir = new Directory(name);
        Directory parentDir = dirs.get(parent);
        dirs.put(id, dir);
        parentDir.addChild(dir);
    }

    /**
     * Adds a new file to the directory structure.
     *
     * @param parent   ID of parent directory
     * @param type     File type ("T" for read-only)
     * @param owner    File owner
     * @param group    File group
     * @param size     File size
     * @param fullName File name with extension
     */
    public void addFile(int parent, String type, String owner, String group, double size, String fullName) {
        String name = fullName.split("\\.")[0];
        String ext = fullName.split("\\.")[1];
        File file = new File(name, size);
        Directory dir = dirs.get(parent);
        file.setProperties(ext, type.equals("T"), owner, group);
        dir.addChild(file);
    }

    /**
     * Calculates and displays total memory usage for a directory.
     *
     * @param id Directory ID
     * @return Formatted string with total memory usage
     */
    public String displayMemory(int id) {
        Directory dir = dirs.get(id);
        SizeVisitor visitor = new SizeVisitor();
        dir.accept(visitor);
        BigDecimal memory = visitor.getTotalSize();
        memory = memory.stripTrailingZeros();
        return "total: " + memory.toPlainString() + "KB";
    }

    /**
     * Displays the directory tree structure.
     *
     * @param id Root directory ID
     * @return Formatted string representing the tree structure
     */
    public String displayTree(int id) {
        Directory dir = dirs.get(id);
        return dir.display("");
    }
}


/**
 * Abstract base class for file system nodes (files and directories).
 */
abstract class Node {
    protected String name;

    /**
     * Creates a new node with given name.
     *
     * @param n Node name
     */
    Node(String n) {
        name = n;
    }

    /**
     * Accepts a visitor for processing the node.
     *
     * @param visitor Visitor implementation
     */
    void accept(Visitor visitor) {
        visitor.visit(this);
    }

    /**
     * Displays the node with given prefix.
     *
     * @param prefix Display prefix
     * @return Formatted string representation
     */
    abstract String display(String prefix);
}

/**
 * Represents a directory in the file system.
 */
class Directory extends Node {
    private ArrayList<Node> children;

    /**
     * Creates a new directory.
     *
     * @param n Directory name
     */
    public Directory(String n) {
        super(n);
        children = new ArrayList<>();
    }

    /**
     * Creates iterator for directory children.
     *
     * @return Children iterator
     */
    public Iterator<Node> createIterator() {
        return children.iterator();
    }

    /**
     * Adds a child node to the directory.
     *
     * @param n Child node to add
     */
    public void addChild(Node n) {
        children.add(n);
    }

    @Override
    public String display(String prefix) {
        StringBuilder sb = new StringBuilder(prefix + name);
        WalkIterator iterator = new WalkIterator(this.createIterator());
        while (iterator.hasNext()) {
            Node node = iterator.next();
            String childPrefix = prefix.replace("└──", "   ").replace("├──", "│  ");
            if (iterator.hasNext()) {
                sb.append("\n").append(node.display(childPrefix + "├── "));
            }else{
                sb.append("\n").append(node.display(childPrefix + "└── "));
            }
        }
        return sb.toString();
    }

    /**
     * Checks if directory has children.
     *
     * @return True if directory is not empty
     */
    public boolean isLeaf() {
        return !children.isEmpty();
    }

    /**
     * Gets directory children.
     *
     * @return List of child nodes
     */
    public List<Node> getChildren() {
        return children;
    }
}

/**
 * Represents a file in the file system.
 */
class File extends Node {
    private String extension;
    private String owner;
    private String group;
    private Boolean type;
    private BigDecimal size;

    /**
     * Creates a new file.
     *
     * @param n    File name
     * @param size File size
     */
    public File(String n, double size) {
        super(n);
        this.size = BigDecimal.valueOf(size);
    }

    /**
     * Sets file properties.
     * @param ext
     * @param type
     * @param owner
     * @param group
     */
    public void setProperties(String ext, boolean type, String owner, String group) {
        this.extension = ext;
        this.type = type;
        this.owner = owner;
        this.group = group;
    }

    /**
     * Gets file size.
     *
     * @return File size
     */
    BigDecimal getSize() {
        return size;
    }

    @Override
    public String display(String prefix) {
        return prefix + name + "." + extension + " (" + formatSize(size) + ")";
    }

    private String formatSize(BigDecimal size) {
        size = size.stripTrailingZeros();
        return size.toPlainString() + "KB";
    }
}

/**
 * Visitor interface for processing nodes.
 */
interface Visitor {
    /**
     * Visits a node.
     *
     * @param node Node to visit
     */
    void visit(Node node);
}

/**
 * Visitor implementation for calculating total size.
 */
class SizeVisitor implements Visitor {
    BigDecimal totalSize;

    /**
     * Creates new size visitor.
     */
    public SizeVisitor() {
        totalSize = new BigDecimal(0);
    }

    @Override
    public void visit(Node node) {
        if (node instanceof File) {
            totalSize = totalSize.add(((File) node).getSize());
        } else {
            for (Node child : ((Directory) node).getChildren()) {
                child.accept(this);
            }
        }
    }

    /**
     * Gets total calculated size.
     *
     * @return Total size
     */
    public BigDecimal getTotalSize() {
        return totalSize;
    }
}

/**
 * Extended iterator interface for directory walking.
 */
interface IteratorDir extends Iterator<Node> {
    /**
     * Checks if there are more nodes to iterate.
     *
     * @return True if more nodes available
     */
    public boolean hasNext();

    /**
     * Gets next node in iteration.
     *
     * @return Next node
     */
    public Node next();
}

/**
 * Iterator implementation for depth-first directory walking.
 */
class WalkIterator implements IteratorDir {
    private final Stack<Iterator<Node>> stack = new Stack<>();

    /**
     * Creates new walk iterator starting from root.
     *
     * @param root Root iterator
     */
    public WalkIterator(Iterator root) {
        stack.push(root);
    }

    @Override
    public boolean hasNext() {
        if (stack.isEmpty()) return false;
        if (stack.peek().hasNext()) return true;
        stack.pop();
        return hasNext();
    }

    @Override
    public Node next() {
        Node node = stack.peek().next();
        if (node instanceof Directory) {
            Directory dir = (Directory) node;
            if (!dir.isLeaf()) stack.push(dir.createIterator());
        }
        return node;
    }
}