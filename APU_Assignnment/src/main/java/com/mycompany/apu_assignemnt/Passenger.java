package com.mycompany.apu_assignemnt;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Passenger extends Thread {

    private int id;
    private Guard guard;
    private TicketSeller ticketBooth1;
    private TicketSeller ticketBooth2;
    private TicketSeller ticketMachine;
    private WaitingArea waitingArea;
    private int desiredWaitingArea;
    private AtomicInteger passengersProcessed;
    private Object monitor;

    public Passenger(int id, Guard guard, TicketSeller ticketBooth1, TicketSeller ticketBooth2, TicketSeller ticketMachine,
                     List<WaitingArea> waitingAreas,
                     int desiredWaitingArea, AtomicInteger passengersProcessed, Object monitor) {
        this.id = id;
        this.guard = guard;
        this.ticketBooth1 = ticketBooth1;
        this.ticketBooth2 = ticketBooth2;
        this.ticketMachine = ticketMachine;

        //TODO Pass Inspector to Bus instead
        this.waitingArea = waitingAreas.get(desiredWaitingArea-1);
        this.desiredWaitingArea = desiredWaitingArea;
        this.passengersProcessed = passengersProcessed;
        this.monitor=monitor;
    }

    @Override
    public void run() {
        try {
            enteringTerminal();
            purchaseTicket();
            waitForBusInArea();
            boardBus();
            exitTerminal();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    private void enteringTerminal() throws InterruptedException {
        System.out.println("Thread-Passenger-" + id + ": Entering the terminal from "+guard.getName()+".\tFoyer Space: "+guard.entry()+"/15");

    }

    private void purchaseTicket() {
        try {
            System.out.println("Thread-Passenger-" + id + ": is queuing to buy ticket from ticket machine");
            while(true){
                if (ticketMachine.buyTicket()){
                    System.out.println("Thread-Passenger-" + id + ": Bought ticket from ticket machine");
                    break;
                }
                System.out.println("Thread-Passenger-" + id + ": did not get ticket from ticket machine and is queuing to buy ticket from ticket booth 1");

                if (ticketBooth1.buyTicket()){
                    System.out.println("Thread-Passenger-" + id + ": Bought ticket from ticket booth 1");
                    break;
                }
                System.out.println("Thread-Passenger-" + id + ": did not get ticket from ticket booth 1 and is queuing to buy ticket from ticket booth 2");

                if (ticketBooth2.buyTicket()){
                    System.out.println("Thread-Passenger-" + id + ": Bought ticket from ticket booth 2");
                    break;
                }
                System.out.println("Thread-Passenger-" + id + ": did not get ticket from ticket booth 2 and is queuing to buy ticket from ticket machine");

            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Passenger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private synchronized void waitForBusInArea() throws InterruptedException {
        int waitingAreaSpace=waitingArea.enter(this);
        int foyerSpace=guard.exit();
        System.out.println("Thread-Passenger-" + id + ": Waiting in Waiting Area " + desiredWaitingArea+"\tFoyer Space: "+foyerSpace+"/15\t Waiting Area Space: "+waitingAreaSpace+"/10");
        wait();
    }

//    private void inspectTicket() {
//        inspector.inspectTicket(id);
//    }

    private void boardBus() {
        System.out.println("Thread-Passenger-" + id + ": Left Waiting Area " + desiredWaitingArea+".\tWaiting Area Space: "+waitingArea.space()+"/10");
        System.out.println("Thread-Passenger-" + id + ": Boarded Bus " + desiredWaitingArea + ".");
    }

    private void exitTerminal() {


        passengersProcessed.getAndIncrement();
        System.out.println("Total Processed passenger " + passengersProcessed.get());

        if (passengersProcessed.get() == 80) {
            synchronized (monitor) {
                monitor.notifyAll();
            }
        }
    }
}
