import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.time.Duration;
import java.time.Instant;

class NeedlemanWunsch {
    private String seq1, seq2;
    private int match, mismatch, gap;

    public NeedlemanWunsch(String seq1, String seq2, int match, int mismatch, int gap) {
        this.seq1 = seq1;
        this.seq2 = seq2;
        this.match = match;
        this.mismatch = mismatch;
        this.gap = gap;
    }

    public Result align() {
        Instant start = Instant.now();
        int lenSeq1 = seq1.length();
        int lenSeq2 = seq2.length();

        // Create the scoring matrix
        int[][] scoreMatrix = new int[lenSeq1 + 1][lenSeq2 + 1];

        // Initialize the scoring matrix
        for (int i = 0; i <= lenSeq1; ++i) {
            scoreMatrix[i][0] = gap * i;
        }
        for (int j = 0; j <= lenSeq2; ++j) {
            scoreMatrix[0][j] = gap * j;
        }

        // Fill the scoring matrix
        for (int i = 1; i <= lenSeq1; ++i) {
            for (int j = 1; j <= lenSeq2; ++j) {
                int matchScore = scoreMatrix[i - 1][j - 1] + (seq1.charAt(i - 1) == seq2.charAt(j - 1) ? match : mismatch);
                int deleteScore = scoreMatrix[i - 1][j] + gap;
                int insertScore = scoreMatrix[i][j - 1] + gap;
                scoreMatrix[i][j] = Math.max(matchScore, Math.max(deleteScore, insertScore));
            }
        }

        // Traceback to find the best alignment
        StringBuilder align1 = new StringBuilder();
        StringBuilder align2 = new StringBuilder();
        int i = lenSeq1, j = lenSeq2;
        while (i > 0 || j > 0) {
            int currentScore = scoreMatrix[i][j];
            if (i > 0 && j > 0 && currentScore == scoreMatrix[i - 1][j - 1] + (seq1.charAt(i - 1) == seq2.charAt(j - 1) ? match : mismatch)) {
                align1.append(seq1.charAt(i - 1));
                align2.append(seq2.charAt(j - 1));
                --i;
                --j;
            } else if (i > 0 && currentScore == scoreMatrix[i - 1][j] + gap) {
                align1.append(seq1.charAt(i - 1));
                align2.append('-');
                --i;
            } else {
                align1.append('-');
                align2.append(seq2.charAt(j - 1));
                --j;
            }
        }

        align1.reverse();
        align2.reverse();
        int gaps = (int) align1.chars().filter(c -> c == '-').count() + (int) align2.chars().filter(c -> c == '-').count();
        int score = scoreMatrix[lenSeq1][lenSeq2];
        Instant end = Instant.now();
        double timeTaken = Duration.between(start, end).toMillis() / 1000.0;

        return new Result(align1.toString(), align2.toString(), gaps, score, timeTaken);
    }
}

class SmithWaterman {
    private String seq1, seq2;
    private int match, mismatch, gap;

    public SmithWaterman(String seq1, String seq2, int match, int mismatch, int gap) {
        this.seq1 = seq1;
        this.seq2 = seq2;
        this.match = match;
        this.mismatch = mismatch;
        this.gap = gap;
    }

    public Result align() {
        Instant start = Instant.now();
        int lenSeq1 = seq1.length();
        int lenSeq2 = seq2.length();

        // Create the scoring matrix
        int[][] scoreMatrix = new int[lenSeq1 + 1][lenSeq2 + 1];

        // Fill the scoring matrix
        int maxScore = 0;
        int[] maxPos = {0, 0};
        for (int i = 1; i <= lenSeq1; ++i) {
            for (int j = 1; j <= lenSeq2; ++j) {
                int matchScore = scoreMatrix[i - 1][j - 1] + (seq1.charAt(i - 1) == seq2.charAt(j - 1) ? match : mismatch);
                int deleteScore = scoreMatrix[i - 1][j] + gap;
                int insertScore = scoreMatrix[i][j - 1] + gap;
                scoreMatrix[i][j] = Math.max(0, Math.max(matchScore, Math.max(deleteScore, insertScore)));
                if (scoreMatrix[i][j] >= maxScore) {
                    maxScore = scoreMatrix[i][j];
                    maxPos[0] = i;
                    maxPos[1] = j;
                }
            }
        }

        // Traceback to find the best alignment
        StringBuilder align1 = new StringBuilder();
        StringBuilder align2 = new StringBuilder();
        int i = maxPos[0], j = maxPos[1];
        while (scoreMatrix[i][j] != 0) {
            int currentScore = scoreMatrix[i][j];
            if (i > 0 && j > 0 && currentScore == scoreMatrix[i - 1][j - 1] + (seq1.charAt(i - 1) == seq2.charAt(j - 1) ? match : mismatch)) {
                align1.append(seq1.charAt(i - 1));
                align2.append(seq2.charAt(j - 1));
                --i;
                --j;
            } else if (i > 0 && currentScore == scoreMatrix[i - 1][j] + gap) {
                align1.append(seq1.charAt(i - 1));
                align2.append('-');
                --i;
            } else {
                align1.append('-');
                align2.append(seq2.charAt(j - 1));
                --j;
            }
        }

        align1.reverse();
        align2.reverse();
        int gaps = (int) align1.chars().filter(c -> c == '-').count() + (int) align2.chars().filter(c -> c == '-').count();
        int score = maxScore;
        Instant end = Instant.now();
        double timeTaken = Duration.between(start, end).toMillis() / 1000.0;

        return new Result(align1.toString(), align2.toString(), gaps, score, timeTaken);
    }
}

class Result {
    String align1, align2;
    int gaps, score;
    double timeTaken;

    public Result(String align1, String align2, int gaps, int score, double timeTaken) {
        this.align1 = align1;
        this.align2 = align2;
        this.gaps = gaps;
        this.score = score;
        this.timeTaken = timeTaken;
    }

    @Override
    public String toString() {
        return "Alignment:\n" + align1 + "\n" + align2 + "\nGaps: " + gaps + ", Score: " + score + ", Time: " + timeTaken + " seconds";
    }
}

public class Main {
    public static void main(String[] args) {
        String seq1 = "TGGCTCCTCGGAAACCCAATGTGCGACGAATTCATCAGCGTGCCGGAATGGTCTTACATAGTGGAGAGGGCTAATCCAGCTAATGACCTCTGTTACCCAGGGAGCCTCAATGACTATGAAGAACTGAAACACCTATTGAGCAGAATAAATCATTTTGAGAAGATTCTGATCATCCCCAAGAGTTCTTGGCCCAATCATGAAACATCATTAGGGGTGAGCGCAGCTTGTCCATACCAGGGAACACCCTCCTTTTTCAGAAATGTGGTGTGGCTTATCAAAAAGAACGATGCATACCCAACAATAAAGATAAGCTACAATAACACCAATCGGGAAGATCTTTTGATACTGTGGGGGATTCATCATTCCAACAATGCAGAAGAGCAGATAAATCTCTATAAAAACCCAACCACCTATATTTCAGTTGGAACATCAACTTTAAACCAGAGATTGGTACCAAAAATAGCTACCAGATCCCAAGTAAACGGG";
        String seq2 = "TATGATAAGAAGCTTGTTTCGCGCATTCAAATTCGAGTTAATCCTTTGCCGAAATTTGATTCTACCGTGTGGGTGACAGTCCGCAAAGTTCCTGCCTCATCGGACTTATCCGTTACCGCCATCTCTGCTATGTTCGCGGACGGAGCCTCACCGGTACTGGTTTATCAGTATGCAGCATCCGGAGTCCAAGCCAACAATAAATTGTTGTATGATCTTTCGGCGATGCGCGCTGATATTGGTGACATGAGAAAGTACGCCGTGCTCGTGTATTCAAAAGACGATGCGCTCGAGACGGACGAATTGGTACTTCATGTTGACATTGAGCACCAACGCATTCCCACATCTGGGGTGCTCCCAGTTTGAACCTGTGTTTTCCAGAACCCTCCCTCCGATTTCTGTGGCGGGAGCTGAGTTGGTAGTGTTGCTATAAACTACCTGAAGTCACTAAACGCTATGCGGTGAACGGGTTGTCCATCCAGCTTACGGC";

        NeedlemanWunsch nw = new NeedlemanWunsch(seq1, seq2, 1, -1, -1);
        Result nwResult = nw.align();
        System.out.println("Needleman-Wunsch " + nwResult);

        SmithWaterman sw = new SmithWaterman(seq1, seq2, 1, -1, -1);
        Result swResult = sw.align();
        System.out.println("Smith-Waterman " + swResult);
    }
}
