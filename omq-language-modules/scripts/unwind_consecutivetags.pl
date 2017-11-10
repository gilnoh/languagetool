# A small perl script that unwinds multiple POS tags attached to a single entry 
# into multiple entries where each has single POS tag.  
# 
# For example: 
# A line from TR dictionary (separator "^DB"): 
# #  ÇÖZÜLMEZ	çöz	Verb^DB:Verb:Pass:Neg:Aor:A3sg
# will be rewritten into: 
# #  ÇÖZÜLMEZ	çöz	Verb
# #  ÇÖZÜLMEZ	çöz	Verb:Pass:Neg:Aor:A3sg
#
# A line from CS dictionary (separator "+"): 
# # Abdéritovo	Abdéritův	k2eAgNnSc1d1+k2eAgNnSc4d1+k2eAgNnSc5d1
# will be rewritten into:
# # Abdéritovo	Abdéritův	k2eAgNnSc1d1
# # Abdéritovo	Abdéritův       k2eAgNnSc4d1
# # Abdéritovo	Abdéritův	k2eAgNnSc5d1
#
# NOTE:
# - this script does NOT remove any duplicated lines, that might be generated
#   (e.g. turkish case) 
####
# usage:
#   perl unwind_tags.pl [separator as regex] < dictionary.txt > unwinded_dictionary.txt
# example:
#   perl unwind_tags.pl "\+" < cs.dict.txt > output.txt 
#   perl unwind_tags.pl "\^DB" < tr.dict.txt > output.txt 
####

use strict;

# argument check / usage output 
if (!@ARGV) {
    # no argument given,
    print "usage:\n\tperl unwind.pl [separator regex] < dict.txt > unwinded_dict.txt\n";
    print "example:\n\tperl unwind.pl \"\+\" < cs.dict.txt > output.txt\n\tperl unwind.pl \"\^DB\" < tr.dict.txt > output.txt \n";
    exit; 
}
my $separator = $ARGV[0];

# read from STDIN and output to STDOUT
# work for each line to finish off 
while(my $line = <STDIN>) {
    chomp $line; # remove \n at the end of the line 
    # split word, lemma and pos
    my ($word, $lemma, $pos) = split /\t/, $line;
    # separate pos tags 
    my @tags = split /$separator/, $pos;
    # write to STDOUT, single-tag-per-line
    foreach(@tags) {
	print $word, "\t", $lemma, "\t", $_, "\n"; 
    }
    # done for current input line 
}
# done all lines 
