package org.example;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import javax.imageio.*;
import java.nio.file.*;
import java.util.Scanner;
import java.util.stream.Stream;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        String dir = "/Users/micha/IdeaProjects/Laby 6/src/main/resources/zdjecia";
        String output = "/Users/micha/IdeaProjects/Laby 6/src/main/resources/wynik";
        int n_threads = scanner.nextInt();
        scanner.close();

        List<Path> files;
        Path source = Path.of(dir);
        Stream<Path> stream = Files.list(source);
        files = stream.toList();

        ForkJoinPool threads = new ForkJoinPool(n_threads);

        long time = System.currentTimeMillis();

        try {
            threads.submit(() ->
                    files.parallelStream()
                            .map(path -> {
                                try {
                                    BufferedImage image = ImageIO.read(path.toFile());
                                    return Pair.of(path.getFileName().toString(), image);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .map(pair -> {
                                BufferedImage original = pair.getRight();
                                BufferedImage newImage = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());

                                for (int i = 0; i < original.getWidth(); i++) {
                                    for (int j = 0; j < original.getHeight(); j++) {
                                        int rgb = original.getRGB(i, j);
                                        Color color = new Color(rgb);

                                        int red = 0;//color.getBlue();
                                        int green = 0;//color.getGreen();
                                        int blue = 0;//color.getRed();

                                        Color outColor = new Color(red, green, blue);
                                        newImage.setRGB(i, j, outColor.getRGB());
                                    }
                                }

                                return Pair.of(pair.getLeft(), newImage);
                            })
                            .forEach(pair -> {
                                try {
                                    ImageIO.write(pair.getRight(), "png", Path.of(output, pair.getLeft()).toFile());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            })).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Czas: " + (System.currentTimeMillis() - time) / 1000.0 + "s");

        threads.shutdown();
    }
}