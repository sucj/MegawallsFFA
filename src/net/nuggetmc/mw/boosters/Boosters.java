package net.nuggetmc.mw.boosters;

public enum Boosters {
    Coins("Coins","Multiplies coins",10,1);


    private final String name;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long getDuration() {
        return duration;
    }

    public int getId() {
        return id;
    }

    private final String description;
    private final long duration;
    private final int id;
    Boosters(String name, String description, int duration, int id) {
        this.name=name;
        this.description=description;
        this.duration=duration;
        this.id=id;
    }
}
