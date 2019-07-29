package com.example.client.test.thread;

public class RunningTest {

    public static void main(String[] args) {
        Rabbit rabbit = new Rabbit();
        Tortoise tortoise = new Tortoise();

        LastOneStep step1 = new LastOneStep(rabbit);
        LastOneStep step2 = new LastOneStep(tortoise);
        rabbit.callToBack = step2;
        tortoise.callToBack = step1;

        rabbit.start();
        tortoise.start();

    }

}
