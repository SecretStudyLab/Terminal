package com.mycompany.apu_assignemnt;

public class TicketMachine extends Thread {

    private boolean isWorking = true;

    @Override
    public void run() {
        while (true) { // Run continuously
            if (Math.random() < 0.2) {
                setBroken();
                try {
                    Thread.sleep((int) (Math.random() * 2 + 5) * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                setWorking();
            }

            try {
                Thread.sleep(5 * 1000); // Wait 5 seconds before checking again
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized boolean buyTicket(int passengerId) throws InterruptedException {
        if (isWorking) {
            Thread.sleep((int) (Math.random() * 1 + 1) * 1000); // Random delay between 1-2 seconds
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
