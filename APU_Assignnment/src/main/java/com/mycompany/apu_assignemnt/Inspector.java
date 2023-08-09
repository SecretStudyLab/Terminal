package com.mycompany.apu_assignemnt;

import java.util.List;
import java.util.Queue;
import java.util.Random;

public class Inspector extends Thread {

    private Random random = new Random();
    private Queue<Integer> waitingArea; // Assuming passengers are represented by their ID
    private boolean isBusAvailable = false; // Flag for bus availability

    public Inspector(List<WaitingArea> waitingAreas) {
        this.waitingArea = waitingArea;
    }

    public synchronized void notifyBusArrival() {
        isBusAvailable = true;
        notifyAll(); // Notify the inspector thread that a bus has arrived
    }

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                while (!isBusAvailable) {
                    try {
                        wait(); // Wait until a bus is available
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // Consume passengers in the waiting area when the bus is available
                while (!waitingArea.isEmpty()) {
                    int passengerId = waitingArea.poll();
                    inspectTicket(passengerId);
                }
                isBusAvailable = false; // After processing all passengers, mark the bus as unavailable
            }
        }
    }

    void inspectTicket(int passengerId) {
        System.out.println("Thread-Passenger-" + passengerId + ": Ticket is being inspected...");
        try {
            int sleepTime = 1000 + random.nextInt(500); // Random time between 500 to 2000 milliseconds
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Thread-Passenger-" + passengerId + ": Ticket inspected.");
    }
}
