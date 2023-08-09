package com.mycompany.apu_assignemnt;

import java.util.concurrent.Semaphore;

public class Guard {

    private Semaphore terminalSpace;

    public Guard(int capacity) {
        terminalSpace = new Semaphore(capacity, true);
    }

    public boolean allowEntry() {
        return terminalSpace.tryAcquire();
    }

    public void exit() {
        terminalSpace.release();
    }
}
