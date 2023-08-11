package com.mycompany.apu_assignemnt;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Inspector extends Thread{

    private Random random = new Random();
    private AtomicInteger passengersProcessed;

    private List<Bus> buses;
    private List<WaitingArea> waitingAreas;

    public Inspector(AtomicInteger passengersProcessed, List<Bus> buses, List<WaitingArea> waitingAreas){
        this.passengersProcessed=passengersProcessed;
        this.buses=buses;
        this.waitingAreas=waitingAreas;
    }
    @Override
    public void run(){
        while(passengersProcessed.get()<80){
            for(int i=0; i<3; i++){
                if(buses.get(i).getBusLock().tryLock()){
                    try {
                        inspectTicket(waitingAreas.get(i),buses.get(i));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        for(int i=0; i<3; i++){
            synchronized (buses.get(i)){
                buses.get(i).notifyAll();

            }

        }
    }
    public void inspectTicket(WaitingArea waitingArea, Bus bus) throws InterruptedException {

        // Sleep to simulate the time taken for ticket inspection, with random duration between 500 to 2000 milliseconds
        System.out.println("Thread-Inspector: Starting to board passengers to bus "+waitingArea.getId());

        int boardedPassengers=0;
        Passenger passenger=waitingArea.leave();
        if(passenger==null){
            System.out.println("Thread-Inspector: No passengers waiting for bus "+waitingArea.getId());
            bus.getBusLock().unlock();
            synchronized(bus){
                //Inform bus to leave
                bus.notifyAll();
            }
            return;
        }else{
            System.out.println("Thread-Inspector: Instructing passenger in waiting area to board bus "+waitingArea.getId()+".\tWaiting Area Space: "+waitingArea.space()+"/10");

        }

        while(passenger!=null){


            int sleepTime = 500 + random.nextInt(1500); // Random time between 500 to 2000 milliseconds
            Thread.sleep(sleepTime);
            boardedPassengers=bus.incrementBoardedPassengers();

            System.out.println("Thread-Inspector: Boarded "+boardedPassengers+" passenger to bus "+waitingArea.getId()+".");

            if(boardedPassengers>=10) break;
            //To ensure that number is correct
            passenger=waitingArea.leave();



        }

        bus.getBusLock().unlock();

        synchronized(bus){
            //Inform bus to leave
            bus.notifyAll();
        }

        // Print a message indicating the ticket has been inspected
        System.out.println("Thread-Inspector: Ticket has inspected ticket for bus "+waitingArea.getId());
    }
}
