package de.omq;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    private static final int EXAMPLE_LIMIT = 15;
    private static final String TMP_FOLDER = "tmp/";
    private static final String OUTPUT_FOLDER = "output/";

    public static class Line {
        public String word;
        public String lemma;
        public String pos;

        public static Line get(String[] lineArray, int lineNumber) {
            Line line = new Line();

            if(lineArray.length == 3) {
                line.word = lineArray[0];
                line.lemma = lineArray[1];
                line.pos = lineArray[2];

                return line;
            }
            else {
                System.out.println("Error in line [" + lineNumber +"]: " + Arrays.toString(lineArray));
                return null;
            }
        }

        public static Line get(String word, String lemma, String pos) {
            Line line = new Line();
            line.word = word;
            line.lemma = lemma;
            line.pos = pos;

            return line;
        }
    }

    protected static Map<String, List<Line>> group(List<String> lines) {
        // group by pos
        return lines.stream()
                .map(x -> Line.get(x.split("\t"), 0))
                .filter(x -> x != null)
                .collect(Collectors.groupingBy(
                        x -> x.pos,
                        Collector.of(
                                ArrayList<Line>::new,
                                (x, y) -> {
                                    if (x.size() < EXAMPLE_LIMIT) x.add(y);
                                },
                                (x, y) -> {
                                    if (x.size() < EXAMPLE_LIMIT) x.addAll(y);
                                    return x;
                                })));
    }

    protected static Map<String, List<Line>> group(List<String> lines, String separator) {
        // group by pos
        return lines.stream()
                .map(x -> Line.get(x.split("\t"), 0))
                .map(x -> Arrays.stream(x.pos.split(separator))
         //       .map(x -> Arrays.stream(x.pos.split("\\+"))
                        .map(y -> Line.get(x.word, x.lemma, y))
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .filter(x -> x != null)
                .collect(Collectors.groupingBy(
                        x -> x.pos,
                        Collector.of(
                                ArrayList<Line>::new,
                                (x, y) -> {
                                    if (x.size() < EXAMPLE_LIMIT) x.add(y);
                                },
                                (x, y) -> {
                                    if (x.size() < EXAMPLE_LIMIT) x.addAll(y);
                                    return x;
                                })));
    }

    protected static void convert(Path input, Path output, Optional<String> separator) throws IOException {
        // group by pos
        List<String> lines = Files.lines(input).collect(Collectors.toList());
        Map<String, List<Line>> sortedByPos = separator
                .map(x -> group(lines, x))
                .orElse(group(lines));
    /*
        Map<String, List<Line>> sortedByPos = Files.lines(input)
                .map(x -> Line.get(x.split("\t"), 0))

                // add for pl
                .map(x -> Arrays.stream(x.pos.split("\\+"))
                        .map(y -> Line.get(x.word, x.lemma, y))
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)

                .filter(x -> x != null)
                .collect(Collectors.groupingBy(
                        x -> x.pos,
                        Collector.of(
                                ArrayList<Line>::new,
                                (x, y) -> {
                                    if (x.size() < EXAMPLE_LIMIT) x.add(y);
                                },
                                (x, y) -> {
                                    if (x.size() < EXAMPLE_LIMIT) x.addAll(y);
                                    return x;
                                })));
        */
        // output sort, println "VB:PRS = ['äcklar'->'äckla'], ['äger'->'äga']"
        List<String> outPutLines = sortedByPos.entrySet().stream()
                .sorted((x, y) -> x.getKey().compareTo(y.getKey()))
                .map(x -> x.getKey() + "=" +
                        x.getValue().stream()
                                .map(y -> "['" + y.word + "'->'" + y.lemma + "']")
                                .collect(Collectors.joining(", ")))
                .collect(Collectors.toList());

        // write to file
        Files.write(output, outPutLines);
    }

    public static void buildPosFile(String inputPath, String languageCode, Optional<String> separator) {
        try {
            // build temp file
            String tmpPath = TMP_FOLDER + languageCode + ".txt";

            // convert and save
            String outputPath = OUTPUT_FOLDER + languageCode + ".txt";
            convert(Paths.get(tmpPath), Paths.get(outputPath), separator);
        }
        catch (IOException e) {
            System.out.println("Error for input file [" + inputPath + "] with language code [" + languageCode + "].");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // 1. Die .dict Dateien werden aus den language Tool Bibliotheken extrahiert
        // 2. Die .dict Dateien werden entpackt mit
        //    java -cp ../LanguageTool-3.0/languagetool.jar org.languagetool.dev.DictionaryExporter input/uk/ukrainian.dict > tmp/uk.txt
        // 3. Die tmp/<CODE> Dateien müssen UTF-8 sein -> Diese können mit Coda konvertiert werden
        // 4. Die entstandenen Dateien werden mit diesem Programm konvertiert
        buildPosFile("EGAL", "nl", Optional.empty());
//        buildPosFile("EGAL", "da", Optional.empty());
//        buildPosFile("EGAL", "en", Optional.empty());
//        buildPosFile("EGAL", "es", Optional.empty());
//        buildPosFile("EGAL", "fr", Optional.empty());
//        buildPosFile("EGAL", "it", Optional.empty());
//        buildPosFile("EGAL", "nl", Optional.empty());
//        buildPosFile("EGAL", "pl", Optional.empty());
//        buildPosFile("EGAL", "pt", Optional.empty());
//        buildPosFile("EGAL", "ru", Optional.empty());
//        buildPosFile("EGAL", "sk", Optional.empty());
/*        buildPosFile("EGAL", "ast", Optional.empty());
        buildPosFile("EGAL", "be", Optional.empty());
        buildPosFile("EGAL", "br", Optional.empty());
        buildPosFile("EGAL", "ca", Optional.empty());
        buildPosFile("EGAL", "cs", Optional.of("\\+"));
        buildPosFile("EGAL", "da", Optional.empty());
        buildPosFile("EGAL", "de", Optional.empty());
        buildPosFile("EGAL", "el", Optional.empty());
        buildPosFile("EGAL", "en", Optional.empty());
        buildPosFile("EGAL", "es", Optional.empty());
        buildPosFile("EGAL", "fr", Optional.empty());
        buildPosFile("EGAL", "gl", Optional.empty());
        buildPosFile("EGAL", "it", Optional.empty());
        buildPosFile("EGAL", "km", Optional.empty());
        buildPosFile("EGAL", "ml", Optional.empty());
        buildPosFile("EGAL", "nl", Optional.empty());
        buildPosFile("EGAL", "pl", Optional.of("\\+"));
        buildPosFile("EGAL", "pt", Optional.empty());
        buildPosFile("EGAL", "ro", Optional.empty());
        buildPosFile("EGAL", "ru", Optional.empty());
        buildPosFile("EGAL", "sk", Optional.empty());
        buildPosFile("EGAL", "sl", Optional.empty());
        buildPosFile("EGAL", "sv", Optional.empty());
        buildPosFile("EGAL", "ta", Optional.empty());
        buildPosFile("EGAL", "tl", Optional.empty());
        buildPosFile("EGAL", "tr", Optional.of("\\^DB:"));
        buildPosFile("EGAL", "uk", Optional.of(":&"));    */
    }
}
