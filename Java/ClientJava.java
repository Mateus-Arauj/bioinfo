import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientJava {

    public static void main(String[] args) {
        new ClientJava().startClient("127.0.0.1", 65432);
    }

    public void startClient(String host, int port) {
        try (Socket clientSocket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            // Receber a sequência do servidor
            String sequence = in.readLine();
            System.out.println("Sequence received from server: " + sequence);

            // Suas sequências predefinidas
            String seq2 = "LKDDAWFCNWISVQGPGAGDEVRFPCYRWVEGNGVLSLPEGTGRTVGEDPQGLFQKHREEELEERRKLYR";
            String[] macaco = {
                "CCCAGCAGTTGAGGATATTGGGCACAGCTGCATGTAAGGTGGTGTCACCTTGTTGAGCACATGGTAGTCCATGTTCATTCTCCAGGAGCCATCAGGCTTCTTTGCAGGCCACACAGGGCTGTTGTGGGGGCTGTGGGAGGCCTATTTGTACTTCATGTAATTCCTGAATTGTTTGGGTAGTTTCAGAGTGTCCCCCAGCAGGAGTATTGCCTCACATTCACTGTCTGCCTGGGCTGGGGCAAGTGTACAGCCACCCATGTGGCCCATCCTCTTTTTGTTCCTTGATTACTTTCTGGACTTGGTCCTTGCTTTTATCCAGCTCACTGAGGTCTGCCAAAGATTCCAGACACATTATAACCCTTCTTAAATTGGAAATGACCCAGACATTCCATGAGTACCTGAAATAATTTTAAGATTTTAAGCTATATAATCAGACAAGAGAAAGAAATAAAGGGCGACCAAATCAGAAAAGGGGAAGTCAAACTGTCATTATGATTATATACCTAAAAAATCCTATAGACTCATCCAAAAAGCTCCTAGAACTGCAAA",
                "TGTGACTTGTACCCTAGAGTTGTCCTGCCTTGGAGTAAAGAGGCTGGCCTTTTGTACCCTTGTATTAGTCAATTATTGGCTACATGACACCAATGTATCCAACTGGTGCAGAGGGGATGGTGCACCCTCTCCAGGATTTCCAGGTAAGGCAACTCCTGTCAGTGGAGGGCAGTGTTAGCTGTGGGGTGCTGCTGTGTTAGCAGCAAACATTCACCACGGCAGGGGGCTGGACTTACCAATGTACATTACCAGTTTAATATACATTTAATGTACAACACCAGTTTAATGCACATTTAATGTACCATCAAGGAGGTTGGATGCATTGACTTTTCAAAGGGGATCTGAGTGGGGCACCAATGGCATCTACTACGTGAGAGACAGAGCTTTATCCTACTGGGAAAGTGGGGGAGGCAATCTAGAATTCACACTTCAATGTGATCTCAATTTGAGGGATGAGGGAGCTGGGCTATTTACCCACCAACTCCTGGCAGCCCTTACTGAAGGAGACTCCTGGGGAGGTTAATTCTCAGTGTGGCCTGTGTGTAGCCACAGAGAGCTCCAGCAGCCGACAGTCATGT",
                "GCTGGGGCCCGGTCTTGGACTCACATGAATGAAGTAGTTGGCATAGGAGTCCTCCTTGGTAACAAAGTGGTACAGGTCAGCCAAGTACTCGTCCTTGGGGACAGAGAGGGGAGAGCTTACGTTGGGTGAGGCTGGGGGGTCTAGAGGGGAAGAGGAGGCTTGGAAGGGCTGGTGACCTCCCCCAGGCCACACAACTGCAGGTGGGAGGTCCCAGCCAGGCTCCTCTCTCTGCCAAGGTTGGTGCTTGGGGCCAGCCCCTGGGAACTCCCTTAAACTTTTCTGGCTTCTTGTCCCCATCTCTGTGTCTCCACAGACTTAGCACAGTGCCTTGTTTTTTGTTTTGTTTCGTTTGAGACGGAGTCTCGCACTGTTGCCCGGGCTGGAGTGCAGTGGTGCAATCTCGGCTCACTGCAACCTCTGCCTCCTGGGTTCAAGCGATTCTCCTGCCTCAGCCTCCTGAGTAGCTGGGATTACAGGCGCGTGCCACTACGCCCAGCTAAGTTTTTGTATTTTTAGTAGAGACAGGGTTTCACCATGTTGGC",
                "CTCTGAAATACAAGACCTTTAGGGATCACTTAGCCCAACAGCCTCAATGGATAGAGATAACTGAGGCCCAGAGCAGGCATTTGTTCAAGTTCAGAGAAGAAGAACGTTAACGCCTCTGTGTGACCTGGCATCAGGCGGGGCTAGACTATCACCTCCAAGTCAGGCTGCCTGAGGTCATACTGGCAGGGATGGCCCAACCACTGGATCAGGAACCCACCAGAGCATGCCTAAGTCTTGTCACTAAATCTCTATGCCCAGCCATTGAGACAAAGAATCTTACAGATCCCATACAAGACTCTACTGTGACCCCACCAGAGCCTTGCATTGTTGGGCACACCTGCCCCAACACTCCCAGCCAGCATCATGACTTCTAGGATGGATTCAGATGGCCACACCCAGATATCACTCCACTCCCCTTTTAGCTTCCATGAGAGATTTAGCTTCTGTGAAAGCAGGGATGTCAGGACCCAAGGGGCAGGTCTGTGGACAAACTGACTCACCTCATACTGTAGCTTCTGTTTCCCTGCAGGCATGCCTGTGGCTTCATGAATCTTCACCTTAATGACAGAGACCTGTGGGATCAAGCAGC",
                "ATACAGGGTCTTGCTCTGTTGACCAGACCGTGACCTCCTGGGTTCAAGCGAGGACTCAGCCTCCTGAGTAGCCGGGACTACAGATTGGACGACTGATTATTGGACAGAATGGCATCTTGTCTACACCTGCGGTCTCCTGCATTATCAGGAAGATCAAGGCAGCTGGTGGAATCATTCTAACAGCCAGCCACTGCCCTGGAGGACCAGGGGGAGAGTTTGGAGTGAAGTTTAATGTTGCCAATGGAGGGCAGACTTCTTGGAGGAAGTGAAATTTGAACTGCGATGTGAAGAATGAGCAGGAGTTAGTGAGGTGAAGATGAGAGAAGGAGTGTTGCAAACACAGGTCAGTCTGTGCAAAGGCCCTGA"
            };
            String[] gorila = {
                "TACCAGTTTAAGGGCCTGTGCTACTTCACCAACGGGACGGAGCGCGTGCGGGCTGTGACCAGACACATCTATAACCGAGAGGAGTACGTGCGCTTCGACAGCGACGTGGGGGTGTACCGGGCAGTGACGCCGCAGGGGCGGCCTGCCGCCGACTACTGGAACAGCCAGAAGGAAGCCTGGAGGAGACCCGGGCGTCGGTGGACAGGGTGTGCAGACACAACTACGAGGTGTCGTACCGCGGGATC",
                "TACCAGTTTAAGGGCATGTGCTACTTCACCAACGGGACGGAGCGCGTGCGTGTTGTGACGAGATACATCTATAACCGAGAGGAGTACGCGCGCTTCGACAGCGACGTGGGGGTGTATCAGGCGGTGACGCCGCTGGGGCCGCCTGACGCCGACTACTGGAACAGCCAGAAGGAAGCCTGGAGGAGACCCGGGCGTCGGTGGACAGGGTGTGCAGACACAACTACCAGTTGGAGCTCCTCACGACC",
                "CCAAGTATTAGCTAACCCATCAATAATTATCATGTATGTCGTGCATTACTGCCAGACACCATGAATAATGCACAGTACTACAAATGTCCAACCACCTGTAACACATACAACCCCCCCCCTCACTGCTCCACCCAACGGAATACCAACCAATCCATCCCTCACAAAAAGTACATAAACATAAAGTCATTTATCGTACATAGCACATTCCAGTTAAATCATCCTCGCCCCCACGGATGCCCCCCCTCAGATA",
                "ATGGCGGTTTTGTGGAATAGAAAAGGGGGCAAGGTGGGGAAAAGATTGAGAAATCGGAAGGTTGCTGTGTCTGTGTAGAAAGAAGTAGACATGGGAGACTTTTCATTTTGTTCTGTACTAAGAAAAATTCTTCTGCCTTGGGATCCTGTTGATCTATGACCTTACCCCCAACCCTGTGCTCTCTGAAACATGTGTTGTGTCCACTCAGGGTTAAATGGATTAAGGGCGGTGCAAGATGTGCTTTGTTAAACAGATGCTTGAAGGCAGCATGCTCGTTAAGAGTCATCACCACTCCCTAATCTCAAGTACCCAGGGACACAAACACTGCGGAAGGCTGCAGGGTCCTCTGCCTAGGAAAACCAGAGACCTTTGTTCACTTGTTTATCTGCTGACCTTCCCTCCACTACTGTCCTATGACCCTGCCACATCCCCCTCTGCG",
                "ATGGCGGTTTTGTGGAATAGAAAAGGGGGCAAGGTGGGGAAAAGATTGAGAAATCGGAAGGTTGCTGTGTCTGTGTAGAAAGAAGTAGATATGGGAGACTTTTCATTTTGTTCTGTACTAAGAAAAATTCTTCTGCCTTGGGATCCTGTTGATCTATGACCTTACCCCCAACCCTGTGCTCTCTGAAACATGTGCTGTGTCCACTCAGGGTTAAATGGATTAAGGGCGGTGCAAGATGTGCTTTGTTAAACAGATGCTTGAAGGCAGCATGCTCCTTAAGAGTCATCACCACTCCCTAATCTCAAGTACCCATGGACACAAACACTCTGCCTAGGAAAACCAGAGACCTTTGTTCACTTGTTTGTCTGCTGACCTTCCCTCCACTACTGTCCTATGACCCTGCCAAATCCCCCTCTGCG"
            };

            // Resultados para sequências de macaco
            NeedlemanWunschResult[] macacoResults = new NeedlemanWunschResult[macaco.length];
            SmithWatermanResult[] macacoSmithResults = new SmithWatermanResult[macaco.length];

            for (int i = 0; i < macaco.length; i++) {
                macacoResults[i] = needlemanWunsch(macaco[i], seq2, 1, -1, -1);
                macacoSmithResults[i] = smithWaterman(macaco[i], seq2, 1, -1, -1);
            }

            // Resultados para sequências de gorila
            NeedlemanWunschResult[] gorilaResults = new NeedlemanWunschResult[gorila.length];
            SmithWatermanResult[] gorilaSmithResults = new SmithWatermanResult[gorila.length];

            for (int i = 0; i < gorila.length; i++) {
                gorilaResults[i] = needlemanWunsch(gorila[i], seq2, 1, -1, -1);
                gorilaSmithResults[i] = smithWaterman(gorila[i], seq2, 1, -1, -1);
            }

            // Escolher o melhor alinhamento para cada algoritmo
            NeedlemanWunschResult bestNeedlemanMacaco = chooseBestNeedlemanWunsch(macacoResults);
            SmithWatermanResult bestSmithMacaco = chooseBestSmithWaterman(macacoSmithResults);
            NeedlemanWunschResult bestNeedlemanGorila = chooseBestNeedlemanWunsch(gorilaResults);
            SmithWatermanResult bestSmithGorila = chooseBestSmithWaterman(gorilaSmithResults);

            // Selecionar o melhor alinhamento entre macaco e gorila para cada algoritmo
            Object[] bestOverall = selectBestOverall(bestNeedlemanMacaco, bestSmithMacaco, bestNeedlemanGorila, bestSmithGorila);
            NeedlemanWunschResult bestNeedleman = (NeedlemanWunschResult) bestOverall[0];
            SmithWatermanResult bestSmith = (SmithWatermanResult) bestOverall[1];

            // Enviar o melhor resultado para o servidor
            String responseMessage = String.format("Java;Needleman;AlignmentScore:%d;Gap:%d;ExecutionTime:%d;Smith;AlignmentScore:%d;Gap:%d;ExecutionTime:%d",
                    bestNeedleman.score, bestNeedleman.numGaps, bestNeedleman.executionTime,
                    bestSmith.score, bestSmith.numGaps[0], bestSmith.executionTime);
            out.println(responseMessage);
            System.out.println(responseMessage);
            System.out.println("Response sent to server");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class NeedlemanWunschResult {
        String alignment1;
        String alignment2;
        int score;
        int numGaps;
        long executionTime;
    }

    NeedlemanWunschResult needlemanWunsch(String seq1, String seq2, int matchScore, int mismatchScore, int gapPenalty) {
        long startTime = System.currentTimeMillis();
        int len1 = seq1.length();
        int len2 = seq2.length();

        // Inicialização da matriz de pontuações
        int[][] score = new int[len1 + 1][len2 + 1];

        // Inicialização das penalidades para gaps
        for (int i = 1; i <= len1; i++) {
            score[i][0] = gapPenalty * i;
        }
        for (int j = 1; j <= len2; j++) {
            score[0][j] = gapPenalty * j;
        }

        // Preenchimento da matriz de pontuações
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int match = score[i - 1][j - 1] + (seq1.charAt(i - 1) == seq2.charAt(j - 1) ? matchScore : mismatchScore);
                int delete = score[i - 1][j] + gapPenalty;
                int insert = score[i][j - 1] + gapPenalty;
                score[i][j] = Math.max(Math.max(match, delete), insert);
            }
        }

        // Recuperação do alinhamento
        String alignment1 = "";
        String alignment2 = "";
        int numGaps = 0;
        int i = len1, j = len2;
        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && score[i][j] == score[i - 1][j - 1] + (seq1.charAt(i - 1) == seq2.charAt(j - 1) ? matchScore : mismatchScore)) {
                alignment1 = seq1.charAt(i - 1) + alignment1;
                alignment2 = seq2.charAt(j - 1) + alignment2;
                i--;
                j--;
            } else if (i > 0 && score[i][j] == score[i - 1][j] + gapPenalty) {
                alignment1 = seq1.charAt(i - 1) + alignment1;
                alignment2 = "-" + alignment2;
                numGaps++;
                i--;
            } else {
                alignment1 = "-" + alignment1;
                alignment2 = seq2.charAt(j - 1) + alignment2;
                numGaps++;
                j--;
            }
        }

        long endTime = System.currentTimeMillis();
        NeedlemanWunschResult result = new NeedlemanWunschResult();
        result.alignment1 = alignment1;
        result.alignment2 = alignment2;
        result.score = score[len1][len2];
        result.numGaps = numGaps;
        result.executionTime = endTime - startTime;

        return result;
    }

    class SmithWatermanResult {
        String[] alignments1;
        String[] alignments2;
        int score;
        int[] numGaps;
        long executionTime;
    }

    SmithWatermanResult smithWaterman(String seq1, String seq2, int matchScore, int mismatchScore, int gapPenalty) {
        long startTime = System.currentTimeMillis();
        int len1 = seq1.length();
        int len2 = seq2.length();

        // Inicialização da matriz de pontuações e da matriz de trilhas
        int[][] score = new int[len1 + 1][len2 + 1];
        int maxScore = 0;
        int[] maxPosition = new int[2];

        // Preenchimento da matriz de pontuações
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int match = score[i - 1][j - 1] + (seq1.charAt(i - 1) == seq2.charAt(j - 1) ? matchScore : mismatchScore);
                int delete = score[i - 1][j] + gapPenalty;
                int insert = score[i][j - 1] + gapPenalty;
                score[i][j] = Math.max(0, Math.max(Math.max(match, delete), insert));
                if (score[i][j] > maxScore) {
                    maxScore = score[i][j];
                    maxPosition[0] = i;
                    maxPosition[1] = j;
                }
            }
        }

        // Recuperar o alinhamento a partir da posição de maior valor
        String[] alignment1 = new String[0];
        String[] alignment2 = new String[0];
        int numGaps = 0;
        if (maxPosition[0] > 0 && maxPosition[1] > 0) {
            String[] result = tracebackAlignment(maxPosition[0], maxPosition[1], score, seq1, seq2, matchScore, mismatchScore, gapPenalty);
            alignment1 = new String[]{result[0]};
            alignment2 = new String[]{result[1]};
            numGaps = Integer.parseInt(result[2]);
        }

        long endTime = System.currentTimeMillis();
        SmithWatermanResult result = new SmithWatermanResult();
        result.alignments1 = alignment1;
        result.alignments2 = alignment2;
        result.score = maxScore;
        result.numGaps = new int[]{numGaps};
        result.executionTime = endTime - startTime;

        return result;
    }

    String[] tracebackAlignment(int i, int j, int[][] score, String seq1, String seq2, int matchScore, int mismatchScore, int gapPenalty) {
        String align1 = "", align2 = "";
        int gaps = 0;
        while (score[i][j] != 0) {
            if (i > 0 && j > 0 && score[i][j] == score[i - 1][j - 1] + (seq1.charAt(i - 1) == seq2.charAt(j - 1) ? matchScore : mismatchScore)) {
                align1 = seq1.charAt(i - 1) + align1;
                align2 = seq2.charAt(j - 1) + align2;
                i--;
                j--;
            } else if (i > 0 && score[i][j] == score[i - 1][j] + gapPenalty) {
                align1 = seq1.charAt(i - 1) + align1;
                align2 = "-" + align2;
                gaps++;
                i--;
            } else {
                align1 = "-" + align1;
                align2 = seq2.charAt(j - 1) + align2;
                gaps++;
                j--;
            }
        }
        return new String[]{align1, align2, Integer.toString(gaps)};
    }

    NeedlemanWunschResult chooseBestNeedlemanWunsch(NeedlemanWunschResult[] results) {
        NeedlemanWunschResult bestResult = null;
        for (NeedlemanWunschResult result : results) {
            if (bestResult == null ||
                    result.executionTime < bestResult.executionTime ||
                    (result.executionTime == bestResult.executionTime && result.numGaps < bestResult.numGaps) ||
                    (result.executionTime == bestResult.executionTime && result.numGaps == bestResult.numGaps && result.score > bestResult.score)) {
                bestResult = result;
            }
        }
        return bestResult;
    }

    SmithWatermanResult chooseBestSmithWaterman(SmithWatermanResult[] results) {
        SmithWatermanResult bestResult = null;
        for (SmithWatermanResult result : results) {
            if (bestResult == null ||
                    result.executionTime < bestResult.executionTime ||
                    (result.executionTime == bestResult.executionTime && result.numGaps[0] < bestResult.numGaps[0]) ||
                    (result.executionTime == bestResult.executionTime && result.numGaps[0] == bestResult.numGaps[0] && result.score > bestResult.score)) {
                bestResult = result;
            }
        }
        return bestResult;
    }

    Object[] selectBestOverall(NeedlemanWunschResult needlemanMacaco, SmithWatermanResult smithMacaco, NeedlemanWunschResult needlemanGorila, SmithWatermanResult smithGorila) {
        NeedlemanWunschResult bestNeedleman = (needlemanMacaco.executionTime < needlemanGorila.executionTime ||
                (needlemanMacaco.executionTime == needlemanGorila.executionTime && needlemanMacaco.numGaps < needlemanGorila.numGaps) ||
                (needlemanMacaco.executionTime == needlemanGorila.executionTime && needlemanMacaco.numGaps == needlemanGorila.numGaps && needlemanMacaco.score > needlemanGorila.score)) ? needlemanMacaco : needlemanGorila;

        SmithWatermanResult bestSmith = (smithMacaco.executionTime < smithGorila.executionTime ||
                (smithMacaco.executionTime == smithGorila.executionTime && smithMacaco.numGaps[0] < smithGorila.numGaps[0]) ||
                (smithMacaco.executionTime == smithGorila.executionTime && smithMacaco.numGaps[0] == smithGorila.numGaps[0] && smithMacaco.score > smithGorila.score)) ? smithMacaco : smithGorila;

        return new Object[]{bestNeedleman, bestSmith};
    }
}
