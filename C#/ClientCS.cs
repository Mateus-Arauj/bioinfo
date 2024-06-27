using System;
using System.IO;

class AlignmentAlgorithms
{
    // Parâmetros de alinhamento
    const int MATCH = 2;
    const int MISMATCH = -1;
    const int GAP = -1;

    static int Max(int a, int b, int c)
    {
        return Math.Max(Math.Max(a, b), c);
    }

    static int Max(int a, int b, int c, int d)
    {
        return Math.Max(Math.Max(a, b), Math.Max(c, d));
    }

    static void SmithWaterman(string seq1, string seq2, out string alignedSeq1, out string alignedSeq2, out int maxScore, out int gapCount1, out int gapCount2)
    {
        int len1 = seq1.Length;
        int len2 = seq2.Length;

        // Matrizes de pontuação e direção
        int[,] score = new int[len1 + 1, len2 + 1];
        char[,] direction = new char[len1 + 1, len2 + 1];

        // Inicialização das matrizes
        int maxI = 0, maxJ = 0;
        maxScore = 0;

        for (int i = 0; i <= len1; i++)
        {
            for (int j = 0; j <= len2; j++)
            {
                score[i, j] = 0;
                direction[i, j] = '0';
            }
        }

        // Preenchimento da matriz de pontuação e matriz de direção
        for (int i = 1; i <= len1; i++)
        {
            for (int j = 1; j <= len2; j++)
            {
                int scoreDiagonal = score[i - 1, j - 1] + (seq1[i - 1] == seq2[j - 1] ? MATCH : MISMATCH);
                int scoreUp = score[i - 1, j] + GAP;
                int scoreLeft = score[i, j - 1] + GAP;

                score[i, j] = Max(0, scoreDiagonal, scoreUp, scoreLeft);

                if (score[i, j] == scoreDiagonal)
                {
                    direction[i, j] = 'D';
                }
                else if (score[i, j] == scoreUp)
                {
                    direction[i, j] = 'U';
                }
                else if (score[i, j] == scoreLeft)
                {
                    direction[i, j] = 'L';
                }

                if (score[i, j] >= maxScore)
                {
                    maxI = i;
                    maxJ = j;
                    maxScore = score[i, j];
                }
            }
        }

        // Alinhamento a partir do ponto de maior pontuação
        alignedSeq1 = "";
        alignedSeq2 = "";
        gapCount1 = 0;
        gapCount2 = 0;

        int iMax = maxI;
        int jMax = maxJ;

        while (score[iMax, jMax] > 0)
        {
            if (direction[iMax, jMax] == 'D')
            {
                alignedSeq1 = seq1[iMax - 1] + alignedSeq1;
                alignedSeq2 = seq2[jMax - 1] + alignedSeq2;
                iMax--;
                jMax--;
            }
            else if (direction[iMax, jMax] == 'U')
            {
                alignedSeq1 = seq1[iMax - 1] + alignedSeq1;
                alignedSeq2 = "-" + alignedSeq2;
                gapCount2++;
                iMax--;
            }
            else if (direction[iMax, jMax] == 'L')
            {
                alignedSeq1 = "-" + alignedSeq1;
                alignedSeq2 = seq2[jMax - 1] + alignedSeq2;
                gapCount1++;
                jMax--;
            }
        }
    }

    static void NeedlemanWunsch(string seq1, string seq2, out string alignedSeq1, out string alignedSeq2, out int finalScore, out int gapCount1, out int gapCount2)
    {
        int len1 = seq1.Length;
        int len2 = seq2.Length;

        // Matrizes de pontuação e direção
        int[,] score = new int[len1 + 1, len2 + 1];
        char[,] direction = new char[len1 + 1, len2 + 1];

        // Inicialização das matrizes
        for (int m = 0; m <= len1; m++)
        {
            score[m, 0] = m * GAP;
            direction[m, 0] = 'U';
        }

        for (int n = 0; n <= len2; n++)
        {
            score[0, n] = n * GAP;
            direction[0, n] = 'L';
        }

        // Preenchimento da matriz de pontuação e matriz de direção
        for (int m = 1; m <= len1; m++)
        {
            for (int n = 1; n <= len2; n++)
            {
                int scoreDiagonal = score[m - 1, n - 1] + (seq1[m - 1] == seq2[n - 1] ? MATCH : MISMATCH);
                int scoreUp = score[m - 1, n] + GAP;
                int scoreLeft = score[m, n - 1] + GAP;

                score[m, n] = Max(scoreDiagonal, scoreUp, scoreLeft);

                if (score[m, n] == scoreDiagonal)
                {
                    direction[m, n] = 'D';
                }
                else if (score[m, n] == scoreUp)
                {
                    direction[m, n] = 'U';
                }
                else if (score[m, n] == scoreLeft)
                {
                    direction[m, n] = 'L';
                }
            }
        }

        // Pontuação final
        finalScore = score[len1, len2];

        // Alinhamento a partir do ponto final
        alignedSeq1 = "";
        alignedSeq2 = "";
        gapCount1 = 0;
        gapCount2 = 0;

        int mIndex = len1;
        int nIndex = len2;

        while (mIndex > 0 || nIndex > 0)
        {
            if (direction[mIndex, nIndex] == 'D')
            {
                alignedSeq1 = seq1[mIndex - 1] + alignedSeq1;
                alignedSeq2 = seq2[nIndex - 1] + alignedSeq2;
                mIndex--;
                nIndex--;
            }
            else if (direction[mIndex, nIndex] == 'U')
            {
                alignedSeq1 = seq1[mIndex - 1] + alignedSeq1;
                alignedSeq2 = "-" + alignedSeq2;
                gapCount2++;
                mIndex--;
            }
            else if (direction[mIndex, nIndex] == 'L')
            {
                alignedSeq1 = "-" + alignedSeq1;
                alignedSeq2 = seq2[nIndex - 1] + alignedSeq2;
                gapCount1++;
                nIndex--;
            }
        }
    }

    static void Main(string[] args)
    {
        string seq1 = "MALWMRLLPLLALLALWGPDPAAAFVNQHLCGSHLVEALYLVCGERGFFYTPKT"; 
        string seq2 = "MIPGTKLVIAFTSDLKDFSPLEYGEKHCRYLIDGRSYQMHLKHATVKKIVKAPGPLFHTGSGSTSSFRVGVVDFMIQGGDF"; 

        // Smith-Waterman
        SmithWaterman(seq1, seq2, out string swAlignedSeq1, out string swAlignedSeq2, out int swMaxScore, out int swGapCount1, out int swGapCount2);

        // Needleman-Wunsch
        NeedlemanWunsch(seq1, seq2, out string nwAlignedSeq1, out string nwAlignedSeq2, out int nwFinalScore, out int nwGapCount1, out int nwGapCount2);

        // Comparação dos resultados
        string melhorAlgoritmo = swMaxScore > nwFinalScore ? "Smith-Waterman" : "Needleman-Wunsch";

        // Impressão do resultado
        using (StreamWriter sw = new StreamWriter("alinhamento.txt"))
        {
            sw.WriteLine($"Seq1: {seq1}");
            sw.WriteLine($"Seq2: {seq2}");

            sw.WriteLine("\nSmith-Waterman Alinhamento Local:");
            sw.WriteLine(swAlignedSeq1);
            sw.WriteLine(swAlignedSeq2);
            sw.WriteLine($"Pontuação (Score) do Alinhamento: {swMaxScore}");
            sw.WriteLine($"Número de Gaps em Seq1: {swGapCount1}");
            sw.WriteLine($"Número de Gaps em Seq2: {swGapCount2}");

            sw.WriteLine("\nNeedleman-Wunsch Alinhamento Global:");
            sw.WriteLine(nwAlignedSeq1);
            sw.WriteLine(nwAlignedSeq2);
            sw.WriteLine($"Pontuação (Score) do Alinhamento: {nwFinalScore}");
            sw.WriteLine($"Número de Gaps em Seq1: {nwGapCount1}");
            sw.WriteLine($"Número de Gaps em Seq2: {nwGapCount2}");

            sw.WriteLine($"\nMelhor algoritmo: {melhorAlgoritmo}");
        }

        Console.WriteLine("Alinhamento salvo no arquivo 'alinhamento.txt'.");
    }
}
