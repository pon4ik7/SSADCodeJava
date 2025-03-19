package Main.Lecture;

interface WaterStateInterface{
    public void Heating();
    public void Freezing();
    public void Cooling();
}

class Water implements WaterStateInterface, Cloneable{//Cloneable is the build-in prototype pattern
    WaterStateInterface State;
    @Override
    public void Heating() {
    }

    @Override
    public void Freezing() {

    }

    @Override
    public void Cooling() {

    }
}

class Singleton{
    private static Singleton uniqueInstance;
    private Singleton(){}
    public static Singleton getInstance(){
        if(uniqueInstance == null){
            uniqueInstance = new Singleton();
        }
        return uniqueInstance;
    }
}

public class Main {
    public static void main(String[] args) {
        Singleton s = Singleton.getInstance();
    }
}
