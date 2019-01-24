package concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestFutureTask {
    public static void main(String[] args) {
        synchronized (TestFutureTask.class){
            ExecutorService executor = Executors.newCachedThreadPool();
            Task task = new Task();
            Future<Integer> result = executor.submit(task);
            executor.shutdown();

//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e1) {
//            e1.printStackTrace();
//        }

            System.out.println("主线程在执行任务");
            boolean sucesses = result.cancel(true);
            System.out.println("取消任务："+ sucesses);
            try {
                int res =  result.get();
                System.out.println("task运行结果" + res);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("所有任务执行完毕");
        }

    }
}

class Task implements Callable<Integer> {
    public Integer call() throws Exception {
        System.out.println("子线程在进行计算");
        int sum = 0;
        for (int i = 0; i < 1000000; i++){
            sum += i;

            if(i % 1000 == 0){
                System.out.print(i);
                if(i % 100000 == 0){
                    System.out.println();
                }
            }
        }
        return sum;
    }
}
