package net.batterbub.banneretmod.component;

public class BanneretData {
    private int used;

    public int getUser(){
        return used;
    }
    public void setUsed(int value) {
        used = value;
    }

    public void increment() {
        used++;
    }
    public void decrement() {
        used--;
    }

}
