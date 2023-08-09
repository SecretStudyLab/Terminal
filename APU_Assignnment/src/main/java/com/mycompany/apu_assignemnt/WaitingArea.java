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

    public void enter() throws InterruptedException {  // Passenger tries to enter the waiting area.
        availableSpace.acquire();
    }

    public void leave() {  // Passenger leaves the waiting area.
        availableSpace.release();
    }
}
