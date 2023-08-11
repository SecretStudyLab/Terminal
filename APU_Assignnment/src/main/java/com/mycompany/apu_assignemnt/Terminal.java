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
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

        // Create shared resources

        //TODO FoyerSpace(MAX_CAPACITY)) (15)
        //Passenger can only acquire when FoyerSpace (12 Space)
        //Passenger 1 Exited to Waiting Area
        //Passenger 2 Exited to Waiting Area
        //Passenger 3 Exited to Waiting Area
        //Guard allows new passenger to come in to terminal
        //Declare Atomic Integer x
        //In Guard exit() function, If availablePermits()==0, x+=1 -> FoyerSpace is still fully acquired cannot be entered
        //In x==3, Guard release(3) x->0,



        Guard westEntranceGuard = new Guard("West Entrance",foyerSpace, entranceThreshold);
        Guard eastEntranceGuard = new Guard("East Entrance",foyerSpace, entranceThreshold);

        TicketSeller ticketBooth1 = new TicketSeller(3, 6);
        TicketSeller ticketBooth2 = new TicketSeller(3,6);
        TicketSeller ticketMachine = new TicketSeller(1,3);

        List<Bus> buses = new ArrayList<>();
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
        service.scheduleAtFixedRate(() -> {
            if (Math.random() < 0.2) { //20% of the down time/to get repair
                if(ticketMachine.isWorking()){
                    ticketMachine.setNotWorking();
                    System.out.println("Ticket Machine: Breakdown!");
                }else{
                    ticketMachine.setWorking();
                    System.out.println("Ticket Machine: Functional again.");
                }

            }
        }, 0, 2, TimeUnit.SECONDS);

        service.scheduleAtFixedRate(() -> {
            if (Math.random() < 0.5) {
                if(ticketBooth1.isWorking()){
                    ticketBooth1.setNotWorking();
                    System.out.println("Ticket Booth 1: Toilet Break!");
                }else{
                    ticketBooth1.setWorking();
                    System.out.println("Ticket Booth 1: Back to Work.");
                }

            }
        }, 3, 10, TimeUnit.SECONDS);

        service.scheduleAtFixedRate(() -> {
            if (Math.random() < 0.5) {
                if(ticketBooth2.isWorking()){
                    ticketBooth2.setNotWorking();
                    System.out.println("Ticket Booth 2: Toilet Break!");
                }else{
                    ticketBooth2.setWorking();
                    System.out.println("Ticket Booth 2: Back to Work.");
                }

            }
        }, 3, 10, TimeUnit.SECONDS);


        for (int i = 1; i <= 80; i++) {
            final int passengerId = i;
            final int desiredWaitingAreaIndex = (int) (Math.random() * waitingAreas.size());

            // We will pass the whole lists of buses and waiting areas to the Passenger.
            // Inside the Passenger class, the passenger will choose a specific bus and a waiting area to interact with.
            Guard guard=westEntranceGuard;
            if(Math.random()<0.5){
                guard=eastEntranceGuard;
            }
            Passenger p = new Passenger(passengerId, guard, ticketBooth1, ticketBooth2, ticketMachine, waitingAreas, desiredWaitingAreaIndex+1, passengersProcessed, monitor);
            p.start();
            try {
                Thread.sleep((int) ((Math.random() * 2 + 3)*1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//

        // After shutting down the executor:
        synchronized (monitor) {
            while (passengersProcessed.get() < 80) {
                monitor.wait();
                // Shutdown the executor service after boarded all passengers
                service.shutdown();
            }
        }

        System.out.println("----------------------------------------------");
        System.out.println("All passengers have been processed. Terminal operations complete.");

    }
}
