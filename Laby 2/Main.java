package org.example;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

class TaskQueue {
    private LinkedList<Task> tasks = new LinkedList<>();

    synchronized void addTask(Task liczba) {
        tasks.add(liczba);
        notify();
    }

    synchronized Task getTask() throws InterruptedException {
        while (tasks.isEmpty()) {
            wait();
        }
        return tasks.removeFirst();
    }
}

class Task {
    private int number;

    Task(int number) {
        this.number = number;
    }

    Result execute() {
        boolean prime = isPrimeNumber(number);
        return new Result(prime,this.number);
    }

    private boolean isPrimeNumber(int n) {
        if (n <= 1) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}
class Result {
    private boolean resultData;
    private int liczba;
    Result(boolean resultData,int liczba) {
        this.resultData = resultData;
        this.liczba = liczba;
    }
    public Integer getNumber(){
        return this.liczba;
    }
    
    boolean getResultData() {
        return resultData;
    }
}
class ResultCollector {
    private List<Result> results = new ArrayList<>();

    synchronized void addResult(Result result) {
        results.add(result);
    }

    synchronized List<Result> getResults() {
        return new ArrayList<>(results);
    }
}
class CalculationThread extends Thread {
    private TaskQueue taskQueue;
    private ResultCollector resultCollector;

    CalculationThread(TaskQueue taskQueue, ResultCollector resultCollector) {
        this.taskQueue = taskQueue;
        this.resultCollector = resultCollector;
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Task task = taskQueue.getTask();
                Result result = task.execute();
                resultCollector.addResult(result);
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TaskQueue taskQueue = new TaskQueue();
        ResultCollector resultCollector = new ResultCollector();
        int nthreads = scanner.nextInt();
        CalculationThread[] threads = new CalculationThread[nthreads];
        for (int i = 0; i < nthreads; i++) {
            threads[i] = new CalculationThread(taskQueue, resultCollector);
            threads[i].start();
        }
        boolean running = true;
        while (running) {
            System.out.println("Podaj liczbê do sprawdzenia (lub wpisz 'e' aby wyjœæ lub 'w' aby wyswietlic wynik):");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("e")) {
                running = false;
            }
            else if (input.equalsIgnoreCase("w"))
            {
                List<Result> results = resultCollector.getResults();
                for (Result result : results) {
                    System.out.println(result.getNumber() + "  " + result.getResultData());
                }
                results.clear();

            }else {
                try {
                    int number = Integer.parseInt(input);
                    Task task = new Task(number);
                    taskQueue.addTask(task);
                } catch (NumberFormatException e) {
                    System.out.println("Niepoprawny format liczby!");
                }
            }
        }

        for (CalculationThread thread : threads) {
            thread.interrupt();
        }
    }
}
