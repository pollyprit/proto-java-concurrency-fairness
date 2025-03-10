import java.util.concurrent.atomic.AtomicInteger;

public class Primes {
    public static void main(String[] args) throws InterruptedException {
        int MAX = 100 * 1000000; // millions

        // Serially
        // countPrimesSerial(MAX);

        // Concurrent - but not fair
        // countConcurrentNotFair(MAX);

        // Fairly concurrent
        countConcurrentFair(MAX);
    }

    private static void countConcurrentFair(int max) throws InterruptedException {
        //        Numbers: 100000000
        //        Primes: 5761456
        //        Time taken(s): 17 (all threads took same 17 secs)

        long begin = System.currentTimeMillis();
        AtomicInteger primeCounter = new AtomicInteger();
        AtomicInteger currentNumber = new AtomicInteger(2);
        int concurrency = 10;
        Thread ts[] = new Thread[concurrency];

        for (int i = 0; i < concurrency; ++i) {
            ts[i] = new Thread(new PrimeCheckerFair(i, max, currentNumber, primeCounter));
            ts[i].start();
        }
        for (int i = 0; i < concurrency; ++i) {
            ts[i].join();
        }

        long period = System.currentTimeMillis() - begin;

        System.out.println("Numbers: " + max);
        System.out.println("Primes: " + primeCounter.get());
        System.out.println("Time taken(s): " + period / 1000);
    }


    private static void countConcurrentNotFair(int max) throws InterruptedException {
        //        Numbers: 100000000
        //        Primes: 5761456
        //        Time taken(s): 22 (threads took 10 sec - 22 sec)

        long begin = System.currentTimeMillis();
        AtomicInteger primeCounter = new AtomicInteger();
        int concurrency = 10;
        Thread ts[] = new Thread[concurrency];
        int start = 0;
        int batch = max / concurrency;

        for (int i = 0; i < concurrency; ++i) {
            ts[i] = new Thread(new PrimeChecker(i, start, start + batch, primeCounter));
            start += batch;
            ts[i].start();
        }
        for (int i = 0; i < concurrency; ++i) {
            ts[i].join();
        }

        long period = System.currentTimeMillis() - begin;

        System.out.println("Numbers: " + max);
        System.out.println("Primes: " + primeCounter.get());
        System.out.println("Time taken(s): " + period / 1000);

    }

    public static void countPrimesSerial(int max) {
//        Numbers: 100000000
//        Primes: 5761456
//        Time taken(s): 103

        int primesCount = 0;
        long start = System.currentTimeMillis();

        for (int i = 1; i <= max; ++i) {
            if (isPrime(i))
                ++primesCount;
        }
        long period = System.currentTimeMillis() - start;

        System.out.println("Numbers: " + max);
        System.out.println("Primes: " + primesCount);
        System.out.println("Time taken(s): " + period / 1000);
    }

    public static boolean isPrime(int n) {
        if (n <= 1)
            return false;
        if (n == 2 || n == 3)
            return true;
        int mx = (int) Math.sqrt(n);
        for (int i = 2; i <= mx; ++i) {
            if (n % i == 0)
                return false;
        }
        return true;
    }

    public static class PrimeChecker extends Thread {
        private int id;
        private int start;
        private int end;
        AtomicInteger primeCounter;

        PrimeChecker(int id, int start, int end, AtomicInteger primeCounter) {
            this.id = id;
            this.start = start;
            this.end = end;
            this.primeCounter = primeCounter;
        }

        @Override
        public void run() {
            long begin = System.currentTimeMillis();
            for (int i = start; i < end; ++i) {
                if (isPrime(i))
                    primeCounter.incrementAndGet();
            }
            long period = System.currentTimeMillis() - begin;
            System.out.println("Thread: " + id + " Start: " + start + " End: " + end + " Time taken(s): " + period / 1000);
        }
    }

    public static class PrimeCheckerFair extends Thread {
        private int id;
        private int max;
        AtomicInteger currentNumber;
        AtomicInteger primeCounter;

        PrimeCheckerFair(int id, int max, AtomicInteger currentNumber, AtomicInteger primeCounter) {
            this.id = id;
            this.max = max;
            this.currentNumber = currentNumber;
            this.primeCounter = primeCounter;
        }

        @Override
        public void run() {
            long begin = System.currentTimeMillis();

            while (currentNumber.get() <= max) {
                if (isPrime(currentNumber.getAndIncrement()))
                    primeCounter.incrementAndGet();
            }

            long period = System.currentTimeMillis() - begin;
            System.out.println("Thread: " + id + " Time taken(s): " + period / 1000);
        }
    }
}
