package me.bruno.nbs;

import java.util.Map;

public class MathUtils {

    public static boolean bitToBoolean(byte b) {
        return b != 0;
    }

    public static <T> void addOne(Map<T, Integer> map, T object) {
        map.put(object, map.get(object) + 1);
    }

    public static int minMax(int value, int min, int max) {
        return value <= min ? min : Math.min(value, max);
    }

    public static double minMax(double value, double min, double max) {
        return value <= min? min : Math.min(value, max);
    }
  //  public static float getPitch(MinecraftNote note) {
  //     return getPitch(note.getKey(), note.getPitch());
  //  }

    public static float getPitch(byte key, int pitch) {
        float mod = (key + (pitch/100f)) - 45;
        double value = Math.pow(2, mod/12f);

        return ((Double) value).floatValue();
    }


    public static double positive(double number) {
        return number < 0 ? number * -1 : number;
    }

    public static float ticksToBeats(float ticks) {
        return ticks*15;
    }

    public static float beatsToTicks(float beats) {
        return beats/15f;
    }

    public static float bpmToBeatsPerMillis(float bpm) {
        float bps = (float) roundToNDecimals(bpm/60d, 2);
        return 1000f / bps;
    }
    public static double roundToNDecimals(double number, int n) {
        int value = (int) Math.pow(10, n);
        return (double) Math.round(number*value)/value;

    }


}
