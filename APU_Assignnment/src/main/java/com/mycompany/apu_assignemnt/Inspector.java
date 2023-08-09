package com.mycompany.apu_assignemnt;

import java.util.Random;

public class Inspector {

    private Random random = new Random();

    public synchronized void inspectTicket(int passengerId) {
        // Print a message indicating the ticket is being inspected

        //TODO Extends Thread
        //TODO Actively Consume Passengers in Waiting Area when Bus arrives
        System.out.println("Thread-Passenger-" + passengerId + ": Ticket is being inspected...");
        // Sleep to simulate the time taken for ticket inspection, with random duration between 500 to 2000 milliseconds
        try {
            int sleepTime = 500 + random.nextInt(1500); // Random time between 500 to 2000 milliseconds
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Print a message indicating the ticket has been inspected
        System.out.println("Thread-Passenger-" + passengerId + ": Ticket inspected.");
    }
}
