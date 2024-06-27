#include <iostream>
#include <fstream>
#include <string>
#include <vector>

#define MATCH 1
#define MISMATCH -1
#define GAP -1

int max(int a, int b, int c) {
    return std::max(a, std::max(b, c));
}

void needleman_wunsch(const std::string& seq1, const std::string& seq2) {
    int len1 = seq1.length();
    int len2 = seq2.length();
    std::vector<std::vector<int>> score(len1 + 1, std::vector<int>(len2 + 1, 0));

    for (int i = 0; i <= len1; i++) score[i][0] = i * GAP;
    for (int j = 0; j <= len2; j++) score[0][j] = j * GAP;

    for (int i = 1; i <= len1; i++) {
        for (int j = 1; j <= len2; j++) {
            int match = score[i-1][j-1] + (seq1[i-1] == seq2[j-1] ? MATCH : MISMATCH);
            int del = score[i-1][j] + GAP;
            int ins = score[i][j-1] + GAP;
            score[i][j] = max(match, del, ins);
        }
    }

    // Traceback
    int alignment_len = len1 + len2;
    std::string aligned_seq1(alignment_len, ' ');
    std::string aligned_seq2(alignment_len, ' ');
    int index1 = len1;
    int index2 = len2;
    int pos = alignment_len - 1;

    while (index1 > 0 || index2 > 0) {
        if (index1 > 0 && index2 > 0 && score[index1][index2] == score[index1-1][index2-1] + (seq1[index1-1] == seq2[index2-1] ? MATCH : MISMATCH)) {
            aligned_seq1[pos] = seq1[index1-1];
            aligned_seq2[pos] = seq2[index2-1];
            index1--;
            index2--;
        } else if (index1 > 0 && score[index1][index2] == score[index1-1][index2] + GAP) {
            aligned_seq1[pos] = seq1[index1-1];
            aligned_seq2[pos] = '-';
            index1--;
        } else {
            aligned_seq1[pos] = '-';
            aligned_seq2[pos] = seq2[index2-1];
            index2--;
        }
        pos--;
    }

    // Open file to write the alignment
    std::ofstream file("alignment_needleman_wunsch.txt");
    if (!file) {
        std::cerr << "Error opening file!" << std::endl;
        exit(1);
    }

    file << "Alignment score (Needleman-Wunsch): " << score[len1][len2] << "\n";
    file << "Number of gaps: " << alignment_len - score[len1][len2] << "\n";
    file << "Aligned Sequences:\n";
    file << aligned_seq1.substr(pos + 1) << "\n";
    file << aligned_seq2.substr(pos + 1) << "\n";
    file.close();

    std::cout << "Alignment score (Needleman-Wunsch): " << score[len1][len2] << std::endl;
    std::cout << "Number of gaps: " << alignment_len - score[len1][len2] << std::endl;
    std::cout << "Alignment saved to alignment_needleman_wunsch.txt" << std::endl;
}

void smith_waterman(const std::string& seq1, const std::string& seq2) {
    int len1 = seq1.length();
    int len2 = seq2.length();
    std::vector<std::vector<int>> score(len1 + 1, std::vector<int>(len2 + 1, 0));
    int max_score = 0;
    int max_i = 0, max_j = 0;

    for (int i = 1; i <= len1; i++) {
        for (int j = 1; j <= len2; j++) {
            int match = score[i-1][j-1] + (seq1[i-1] == seq2[j-1] ? MATCH : MISMATCH);
            int del = score[i-1][j] + GAP;
            int ins = score[i][j-1] + GAP;
            score[i][j] = std::max(0, max(match, del, ins)); // Local alignment modification
            if (score[i][j] >= max_score) {
                max_score = score[i][j];
                max_i = i;
                max_j = j;
            }
        }
    }

    // Traceback starting from the maximum score position
    int alignment_len = max_i + max_j;
    std::string aligned_seq1(alignment_len, ' ');
    std::string aligned_seq2(alignment_len, ' ');
    int pos = alignment_len - 1;
    int index1 = max_i;
    int index2 = max_j;

    while (index1 > 0 && index2 > 0 && score[index1][index2] != 0) {
        if (score[index1][index2] == score[index1-1][index2-1] + (seq1[index1-1] == seq2[index2-1] ? MATCH : MISMATCH)) {
            aligned_seq1[pos] = seq1[index1-1];
            aligned_seq2[pos] = seq2[index2-1];
            index1--;
            index2--;
        } else if (score[index1][index2] == score[index1-1][index2] + GAP) {
            aligned_seq1[pos] = seq1[index1-1];
            aligned_seq2[pos] = '-';
            index1--;
        } else {
            aligned_seq1[pos] = '-';
            aligned_seq2[pos] = seq2[index2-1];
            index2--;
        }
        pos--;
    }

    // Open file to write the alignment
    std::ofstream file("alignment_smith_waterman.txt");
    if (!file) {
        std::cerr << "Error opening file!" << std::endl;
        exit(1);
    }

    file << "Alignment score (Smith-Waterman): " << max_score << "\n";
    file << "Number of gaps: " << alignment_len - max_score << "\n";
    file << "Aligned Sequences:\n";
    file << aligned_seq1.substr(pos + 1) << "\n";
    file << aligned_seq2.substr(pos + 1) << "\n";
    file.close();

    std::cout << "Alignment score (Smith-Waterman): " << max_score << std::endl;
    std::cout << "Number of gaps: " << alignment_len - max_score << std::endl;
    std::cout << "Alignment saved to alignment_smith_waterman.txt" << std::endl;
}

int main() {
    std::string seq1 = "MALWMRLLPLLALLALWGPDPAAAFVNQHLCGSHLVEALYLVCGERGFFYTPKT";
    std::string seq2 = "MIPGTKLVIAFTSDLKDFSPLEYGEKHCRYLIDGRSYQMHLKHATVKKIVKAPGPLFHTGSGSTSSFRVGVVDFMIQGGDF";

    // Perform Needleman-Wunsch (Global Alignment)
    needleman_wunsch(seq1, seq2);

    // Perform Smith-Waterman (Local Alignment)
    smith_waterman(seq1, seq2);

    return 0;
}
