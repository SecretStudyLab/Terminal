package com.mycompany.apu_assignemnt;

import java.util.concurrent.Semaphore;

public class WaitingArea {

    private int id;
    private static final int CAPACITY = 10;  // Maximum number of passengers that can wait in the area.
    private Semaphore availableSpace;

    public WaitingArea(int id) {
        this.id = id;
        this.availableSpace = new Semaphore(CAPACITY, true);
    }

    public int getId() {
        return id;
    }

    public boolean enter() {  // Passenger tries to enter the waiting area.
        return availableSpace.tryAcquire();

//        if (availableSpace.tryAcquire()) {
//            //TODO Move all sout to passenger classs
//            System.out.println("Thread-Passenger-" + Thread.currentThread().getId() + ": Entered Waiting Area " + id + ".");
//            return true;
//        } else {
//            System.out.println("Thread-Passenger-" + Thread.currentThread().getId() + ": Waiting Area " + id + " is full.");
//            return false;
//        }
    }

    public void leave() {  // Passenger leaves the waiting area.
        availableSpace.release();
        System.out.println("Thread-Passenger-" + Thread.currentThread().getId() + ": Left Waiting Area " + id + ".");
    }
}
