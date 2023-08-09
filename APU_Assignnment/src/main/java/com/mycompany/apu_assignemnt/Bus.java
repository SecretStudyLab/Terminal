package com.mycompany.apu_assignemnt;

public class Bus extends Thread {

    private int id;
    private int queueLength = 0;
    private int passengersBoardedAndDeparted = 0;
    private boolean atTerminal = true;
    private final Object busLock = new Object();

    public Bus(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (busLock) {
                if (atTerminal && queueLength >= 10) {
                    depart();
                }
                try {
                    busLock.wait();
                } catch (InterruptedException e) {
                    System.out.println("Thread-Bus-" + id + " interrupted.");
                    break;
                }
            }
        }
    }

    public void boardBus(int passengerId) throws InterruptedException {
        synchronized (busLock) {
            while (!isAvailable() || !atTerminal) {
                System.out.println("Thread-Passenger-" + passengerId + ": Bus " + id + " is not available. Waiting.");
                busLock.wait();
            }
            System.out.println("Thread-Passenger-" + passengerId + ": Boarding Bus " + id + ".");
            Thread.sleep((int) (Math.random() * 2 + 3) * 1000);
            queueLength++;
            passengersBoardedAndDeparted++;
            busLock.notifyAll();
        }
    }

    private void depart() {
        synchronized (busLock) {
            System.out.println("Thread-Bus-" + id + ": Departing with " + queueLength + " passengers.");
            queueLength = 0;
            atTerminal = false;

            new Thread(() -> {
                try {
                    Thread.sleep((int) (Math.random() * 2 + 3) * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (busLock) {
                    atTerminal = true;
                    System.out.println("Thread-Bus-" + id + ": Returned to the terminal.");
                    busLock.notifyAll();
                }
            }).start();
        }
    }

    public boolean isAvailable() {
        return queueLength < 10;
    }

    public int getPassengersBoardedAndDeparted() {
        return passengersBoardedAndDeparted;
    }
}
