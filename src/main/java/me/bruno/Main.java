package me.bruno;

import me.bruno.nbs.*;

import javax.sound.midi.*;
import java.io.DataInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

public class Main {

    public static final int NOTE_ON = 144;
    public static final int NOTE_OFF = 128;
    public static final int SET_INSTRUMENT = 192;

    public static String currentDir = System.getProperty("user.dir");
    public static File mainDirectory = new File(currentDir);
    public static void main(String[] args) {

        if (Arrays.stream(mainDirectory.listFiles()).anyMatch(file -> FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("nbs"))) {
            for (File file : mainDirectory.listFiles((file, name) -> FilenameUtils.getExtension(name).equalsIgnoreCase("nbs"))) {
                System.out.println("converting " + FilenameUtils.getBaseName(file.getPath()));
                if (parse(file, FilenameUtils.getBaseName(file.getPath()))) {
                    System.out.println(FilenameUtils.getBaseName(file.getPath()) + " was successfully converted to " + FilenameUtils.getBaseName(file.getPath()) + ".mid");
                } else {
                    System.out.println("An unexpected error occured");
                }
            }
        } else {
            System.out.println("No files with extension .nbs found.");
        }


    }

    public static int mostRecentInt(Map<Byte, Integer> map) {
       return Collections.max(map.values());
    }



    public static boolean parse(File f, String fileName) {

        try {
            InputStream stream = Files.newInputStream(f.toPath());
            DataInputStream dis = new DataInputStream(stream);
            short value = FileReader.readShort(dis); // if 0, its version 1-5, else its classic song lenght
            Version version;
            short lenght = 0;
            byte vanillaInstruments = 16;
            short layerCount;
            float tempo;
            List<Layer> layers = new ArrayList<>();
            if (value != 0) {
                version = Version.CLASSIC;
                lenght = value;
            } else {
                version = Version.getFromByte(dis.readByte());
                vanillaInstruments = dis.readByte();

                if (version.getVersionByte() >= 3)
                    lenght = FileReader.readShort(dis);
            }

            layerCount = FileReader.readShort(dis);
            FileReader.readString(dis);
            FileReader.readString(dis);
            FileReader.readString(dis);
            FileReader.readString(dis);
            tempo = FileReader.readShort(dis)/100f;
            MathUtils.bitToBoolean(dis.readByte());
            dis.readByte();
            dis.readByte();
            FileReader.readInt(dis);
            FileReader.readInt(dis);
            FileReader.readInt(dis);
            FileReader.readInt(dis);
            FileReader.readInt(dis);
            FileReader.readString(dis);
            if (version.getVersionByte() >= 4) {
                MathUtils.bitToBoolean(dis.readByte());
                dis.readByte();
                FileReader.readShort(dis);
            }


            for (int i = 0; i < layerCount; i++) {
                Layer layer = new Layer((short) i);
                layers.add(layer);
            }


            Map<Byte, Integer> instrumentMap = new HashMap<>();

            short tick = -1;
            while (true) {
                short jumpTick = FileReader.readShort(dis);
                if (jumpTick == 0) {
                    if (lenght == 0)
                        lenght = tick;
                    break;
                }

                tick += jumpTick;

                short layer = -1;
                while (true) {
                    short jumpLayer = FileReader.readShort(dis);
                    if (jumpLayer == 0)
                        break;
                    layer += jumpLayer;
                    byte instrument = dis.readByte();
                    byte key = dis.readByte();
                    byte velocity = 100;
                    byte panning = 100;
                    short pitch = 0;
                    if (version.getVersionByte() >= 4) {
                        velocity = dis.readByte();
                        panning = dis.readByte();
                        pitch = dis.readShort();
                    }
                    Layer foundLayer = Layer.getFromNumber(layers, layer);
                    if (instrumentMap.isEmpty()) {
                        instrumentMap.put(instrument, 0);
                    } else if (!instrumentMap.containsKey(instrument)) {
                        instrumentMap.put(instrument, mostRecentInt(instrumentMap) + 1);
                    }
                    foundLayer.addNote(new Note(tick, instrument, key, velocity, panning, pitch));
                }
            }



            for (int i = 0; i < layerCount; i++) {
                Layer currentLayer = Layer.getFromNumber(layers, (short) i);
                String layerName = FileReader.readString(dis);
                currentLayer.setName(layerName);

                boolean layerLock = false;
                if (version.getVersionByte() >= 4) {

                    layerLock = MathUtils.bitToBoolean(dis.readByte());
                }
                currentLayer.setLocked(layerLock);

                byte layerVolume = dis.readByte();
                currentLayer.setVolume(layerVolume);

                byte layerStereo = 0;

                if (version.getVersionByte() >= 2) {
                    layerStereo = dis.readByte();
                }
                currentLayer.setStereo(layerStereo);
                currentLayer.updateNotes();
            }


            byte customInstruments = dis.readByte();

            Map<Byte, Byte> customInstrumentPitchMap = new HashMap<>();

            for (int i = 0; i < customInstruments; i++) {
                String instrumentName = FileReader.readString(dis);
                String soundFile = FileReader.readString(dis);
                byte soundPitch = dis.readByte();
                boolean pressKey = MathUtils.bitToBoolean(dis.readByte());
                customInstrumentPitchMap.put((byte) (vanillaInstruments + i), soundPitch);
                if (instrumentMap.isEmpty()) {
                    instrumentMap.put((byte) (vanillaInstruments + i),0);
                } else if (!instrumentMap.containsKey((byte) (vanillaInstruments + i))) {
                    instrumentMap.put((byte) (vanillaInstruments + i), mostRecentInt(instrumentMap) + 1);
                }

            }


            List<List<Note>> instrumentLists = new ArrayList<>();

            for (int i = 0; i <= lenght; i ++) {
                instrumentLists.add(new ArrayList<>());
            }


            for (Layer layer : layers) {
                for (Note note : layer.getNotes()) {
                        instrumentLists.get(note.getTick()).add(note);
                }
            }


            File file = new File(FilenameUtils.getFullPath(f.getPath()) + fileName + ".mid");

            Sequence sequence =  new Sequence(Sequence.PPQ, 4);



            List<Track> tracks = new ArrayList<>();
            for (int i = 0; i < instrumentMap.size(); i++) {
                tracks.add(sequence.createTrack());
            }
            long tempoToBpm = (long) ((tempo) * 15);
            tracks.forEach(track -> track.add(createSetTempoEvent(0, tempoToBpm)));

            for (int i = 0; i < instrumentMap.size(); i++) {
                tracks.get(i).add(makeEvent(SET_INSTRUMENT, i, i, 0, 0));
            }
            for (int i = 0; i < instrumentLists.size(); i++) {

                for (Note note : instrumentLists.get(i)) {
                    int val = instrumentMap.get(note.getInstrument());
                    int finalNote = note.getKey()+ 21;
                    switch (note.getInstrument()) {
                        case 1, 12 -> finalNote = finalNote - 24;
                        case 5 -> finalNote = finalNote - 12;
                        case 6, 8 -> finalNote = finalNote + 12;
                        case 7, 9 -> finalNote = finalNote + 24;
                    }
                    if (note.getInstrument() >= vanillaInstruments) {
                            finalNote = finalNote - (customInstrumentPitchMap.get(note.getInstrument()) - 45);
                    }


                    tracks.get(val).add(makeEvent(NOTE_ON, val, finalNote, note.getVelocity(), i));
                    tracks.get(val).add(makeEvent(NOTE_OFF, val, finalNote, note.getVelocity(), i + 1));
                }
            }
            if (file.exists()) {
                file.delete();
            }
            MidiSystem.write(sequence, 1, file);
            file.createNewFile();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static MidiEvent makeEvent(int command, int channel, int value1, int value2, int tick) {

        MidiEvent event = null;

        try {

            // ShortMessage stores a note as command type, channel,
            // instrument it has to be played on and its speed.
            ShortMessage a = new ShortMessage();
            a.setMessage(command, channel, value1, value2);

            // A midi event is comprised of a short message(representing
            // a note) and the tick at which that note has to be played
            event = new MidiEvent(a, tick);
        }
        catch (Exception ex) {

            ex.printStackTrace();
        }
        return event;
    }

    public static long MICROSECONDS_PER_MINUTE = 60000000;

    public static MidiEvent createSetTempoEvent(long tick, long tempo) {
        // microseconds per quarternote
        long mpqn = MICROSECONDS_PER_MINUTE / tempo;

        MetaMessage metaMessage = new MetaMessage();

        // create the tempo byte array
        byte[] array = new byte[] { 0, 0, 0 };

        for (int i = 0; i < 3; i++) {
            int shift = (3 - 1 - i) * 8;
            array[i] = (byte) (mpqn >> shift);
        }

        // now set the message
        try {
            metaMessage.setMessage(81, array, 3);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return new MidiEvent(metaMessage, tick);
    }


}
