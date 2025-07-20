package MainFolder;

import java.util.concurrent.CompletableFuture;

public class Main2 {
    public static void main(String[] args) {
        CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> {
            try {
                task("Module 1");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        CompletableFuture<Void> f2 = CompletableFuture.runAsync(() -> {
            try {
                task("Module 2");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        CompletableFuture<Void> f3 = CompletableFuture.runAsync(() -> {
            try {
                task("Module 3");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<Void> allDone = CompletableFuture.allOf(f1, f2, f3);

        allDone.thenRun(() -> System.out.println("All modules are done!"));

    }

    private static void task(String s) throws InterruptedException {
        Thread.sleep(5000);
        System.out.println(s);
    }
}



