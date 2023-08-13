package com.mycompany.apu_assignemnt;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

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
                Bus bus= buses.get(i);
                Lock lock=bus.getAtTerminalLock();

                if(lock.tryLock()){
                    try {
                        inspectTicket(waitingAreas.get(i),bus);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }finally {
                        lock.unlock();
                        synchronized (bus){
                            bus.notifyAll();

                        }
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
        Thread.sleep((int)(Math.random()*4*500));

        int boardedPassengers=0;
        Passenger passenger=waitingArea.leave();

        if(passenger==null){
            System.out.println("Thread-Inspector: No passengers waiting for bus "+waitingArea.getId());
            return;
        }else{
            System.out.println("Thread-Inspector: Instructing passenger in waiting area to board bus "+waitingArea.getId()+".\tWaiting Area Space: "+(waitingArea.space()-1)+"/10");

        }

        while(passenger!=null){

            passenger.join();

            boardedPassengers=bus.incrementBoardedPassengers();

            System.out.println("Thread-Inspector: Boarded "+boardedPassengers+" passenger to bus "+waitingArea.getId()+".");

            if(boardedPassengers>=10) break;
            //To ensure that number is correct
            passenger=waitingArea.leave();


        }
        // Print a message indicating the ticket has been inspected
        System.out.println("Thread-Inspector: has done ticket inspection for bus "+waitingArea.getId());
    }
}
