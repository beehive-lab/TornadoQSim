package simulators;

import uk.ac.manchester.tornado.api.annotations.Parallel;

public class UnitaryMatrixSimulator {

    private void KroneckerProduct(double[] aSourceR, double[] aSourceC, int aSourceDimension,
                                  double[] bSourceR, double[] bSourceC, int bSourceDimension,
                                  double[] destinationR, double[] destinationC) {

        int cDestinationDimension = aSourceDimension * bSourceDimension;
        int iaFlat, ibFlat, icFlat;

        for (@Parallel int ia = 0; ia < aSourceDimension; ia++) {
            for (@Parallel int ja = 0; ja < aSourceDimension; ja++) {
                for (@Parallel int ib = 0; ib < bSourceDimension; ib++) {
                    for (@Parallel int jb = 0; jb < bSourceDimension; jb++) {
                        iaFlat = ja + ia * aSourceDimension;
                        ibFlat = jb + ib * bSourceDimension;
                        icFlat = (bSourceDimension * ja + jb) + (bSourceDimension * ia + ib) * cDestinationDimension;

                        // Complex multiplication
                        destinationR[icFlat] = (aSourceR[iaFlat] * bSourceR[ibFlat])
                                               - (aSourceC[iaFlat] * bSourceC[ibFlat]);
                        destinationC[icFlat] = (aSourceR[iaFlat] * bSourceC[ibFlat])
                                               + (aSourceC[iaFlat] * bSourceR[ibFlat]);
                    }
                }
            }
        }

    }
}
