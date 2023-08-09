package com.mycompany.apu_assignemnt;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;

public class Terminal {

    public static final int MAX_CAPACITY = 15;
    public static final int ALLOW_ENTRY_CAPACITY = 12; // 80% of MAX_CAPACITY

    public static Semaphore terminalCapacity = new Semaphore(MAX_CAPACITY, true);
    public static AtomicInteger passengersProcessed = new AtomicInteger(0);
    final static Object monitor = new Object();

    public static void main(String[] args) throws InterruptedException {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

        // Create shared resources
        Guard guard = new Guard(ALLOW_ENTRY_CAPACITY);
        TicketBooth ticketBooth1 = new TicketBooth(1);
        TicketBooth ticketBooth2 = new TicketBooth(2);
        TicketMachine ticketMachine = new TicketMachine();
        Inspector inspector = new Inspector();

        List<Bus> buses = new ArrayList<>();
        int numOfBuses = (int) (Math.random() * 4 + 3); // Random generate 3-6 buses
        for (int i = 0; i < numOfBuses; i++) {
            buses.add(new Bus(i + 1));
        }

        List<WaitingArea> waitingAreas = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            waitingAreas.add(new WaitingArea(i + 1));
        }

        // Schedule the ticket machine breakdown simulation
        service.scheduleAtFixedRate(() -> {
            if (Math.random() < 0.2) { //20% of the down time
                ticketMachine.setBroken();
                try {
                    Thread.sleep((int) (Math.random() * 2 + 5) * 1000); // Random downtime between 2-5 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ticketMachine.setWorking();
            }
        }, 5, 5, TimeUnit.SECONDS);

        for (int i = 1; i <= 80; i++) {
            final int passengerId = i;
            final int desiredWaitingAreaIndex = (int) (Math.random() * waitingAreas.size());

            // We will pass the whole lists of buses and waiting areas to the Passenger.
            // Inside the Passenger class, the passenger will choose a specific bus and a waiting area to interact with.
            Passenger p = new Passenger(passengerId, guard, ticketBooth1, ticketBooth2, ticketMachine, inspector, buses, waitingAreas, desiredWaitingAreaIndex, passengersProcessed);
            p.start();
            try {
                Thread.sleep((int) ((Math.random() * 2 + 3)*1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//
//        // Shutdown the executor service after launching all passenger threads
//        service.shutdown();
        // After shutting down the executor:
        synchronized (monitor) {
            while (passengersProcessed.get() < 80) {
                monitor.wait();
            }
        }

        System.out.println("----------------------------------------------");
        System.out.println("All passengers have been processed. Terminal operations complete.");

    }
}
