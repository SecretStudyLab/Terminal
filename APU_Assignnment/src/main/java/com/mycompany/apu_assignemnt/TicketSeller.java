package com.mycompany.apu_assignemnt;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class TicketSeller {

    private boolean isWorking = true;
    private Lock myTurnLock =new ReentrantLock(true);
    private int waitingMinTime;
    private int waitingAddTime;

    public TicketSeller(int waitingMinTime, int waitingAddTime) {
        this.waitingMinTime = waitingMinTime;
        this.waitingAddTime=waitingAddTime;
    }
    public synchronized boolean operating() throws InterruptedException {
        if(isWorking){
            Thread.sleep((int) (Math.random() * waitingAddTime + waitingMinTime) * 500); // Random delay between 1-1 seconds
            return true;

        }
        return false;

    }

    public void setNotWorking() {
        this.isWorking = false;
    }

    public void setWorking() {
        this.isWorking = true;
    }

    public boolean isWorking(){
        return isWorking;
    }

    public boolean buyTicket() throws InterruptedException {
        //Only wait for X seconds
        if(!isWorking){
            //Wait a while
            return false;
        }
        myTurnLock.lock();
        boolean isPurchased=operating();
        myTurnLock.unlock();
        return isPurchased;


    }
}
