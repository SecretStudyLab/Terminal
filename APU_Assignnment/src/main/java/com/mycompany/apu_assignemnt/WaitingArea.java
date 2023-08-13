package com.mycompany.apu_assignemnt;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class WaitingArea {

    private int id;
    private static final int CAPACITY = 10;  // Maximum number of passengers that can wait in the area.



    private BlockingQueue<Passenger> waitingPassengers;

    public WaitingArea(int id) {
        this.id = id;
        this.waitingPassengers = new ArrayBlockingQueue<>(CAPACITY, true); ///FIFO
    }

    public int enter(Passenger passenger) throws InterruptedException {  // Passenger tries to enter the waiting area.
         waitingPassengers.put(passenger);
         return waitingPassengers.remainingCapacity();
    }

    public Passenger leave() throws InterruptedException {  // Passenger leaves the waiting area.
        //Wait X seconds for the next passenger
        Passenger passenger=waitingPassengers.poll(3, TimeUnit.SECONDS);
        if(passenger!=null){
            synchronized (passenger){
                passenger.notifyAll();
            }
        }
        return passenger;
    }

    public int space(){
        return waitingPassengers.remainingCapacity();
    }

    public int getId() {
        return id;
    }


}
