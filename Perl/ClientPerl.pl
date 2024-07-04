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

    my $sequence = '';
    $client_socket->recv($sequence, 1024);
    print "Sequence received from server: $sequence\n";

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

    sub needleman_wunsch {
        my ($seq1, $seq2, $match_score, $mismatch_score, $gap_penalty) = @_;
        $match_score = 1 unless defined $match_score;
        $mismatch_score = -1 unless defined $mismatch_score;
        $gap_penalty = -1 unless defined $gap_penalty;
        my $start_time = time;
        my $len1 = length($seq1);
        my $len2 = length($seq2);

        my @score = map { [(0) x ($len2 + 1)] } 0..$len1;

        for my $i (1..$len1) {
            $score[$i][0] = $gap_penalty * $i;
        }
        for my $j (1..$len2) {
            $score[0][$j] = $gap_penalty * $j;
        }

        for my $i (1..$len1) {
            for my $j (1..$len2) {
                my $match = $score[$i-1][$j-1] + ($seq1 =~ /$seq2/ ? $match_score : $mismatch_score);
                my $delete = $score[$i-1][$j] + $gap_penalty;
                my $insert = $score[$i][$j-1] + $gap_penalty;
                $score[$i][$j] = max($match, $delete, $insert);
            }
        }

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

    sub smith_waterman {
        my ($seq1, $seq2, $match_score, $mismatch_score, $gap_penalty) = @_;
        $match_score = 1 unless defined $match_score;
        $mismatch_score = -1 unless defined $mismatch_score;
        $gap_penalty = -1 unless defined $gap_penalty;
        my $start_time = time;
        my $len1 = length($seq1);
        my $len2 = length($seq2);

        my @score = map { [(0) x ($len2 + 1)] } 0..$len1;
        my ($max_score, $max_i, $max_j) = (0, 0, 0);

        for my $i (1..$len1) {
            for my $j (1..$len2) {
                my $match = $score[$i-1][$j-1] + (substr($seq1, $i-1, 1) eq substr($seq2, $j-1, 1) ? $match_score : $mismatch_score);
                my $delete = $score[$i-1][$j] + $gap_penalty;
                my $insert = $score[$i][$j-1] + $gap_penalty;
                $score[$i][$j] = max($match, $delete, $insert, 0);

                if ($score[$i][$j] > $max_score) {
                    $max_score = $score[$i][$j];
                    $max_i = $i;
                    $max_j = $j;
                }
            }
        }

        my ($alignment1, $alignment2, $num_gaps) = ('', '', 0);
        my ($i, $j) = ($max_i, $max_j);
        while ($i > 0 && $j > 0 && $score[$i][$j] > 0) {
            if ($i > 0 && $j > 0 && $score[$i][$j] == $score[$i-1][$j-1] + (substr($seq1, $i-1, 1) eq substr($seq2, $j-1, 1) ? $match_score : $mismatch_score)) {
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

        return ([$alignment1], [$alignment2], $max_score, [$num_gaps], $execution_time);
    }

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

    foreach my $seq (@macaco) {
        my ($alignment1, $alignment2, $alignment_score_n, $num_gaps, $execution_time_n) = needleman_wunsch($seq, $seq2);
        my ($alignments1, $alignments2, $alignment_score_s, $smith_gaps, $execution_time_s) = smith_waterman($seq, $seq2);
        push @macaco_results, [['needleman', $alignment1, $alignment2, $alignment_score_n, $num_gaps, $execution_time_n], ['smith', $alignments1, $alignments2, $alignment_score_s, $smith_gaps, $execution_time_s]];
    }

    foreach my $seq (@gorila) {
        my ($alignment1, $alignment2, $alignment_score_n, $num_gaps, $execution_time_n) = needleman_wunsch($seq, $seq2);
        my ($alignments1, $alignments2, $alignment_score_s, $smith_gaps, $execution_time_s) = smith_waterman($seq, $seq2);
        push @gorila_results, [['needleman', $alignment1, $alignment2, $alignment_score_n, $num_gaps, $execution_time_n], ['smith', $alignments1, $alignments2, $alignment_score_s, $smith_gaps, $execution_time_s]];
    }

    my ($best_needleman_macaco, $best_smith_macaco) = choose_best_alignment(@macaco_results);
    my ($best_needleman_gorila, $best_smith_gorila) = choose_best_alignment(@gorila_results);

    my ($best_needleman, $best_smith) = select_best_overall($best_needleman_macaco, $best_smith_macaco, $best_needleman_gorila, $best_smith_gorila);

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
