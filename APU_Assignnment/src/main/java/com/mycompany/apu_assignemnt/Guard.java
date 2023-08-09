package com.mycompany.apu_assignemnt;

import java.util.concurrent.Semaphore;

public class Guard {

    //TODO Change terminalSpace name to FoyerSpace
    //TODO Move foyerSpace to Terminal
    private Semaphore terminalSpace;

    //Guard(this)
    //private Terminal terminal
    //terminal.foyerSpace.acquire();
    //terminal.foyerSpace.release();

    //Add one more logic for passenger to randomly choose enter from either entrance
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
