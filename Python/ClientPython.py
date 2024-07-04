import time
import socket

def start_client(host='127.0.0.1', port=65432):
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as client_socket:
        client_socket.connect((host, port))

        while True:
            # Receber a sequência do servidor
            sequence = client_socket.recv(1024).decode()
            if not sequence:
                print("No sequence received, closing connection.")
                break
            print("Sequence received from server:", sequence)

            seq1, seq2 = sequence.split(";")
            print("Seq1:", seq1)
            print("Seq2:", seq2)

            # Função para executar o algoritmo de Needleman-Wunsch
            def needleman_wunsch(seq1, seq2, match_score=1, mismatch_score=-1, gap_penalty=-1):
                start_time = time.time()
                len1 = len(seq1)
                len2 = len(seq2)

                # Inicialização da matriz de pontuações
                score = [[0 for _ in range(len2 + 1)] for _ in range(len1 + 1)]

                # Inicialização das penalidades para gaps
                for i in range(1, len1 + 1):
                    score[i][0] = gap_penalty * i
                for j in range(1, len2 + 1):
                    score[0][j] = gap_penalty * j

                # Preenchimento da matriz de pontuações
                for i in range(1, len1 + 1):
                    for j in range(1, len2 + 1):
                        match = score[i-1][j-1] + (match_score if seq1[i-1] == seq2[j-1] else mismatch_score)
                        delete = score[i-1][j] + gap_penalty
                        insert = score[i][j-1] + gap_penalty
                        score[i][j] = max(match, delete, insert)

                # Recuperação do alinhamento
                alignment1 = ''
                alignment2 = ''
                num_gaps = 0
                i, j = len1, len2
                while i > 0 or j > 0:
                    if i > 0 and j > 0 and score[i][j] == score[i-1][j-1] + (match_score if seq1[i-1] == seq2[j-1] else mismatch_score):
                        alignment1 = str(seq1[i-1]) + alignment1
                        alignment2 = str(seq2[j-1]) + alignment2
                        i -= 1
                        j -= 1
                    elif i > 0 and score[i][j] == score[i-1][j] + gap_penalty:
                        alignment1 = str(seq1[i-1]) + alignment1
                        alignment2 = '-' + alignment2
                        num_gaps += 1
                        i -= 1
                    else:
                        alignment1 = '-' + alignment1
                        alignment2 = str(seq2[j-1]) + alignment2
                        num_gaps += 1
                        j -= 1

                end_time = time.time()
                execution_time = end_time - start_time

                return alignment1, alignment2, score[len1][len2], num_gaps, execution_time

            # Função para executar o algoritmo de Smith-Waterman ajustado
            def smith_waterman(seq1, seq2, match_score=1, mismatch_score=-1, gap_penalty=-1):
                start_time = time.time()
                len1 = len(seq1)
                len2 = len(seq2)

                # Inicialização da matriz de pontuações e da matriz de trilhas
                score = [[0 for _ in range(len2 + 1)] for _ in range(len1 + 1)]
                traceback = [[[] for _ in range(len2 + 1)] for _ in range(len1 + 1)]
                max_score = 0
                max_position = None

                # Preenchimento da matriz de pontuações
                for i in range(1, len1 + 1):
                    for j in range(1, len2 + 1):
                        match = score[i-1][j-1] + (match_score if seq1[i-1] == seq2[j-1] else mismatch_score)
                        delete = score[i-1][j] + gap_penalty
                        insert = score[i][j-1] + gap_penalty
                        best_score = max(match, delete, insert, 0)
                        score[i][j] = best_score

                        # Rastreando os caminhos que levam ao melhor score
                        if best_score == 0:
                            continue
                        if best_score == match:
                            traceback[i][j].append((i-1, j-1))
                        if best_score == delete:
                            traceback[i][j].append((i-1, j))
                        if best_score == insert:
                            traceback[i][j].append((i, j-1))

                        if best_score > max_score:
                            max_score = best_score
                            max_position = (i, j)

                # Função para traçar o caminho de volta a partir da posição de maior valor
                def traceback_alignment(i, j):
                    align1, align2, gaps = "", "", 0
                    while score[i][j] != 0:
                        if traceback[i][j] == [(i-1, j-1)]:
                            align1 = seq1[i-1] + align1
                            align2 = seq2[j-1] + align2
                            i, j = i-1, j-1
                        elif traceback[i][j] == [(i-1, j)]:
                            align1 = seq1[i-1] + align1
                            align2 = "-" + align2
                            gaps += 1
                            i -= 1
                        elif traceback[i][j] == [(i, j-1)]:
                            align1 = "-" + align1
                            align2 = seq2[j-1] + align2
                            gaps += 1
                            j -= 1
                    return align1, align2, gaps

                # Recuperar o alinhamento a partir da posição de maior valor
                if max_position:
                    alignment1, alignment2, num_gaps = traceback_alignment(*max_position)
                else:
                    alignment1, alignment2, num_gaps = "", "", 0

                end_time = time.time()
                execution_time = end_time - start_time

                return [alignment1], [alignment2], max_score, [num_gaps], execution_time

            # Executa o algoritmo de Needleman-Wunsch
            alignment1_n, alignment2_n, score_n, num_gaps_n, execution_time_n = needleman_wunsch(seq1, seq2)
            # Executa o algoritmo de Smith-Waterman
            alignments1_s, alignments2_s, max_score_s, smith_gaps, execution_time_s = smith_waterman(seq1, seq2)

            # Enviar o melhor resultado para o servidor
            response_message = f"Python;Needleman;AlignmentScore:{score_n};Gap:{num_gaps_n};ExecutionTime:{execution_time_n:.6f};Smith;AlignmentScore:{max_score_s};Gap:{smith_gaps[0]};ExecutionTime:{execution_time_s:.6f}"
            client_socket.sendall(response_message.encode())
            print(response_message)
            print("Response sent to server")

if __name__ == '__main__':
    start_client()
