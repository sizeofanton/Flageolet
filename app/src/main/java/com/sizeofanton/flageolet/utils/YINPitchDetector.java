package com.sizeofanton.flageolet.utils;

public class YINPitchDetector {

    private static final float ABSOLUTE_THRESHOLD = 0.125f;

    private final double sampleRate;
    private final float[] resultBuffer;

    public YINPitchDetector(final double sampleRate, final int readSize){
        this.sampleRate = sampleRate;
        this.resultBuffer = new float[readSize / 2];
    }

    public double detect(float[] wave){
        int tau;
        autoCorrelationDifference(wave);
        cumulativeMeanNormalizedDifference();
        tau = absoluteThreshold();
        float betterTau = parabolicInterpolation(tau);
        return sampleRate / betterTau;
    }

    private void autoCorrelationDifference(final float[] wave) {
        int length = resultBuffer.length;
        int i, j;

        for (j = 1; j < length; j++) {
            for (i = 0; i < length; i++) {
                resultBuffer[j] += Math.pow((wave[i] - wave[i + j]), 2);
            }
        }
    }

    private void cumulativeMeanNormalizedDifference(){
        int i;
        int length = resultBuffer.length;
        float runningSum = 0;

        resultBuffer[0] = 1;

        for (i = 1; i < length; i++){
            runningSum += resultBuffer[i];
            resultBuffer[i] *= i / runningSum;
        }
    }

    private int absoluteThreshold(){
        int tau;
        int length = resultBuffer.length;

        for (tau = 2; tau < length; tau++){
            if (resultBuffer[tau] < ABSOLUTE_THRESHOLD){
                while (tau + 1 < length && resultBuffer[tau + 1] < resultBuffer[tau]){
                    tau ++;
                }

                break;
            }
        }

        tau = tau >= length ? length - 1 : tau;
        return  tau;
    }

    private float parabolicInterpolation(final int currentTau) {

        int x0 = currentTau < 1 ? currentTau : currentTau - 1;
        int x2 = currentTau + 1 < resultBuffer.length ? currentTau + 1 : currentTau;


        float betterTau;

        if (x0 == currentTau) {
            if (resultBuffer[currentTau] <= resultBuffer[x2]) {
                betterTau = currentTau;
            } else {
                betterTau = x2;
            }
        } else if (x2 == currentTau) {
            if (resultBuffer[currentTau] <= resultBuffer[x0]) {
                betterTau = currentTau;
            } else {
                betterTau = x0;
            }
        } else {

            float s0 = resultBuffer[x0];
            float s1 = resultBuffer[currentTau];
            float s2 = resultBuffer[x2];

            betterTau = currentTau + (s2 - s0) / (2 * (2 * s1 - s2 - s0));
        }

        return betterTau;
    }

}
