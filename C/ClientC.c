#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <time.h>

#define BUFFER_SIZE 1024

// Função para medir o tempo de execução
double get_time() {
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    return (double)ts.tv_sec + (double)ts.tv_nsec / 1000000000.0;
}

// Função para inicializar a matriz de pontuações
void initialize_score_matrix(int *score, int len1, int len2, int gap_penalty) {
    for (int i = 1; i <= len1; i++) {
        score[i * (len2 + 1)] = gap_penalty * i;
    }
    for (int j = 1; j <= len2; j++) {
        score[j] = gap_penalty * j;
    }
}

// Função para executar o algoritmo de Needleman-Wunsch
void needleman_wunsch(const char *seq1, const char *seq2, int len1, int len2, int match_score, int mismatch_score, int gap_penalty, char *alignment1, char *alignment2, int *alignment_score, int *num_gaps, double *execution_time) {
    double start_time = get_time();

    int *score = (int *)malloc((len1 + 1) * (len2 + 1) * sizeof(int));
    initialize_score_matrix(score, len1, len2, gap_penalty);

    // Preenchimento da matriz de pontuações
    for (int i = 1; i <= len1; i++) {
        for (int j = 1; j <= len2; j++) {
            int match = score[(i - 1) * (len2 + 1) + (j - 1)] + (seq1[i - 1] == seq2[j - 1] ? match_score : mismatch_score);
            int delete = score[(i - 1) * (len2 + 1) + j] + gap_penalty;
            int insert = score[i * (len2 + 1) + (j - 1)] + gap_penalty;
            score[i * (len2 + 1) + j] = match > delete ? (match > insert ? match : insert) : (delete > insert ? delete : insert);
        }
    }

    // Recuperação do alinhamento
    int i = len1, j = len2;
    *num_gaps = 0;
    int alignment_index = 0;

    while (i > 0 || j > 0) {
        if (i > 0 && j > 0 && score[i * (len2 + 1) + j] == score[(i - 1) * (len2 + 1) + (j - 1)] + (seq1[i - 1] == seq2[j - 1] ? match_score : mismatch_score)) {
            alignment1[alignment_index] = seq1[i - 1];
            alignment2[alignment_index] = seq2[j - 1];
            i--;
            j--;
        } else if (i > 0 && score[i * (len2 + 1) + j] == score[(i - 1) * (len2 + 1) + j] + gap_penalty) {
            alignment1[alignment_index] = seq1[i - 1];
            alignment2[alignment_index] = '-';
            (*num_gaps)++;
            i--;
        } else {
            alignment1[alignment_index] = '-';
            alignment2[alignment_index] = seq2[j - 1];
            (*num_gaps)++;
            j--;
        }
        alignment_index++;
    }

    alignment1[alignment_index] = '\0';
    alignment2[alignment_index] = '\0';
    *alignment_score = score[len1 * (len2 + 1) + len2];
    *execution_time = get_time() - start_time;

    free(score);
}

// Função para executar o algoritmo de Smith-Waterman
void smith_waterman(const char *seq1, const char *seq2, int len1, int len2, int match_score, int mismatch_score, int gap_penalty, char *alignment1, char *alignment2, int *alignment_score, int *num_gaps, double *execution_time) {
    double start_time = get_time();

    int *score = (int *)calloc((len1 + 1) * (len2 + 1), sizeof(int));
    int max_score = 0, max_i = 0, max_j = 0;

    // Preenchimento da matriz de pontuações
    for (int i = 1; i <= len1; i++) {
        for (int j = 1; j <= len2; j++) {
            int match = score[(i - 1) * (len2 + 1) + (j - 1)] + (seq1[i - 1] == seq2[j - 1] ? match_score : mismatch_score);
            int delete = score[(i - 1) * (len2 + 1) + j] + gap_penalty;
            int insert = score[i * (len2 + 1) + (j - 1)] + gap_penalty;
            int best_score = match > delete ? (match > insert ? match : insert) : (delete > insert ? delete : insert);
            score[i * (len2 + 1) + j] = best_score > 0 ? best_score : 0;

            if (score[i * (len2 + 1) + j] > max_score) {
                max_score = score[i * (len2 + 1) + j];
                max_i = i;
                max_j = j;
            }
        }
    }

    // Recuperação do alinhamento
    int i = max_i, j = max_j;
    *num_gaps = 0;
    int alignment_index = 0;

    while (i > 0 && j > 0 && score[i * (len2 + 1) + j] != 0) {
        if (score[i * (len2 + 1) + j] == score[(i - 1) * (len2 + 1) + (j - 1)] + (seq1[i - 1] == seq2[j - 1] ? match_score : mismatch_score)) {
            alignment1[alignment_index] = seq1[i - 1];
            alignment2[alignment_index] = seq2[j - 1];
            i--;
            j--;
        } else if (score[i * (len2 + 1) + j] == score[(i - 1) * (len2 + 1) + j] + gap_penalty) {
            alignment1[alignment_index] = seq1[i - 1];
            alignment2[alignment_index] = '-';
            (*num_gaps)++;
            i--;
        } else {
            alignment1[alignment_index] = '-';
            alignment2[alignment_index] = seq2[j - 1];
            (*num_gaps)++;
            j--;
        }
        alignment_index++;
    }

    alignment1[alignment_index] = '\0';
    alignment2[alignment_index] = '\0';
    *alignment_score = max_score;
    *execution_time = get_time() - start_time;

    free(score);
}

// Função para iniciar o cliente e conectar ao servidor
void start_client(const char *host, int port) {
    int client_socket;
    struct sockaddr_in server_addr;
    char buffer[BUFFER_SIZE];
    char alignment1[BUFFER_SIZE], alignment2[BUFFER_SIZE];
    int alignment_score, num_gaps;
    double execution_time;

    const char *seq2 = "LKDDAWFCNWISVQGPGAGDEVRFPCYRWVEGNGVLSLPEGTGRTVGEDPQGLFQKHREEELEERRKLYR";
    const char *macaco[] = {
        "CCCAGCAGTTGAGGATATTGGGCACAGCTGCATGTAAGGTGGTGTCACCTTGTTGAGCACATGGTAGTCCATGTTCATTCTCCAGGAGCCATCAGGCTTCTTTGCAGGCCACACAGGGCTGTTGTGGGGGCTGTGGGAGGCCTATTTGTACTTCATGTAATTCCTGAATTGTTTGGGTAGTTTCAGAGTGTCCCCCAGCAGGAGTATTGCCTCACATTCACTGTCTGCCTGGGCTGGGGCAAGTGTACAGCCACCCATGTGGCCCATCCTCTTTTTGTTCCTTGATTACTTTCTGGACTTGGTCCTTGCTTTTATCCAGCTCACTGAGGTCTGCCAAAGATTCCAGACACATTATAACCCTTCTTAAATTGGAAATGACCCAGACATTCCATGAGTACCTGAAATAATTTTAAGATTTTAAGCTATATAATCAGACAAGAGAAAGAAATAAAGGGCGACCAAATCAGAAAAGGGGAAGTCAAACTGTCATTATGATTATATACCTAAAAAATCCTATAGACTCATCCAAAAAGCTCCTAGAACTGCAAA",
        "TGTGACTTGTACCCTAGAGTTGTCCTGCCTTGGAGTAAAGAGGCTGGCCTTTTGTACCCTTGTATTAGTCAATTATTGGCTACATGACACCAATGTATCCAACTGGTGCAGAGGGGATGGTGCACCCTCTCCAGGATTTCCAGGTAAGGCAACTCCTGTCAGTGGAGGGCAGTGTTAGCTGTGGGGTGCTGCTGTGTTAGCAGCAAACATTCACCACGGCAGGGGGCTGGACTTACCAATGTACATTACCAGTTTAATATACATTTAATGTACAACACCAGTTTAATGCACATTTAATGTACCATCAAGGAGGTTGGATGCATTGACTTTTCAAAGGGGATCTGAGTGGGGCACCAATGGCATCTACTACGTGAGAGACAGAGCTTTATCCTACTGGGAAAGTGGGGGAGGCAATCTAGAATTCACACTTCAATGTGATCTCAATTTGAGGGATGAGGGAGCTGGGCTATTTACCCACCAACTCCTGGCAGCCCTTACTGAAGGAGACTCCTGGGGAGGTTAATTCTCAGTGTGGCCTGTGTGTAGCCACAGAGAGCTCCAGCAGCCGACAGTCATGT",
        "GCTGGGGCCCGGTCTTGGACTCACATGAATGAAGTAGTTGGCATAGGAGTCCTCCTTGGTAACAAAGTGGTACAGGTCAGCCAAGTACTCGTCCTTGGGGACAGAGAGGGGAGAGCTTACGTTGGGTGAGGCTGGGGGGTCTAGAGGGGAAGAGGAGGCTTGGAAGGGCTGGTGACCTCCCCCAGGCCACACAACTGCAGGTGGGAGGTCCCAGCCAGGCTCCTCTCTCTGCCAAGGTTGGTGCTTGGGGCCAGCCCCTGGGAACTCCCTTAAACTTTTCTGGCTTCTTGTCCCCATCTCTGTGTCTCCACAGACTTAGCACAGTGCCTTGTTTTTTGTTTTGTTTCGTTTGAGACGGAGTCTCGCACTGTTGCCCGGGCTGGAGTGCAGTGGTGCAATCTCGGCTCACTGCAACCTCTGCCTCCTGGGTTCAAGCGATTCTCCTGCCTCAGCCTCCTGAGTAGCTGGGATTACAGGCGCGTGCCACTACGCCCAGCTAAGTTTTTGTATTTTTAGTAGAGACAGGGTTTCACCATGTTGGC",
        "CTCTGAAATACAAGACCTTTAGGGATCACTTAGCCCAACAGCCTCAATGGATAGAGATAACTGAGGCCCAGAGCAGGCATTTGTTCAAGTTCAGAGAAGAAGAACGTTAACGCCTCTGTGTGACCTGGCATCAGGCGGGGCTAGACTATCACCTCCAAGTCAGGCTGCCTGAGGTCATACTGGCAGGGATGGCCCAACCACTGGATCAGGAACCCACCAGAGCATGCCTAAGTCTTGTCACTAAATCTCTATGCCCAGCCATTGAGACAAAGAATCTTACAGATCCCATACAAGACTCTACTGTGACCCCACCAGAGCCTTGCATTGTTGGGCACACCTGCCCCAACACTCCCAGCCAGCATCATGACTTCTAGGATGGATTCAGATGGCCACACCCAGATATCACTCCACTCCCCTTTTAGCTTCCATGAGAGATTTAGCTTCTGTGAAAGCAGGGATGTCAGGACCCAAGGGGCAGGTCTGTGGACAAACTGACTCACCTCATACTGTAGCTTCTGTTTCCCTGCAGGCATGCCTGTGGCTTCATGAATCTTCACCTTAATGACAGAGACCTGTGGGATCAAGCAGC",
        "ATACAGGGTCTTGCTCTGTTGACCAGACCGTGACCTCCTGGGTTCAAGCGAGGACTCAGCCTCCTGAGTAGCCGGGACTACAGATTGGACGACTGATTATTGGACAGAATGGCATCTTGTCTACACCTGCGGTCTCCTGCATTATCAGGAAGATCAAGGCAGCTGGTGGAATCATTCTAACAGCCAGCCACTGCCCTGGAGGACCAGGGGGAGAGTTTGGAGTGAAGTTTAATGTTGCCAATGGAGGGCAGACTTCTTGGAGGAAGTGAAATTTGAACTGCGATGTGAAGAATGAGCAGGAGTTAGTGAGGTGAAGATGAGAGAAGGAGTGTTGCAAACACAGGTCAGTCTGTGCAAAGGCCCTGA"
    };

    const char *gorila[] = {
        "TACCAGTTTAAGGGCCTGTGCTACTTCACCAACGGGACGGAGCGCGTGCGGGCTGTGACCAGACACATCTATAACCGAGAGGAGTACGTGCGCTTCGACAGCGACGTGGGGGTGTACCGGGCAGTGACGCCGCAGGGGCGGCCTGCCGCCGACTACTGGAACAGCCAGAAGGAAGCCTGGAGGAGACCCGGGCGTCGGTGGACAGGGTGTGCAGACACAACTACGAGGTGTCGTACCGCGGGATC",
        "TACCAGTTTAAGGGCATGTGCTACTTCACCAACGGGACGGAGCGCGTGCGTGTTGTGACGAGATACATCTATAACCGAGAGGAGTACGCGCGCTTCGACAGCGACGTGGGGGTGTATCAGGCGGTGACGCCGCTGGGGCCGCCTGACGCCGACTACTGGAACAGCCAGAAGGAAGCCTGGAGGAGACCCGGGCGTCGGTGGACAGGGTGTGCAGACACAACTACCAGTTGGAGCTCCTCACGACC",
        "CCAAGTATTAGCTAACCCATCAATAATTATCATGTATGTCGTGCATTACTGCCAGACACCATGAATAATGCACAGTACTACAAATGTCCAACCACCTGTAACACATACAACCCCCCCCCTCACTGCTCCACCCAACGGAATACCAACCAATCCATCCCTCACAAAAAGTACATAAACATAAAGTCATTTATCGTACATAGCACATTCCAGTTAAATCATCCTCGCCCCCACGGATGCCCCCCCTCAGATA",
        "ATGGCGGTTTTGTGGAATAGAAAAGGGGGCAAGGTGGGGAAAAGATTGAGAAATCGGAAGGTTGCTGTGTCTGTGTAGAAAGAAGTAGACATGGGAGACTTTTCATTTTGTTCTGTACTAAGAAAAATTCTTCTGCCTTGGGATCCTGTTGATCTATGACCTTACCCCCAACCCTGTGCTCTCTGAAACATGTGTTGTGTCCACTCAGGGTTAAATGGATTAAGGGCGGTGCAAGATGTGCTTTGTTAAACAGATGCTTGAAGGCAGCATGCTCGTTAAGAGTCATCACCACTCCCTAATCTCAAGTACCCAGGGACACAAACACTGCGGAAGGCTGCAGGGTCCTCTGCCTAGGAAAACCAGAGACCTTTGTTCACTTGTTTATCTGCTGACCTTCCCTCCACTACTGTCCTATGACCCTGCCACATCCCCCTCTGCG",
        "ATGGCGGTTTTGTGGAATAGAAAAGGGGGCAAGGTGGGGAAAAGATTGAGAAATCGGAAGGTTGCTGTGTCTGTGTAGAAAGAAGTAGATATGGGAGACTTTTCATTTTGTTCTGTACTAAGAAAAATTCTTCTGCCTTGGGATCCTGTTGATCTATGACCTTACCCCCAACCCTGTGCTCTCTGAAACATGTGCTGTGTCCACTCAGGGTTAAATGGATTAAGGGCGGTGCAAGATGTGCTTTGTTAAACAGATGCTTGAAGGCAGCATGCTCCTTAAGAGTCATCACCACTCCCTAATCTCAAGTACCCATGGACACAAACACTCTGCCTAGGAAAACCAGAGACCTTTGTTCACTTGTTTGTCTGCTGACCTTCCCTCCACTACTGTCCTATGACCCTGCCAAATCCCCCTCTGCG"
    };

    // Criar socket
    client_socket = socket(AF_INET, SOCK_STREAM, 0);
    if (client_socket == -1) {
        perror("Could not create socket");
        exit(EXIT_FAILURE);
    }

    server_addr.sin_addr.s_addr = inet_addr(host);
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(port);

    // Conectar ao servidor
    if (connect(client_socket, (struct sockaddr *)&server_addr, sizeof(server_addr)) < 0) {
        perror("Connect failed");
        close(client_socket);
        exit(EXIT_FAILURE);
    }

    // Receber a sequência do servidor
    if (recv(client_socket, buffer, BUFFER_SIZE, 0) < 0) {
        perror("Recv failed");
        close(client_socket);
        exit(EXIT_FAILURE);
    }
    printf("Sequence received from server: %s\n", buffer);

    // Executa o algoritmo de Needleman-Wunsch e Smith-Waterman para sequências de macaco
    needleman_wunsch(macaco[0], seq2, strlen(macaco[0]), strlen(seq2), 1, -1, -1, alignment1, alignment2, &alignment_score, &num_gaps, &execution_time);
    printf("Needleman-Wunsch:\nAlignment1: %s\nAlignment2: %s\nScore: %d\nGaps: %d\nExecution Time: %lf seconds\n", alignment1, alignment2, alignment_score, num_gaps, execution_time);

    smith_waterman(macaco[0], seq2, strlen(macaco[0]), strlen(seq2), 1, -1, -1, alignment1, alignment2, &alignment_score, &num_gaps, &execution_time);
    printf("Smith-Waterman:\nAlignment1: %s\nAlignment2: %s\nScore: %d\nGaps: %d\nExecution Time: %lf seconds\n", alignment1, alignment2, alignment_score, num_gaps, execution_time);

    // Enviar o melhor resultado para o servidor
    char response_message[BUFFER_SIZE];
    snprintf(response_message, BUFFER_SIZE, "C;Needleman;AlignmentScore:%d;Gap:%d;ExecutionTime:%lf;Smith;AlignmentScore:%d;Gap:%d;ExecutionTime:%lf",
             alignment_score, num_gaps, execution_time, alignment_score, num_gaps, execution_time);

    if (send(client_socket, response_message, strlen(response_message), 0) < 0) {
        perror("Send failed");
        close(client_socket);
        exit(EXIT_FAILURE);
    }
    printf("Response sent to server\n");

    close(client_socket);
}

int main() {
    start_client("127.0.0.1", 65432);
    return 0;
}