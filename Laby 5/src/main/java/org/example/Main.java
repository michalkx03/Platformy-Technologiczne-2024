package org.example;
import java.util.*;
public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        MageRepository repository = new MageRepository();
        MageController controller = new MageController(repository);

        label:
        while (true) {
            String[] com = scanner.nextLine().split(" ");
            switch (com[0]) {
                case "f":
                    System.out.println(controller.find(com[1]));
                    break;
                case "d":
                    System.out.println(controller.delete(com[1]));
                    break;
                case "s":
                    System.out.println(controller.save(com[1], Integer.parseInt(com[2])));
                    break;
                case "e":
                    break label;
            }
        }

        scanner.close();
    }
}