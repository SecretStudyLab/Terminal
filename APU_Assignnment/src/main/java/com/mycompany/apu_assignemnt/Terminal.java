package com.mycompany.apu_assignemnt;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Terminal {

    public static final int MAX_CAPACITY = 15;
    public static final int ALLOW_ENTRY_CAPACITY = 12;

    public static Semaphore terminalCapacity = new Semaphore(MAX_CAPACITY, true);
    public static AtomicInteger passengersProcessed = new AtomicInteger(0);
    public static final Object monitor = new Object();
    public static AtomicInteger exitedPassengerCount = new AtomicInteger(0);
    public static Semaphore FoyerSpace = new Semaphore(MAX_CAPACITY, true);

    public static void main(String[] args) throws InterruptedException {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

        List<Guard> guards = new ArrayList<>();
        guards.add(new Guard(Terminal.class)); // guard1
        guards.add(new Guard(Terminal.class)); // guard2
        guards.forEach(Thread::start);

        TicketBooth ticketBooth1 = new TicketBooth(1);
        TicketBooth ticketBooth2 = new TicketBooth(2);
        TicketMachine ticketMachine = new TicketMachine();
        ticketMachine.start();

        List<Bus> buses = new ArrayList<>();
        int numOfBuses = (int) (Math.random() * 4 + 3);
        for (int i = 0; i < numOfBuses; i++) {
            buses.add(new Bus(i + 1));
        }

        List<WaitingArea> waitingAreas = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            waitingAreas.add(new WaitingArea(i + 1));
        }

        Inspector inspector = new Inspector(waitingAreas);

        for (int i = 1; i <= 80; i++) {
            int passengerId = i;
            int desiredWaitingAreaIndex = (int) (Math.random() * waitingAreas.size());

            Passenger p = new Passenger(passengerId, ticketBooth1, ticketBooth2, ticketMachine, inspector, buses, waitingAreas, desiredWaitingAreaIndex, passengersProcessed);
            p.start();

            Thread.sleep((int) ((Math.random() * 1 + 1) * 1000));
        }

        synchronized (monitor) {
            while (passengersProcessed.get() < 80) {
                monitor.wait();
            }
        }

        service.shutdown();

        System.out.println("----------------------------------------------");
        System.out.println("All passengers have been processed. Terminal operations complete.");
    }
}
