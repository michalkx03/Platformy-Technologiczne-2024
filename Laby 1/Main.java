package org.example;

import java.util.Set;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;

class Mage implements Comparable<Mage> {
    private String name;
    private int level;
    private double power;
    private Set<Mage> apprentices;

    public Mage(String name, int level, double power) {
        this.name = name;
        this.level = level;
        this.power = power;
        this.apprentices = new TreeSet<>();
    }

    @Override
    public int compareTo(Mage other) {
        int result = this.name.compareTo(other.name);
        if (result == 0) {
            result = Integer.compare(this.level, other.level);
            if (result == 0) {
                result = Double.compare(this.power, other.power);
            }
        }
        return result;
    }

    public Set<Mage> getApprentices() {
        return apprentices;
    }

    public void addApprentice(Mage apprentice) {
        apprentices.add(apprentice);
    }

    @Override
    public String toString() {
        return "Mage{name='" + name + "', level=" + level + ", power=" + power + "}";
    }

    public static class MageLevelComparator implements Comparator<Mage> {
        @Override
        public int compare(Mage mage1, Mage mage2) {
            int result = Integer.compare(mage1.level, mage2.level);
            if (result == 0) {
                result = mage1.name.compareTo(mage2.name);
                if (result == 0) {
                    result = Double.compare(mage1.power, mage2.power);
                }
            }
            return result;
        }
    }
}
public class Main {
    public static void main(String[] args) {
        boolean sortowanie = false; // Zmienić na true, aby włączyć sortowanie

        Set<Mage> mages;
        if (sortowanie) {
            mages = new TreeSet<>(new Mage.MageLevelComparator());
        } else {
            mages = new TreeSet<>();
        }
        dodajPrzykladoweDane(mages);

        System.out.println("Zbiór mages:");
        wypiszZbior(mages);

        System.out.println("\nStatystyki liczby potomków:");
        Map<Mage, Integer> statystyki = generujStatystyki(mages, sortowanie);
        for (Map.Entry<Mage, Integer> entry : statystyki.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    private static void dodajPrzykladoweDane(Set<Mage> mages) {
        Mage mage1 = new Mage("Gandalf", 10, 100.0);
        Mage mage2 = new Mage("Merlin", 15, 150.0);
        Mage mage3 = new Mage("Dumbledore", 20, 200.0);

        mage1.getApprentices().add(new Mage("Apprentice1", 5, 50.0));
        mage1.getApprentices().add(new Mage("Apprentice2", 8, 80.0));

        mage2.getApprentices().add(new Mage("Apprentice3", 12, 120.0));

        mage3.getApprentices().add(new Mage("Apprentice4", 18, 180.0));
        mage3.getApprentices().add(new Mage("Apprentice5", 22, 220.0));

        mages.add(mage1);
        mages.add(mage2);
        mages.add(mage3);
    }

    private static void wypiszZbior(Set<Mage> mages) {
        for (Mage mage : mages) {
            System.out.println(mage.toString());
            wypiszZbior(mage.getApprentices(), 1);
        }
    }

    private static void wypiszZbior(Set<Mage> mages, int depth) {
        for (Mage mage : mages) {
            for (int i = 0; i < depth; i++) {
                System.out.print("-");
            }
            System.out.println(mage.toString());
            wypiszZbior(mage.getApprentices(), depth + 1);
        }
    }

    private static Map<Mage, Integer> generujStatystyki(Set<Mage> mages, boolean sortowanie) {
        Map<Mage, Integer> statystyki;
        if (sortowanie) {
            statystyki = new TreeMap<>();
        } else {
            statystyki = new HashMap<>();
        }

        for (Mage mage : mages) {
            generujStatystykiRekurencyjnie(mage, statystyki);
        }
        return statystyki;
    }

    private static void generujStatystykiRekurencyjnie(Mage mage, Map<Mage, Integer> statystyki) {
        statystyki.put(mage, countApprentices(mage.getApprentices()));
        for (Mage apprentice : mage.getApprentices()) {
            generujStatystykiRekurencyjnie(apprentice, statystyki);
        }
    }

    private static int countApprentices(Set<Mage> apprentices) {
        int count = 0;
        for (Mage apprentice : apprentices) {
            count++;
            count += countApprentices(apprentice.getApprentices());
        }
        return count;
    }
}
