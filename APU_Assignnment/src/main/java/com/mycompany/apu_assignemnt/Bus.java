package com.mycompany.apu_assignemnt;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Bus extends Thread{

    private int id;



    private AtomicInteger boardedPassengers = new AtomicInteger(0);



    //Means is at terminal
    private Lock atTerminalLock = new ReentrantLock(true);

    private AtomicInteger passengersProcessed;

    public Bus(int id, AtomicInteger passengersProcessed) {
        this.id = id;
        this.passengersProcessed=passengersProcessed;

    }

    @Override
    public void run(){
        while(true){
            try {

            //Bus departs when notify by Inspector
                if(passengersProcessed.get()>=80) break;

                boardedPassengers.set(0);

                //Bus reaches station
                System.out.println("Thread-Bus-" + id + ": Returned to the terminal.");
                synchronized (this){
                    wait();
                    atTerminalLock.lock();
                }

                //Bus is driving
                System.out.println("Thread-Bus-" + id + ": Departed with " + boardedPassengers.get() + " passengers.");

                Thread.sleep((int) (Math.random() * 20 + 10) * 1000);
                atTerminalLock.unlock();


            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }





        }


    }

    public int incrementBoardedPassengers() {
        return boardedPassengers.incrementAndGet();
    }

    public Lock getAtTerminalLock() {
        return atTerminalLock;
    }
}
