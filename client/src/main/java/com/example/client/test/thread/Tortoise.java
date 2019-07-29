package com.example.client.test.thread;

public class Tortoise extends Animal {

    public Tortoise() {
        setName("Tortoise");
    }

    @Override
    public void running() {
        double dis = 0.1;
        length -= dis;
        if (length <= 0) {
            System.out.println("Tortoise is winner");

            if (callToBack != null) {
                callToBack.win();
            }
        }
        System.out.println("乌龟跑了" + dis + "米，距离终点还有" + length + "米");
    }


}
