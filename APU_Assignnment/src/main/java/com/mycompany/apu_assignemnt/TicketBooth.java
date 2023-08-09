package com.mycompany.apu_assignemnt;

public class TicketBooth {

    private int id; // Represents the number of passengers waiting at this booth
    private int queueLength; // Represents the number of passengers waiting at this booth
    public static final int MAX_QUEUE_LENGTH = 4;

    public TicketBooth(int id) {
        this.id = id;
    }

    public synchronized boolean buyTicket(int passengerId) throws InterruptedException {
        // This is where you'd simulate the action of buying a ticket.
        // To keep it simple, we're just returning true (successful purchase).
        queueLength++;
        System.out.println("Thread-Passenger-" + passengerId + ": Buying a ticket from the booth " + id);
        Thread.sleep((int) (Math.random() * 2 + 3) * 1000); // Random delay between 2-5 seconds
        System.out.println("Thread-Passenger-" + passengerId + ": Bought a ticket from the booth " + id);
        queueLength--;

        return true;
    }

    public boolean isAvailable() {
        return queueLength < MAX_QUEUE_LENGTH;
    }
}
