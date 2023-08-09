package com.mycompany.apu_assignemnt;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Passenger extends Thread {

    private int id;
    private TicketBooth ticketBooth1;
    private TicketBooth ticketBooth2;
    private TicketMachine ticketMachine;
    private Inspector inspector;
    private List<Bus> buses;
    private List<WaitingArea> waitingAreas;
    private int desiredWaitingArea;
    private AtomicInteger passengersProcessed;
    private Random random = new Random();

    public Passenger(int id, TicketBooth ticketBooth1, TicketBooth ticketBooth2, TicketMachine ticketMachine,
            Inspector inspector, List<Bus> buses, List<WaitingArea> waitingAreas,
            int desiredWaitingArea, AtomicInteger passengersProcessed) {
        this.id = id;
        this.ticketBooth1 = ticketBooth1;
        this.ticketBooth2 = ticketBooth2;
        this.ticketMachine = ticketMachine;
        this.inspector = inspector;
        this.buses = buses;
        this.waitingAreas = waitingAreas;
        this.desiredWaitingArea = desiredWaitingArea;
        this.passengersProcessed = passengersProcessed;
    }

    @Override
    public void run() {
        try {
            purchaseTicket();
            waitForBusInArea();
            boardBus();
            exitTerminal();
        } catch (InterruptedException e) {
            Logger.getLogger(Passenger.class.getName()).log(Level.SEVERE, "Passenger interrupted.", e);
        }
    }

    private void purchaseTicket() {
        try {
            if (!ticketMachine.buyTicket(id)) {
                TicketBooth chosenBooth = (ticketBooth1.isAvailable()) ? ticketBooth1 : ticketBooth2;
                chosenBooth.buyTicket(id);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Passenger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void waitForBusInArea() throws InterruptedException {
        // Randomly choose an area instead of always using desiredWaitingArea.
        int chosenIndex = random.nextInt(waitingAreas.size());
        WaitingArea chosenArea = waitingAreas.get(chosenIndex);

        chosenArea.enter();
        System.out.println("Thread-Passenger-" + id + ": Entered Waiting Area.");
        System.out.println("Thread-Passenger-" + id + ": Waiting in " + chosenArea.getId());
    }

    private void boardBus() {
        // Randomly choose a bus for boarding
        int chosenIndex = random.nextInt(buses.size());
        Bus chosenBus = buses.get(chosenIndex);

        if (chosenBus.isAvailable()) {
            try {
                // Move ticket inspection here if required before boarding
                inspector.inspectTicket(id);
                chosenBus.boardBus(id);
            } catch (InterruptedException ex) {
                Logger.getLogger(Passenger.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void exitTerminal() {
        waitingAreas.get(desiredWaitingArea).leave();
        System.out.println("Thread-Passenger-" + id + ": Left Waiting Area.");

        passengersProcessed.getAndIncrement();
        System.out.println("Total Processed passenger " + passengersProcessed.get());

        if (passengersProcessed.get() == 80) {
            synchronized (Terminal.monitor) {
                Terminal.monitor.notify();
            }
        }
    }
}
