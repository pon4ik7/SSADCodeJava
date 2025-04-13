package Main.Labs.Lab8;

interface DocumentPrototype {

    DocumentPrototype Clone();

    public void handleInput();
}

interface DocumentState {

    public void handleInput();
}

class Draft implements DocumentState {

    @Override
    public void handleInput() {
        System.out.println("Draft, you can write all you want");
    }
}

class Review implements DocumentState {

    @Override
    public void handleInput() {
        System.out.println("Review, write you comment");
    }
}

class Final implements DocumentState {

    @Override
    public void handleInput() {
        System.out.println("Final, you can not write");
    }
}

class Document implements DocumentPrototype {

    private String fileName;
    private DocumentState state;

    @Override
    public DocumentPrototype Clone() {
        return new Document();
    }

    public Document() {
        fileName = "Document.txt";
        state = new Draft();
    }

    public Document(String fileName) {
        this.fileName = fileName;
        state = new Draft();
    }

    public Document(DocumentState state) {
        this.fileName = "Document.txt";
        this.state = state;
    }

    public void handleInput() {
        state.handleInput();
    }

    public String getFileName() {
        return fileName;
    }

    public void changeState(DocumentState newState) {
        state = newState;
    }
}

class Logger {

    private static Logger instance;

    private Logger() {
    }

    public String message() {
        return "Hello World";
    }

    static public Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }
}

public class SmartDocumentEditor {

    public static void main(String[] args) {
        Logger log = Logger.getInstance();
        Logger log1 = Logger.getInstance();
        System.out.println(log == log1);
        Document d = new Document(new Draft());
        d.handleInput();
        d.changeState(new Review());
        d.handleInput();
        d.changeState(new Final());
        d.handleInput();

        DocumentPrototype clone = d.Clone();
        clone.handleInput();
    }
}
