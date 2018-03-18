package com.ncode;


import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


class MyThread extends Thread {

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(1000);
                System.out.println(String.format("%s:%d", Thread.currentThread().getName(), i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


public class MultiThreadTests implements Runnable {


    @Override
    public void run() {

    }

    private static int vol = 0;

    private static void testVolatile() {
        for(int i = 0; i < 20 ; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int N = 100;
                    while(N-- > 0)
                        vol = N;
                    System.out.println(vol);
                }
            }).start();
        }
    }

    private static final Object obj = new Object();

    private static void testThread() {
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (obj) {
                        for (int i = 0; i < 5; i++) {
                            try {
                                Thread.sleep(1000);
                                System.out.println(String.format("%s:%d", Thread.currentThread().getName(), i));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }).start();
        }
    }

    static Lock lock = new ReentrantLock();

    private static void testReentranLock() {
        lock.lock();
    }

    static BlockingQueue<Integer> bq = new ArrayBlockingQueue<Integer>(10);

    private static void testBlockingQueue() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 100; i++) {
                        Thread.sleep(500);
                        bq.put(i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        int j = 2;
        while (j-- >= 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int i = 0; i < 100; i++) {
                            Thread.sleep(100);
                            System.out.println(String.format("%s:%d", Thread.currentThread().getName(), bq.take()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    static ThreadLocal<String> tl = new ThreadLocal<>();
    static String name = null;

    private static void testThreadLocal() {

        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        name = Thread.currentThread().getName();
                        Thread.sleep(1000);
                        System.out.println(name);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }).start();
        }
    }


    private static void testExecutorService() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        for(int i = 0; i < 10; i++) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        System.out.println(Thread.currentThread().getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

        }

        executorService.shutdown();
        while (!executorService.isTerminated()) {
            try {
                Thread.sleep(1000);
                System.out.println("Wait for termination.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void testFuture() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<Integer> future = executorService.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Thread.sleep(1000);
                throw new IllegalArgumentException("异常");
            }
        });
        try {
            System.out.println(future.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
        executorService.shutdown();
    }

    private static AtomicInteger atomicInteger = new AtomicInteger(0);
    private static int counter = 0;

    private static void testAtomic() {
        for(int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        for(int i = 0; i < 10000; i++) {
                            counter++;
                            atomicInteger.incrementAndGet();
                        }
                        System.out.println(counter);
                        System.out.println(String.format("Atomic:%d", atomicInteger.get()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }


    }

    public static void main(String[] args) {
        testVolatile();
        // testThread();
        // testBlockingQueue();
        // testThreadLocal();
        // testExecutorService();
        // testFuture();
        // testAtomic();
    }
}
