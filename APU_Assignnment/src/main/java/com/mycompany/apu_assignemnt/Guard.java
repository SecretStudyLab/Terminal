package com.mycompany.apu_assignemnt;

import java.util.concurrent.Semaphore;

public class Guard {

    private Semaphore terminalSpace;

    public Guard(int capacity) {
        terminalSpace = new Semaphore(capacity, true);
    }

    public void entry() throws InterruptedException {
        terminalSpace.acquire();
    }

    public void exit() {
        terminalSpace.release();
    }
}
