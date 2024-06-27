#include <iostream>
#include <vector>
#include <string>
#include <chrono>
#include <algorithm>
#include <asio.hpp>  // Necessário instalar a biblioteca ASIO para operações de rede

using asio::ip::tcp;

const int MATCH = 2;
const int MISMATCH = -1;
const int GAP = -1;

int Max(int a, int b, int c) {
    return std::max({a, b, c});
}

int Max(int a, int b, int c, int d) {
    return std::max({a, b, c, d});
}

void SmithWaterman(const std::string& seq1, const std::string& seq2, std::string& alignedSeq1, std::string& alignedSeq2, int& maxScore, int& gapCount1, int& gapCount2) {
    int len1 = seq1.length();
    int len2 = seq2.length();

    // Matrizes de pontuação e direção
    std::vector<std::vector<int>> score(len1 + 1, std::vector<int>(len2 + 1, 0));
    std::vector<std::vector<char>> direction(len1 + 1, std::vector<char>(len2 + 1, '0'));

    // Inicialização das matrizes
    int maxI = 0, maxJ = 0;
    maxScore = 0;

    // Preenchimento da matriz de pontuação e matriz de direção
    for (int i = 1; i <= len1; ++i) {
        for (int j = 1; j <= len2; ++j) {
            int scoreDiagonal = score[i - 1][j - 1] + (seq1[i - 1] == seq2[j - 1] ? MATCH : MISMATCH);
            int scoreUp = score[i - 1][j] + GAP;
            int scoreLeft = score[i][j - 1] + GAP;

            score[i][j] = Max(0, scoreDiagonal, scoreUp, scoreLeft);

            if (score[i][j] == scoreDiagonal) {
                direction[i][j] = 'D';
            } else if (score[i][j] == scoreUp) {
                direction[i][j] = 'U';
            } else if (score[i][j] == scoreLeft) {
                direction[i][j] = 'L';
            }

            if (score[i][j] >= maxScore) {
                maxI = i;
                maxJ = j;
                maxScore = score[i][j];
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

    while (score[iMax][jMax] > 0) {
        if (direction[iMax][jMax] == 'D') {
            alignedSeq1 = seq1[iMax - 1] + alignedSeq1;
            alignedSeq2 = seq2[jMax - 1] + alignedSeq2;
            iMax--;
            jMax--;
        } else if (direction[iMax][jMax] == 'U') {
            alignedSeq1 = seq1[iMax - 1] + alignedSeq1;
            alignedSeq2 = "-" + alignedSeq2;
            gapCount2++;
            iMax--;
        } else if (direction[iMax][jMax] == 'L') {
            alignedSeq1 = "-" + alignedSeq1;
            alignedSeq2 = seq2[jMax - 1] + alignedSeq2;
            gapCount1++;
            jMax--;
        }
    }
}

void NeedlemanWunsch(const std::string& seq1, const std::string& seq2, std::string& alignedSeq1, std::string& alignedSeq2, int& finalScore, int& gapCount1, int& gapCount2) {
    int len1 = seq1.length();
    int len2 = seq2.length();

    // Matrizes de pontuação e direção
    std::vector<std::vector<int>> score(len1 + 1, std::vector<int>(len2 + 1, 0));
    std::vector<std::vector<char>> direction(len1 + 1, std::vector<char>(len2 + 1, '0'));

    // Inicialização das matrizes
    for (int m = 0; m <= len1; ++m) {
        score[m][0] = m * GAP;
        direction[m][0] = 'U';
    }

    for (int n = 0; n <= len2; ++n) {
        score[0][n] = n * GAP;
        direction[0][n] = 'L';
    }

    // Preenchimento da matriz de pontuação e matriz de direção
    for (int m = 1; m <= len1; ++m) {
        for (int n = 1; n <= len2; ++n) {
            int scoreDiagonal = score[m - 1][n - 1] + (seq1[m - 1] == seq2[n - 1] ? MATCH : MISMATCH);
            int scoreUp = score[m - 1][n] + GAP;
            int scoreLeft = score[m][n - 1] + GAP;

            score[m][n] = Max(scoreDiagonal, scoreUp, scoreLeft);

            if (score[m][n] == scoreDiagonal) {
                direction[m][n] = 'D';
            } else if (score[m][n] == scoreUp) {
                direction[m][n] = 'U';
            } else if (score[m][n] == scoreLeft) {
                direction[m][n] = 'L';
            }
        }
    }

    // Pontuação final
    finalScore = score[len1][len2];

    // Alinhamento a partir do ponto final
    alignedSeq1 = "";
    alignedSeq2 = "";
    gapCount1 = 0;
    gapCount2 = 0;

    int mIndex = len1;
    int nIndex = len2;

    while (mIndex > 0 || nIndex > 0) {
        if (direction[mIndex][nIndex] == 'D') {
            alignedSeq1 = seq1[mIndex - 1] + alignedSeq1;
            alignedSeq2 = seq2[nIndex - 1] + alignedSeq2;
            mIndex--;
            nIndex--;
        } else if (direction[mIndex][nIndex] == 'U') {
            alignedSeq1 = seq1[mIndex - 1] + alignedSeq1;
            alignedSeq2 = "-" + alignedSeq2;
            gapCount2++;
            mIndex--;
        } else if (direction[mIndex][nIndex] == 'L') {
            alignedSeq1 = "-" + alignedSeq1;
            alignedSeq2 = seq2[nIndex - 1] + alignedSeq2;
            gapCount1++;
            nIndex--;
        }
    }
}

void start_client(const std::string& host = "127.0.0.1", int port = 65432) {
    asio::io_context io_context;
    tcp::socket socket(io_context);
    tcp::resolver resolver(io_context);
    asio::connect(socket, resolver.resolve(host, std::to_string(port)));

    // Receber a sequência do servidor
    std::array<char, 1024> buffer;
    size_t len = socket.read_some(asio::buffer(buffer));
    std::string sequence(buffer.data(), len);
    std::cout << "Sequence received from server: " << sequence << std::endl;

    std::string seq2 = "LKDDAWFCNWISVQGPGAGDEVRFPCYRWVEGNGVLSLPEGTGRTVGEDPQGLFQKHREEELEERRKLYR";

    std::vector<std::string> macaco = {"CCCAGCAGTTGAGGATATTGGGCACAGCTGCATGTAAGGTGGTGTCACCTTGTTGAGCACATGGTAGTCCATGTTCATTCTCCAGGAGCCATCAGGCTTCTTTGCAGGCCACACAGGGCTGTTGTGGGGGCTGTGGGAGGCCTATTTGTACTTCATGTAATTCCTGAATTGTTTGGGTAGTTTCAGAGTGTCCCCCAGCAGGAGTATTGCCTCACATTCACTGTCTGCCTGGGCTGGGGCAAGTGTACAGCCACCCATGTGGCCCATCCTCTTTTTGTTCCTTGATTACTTTCTGGACTTGGTCCTTGCTTTTATCCAGCTCACTGAGGTCTGCCAAAGATTCCAGACACATTATAACCCTTCTTAAATTGGAAATGACCCAGACATTCCATGAGTACCTGAAATAATTTTAAGATTTTAAGCTATATAATCAGACAAGAGAAAGAAATAAAGGGCGACCAAATCAGAAAAGGGGAAGTCAAACTGTCATTATGATTATATACCTAAAAAATCCTATAGACTCATCCAAAAAGCTCCTAGAACTGCAAA",
                                    "TGTGACTTGTACCCTAGAGTTGTCCTGCCTTGGAGTAAAGAGGCTGGCCTTTTGTACCCTTGTATTAGTCAATTATTGGCTACATGACACCAATGTATCCAACTGGTGCAGAGGGGATGGTGCACCCTCTCCAGGATTTCCAGGTAAGGCAACTCCTGTCAGTGGAGGGCAGTGTTAGCTGTGGGGTGCTGCTGTGTTAGCAGCAAACATTCACCACGGCAGGGGGCTGGACTTACCAATGTACATTACCAGTTTAATATACATTTAATGTACAACACCAGTTTAATGCACATTTAATGTACCATCAAGGAGGTTGGATGCATTGACTTTTCAAAGGGGATCTGAGTGGGGCACCAATGGCATCTACTACGTGAGAGACAGAGCTTTATCCTACTGGGAAAGTGGGGGAGGCAATCTAGAATTCACACTTCAATGTGATCTCAATTTGAGGGATGAGGGAGCTGGGCTATTTACCCACCAACTCCTGGCAGCCCTTACTGAAGGAGACTCCTGGGGAGGTTAATTCTCAGTGTGGCCTGTGTGTAGCCACAGAGAGCTCCAGCAGCCGACAGTCATGT",
                                    "GCTGGGGCCCGGTCTTGGACTCACATGAATGAAGTAGTTGGCATAGGAGTCCTCCTTGGTAACAAAGTGGTACAGGTCAGCCAAGTACTCGTCCTTGGGGACAGAGAGGGGAGAGCTTACGTTGGGTGAGGCTGGGGGGTCTAGAGGGGAAGAGGAGGCTTGGAAGGGCTGGTGACCTCCCCCAGGCCACACAACTGCAGGTGGGAGGTCCCAGCCAGGCTCCTCTCTCTGCCAAGGTTGGTGCTTGGGGCCAGCCCCTGGGAACTCCCTTAAACTTTTCTGGCTTCTTGTCCCCATCTCTGTGTCTCCACAGACTTAGCACAGTGCCTTGTTTTTTGTTTTGTTTCGTTTGAGACGGAGTCTCGCACTGTTGCCCGGGCTGGAGTGCAGTGGTGCAATCTCGGCTCACTGCAACCTCTGCCTCCTGGGTTCAAGCGATTCTCCTGCCTCAGCCTCCTGAGTAGCTGGGATTACAGGCGCGTGCCACTACGCCCAGCTAAGTTTTTGTATTTTTAGTAGAGACAGGGTTTCACCATGTTGGC",
                                    "CTCTGAAATACAAGACCTTTAGGGATCACTTAGCCCAACAGCCTCAATGGATAGAGATAACTGAGGCCCAGAGCAGGCATTTGTTCAAGTTCAGAGAAGAAGAACGTTAACGCCTCTGTGTGACCTGGCATCAGGCGGGGCTAGACTATCACCTCCAAGTCAGGCTGCCTGAGGTCATACTGGCAGGGATGGCCCAACCACTGGATCAGGAACCCACCAGAGCATGCCTAAGTCTTGTCACTAAATCTCTATGCCCAGCCATTGAGACAAAGAATCTTACAGATCCCATACAAGACTCTACTGTGACCCCACCAGAGCCTTGCATTGTTGGGCACACCTGCCCCAACACTCCCAGCCAGCATCATGACTTCTAGGATGGATTCAGATGGCCACACCCAGATATCACTCCACTCCCCTTTTAGCTTCCATGAGAGATTTAGCTTCTGTGAAAGCAGGGATGTCAGGACCCAAGGGGCAGGTCTGTGGACAAACTGACTCACCTCATACTGTAGCTTCTGTTTCCCTGCAGGCATGCCTGTGGCTTCATGAATCTTCACCTTAATGACAGAGACCTGTGGGATCAAGCAGC",
                                    "ATACAGGGTCTTGCTCTGTTGACCAGACCGTGACCTCCTGGGTTCAAGCGAGGACTCAGCCTCCTGAGTAGCCGGGACTACAGATTGGACGACTGATTATTGGACAGAATGGCATCTTGTCTACACCTGCGGTCTCCTGCATTATCAGGAAGATCAAGGCAGCTGGTGGAATCATTCTAACAGCCAGCCACTGCCCTGGAGGACCAGGGGGAGAGTTTGGAGTGAAGTTTAATGTTGCCAATGGAGGGCAGACTTCTTGGAGGAAGTGAAATTTGAACTGCGATGTGAAGAATGAGCAGGAGTTAGTGAGGTGAAGATGAGAGAAGGAGTGTTGCAAACACAGGTCAGTCTGTGCAAAGGCCCTGA"};

    std::vector<std::string> gorila = {"TACCAGTTTAAGGGCCTGTGCTACTTCACCAACGGGACGGAGCGCGTGCGGGCTGTGACCAGACACATCTATAACCGAGAGGAGTACGTGCGCTTCGACAGCGACGTGGGGGTGTACCGGGCAGTGACGCCGCAGGGGCGGCCTGCCGCCGACTACTGGAACAGCCAGAAGGAAGCCTGGAGGAGACCCGGGCGTCGGTGGACAGGGTGTGCAGACACAACTACGAGGTGTCGTACCGCGGGATC",
                                     "TACCAGTTTAAGGGCATGTGCTACTTCACCAACGGGACGGAGCGCGTGCGTGTTGTGACGAGATACATCTATAACCGAGAGGAGTACGCGCGCTTCGACAGCGACGTGGGGGTGTATCAGGCGGTGACGCCGCTGGGGCCGCCTGACGCCGACTACTGGAACAGCCAGAAGGAAGCCTGGAGGAGACCCGGGCGTCGGTGGACAGGGTGTGCAGACACAACTACCAGTTGGAGCTCCTCACGACC",
                                     "CCAAGTATTAGCTAACCCATCAATAATTATCATGTATGTCGTGCATTACTGCCAGACACCATGAATAATGCACAGTACTACAAATGTCCAACCACCTGTAACACATACAACCCCCCCCCTCACTGCTCCACCCAACGGAATACCAACCAATCCATCCCTCACAAAAAGTACATAAACATAAAGTCATTTATCGTACATAGCACATTCCAGTTAAATCATCCTCGCCCCCACGGATGCCCCCCCTCAGATA",
                                     "ATGGCGGTTTTGTGGAATAGAAAAGGGGGCAAGGTGGGGAAAAGATTGAGAAATCGGAAGGTTGCTGTGTCTGTGTAGAAAGAAGTAGACATGGGAGACTTTTCATTTTGTTCTGTACTAAGAAAAATTCTTCTGCCTTGGGATCCTGTTGATCTATGACCTTACCCCCAACCCTGTGCTCTCTGAAACATGTGTTGTGTCCACTCAGGGTTAAATGGATTAAGGGCGGTGCAAGATGTGCTTTGTTAAACAGATGCTTGAAGGCAGCATGCTCGTTAAGAGTCATCACCACTCCCTAATCTCAAGTACCCAGGGACACAAACACTGCGGAAGGCTGCAGGGTCCTCTGCCTAGGAAAACCAGAGACCTTTGTTCACTTGTTTATCTGCTGACCTTCCCTCCACTACTGTCCTATGACCCTGCCACATCCCCCTCTGCG",
                                     "ATGGCGGTTTTGTGGAATAGAAAAGGGGGCAAGGTGGGGAAAAGATTGAGAAATCGGAAGGTTGCTGTGTCTGTGTAGAAAGAAGTAGATATGGGAGACTTTTCATTTTGTTCTGTACTAAGAAAAATTCTTCTGCCTTGGGATCCTGTTGATCTATGACCTTACCCCCAACCCTGTGCTCTCTGAAACATGTGCTGTGTCCACTCAGGGTTAAATGGATTAAGGGCGGTGCAAGATGTGCTTTGTTAAACAGATGCTTGAAGGCAGCATGCTCCTTAAGAGTCATCACCACTCCCTAATCTCAAGTACCCATGGACACAAACACTCTGCCTAGGAAAACCAGAGACCTTTGTTCACTTGTTTGTCTGCTGACCTTCCCTCCACTACTGTCCTATGACCCTGCCAAATCCCCCTCTGCG"};

    std::string best_needleman, best_smith;
    int best_needleman_score = -1, best_smith_score = -1;

    // Executa o algoritmo de Needleman-Wunsch e Smith-Waterman para sequências de macaco
    for (const auto& seq : macaco) {
        std::string nwAlignedSeq1, nwAlignedSeq2, swAlignedSeq1, swAlignedSeq2;
        int nwFinalScore, swMaxScore, nwGapCount1, nwGapCount2, swGapCount1, swGapCount2;

        NeedlemanWunsch(seq, seq2, nwAlignedSeq1, nwAlignedSeq2, nwFinalScore, nwGapCount1, nwGapCount2);
        SmithWaterman(seq, seq2, swAlignedSeq1, swAlignedSeq2, swMaxScore, swGapCount1, swGapCount2);

        if (nwFinalScore > best_needleman_score) {
            best_needleman = "Needleman;AlignmentScore:" + std::to_string(nwFinalScore) + ";Gap1:" + std::to_string(nwGapCount1) + ";Gap2:" + std::to_string(nwGapCount2);
            best_needleman_score = nwFinalScore;
        }

        if (swMaxScore > best_smith_score) {
            best_smith = "Smith;AlignmentScore:" + std::to_string(swMaxScore) + ";Gap1:" + std::to_string(swGapCount1) + ";Gap2:" + std::to_string(swGapCount2);
            best_smith_score = swMaxScore;
        }
    }

    // Executa o algoritmo de Needleman-Wunsch e Smith-Waterman para sequências de gorila
    for (const auto& seq : gorila) {
        std::string nwAlignedSeq1, nwAlignedSeq2, swAlignedSeq1, swAlignedSeq2;
        int nwFinalScore, swMaxScore, nwGapCount1, nwGapCount2, swGapCount1, swGapCount2;

        NeedlemanWunsch(seq, seq2, nwAlignedSeq1, nwAlignedSeq2, nwFinalScore, nwGapCount1, nwGapCount2);
        SmithWaterman(seq, seq2, swAlignedSeq1, swAlignedSeq2, swMaxScore, swGapCount1, swGapCount2);

        if (nwFinalScore > best_needleman_score) {
            best_needleman = "Needleman;AlignmentScore:" + std::to_string(nwFinalScore) + ";Gap:" + std::to_string(nwGapCount1) + ";Gap2:" + std::to_string(nwGapCount2);
            best_needleman_score = nwFinalScore;
        }

        if (swMaxScore > best_smith_score) {
            best_smith = "Smith;AlignmentScore:" + std::to_string(swMaxScore) + ";Gap:" + std::to_string(swGapCount1) + ";Gap2:" + std::to_string(swGapCount2);
            best_smith_score = swMaxScore;
        }
    }

    // Enviar o melhor resultado para o servidor
    std::string response_message = "C++;" + best_needleman + ";" + best_smith;
    socket.send(asio::buffer(response_message));
    std::cout << response_message << std::endl;
    std::cout << "Response sent to server" << std::endl;
}

int main() {
    start_client();
    return 0;
}
