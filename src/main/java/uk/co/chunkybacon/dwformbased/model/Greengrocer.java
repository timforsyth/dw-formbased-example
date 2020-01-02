package uk.co.chunkybacon.dwformbased.model;

public class Greengrocer {

    public static Fruit banana() {
        return new Fruit("Banana", "Yellow", false);
    }

    public static Fruit blueberry() {
        return new Fruit("Blueberry", "Dark Blue", true);
    }

    public static Fruit raspberry() {
        return new Fruit("Raspberry", "Red", true);
    }

    public static Fruit kiwi() {
        return new Fruit("Kiwi", "Green", false);
    }

}
