package com.mycompany.apu_assignemnt;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Guard {

    //TODO Change terminalSpace name to FoyerSpace
    //TODO Move foyerSpace to Terminal

    private String name;
    private Semaphore foyerSpace;
    private AtomicInteger entranceThreshold;



    //Add one more logic for passenger to randomly choose enter from either entrance
    public Guard(String name, Semaphore foyerSpace, AtomicInteger entranceThreshold) {
        this.name=name;
        this.foyerSpace = foyerSpace;
        this.entranceThreshold = entranceThreshold;

    }

    public int entry() throws InterruptedException {
        foyerSpace.acquire();
        return foyerSpace.availablePermits()+ entranceThreshold.get();
    }

    public int exit() {
        if(foyerSpace.availablePermits()==0){
            synchronized (entranceThreshold){
                if (entranceThreshold.incrementAndGet()==3){
                    entranceThreshold.set(0);
                    foyerSpace.release(3);
                }
            }

        }else{
            foyerSpace.release();
        }
        return foyerSpace.availablePermits()+ entranceThreshold.get();
    }

    public String getName() {
        return name;
    }
}
