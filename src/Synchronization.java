import java.io.*;
import java.util.*;
class Router{
    private boolean connections[];
    private Semaphore semaphore;
    Router(int maxConnections) {
        semaphore = new Semaphore(maxConnections);
        connections = new boolean[maxConnections];
    }
    public void occupyDevice(Device device){
        if (semaphore.getValue() == 0){
            semaphore.output("(" +device.getDeviceName() + ") (" + device.getDeviceType() + ") arrived and waiting");
        }else {
            semaphore.output("(" +device.getDeviceName() + ") (" + device.getDeviceType() + ") arrived ");
        }
        semaphore.acquire();
        for (int i = 0; i < connections.length; i++) {
            if (!connections[i]){
                connections[i] = true;
                device.setIndex(i+1);
                break;
            }
        }
        semaphore.output("Connection " + device.getIndex() + ": " + device.getDeviceName() +" Occupied");
        semaphore.output("Connection " + device.getIndex() + ": "+ device.getDeviceName() +" login");
    }
    public void removeDevice(Device device){
        connections[device.getIndex()-1] = false;
        semaphore.remove();
    }
    public void output(String text) {
        semaphore.output(text);
    }
}
//_____________________________________________________________________________________
class Device implements Runnable{
    private String deviceName;
    private String deviceType;
    private Router router;
    private int index;
    Device(String deviceName, String deviceType, Router router) {
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        this.router = router;
    }
    @Override
    public void run() {
        connect();
        performActivity();
        logout();
    }
    public void connect(){
        router.occupyDevice(this);
    }
    public void performActivity(){
        router.output("Connection " + index + ": "+ deviceName +" performs online activity");
        try {
            Random random = new Random();
            Thread.sleep(random.nextInt(2000, 4000));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void logout(){
        router.output("Connection " + index + ": " + deviceName + " logged out");
        router.removeDevice(this);
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    public int getIndex() {
        return index;
    }

    public String getDeviceType() {
        return deviceType;
    }
}
//_______________________________________________________________________________________
class Semaphore{
    private int value;
    Semaphore(int value) {
        this.value = value;
    }
    public synchronized void acquire(){
        while (value == 0){
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        value--;
    }
    public synchronized void remove(){
        value++;
        notify();
    }

    public void output(String text) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true));
            writer.write(text + "\n");
            writer.close();
        }catch (IOException e) {
            System.out.println("Error writing to file");
        }
    }

    public int getValue() {
        return value;
    }
}
//_____________________________________________________________________________________
class Network {
    public void run() throws InterruptedException {
        System.out.println("What is the number of WI-FI Connections?");
        int maxConnections = new Scanner(System.in).nextInt();

        System.out.println("What is the number of devices Clients want to connect?");
        int numOfDevices = new Scanner(System.in).nextInt();

        ArrayList<Device> devices = new ArrayList<>();
        Router router = new Router(maxConnections);

        for (int i = 0; i < numOfDevices; i++) {
            String deviceNT[] = new Scanner(System.in).nextLine().split(" ");
            devices.add(new Device(deviceNT[0], deviceNT[1], router));
        }
        for (Device device : devices) {
            Thread thread = new Thread(device);
            thread.start();
            Thread.sleep(10);
        }
    }
}
//_____________________________________________________________________________________
class Main{
    public static void main(String[] args) {
        Network n = new Network();
        try {
            n.run();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}