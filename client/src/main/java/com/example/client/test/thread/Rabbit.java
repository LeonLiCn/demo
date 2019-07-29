package com.example.client.test.thread;

public class Rabbit extends Animal {

    public Rabbit() {
        setName("rabbit");
    }

    @Override
    public void running() {
        double dis = 0.5;
        length -= dis;
        if (length <= 0) {
            System.out.println("rabbit is winner!");
            if (callToBack != null) {
                callToBack.win();
            }
        }
        System.out.println("兔子跑了" + dis + "米，距离终点还有" + length + "米");
        if (length % 2 == 0) {
            try {
                sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
