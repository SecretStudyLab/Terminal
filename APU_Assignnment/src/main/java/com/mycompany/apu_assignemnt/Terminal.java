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

    public static Semaphore foyerSpace = new Semaphore(MAX_CAPACITY, true);
    public static AtomicInteger entranceThreshold =new AtomicInteger(0);

    public static AtomicInteger passengersProcessed = new AtomicInteger(0);
    final static Object monitor = new Object();

    public static void main(String[] args) throws InterruptedException {

        // Create shared resources

        Guard westEntranceGuard = new Guard("West Entrance",foyerSpace, entranceThreshold);
        Guard eastEntranceGuard = new Guard("East Entrance",foyerSpace, entranceThreshold);

        TicketSeller ticketBooth1 = new TicketSeller(3, 6);
        TicketSeller ticketBooth2 = new TicketSeller(3,6);
        TicketSeller ticketMachine = new TicketSeller(1,3);

        List<Bus> buses = new ArrayList<>();
        List<Passenger> passengers=new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Bus bus=new Bus(i + 1, passengersProcessed);
            buses.add(bus);
            bus.start();
        }

        List<WaitingArea> waitingAreas = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            waitingAreas.add(new WaitingArea(i + 1));
        }
        Inspector inspector = new Inspector(passengersProcessed, buses, waitingAreas);
        inspector.start();

        // Schedule the ticket machine breakdown simulation
        Thread ticketMachineSwitch=new Thread(() -> {
            while (passengersProcessed.get() < 80) {
                if (Math.random() < 0.2) { // 20% of the down time/to get repair
                    if (ticketMachine.isWorking()) {
                        ticketMachine.setNotWorking();
                        System.out.println("Ticket Machine: Breakdown!");
                    } else {
                        ticketMachine.setWorking();
                        System.out.println("Ticket Machine: Functional again.");
                    }
                }
                try {
                    Thread.sleep(20000); // Delay for 2 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        ticketMachineSwitch.start();

        Thread ticketBooth1Switch=new Thread(() -> {
            while (passengersProcessed.get() < 80) {
                if (Math.random() < 0.1) {
                    if (ticketBooth1.isWorking()) {
                        ticketBooth1.setNotWorking();
                        System.out.println("Ticket Booth 1: Toilet Break!");
                    } else {
                        ticketBooth1.setWorking();
                        System.out.println("Ticket Booth 1: Back to Work.");
                    }
                }
                try {
                    Thread.sleep(30000); // Delay for 10 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        ticketBooth1Switch.start();

        Thread ticketBooth2Switch=new Thread(() -> {
            while (passengersProcessed.get() < 80) {
                if (Math.random() < 0.1) {
                    if (ticketBooth2.isWorking()) {
                        ticketBooth2.setNotWorking();
                        System.out.println("Ticket Booth 2: Toilet Break!");
                    } else {
                        ticketBooth2.setWorking();
                        System.out.println("Ticket Booth 2: Back to Work.");
                    }
                }
                try {
                    Thread.sleep(30000); // Delay for 10 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        ticketBooth2Switch.start();



        for (int i = 1; i <= 80; i++) {
            final int passengerId = i;
            final int desiredWaitingAreaIndex = (int) (Math.random() * waitingAreas.size());

            Guard guard=westEntranceGuard;
            if(Math.random()<0.5){
                guard=eastEntranceGuard;
            }

            // Inside the Passenger class, the passenger will choose a specific bus and a waiting area to interact with.
            Passenger p = new Passenger(passengerId, guard, ticketBooth1, ticketBooth2, ticketMachine, waitingAreas, desiredWaitingAreaIndex+1, passengersProcessed, monitor);
            p.start();
            try {
                Thread.sleep((int) ((Math.random() * 2 + 1) * 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//

        synchronized (monitor) {
            while (passengersProcessed.get() < 80) {
                monitor.wait();
                // Shutdown the executor service after boarded all passengers
                ticketMachineSwitch.join();
                ticketBooth1Switch.join();
                ticketBooth2Switch.join();
                System.out.println("All ticket sellers have done operation!");

                for(Bus bus:buses){
                    bus.join();
                    System.out.println("All buses have done operation!");

                }
                for(Passenger passenger:passengers){
                    passenger.join();
                    System.out.println("All passengers processed!");
                }
            }
        }

        System.out.println("----------------------------------------------");
        System.out.println("Terminal operations complete.");

    }
}
