package com.mycompany.apu_assignemnt;

public class TicketMachine {

    private boolean isWorking = true;

    public synchronized boolean buyTicket(int passengerId) throws InterruptedException {
        if (isWorking) {
            Thread.sleep((int) (Math.random() * 1 + 1) * 1000); // Random delay between 1-1 seconds
            System.out.println("Thread-Passenger-" + passengerId + ": Bought a ticket from the machine.");
            return true;
        } else {
            return false;
        }
    }

    public void setBroken() {
        System.out.println("Thread-TicketMachine: Breakdown!");
        this.isWorking = false;
    }

    public void setWorking() {
        System.out.println("Thread-TicketMachine: Functional again.");
        this.isWorking = true;
    }
}
