package com.example.client.test.thread;

import lombok.Data;

public abstract class Animal extends Thread {

    public double length = 20;

    public abstract void running();

    @Override
    public void run() {
        while (length > 0) {
            running();
        }
    }

    public interface CallToBack{
        void win();
    }

    public CallToBack callToBack;

}
