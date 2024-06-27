use strict;
use warnings;
use Time::HiRes qw(time);
use IO::Socket::INET;
use List::Util qw(max);

sub start_client {
    my ($host, $port) = @_;
    $host ||= '127.0.0.1';
    $port ||= 65432;

    my $client_socket = IO::Socket::INET->new(
        PeerAddr => $host,
        PeerPort => $port,
        Proto    => 'tcp'
    ) or die "Could not connect to server: $!";

    # Receber a sequência do servidor
    my $sequence = '';
    $client_socket->recv($sequence, 1024);
    print "Sequence received from server: $sequence\n";

    # Suas sequências predefinidas
    my $seq2 = "LKDDAWFCNWISVQGPGAGDEVRFPCYRWVEGNGVLSLPEGTGRTVGEDPQGLFQKHREEELEERRKLYR";
    my @macaco = (
        "CCCAGCAGTTGAGGATATTGGGCACAGCTGCATGTAAGGTGGTGTCACCTTGTTGAGCACATGGTAGTCCATGTTCATTCTCCAGGAGCCATCAGGCTTCTTTGCAGGCCACACAGGGCTGTTGTGGGGGCTGTGGGAGGCCTATTTGTACTTCATGTAATTCCTGAATTGTTTGGGTAGTTTCAGAGTGTCCCCCAGCAGGAGTATTGCCTCACATTCACTGTCTGCCTGGGCTGGGGCAAGTGTACAGCCACCCATGTGGCCCATCCTCTTTTTGTTCCTTGATTACTTTCTGGACTTGGTCCTTGCTTTTATCCAGCTCACTGAGGTCTGCCAAAGATTCCAGACACATTATAACCCTTCTTAAATTGGAAATGACCCAGACATTCCATGAGTACCTGAAATAATTTTAAGATTTTAAGCTATATAATCAGACAAGAGAAAGAAATAAAGGGCGACCAAATCAGAAAAGGGGAAGTCAAACTGTCATTATGATTATATACCTAAAAAATCCTATAGACTCATCCAAAAAGCTCCTAGAACTGCAAA",
        "TGTGACTTGTACCCTAGAGTTGTCCTGCCTTGGAGTAAAGAGGCTGGCCTTTTGTACCCTTGTATTAGTCAATTATTGGCTACATGACACCAATGTATCCAACTGGTGCAGAGGGGATGGTGCACCCTCTCCAGGATTTCCAGGTAAGGCAACTCCTGTCAGTGGAGGGCAGTGTTAGCTGTGGGGTGCTGCTGTGTTAGCAGCAAACATTCACCACGGCAGGGGGCTGGACTTACCAATGTACATTACCAGTTTAATATACATTTAATGTACAACACCAGTTTAATGCACATTTAATGTACCATCAAGGAGGTTGGATGCATTGACTTTTCAAAGGGGATCTGAGTGGGGCACCAATGGCATCTACTACGTGAGAGACAGAGCTTTATCCTACTGGGAAAGTGGGGGAGGCAATCTAGAATTCACACTTCAATGTGATCTCAATTTGAGGGATGAGGGAGCTGGGCTATTTACCCACCAACTCCTGGCAGCCCTTACTGAAGGAGACTCCTGGGGAGGTTAATTCTCAGTGTGGCCTGTGTGTAGCCACAGAGAGCTCCAGCAGCCGACAGTCATGT",
        "GCTGGGGCCCGGTCTTGGACTCACATGAATGAAGTAGTTGGCATAGGAGTCCTCCTTGGTAACAAAGTGGTACAGGTCAGCCAAGTACTCGTCCTTGGGGACAGAGAGGGGAGAGCTTACGTTGGGTGAGGCTGGGGGGTCTAGAGGGGAAGAGGAGGCTTGGAAGGGCTGGTGACCTCCCCCAGGCCACACAACTGCAGGTGGGAGGTCCCAGCCAGGCTCCTCTCTCTGCCAAGGTTGGTGCTTGGGGCCAGCCCCTGGGAACTCCCTTAAACTTTTCTGGCTTCTTGTCCCCATCTCTGTGTCTCCACAGACTTAGCACAGTGCCTTGTTTTTTGTTTTGTTTCGTTTGAGACGGAGTCTCGCACTGTTGCCCGGGCTGGAGTGCAGTGGTGCAATCTCGGCTCACTGCAACCTCTGCCTCCTGGGTTCAAGCGATTCTCCTGCCTCAGCCTCCTGAGTAGCTGGGATTACAGGCGCGTGCCACTACGCCCAGCTAAGTTTTTGTATTTTTAGTAGAGACAGGGTTTCACCATGTTGGC",
        "CTCTGAAATACAAGACCTTTAGGGATCACTTAGCCCAACAGCCTCAATGGATAGAGATAACTGAGGCCCAGAGCAGGCATTTGTTCAAGTTCAGAGAAGAAGAACGTTAACGCCTCTGTGTGACCTGGCATCAGGCGGGGCTAGACTATCACCTCCAAGTCAGGCTGCCTGAGGTCATACTGGCAGGGATGGCCCAACCACTGGATCAGGAACCCACCAGAGCATGCCTAAGTCTTGTCACTAAATCTCTATGCCCAGCCATTGAGACAAAGAATCTTACAGATCCCATACAAGACTCTACTGTGACCCCACCAGAGCCTTGCATTGTTGGGCACACCTGCCCCAACACTCCCAGCCAGCATCATGACTTCTAGGATGGATTCAGATGGCCACACCCAGATATCACTCCACTCCCCTTTTAGCTTCCATGAGAGATTTAGCTTCTGTGAAAGCAGGGATGTCAGGACCCAAGGGGCAGGTCTGTGGACAAACTGACTCACCTCATACTGTAGCTTCTGTTTCCCTGCAGGCATGCCTGTGGCTTCATGAATCTTCACCTTAATGACAGAGACCTGTGGGATCAAGCAGC",
        "ATACAGGGTCTTGCTCTGTTGACCAGACCGTGACCTCCTGGGTTCAAGCGAGGACTCAGCCTCCTGAGTAGCCGGGACTACAGATTGGACGACTGATTATTGGACAGAATGGCATCTTGTCTACACCTGCGGTCTCCTGCATTATCAGGAAGATCAAGGCAGCTGGTGGAATCATTCTAACAGCCAGCCACTGCCCTGGAGGACCAGGGGGAGAGTTTGGAGTGAAGTTTAATGTTGCCAATGGAGGGCAGACTTCTTGGAGGAAGTGAAATTTGAACTGCGATGTGAAGAATGAGCAGGAGTTAGTGAGGTGAAGATGAGAGAAGGAGTGTTGCAAACACAGGTCAGTCTGTGCAAAGGCCCTGA"
    );

    my @gorila = (
        "TACCAGTTTAAGGGCCTGTGCTACTTCACCAACGGGACGGAGCGCGTGCGGGCTGTGACCAGACACATCTATAACCGAGAGGAGTACGTGCGCTTCGACAGCGACGTGGGGGTGTACCGGGCAGTGACGCCGCAGGGGCGGCCTGCCGCCGACTACTGGAACAGCCAGAAGGAAGCCTGGAGGAGACCCGGGCGTCGGTGGACAGGGTGTGCAGACACAACTACGAGGTGTCGTACCGCGGGATC",
        "TACCAGTTTAAGGGCATGTGCTACTTCACCAACGGGACGGAGCGCGTGCGTGTTGTGACGAGATACATCTATAACCGAGAGGAGTACGCGCGCTTCGACAGCGACGTGGGGGTGTATCAGGCGGTGACGCCGCTGGGGCCGCCTGACGCCGACTACTGGAACAGCCAGAAGGAAGCCTGGAGGAGACCCGGGCGTCGGTGGACAGGGTGTGCAGACACAACTACCAGTTGGAGCTCCTCACGACC",
        "CCAAGTATTAGCTAACCCATCAATAATTATCATGTATGTCGTGCATTACTGCCAGACACCATGAATAATGCACAGTACTACAAATGTCCAACCACCTGTAACACATACAACCCCCCCCCTCACTGCTCCACCCAACGGAATACCAACCAATCCATCCCTCACAAAAAGTACATAAACATAAAGTCATTTATCGTACATAGCACATTCCAGTTAAATCATCCTCGCCCCCACGGATGCCCCCCCTCAGATA",
        "ATGGCGGTTTTGTGGAATAGAAAAGGGGGCAAGGTGGGGAAAAGATTGAGAAATCGGAAGGTTGCTGTGTCTGTGTAGAAAGAAGTAGACATGGGAGACTTTTCATTTTGTTCTGTACTAAGAAAAATTCTTCTGCCTTGGGATCCTGTTGATCTATGACCTTACCCCCAACCCTGTGCTCTCTGAAACATGTGTTGTGTCCACTCAGGGTTAAATGGATTAAGGGCGGTGCAAGATGTGCTTTGTTAAACAGATGCTTGAAGGCAGCATGCTCGTTAAGAGTCATCACCACTCCCTAATCTCAAGTACCCAGGGACACAAACACTGCGGAAGGCTGCAGGGTCCTCTGCCTAGGAAAACCAGAGACCTTTGTTCACTTGTTTATCTGCTGACCTTCCCTCCACTACTGTCCTATGACCCTGCCACATCCCCCTCTGCG",
        "ATGGCGGTTTTGTGGAATAGAAAAGGGGGCAAGGTGGGGAAAAGATTGAGAAATCGGAAGGTTGCTGTGTCTGTGTAGAAAGAAGTAGATATGGGAGACTTTTCATTTTGTTCTGTACTAAGAAAAATTCTTCTGCCTTGGGATCCTGTTGATCTATGACCTTACCCCCAACCCTGTGCTCTCTGAAACATGTGCTGTGTCCACTCAGGGTTAAATGGATTAAGGGCGGTGCAAGATGTGCTTTGTTAAACAGATGCTTGAAGGCAGCATGCTCCTTAAGAGTCATCACCACTCCCTAATCTCAAGTACCCATGGACACAAACACTCTGCCTAGGAAAACCAGAGACCTTTGTTCACTTGTTTGTCTGCTGACCTTCCCTCCACTACTGTCCTATGACCCTGCCAAATCCCCCTCTGCG"
    );

    # Função para executar o algoritmo de Needleman-Wunsch
    sub needleman_wunsch {
        my ($seq1, $seq2, $match_score, $mismatch_score, $gap_penalty) = @_;
        $match_score = 1 unless defined $match_score;
        $mismatch_score = -1 unless defined $mismatch_score;
        $gap_penalty = -1 unless defined $gap_penalty;
        my $start_time = time;
        my $len1 = length($seq1);
        my $len2 = length($seq2);

        # Inicialização da matriz de pontuações
        my @score = map { [(0) x ($len2 + 1)] } 0..$len1;

        # Inicialização das penalidades para gaps
        for my $i (1..$len1) {
            $score[$i][0] = $gap_penalty * $i;
        }
        for my $j (1..$len2) {
            $score[0][$j] = $gap_penalty * $j;
        }

        # Preenchimento da matriz de pontuações
        for my $i (1..$len1) {
            for my $j (1..$len2) {
                my $match = $score[$i-1][$j-1] + ($seq1 =~ /$seq2/ ? $match_score : $mismatch_score);
                my $delete = $score[$i-1][$j] + $gap_penalty;
                my $insert = $score[$i][$j-1] + $gap_penalty;
                $score[$i][$j] = max($match, $delete, $insert);
            }
        }

        # Recuperação do alinhamento
        my ($alignment1, $alignment2, $num_gaps) = ('', '', 0);
        my ($i, $j) = ($len1, $len2);
        while ($i > 0 || $j > 0) {
            if ($i > 0 && $j > 0 && $score[$i][$j] == $score[$i-1][$j-1] + ($seq1 =~ /$seq2/ ? $match_score : $mismatch_score)) {
                $alignment1 = substr($seq1, $i-1, 1) . $alignment1;
                $alignment2 = substr($seq2, $j-1, 1) . $alignment2;
                $i--; $j--;
            } elsif ($i > 0 && $score[$i][$j] == $score[$i-1][$j] + $gap_penalty) {
                $alignment1 = substr($seq1, $i-1, 1) . $alignment1;
                $alignment2 = '-' . $alignment2;
                $num_gaps++;
                $i--;
            } else {
                $alignment1 = '-' . $alignment1;
                $alignment2 = substr($seq2, $j-1, 1) . $alignment2;
                $num_gaps++;
                $j--;
            }
        }

        my $end_time = time;
        my $execution_time = $end_time - $start_time;

        return ($alignment1, $alignment2, $score[$len1][$len2], $num_gaps, $execution_time);
    }

    # Função para executar o algoritmo de Smith-Waterman ajustado
    sub smith_waterman {
        my ($seq1, $seq2, $match_score, $mismatch_score, $gap_penalty) = @_;
        $match_score = 1 unless defined $match_score;
        $mismatch_score = -1 unless defined $mismatch_score;
        $gap_penalty = -1 unless defined $gap_penalty;
        my $start_time = time;
        my $len1 = length($seq1);
        my $len2 = length($seq2);

        # Inicialização da matriz de pontuações e da matriz de trilhas
        my @score = map { [(0) x ($len2 + 1)] } 0..$len1;
        my @traceback = map { [([]) x ($len2 + 1)] } 0..$len1;
        my ($max_score, $max_position) = (0, undef);

        # Preenchimento da matriz de pontuações
        for my $i (1..$len1) {
            for my $j (1..$len2) {
                my $match = $score[$i-1][$j-1] + ($seq1 =~ /$seq2/ ? $match_score : $mismatch_score);
                my $delete = $score[$i-1][$j] + $gap_penalty;
                my $insert = $score[$i][$j-1] + $gap_penalty;
                my $best_score = max($match, $delete, $insert, 0);
                $score[$i][$j] = $best_score;

                # Rastreando os caminhos que levam ao melhor score
                if ($best_score == 0) {
                    next;
                }
                if ($best_score == $match) {
                    push @{$traceback[$i][$j]}, [$i-1, $j-1];
                }
                if ($best_score == $delete) {
                    push @{$traceback[$i][$j]}, [$i-1, $j];
                }
                if ($best_score == $insert) {
                    push @{$traceback[$i][$j]}, [$i, $j-1];
                }

                if ($best_score > $max_score) {
                    $max_score = $best_score;
                    $max_position = [$i, $j];
                }
            }
        }

        # Função para traçar o caminho de volta a partir da posição de maior valor
        sub traceback_alignment {
            my ($i, $j, $seq1, $seq2, $score_ref, $traceback_ref) = @_;
            my ($align1, $align2, $gaps) = ("", "", 0);
            my @score = @$score_ref;
            my @traceback = @$traceback_ref;
            while ($score[$i][$j] != 0) {
                if (@{$traceback[$i][$j]} == 1 && $traceback[$i][$j][0]->[0] == $i-1 && $traceback[$i][$j][0]->[1] == $j-1) {
                    $align1 = substr($seq1, $i-1, 1) . $align1;
                    $align2 = substr($seq2, $j-1, 1) . $align2;
                    ($i, $j) = ($i-1, $j-1);
                } elsif (@{$traceback[$i][$j]} == 1 && $traceback[$i][$j][0]->[0] == $i-1) {
                    $align1 = substr($seq1, $i-1, 1) . $align1;
                    $align2 = "-" . $align2;
                    $gaps++;
                    $i--;
                } elsif (@{$traceback[$i][$j]} == 1 && $traceback[$i][$j][0]->[1] == $j-1) {
                    $align1 = "-" . $align1;
                    $align2 = substr($seq2, $j-1, 1) . $align2;
                    $gaps++;
                    $j--;
                }
            }
            return ($align1, $align2, $gaps);
        }

        # Recuperar o alinhamento a partir da posição de maior valor
        my ($alignment1, $alignment2, $num_gaps) = $max_position ? traceback_alignment($max_position->[0], $max_position->[1], $seq1, $seq2, \@score, \@traceback) : ("", "", 0);

        my $end_time = time;
        my $execution_time = $end_time - $start_time;

        return ([$alignment1], [$alignment2], $max_score, [$num_gaps], $execution_time);
    }

    # Função para escolher o melhor alinhamento com base no número de gaps, alinhamento e tempo de execução
    sub choose_best_alignment {
        my (@results) = @_;
        my ($best_needleman, $best_smith);

        for my $result (@results) {
            my ($needleman_result, $smith_result) = @$result;

            if (!defined $best_needleman ||
                $needleman_result->[4] < $best_needleman->[4] ||
                ($needleman_result->[4] == $best_needleman->[4] && $needleman_result->[3] > $best_needleman->[3]) ||
                ($needleman_result->[4] == $best_needleman->[4] && $needleman_result->[3] == $best_needleman->[3] && $needleman_result->[5] < $best_needleman->[5])) {
                $best_needleman = $needleman_result;
            }

            for my $i (0 .. $#{ $smith_result->[1] }) {
                if (!defined $best_smith ||
                    $smith_result->[4][$i] < $best_smith->[4] ||
                    ($smith_result->[4][$i] == $best_smith->[4] && $smith_result->[3] > $best_smith->[3]) ||
                    ($smith_result->[4][$i] == $best_smith->[4] && $smith_result->[3] == $best_smith->[3] && $smith_result->[5] < $best_smith->[5])) {
                    $best_smith = [$smith_result->[0], $smith_result->[1][$i], $smith_result->[2][$i], $smith_result->[3], $smith_result->[4][$i], $smith_result->[5]];
                }
            }
        }

        return ($best_needleman, $best_smith);
    }

    # Função para escolher o melhor alinhamento entre macaco e gorila para cada algoritmo
    sub select_best_overall {
        my ($needleman_macaco, $smith_macaco, $needleman_gorila, $smith_gorila) = @_;

        my $best_needleman = ($needleman_macaco->[4] < $needleman_gorila->[4] ||
            ($needleman_macaco->[4] == $needleman_gorila->[4] && $needleman_macaco->[3] > $needleman_gorila->[3]) ||
            ($needleman_macaco->[4] == $needleman_gorila->[4] && $needleman_macaco->[3] == $needleman_gorila->[3] && $needleman_macaco->[5] < $needleman_gorila->[5])) ?
            $needleman_macaco : $needleman_gorila;

        my $best_smith = ($smith_macaco->[4] < $smith_gorila->[4] ||
            ($smith_macaco->[4] == $smith_gorila->[4] && $smith_macaco->[3] > $smith_gorila->[3]) ||
            ($smith_macaco->[4] == $smith_gorila->[4] && $smith_macaco->[3] == $smith_gorila->[3] && $smith_macaco->[5] < $smith_gorila->[5])) ?
            $smith_macaco : $smith_gorila;

        return ($best_needleman, $best_smith);
    }

    my @macaco_results;
    my @gorila_results;

    # Executa o algoritmo de Needleman-Wunsch e Smith-Waterman para sequências de macaco
    foreach my $seq (@macaco) {
        my ($alignment1, $alignment2, $alignment_score_n, $num_gaps, $execution_time_n) = needleman_wunsch($seq, $seq2);
        my ($alignments1, $alignments2, $alignment_score_s, $smith_gaps, $execution_time_s) = smith_waterman($seq, $seq2);
        push @macaco_results, [['needleman', $alignment1, $alignment2, $alignment_score_n, $num_gaps, $execution_time_n], ['smith', $alignments1, $alignments2, $alignment_score_s, $smith_gaps, $execution_time_s]];
    }

    # Executa o algoritmo de Needleman-Wunsch e Smith-Waterman para sequências de gorila
    foreach my $seq (@gorila) {
        my ($alignment1, $alignment2, $alignment_score_n, $num_gaps, $execution_time_n) = needleman_wunsch($seq, $seq2);
        my ($alignments1, $alignments2, $alignment_score_s, $smith_gaps, $execution_time_s) = smith_waterman($seq, $seq2);
        push @gorila_results, [['needleman', $alignment1, $alignment2, $alignment_score_n, $num_gaps, $execution_time_n], ['smith', $alignments1, $alignments2, $alignment_score_s, $smith_gaps, $execution_time_s]];
    }

    # Escolhe o melhor alinhamento para cada algoritmo
    my ($best_needleman_macaco, $best_smith_macaco) = choose_best_alignment(@macaco_results);
    my ($best_needleman_gorila, $best_smith_gorila) = choose_best_alignment(@gorila_results);

    # Seleciona o melhor alinhamento entre macaco e gorila para cada algoritmo
    my ($best_needleman, $best_smith) = select_best_overall($best_needleman_macaco, $best_smith_macaco, $best_needleman_gorila, $best_smith_gorila);

    # Enviar o melhor resultado para o servidor
    my $response_message = sprintf("Perl;Needleman;AlignmentScore:%d;Gap:%d;ExecutionTime:%.6f;Smith;AlignmentScore:%d;Gap:%d;ExecutionTime:%.6f",
        $best_needleman->[3], $best_needleman->[4], $best_needleman->[5],
        $best_smith->[3], $best_smith->[4], $best_smith->[5]
    );
    $client_socket->send($response_message);
    print "$response_message\n";
    print "Response sent to server\n";

    close($client_socket);
}

start_client();
