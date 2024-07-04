use strict;
use warnings;
use Time::HiRes qw(time);
use List::Util qw(max);

sub needleman_wunsch {
    my ($seq1, $seq2, $match, $mismatch, $gap) = @_;
    $match = defined $match ? $match : 1;
    $mismatch = defined $mismatch ? $mismatch : -1;
    $gap = defined $gap ? $gap : -1;

    my $start_time = time();
    my $len_seq1 = length($seq1);
    my $len_seq2 = length($seq2);

    my @score_matrix;
    for my $i (0 .. $len_seq1) {
        $score_matrix[$i][0] = $gap * $i;
    }
    for my $j (0 .. $len_seq2) {
        $score_matrix[0][$j] = $gap * $j;
    }

    for my $i (1 .. $len_seq1) {
        for my $j (1 .. $len_seq2) {
            my $seq1_char = substr($seq1, $i - 1, 1);
            my $seq2_char = substr($seq2, $j - 1, 1);
            my $match_score = $score_matrix[$i - 1][$j - 1] + ($seq1_char eq $seq2_char ? $match : $mismatch);
            my $delete = $score_matrix[$i - 1][$j] + $gap;
            my $insert = $score_matrix[$i][$j - 1] + $gap;
            $score_matrix[$i][$j] = max($match_score, $delete, $insert);
        }
    }

    my ($align1, $align2) = ('', '');
    my ($i, $j) = ($len_seq1, $len_seq2);
    while ($i > 0 || $j > 0) {
        my $current_score = $score_matrix[$i][$j];
        if ($i > 0 && $j > 0 && $current_score == $score_matrix[$i - 1][$j - 1] + (substr($seq1, $i - 1, 1) eq substr($seq2, $j - 1, 1) ? $match : $mismatch)) {
            $align1 = substr($seq1, $i - 1, 1) . $align1;
            $align2 = substr($seq2, $j - 1, 1) . $align2;
            $i--; $j--;
        } elsif ($i > 0 && $current_score == $score_matrix[$i - 1][$j] + $gap) {
            $align1 = substr($seq1, $i - 1, 1) . $align1;
            $align2 = '-' . $align2;
            $i--;
        } else {
            $align1 = '-' . $align1;
            $align2 = substr($seq2, $j - 1, 1) . $align2;
            $j--;
        }
    }

    my $gaps = ($align1 =~ tr/-//) + ($align2 =~ tr/-//);
    my $score = $score_matrix[$len_seq1][$len_seq2];
    my $time_taken = time() - $start_time;

    return ($align1, $align2, $gaps, $score, $time_taken);
}

sub smith_waterman {
    my ($seq1, $seq2, $match, $mismatch, $gap) = @_;
    $match = defined $match ? $match : 1;
    $mismatch = defined $mismatch ? $mismatch : -1;
    $gap = defined $gap ? $gap : -1;

    my $start_time = time();
    my $len_seq1 = length($seq1);
    my $len_seq2 = length($seq2);

    my @score_matrix;
    my $max_score = 0;
    my $max_pos = [0, 0];
    for my $i (0 .. $len_seq1) {
        for my $j (0 .. $len_seq2) {
            $score_matrix[$i][$j] = 0;
        }
    }

    for my $i (1 .. $len_seq1) {
        for my $j (1 .. $len_seq2) {
            my $seq1_char = substr($seq1, $i - 1, 1);
            my $seq2_char = substr($seq2, $j - 1, 1);
            my $match_score = $score_matrix[$i - 1][$j - 1] + ($seq1_char eq $seq2_char ? $match : $mismatch);
            my $delete = $score_matrix[$i - 1][$j] + $gap;
            my $insert = $score_matrix[$i][$j - 1] + $gap;
            $score_matrix[$i][$j] = max(0, $match_score, $delete, $insert);
            if ($score_matrix[$i][$j] >= $max_score) {
                $max_score = $score_matrix[$i][$j];
                $max_pos = [$i, $j];
            }
        }
    }

    my ($align1, $align2) = ('', '');
    my ($i, $j) = @$max_pos;
    while ($score_matrix[$i][$j] != 0) {
        my $current_score = $score_matrix[$i][$j];
        if ($i > 0 && $j > 0 && $current_score == $score_matrix[$i - 1][$j - 1] + (substr($seq1, $i - 1, 1) eq substr($seq2, $j - 1, 1) ? $match : $mismatch)) {
            $align1 = substr($seq1, $i - 1, 1) . $align1;
            $align2 = substr($seq2, $j - 1, 1) . $align2;
            $i--; $j--;
        } elsif ($i > 0 && $current_score == $score_matrix[$i - 1][$j] + $gap) {
            $align1 = substr($seq1, $i - 1, 1) . $align1;
            $align2 = '-' . $align2;
            $i--;
        } else {
            $align1 = '-' . $align1;
            $align2 = substr($seq2, $j - 1, 1) . $align2;
            $j--;
        }
    }

    my $gaps = ($align1 =~ tr/-//) + ($align2 =~ tr/-//);
    my $score = $max_score;
    my $time_taken = time() - $start_time;

    return ($align1, $align2, $gaps, $score, $time_taken);
}

my $seq1 = "TGGCTCCTCGGAAACCCAATGTGCGACGAATTCATCAGCGTGCCGGAATGGTCTTACATAGTGGAGAGGGCTAATCCAGCTAATGACCTCTGTTACCCAGGGAGCCTCAATGACTATGAAGAACTGAAACACCTATTGAGCAGAATAAATCATTTTGAGAAGATTCTGATCATCCCCAAGAGTTCTTGGCCCAATCATGAAACATCATTAGGGGTGAGCGCAGCTTGTCCATACCAGGGAACACCCTCCTTTTTCAGAAATGTGGTGTGGCTTATCAAAAAGAACGATGCATACCCAACAATAAAGATAAGCTACAATAACACCAATCGGGAAGATCTTTTGATACTGTGGGGGATTCATCATTCCAACAATGCAGAAGAGCAGATAAATCTCTATAAAAACCCAACCACCTATATTTCAGTTGGAACATCAACTTTAAACCAGAGATTGGTACCAAAAATAGCTACCAGATCCCAAGTAAACGGG";
my $seq2 = "TATGATAAGAAGCTTGTTTCGCGCATTCAAATTCGAGTTAATCCTTTGCCGAAATTTGATTCTACCGTGTGGGTGACAGTCCGCAAAGTTCCTGCCTCATCGGACTTATCCGTTACCGCCATCTCTGCTATGTTCGCGGACGGAGCCTCACCGGTACTGGTTTATCAGTATGCAGCATCCGGAGTCCAAGCCAACAATAAATTGTTGTATGATCTTTCGGCGATGCGCGCTGATATTGGTGACATGAGAAAGTACGCCGTGCTCGTGTATTCAAAAGACGATGCGCTCGAGACGGACGAATTGGTACTTCATGTTGACATTGAGCACCAACGCATTCCCACATCTGGGGTGCTCCCAGTTTGAACCTGTGTTTTCCAGAACCCTCCCTCCGATTTCTGTGGCGGGAGCTGAGTTGGTAGTGTTGCTATAAACTACCTGAAGTCACTAAACGCTATGCGGTGAACGGGTTGTCCATCCAGCTTACGGC";

my ($nw_align1, $nw_align2, $nw_gaps, $nw_score, $nw_time) = needleman_wunsch($seq1, $seq2);
print "Needleman-Wunsch Alignment:\n$nw_align1\n$nw_align2\nGaps: $nw_gaps, Score: $nw_score, Time: $nw_time seconds\n";
my ($sw_align1, $sw_align2, $sw_gaps, $sw_score, $sw_time) = smith_waterman($seq1, $seq2);
print "Smith-Waterman Alignment:\n$sw_align1\n$sw_align2\nGaps: $sw_gaps, Score: $sw_score, Time: $sw_time seconds\n";
my $results = "Perl;Needleman;Alignment1:$nw_align1;Alignment2:$nw_align2;AlignmentScore:$nw_score;Gap:$nw_gaps;ExecutionTime:$nw_time;Smith;Alignment1:$sw_align1;Alignment2:$sw_align2;AlignmentScore:$sw_score;Gap:$sw_gaps;ExecutionTime:$sw_time";
#print $results;
