package me.bruno.nbs;


import java.util.ArrayList;
import java.util.List;

public class Layer {

    private short number;
    private List<Note> notes;
    private String name;
    private boolean locked;
    private byte volume;
    private byte stereo;

    public Layer(short number, List<Note> notes, String name, boolean locked, byte volume, byte stereo) {
        this.number = number;
        this.notes = notes;
        this.name = name;
        this.locked = locked;
        this.volume = volume;
        this.stereo = stereo;
    }

    public Layer(short number) {
        this.number = number;
        notes = new ArrayList<>();
    }

    public short getLayerNumber() {
        return number;
    }


    public void setNumber(short layerNumber) {
        this.number = layerNumber;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public String getName() {
        return name;
    }

    public void setName(String s) {
        name = s;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean val) {
        locked = val;
    }

    public byte getVolume() {
        return volume;
    }

    public void setVolume(byte volume) {
        this.volume = volume;
    }

    public byte getStereo() {
        return stereo;
    }

    public void setStereo(byte stereo) {
        this.stereo = stereo;
    }

    public void addNote(Note note) {
        notes.add(note);
    }

    public static Layer getFromNumber(List<Layer> layers, short number) {
        return layers.stream().filter(layer -> layer.getLayerNumber() == number).findFirst().orElse(null);
    }

    public void updateNotes() {

        for (Note note : notes) {
            byte finalVelo = (byte) ((getVolume()/100f) * note.getVelocity());
            note.setVelocity(finalVelo);
        }

    }
}
