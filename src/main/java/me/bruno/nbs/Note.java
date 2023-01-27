package me.bruno.nbs;

import java.util.List;

public class Note {

    private int tick;
    private byte instrument;
    private byte key;
    private byte velocity;
    private byte panning;
    private short pitch;


    public Note(int tick, byte instrument, byte key, byte velocity, byte panning, short pitch) {
        this.tick = tick;
        this.instrument = instrument;
        this.key = key;
        this.velocity = velocity;
        this.panning = panning;
        this.pitch = pitch;
    }

    public byte getInstrument() {
        return instrument;
    }

    public byte getKey() {
        return key;
    }

    public byte getVelocity() {
        return velocity;
    }

    public void setVelocity(byte velocity) {
        this.velocity = velocity;
    }

    public byte getPanning() {
        return panning;
    }

    public short getPitch() {
        return pitch;
    }

    public int getTick() {
        return tick;
    }

    //public Instrument getInstrumentFromInstance(InstrumentList instruments) {
    //    return instruments.getInstrumentFromByte(instrument);
    //}

    public static Note getLastNote(List<Note> notes) {
        return notes.stream().sorted((o1, o2) -> Integer.compare(o2.getTick(), o1.getTick())).findFirst().orElse(null);
    }
}
