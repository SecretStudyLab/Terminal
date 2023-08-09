package com.mycompany.apu_assignemnt;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Passenger extends Thread {

    private int id;
    private Guard guard;
    private TicketBooth ticketBooth1;
    private TicketBooth ticketBooth2;
    private TicketMachine ticketMachine;
    private Inspector inspector;
    private List<Bus> buses;
    private List<WaitingArea> waitingAreas;
    private int desiredWaitingArea;
    private AtomicInteger passengersProcessed;

    public Passenger(int id, Guard guard, TicketBooth ticketBooth1, TicketBooth ticketBooth2, TicketMachine ticketMachine, 
                     Inspector inspector, List<Bus> buses, List<WaitingArea> waitingAreas, 
                     int desiredWaitingArea, AtomicInteger passengersProcessed) {
        this.id = id;
        this.guard = guard;
        this.ticketBooth1 = ticketBooth1;
        this.ticketBooth2 = ticketBooth2;
        this.ticketMachine = ticketMachine;
        this.inspector = inspector;

        //TODO Pass Inspector to Bus instead
        this.buses = buses;
        this.waitingAreas = waitingAreas;
        this.desiredWaitingArea = desiredWaitingArea;
        this.passengersProcessed = passengersProcessed;
    }

    @Override
    public void run() {
        try {
            enteringTerminal();
            purchaseTicket();
            waitForBusInArea();
            inspectTicket();
            boardBus();
            exitTerminal();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    private void enteringTerminal() throws InterruptedException {
        guard.entry();
        System.out.println("Thread-Passenger-" + id + ": Entering the terminal.");
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
        WaitingArea chosenArea = waitingAreas.get(desiredWaitingArea);
        chosenArea.enter();
        guard.exit();
        System.out.println("Thread-Passenger-" + id + ": Waiting in " + chosenArea.getId());
    }

    private void inspectTicket() {
        inspector.inspectTicket(id);
    }

    private void boardBus() {
        Bus chosenBus = buses.get(desiredWaitingArea);
        if (chosenBus.isAvailable()) {
            try {
                chosenBus.boardBus(id);
            } catch (InterruptedException ex) {
                Logger.getLogger(Passenger.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void exitTerminal() {

        waitingAreas.get(desiredWaitingArea).leave();

        passengersProcessed.getAndIncrement();
        System.out.println("Total Processed passanger " + passengersProcessed.get());

        if (passengersProcessed.get() == 80) {
            synchronized (Terminal.monitor) {
                Terminal.monitor.notify();
            }
        }
    }
}
