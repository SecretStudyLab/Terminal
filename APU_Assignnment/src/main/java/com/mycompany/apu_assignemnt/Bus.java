package com.mycompany.apu_assignemnt;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Bus extends Thread{

    private int id;



    private AtomicInteger boardedPassengers = new AtomicInteger(0);
    private boolean atTerminal = true;



    private Lock busLock = new ReentrantLock();

    private AtomicInteger passengersProcessed;

    public Bus(int id, AtomicInteger passengersProcessed) {
        this.id = id;
        this.passengersProcessed=passengersProcessed;
    }

    @Override
    public void run(){

        while(true){


            try {
                
                //Bus is driving
                busLock.lock();
                Thread.sleep((int) (Math.random() * 20 + 5) * 1000);

                if(passengersProcessed.get()>=80) break;
                boardedPassengers.set(0);

                //Bus reaches station
                System.out.println("Thread-Bus-" + id + ": Returned to the terminal.");
                busLock.unlock();

                //Bus departs when notify by Inspector
                synchronized (this){
                    wait();
                }

                System.out.println("Thread-Bus-" + id + ": Departed with " + boardedPassengers.get() + " passengers.");


            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }





        }


    }

    public int incrementBoardedPassengers() {
        return boardedPassengers.incrementAndGet();
    }

    public Lock getBusLock() {
        return busLock;
    }
}
