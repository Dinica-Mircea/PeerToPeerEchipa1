import business.ChatApplication;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        try {
            ChatApplication chatApplication = new ChatApplication();
            executorService.submit(chatApplication::runServer);
            sleep(10);
            executorService.submit(chatApplication::runClient);
            executorService.shutdown();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}