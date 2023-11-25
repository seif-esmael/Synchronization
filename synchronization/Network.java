/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package synchronization;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Network {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("What is the number of WI-FI Connections?");
        int maxConnections = new Scanner(System.in).nextInt();
        System.out.println("What is the number of devices Clients want to connect?");
        int numOfDevices = new Scanner(System.in).nextInt();
        ArrayList<Device> devices = new ArrayList<>();
        Router router = new Router(maxConnections);
        for (int i = 0; i < numOfDevices; i++) {
            String deviceNT = new Scanner(System.in).nextLine();
            String deviceName = deviceNT.split(" ")[0];
            String deviceType = deviceNT.split(" ")[1];
            devices.add(new Device(deviceName, deviceType, router));
        }
        for (Device device : devices) {
            System.out.println(device.getName() + " " + device.getType() + " arrived");
            Thread thread = new Thread(device);
            thread.start();
            Thread.sleep(1000);
        }
    }
}
// _____________________________________________________________________________________________________________
class Device implements Runnable {
    private String name;
    private String type;
    private Router router;
    private int index;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Router getRouter() {
        return router;
    }

    public void setRouter(Router router) {
        this.router = router;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    // ++++++++++++++++++
    Device(String name, String type, Router router) {
        this.name = name;
        this.type = type;
        this.router = router;
    }

    // +++++++++++++++++++
    @Override // the Threads run method
    public void run() {
        connect();
        performActivity();
        logout();
    }

    // ++++++++++++++++++++
    // define each of the run functions
    private void connect() {
        System.out.println(name + " Waiting");
        router.occupyConnection(this);
        System.out.println(name + " Connected");
    }

    // ++++++++++++++++++++++
    private void performActivity() {
        // gets the connection index from the router
        int connectionIndex = router.getConnectedDevices().indexOf(this) + 1;
        System.out.println(
                "Connection " + connectionIndex + ": " + name + " performs online activity");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    // +++++++++++++++++++++++
    private void logout() {
        // gets the connection index from the router
        int connectionIndex = router.getConnectedDevices().indexOf(this) + 1;
        System.out.println("Connection " + connectionIndex + ": " + name + " Disconnected");
        router.releaseConnection(this);
    }
    // +++++++++++++++++++++++

    @Override
    public String toString() {
        return "(" + name + ") (" + type + ")";
    }
}

// _____________________________________________________________________________________________________________
class Router {
    private int maxConnections;
    private List<Device> ConnectedDevices;
    private Semaphore semaphore;

    Router(int maxConnections) {
        this.maxConnections = maxConnections;
        this.ConnectedDevices = new ArrayList<>();
        this.semaphore = new Semaphore(maxConnections);
    }

    public void occupyConnection(Device device) {
        semaphore.acquire();
        ConnectedDevices.add(device);
    }

    public void releaseConnection(Device device) {
        ConnectedDevices.remove(device);
        semaphore.release();
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public List<Device> getConnectedDevices() {
        return ConnectedDevices;
    }

}

// _____________________________________________________________________________________________________________
class Semaphore {
    private int value;

    Semaphore(int value) {
        this.value = value;
    }

    public synchronized void acquire() {
        while (value == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        value--;
    }

    public synchronized void release() {
        value++;
        notify();
    }

    public int getValue() {
        return value;
    }
}
