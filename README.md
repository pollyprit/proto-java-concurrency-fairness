## Java Concurrency - Thread fairness. Counting All Primes till 100m.
#### If ranges of number is divided among threads, i.e. each thread is given a range to work with, few threads will finish early (which were having a lower valued range) as compared to some threads which are having large numbered ranges. So, even if this is concurrent but not fair.

#### Fairness comes if all threads ends up working (almost) as other threads. So work is taken from the work-queue (till number reaches 100m) and all threads work on it. 
