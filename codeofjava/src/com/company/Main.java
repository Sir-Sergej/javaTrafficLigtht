package com.company;


enum TrafficLightColor {
    YELLOW(2000), GREEN(10000), RED(12000);
    private int delay;

    TrafficLightColor(int d) {
        delay = d;
    }

    int getDelay() {
        return delay;
    }
}

class TrafficLightSimulator implements Runnable {
    private TrafficLightColor tlc;
    boolean stop = false;
    boolean changed = false;

    TrafficLightSimulator(TrafficLightColor init) {
        tlc = init;
    }
    TrafficLightSimulator() {
        tlc = TrafficLightColor.RED;
    }

    public void run() {
        while(!stop) {
            try {
                Thread.sleep(tlc.getDelay());
            } catch (InterruptedException exc) {
                System.out.println(exc);
            }
            changeColor();
        }
    }

    synchronized void changeColor () {
        switch (tlc) {
            case RED:
                tlc = TrafficLightColor.GREEN;
                break;
            case YELLOW:
                tlc = TrafficLightColor.RED;
                break;
            case GREEN:
                tlc = TrafficLightColor.YELLOW;
                break;
        }
        changed = true;
        notify();
    }

    synchronized void waitForChange() {
        try {
            while(!changed) {
                wait();
            }
            changed = false;
        } catch (InterruptedException exc) {
            System.out.println(exc);
        }
    }

    synchronized TrafficLightColor getColor() {
        return tlc;
    }
    synchronized void cancel() {
        stop = true;
    }

}

class Main {
    public static void main(String[] args) {
        TrafficLightSimulator tls = new TrafficLightSimulator(TrafficLightColor.GREEN);

        Thread thrd = new Thread(tls);
        thrd.start();

        for(int i = 0 ; i < 9; i++) {
            System.out.println(tls.getColor());
            tls.waitForChange();
        }
        tls.cancel();
    }
}