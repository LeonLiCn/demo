package com.example.client.test.thread;

public class LastOneStep implements Animal.CallToBack {

    private Animal animal;

    public LastOneStep(Animal animal) {
        this.animal = animal;
    }

    @Override
    public void win() {
        animal.stop();
    }
}
