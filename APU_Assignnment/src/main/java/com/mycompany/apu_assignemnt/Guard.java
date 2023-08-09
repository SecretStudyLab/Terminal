package com.mycompany.apu_assignemnt;

import java.util.Random;

public class Guard extends Thread {

    private Class<Terminal> terminalClass;
    private final Random random = new Random();

    public Guard(Class<Terminal> terminalClass) {
        this.terminalClass = terminalClass;
    }

    public void entry() throws InterruptedException {
        if (random.nextBoolean()) {
            // Simulate random choice of entrance
            System.out.println("Passenger chose entrance A");
        } else {
            System.out.println("Passenger chose entrance B");
        }
        Terminal.FoyerSpace.acquire();
    }

    public void exit() {
        Terminal.FoyerSpace.release();
    }

    public void run() {
        while (true) {
            try {
                if (Terminal.FoyerSpace.availablePermits() < 15) {
                    entry();
                    // Simulate some processing for each passenger. E.g. checking the ticket.
                    Thread.sleep(random.nextInt(1000));
                } else {
                    // Optionally sleep a bit before the next check, to avoid busy-waiting
                    Thread.sleep(500);
                }

            } catch (InterruptedException e) {
                // Handle exception or break from loop, e.g., if you want to stop the Guard.
                System.out.println("Guard was interrupted");
                break;
            }
        }
    }
}
