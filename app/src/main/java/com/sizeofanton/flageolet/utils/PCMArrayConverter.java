package com.sizeofanton.flageolet.utils;

public class PCMArrayConverter {

    private static final short SHORT_DIVISOR = (short) (-1 * Short.MAX_VALUE);

    public PCMArrayConverter(){

    }


    public void convert(final byte[] array, final float[] convertedArray) {
        int arrayLength = array.length;

        for (int i = 0, j = 0; i < arrayLength; i += 2, j++) {

            short twoBytes = (short) (array[i] | (array[i + 1] << 8));

            convertedArray[j] = ((float) twoBytes) / SHORT_DIVISOR;
            convertedArray[j] = convertedArray[j] < -1 ? -1 : Math.min(convertedArray[j], 1);
        }
    }

    public void convert(final short[] array, final float[] convertedArray) {
        int arrayLength = array.length;
        int convertedArrayLength = convertedArray.length;

        for (int i = 0; i < arrayLength && i < convertedArrayLength; i++) {
            convertedArray[i] = ((float) array[i]) / SHORT_DIVISOR;
            convertedArray[i] = convertedArray[i] < -1 ? -1 : Math.min(convertedArray[i], 1);
        }
    }

}
