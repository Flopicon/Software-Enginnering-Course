# Task 2 — Fixing a race condition
## why does making only increment() synchronized but not get() still leave a bug?
If `increment()` is synchronized but `get()` is not, the two methods do not use the same synchronization protocol. A concurrent call to `get()` is not required to acquire the monitor, so it does not get the visibility guarantee provided by that monitor and may observe a stale value. Synchronizing both methods makes reads and writes consistently use the same monitor. In this benchmark, `join()` also establishes a happens-before relationship between each worker's actions and the main thread after the join, but that does not make an unsynchronized `get()` safe for general concurrent use.

# Task 3 — Producer–consumer with `wait()`/`notifyAll()` and `BlockingQueue`
## Experiment: changing `notifyAll()` to `notify()` and `while` to `if`
- Replacing `while` with `if` can allow a thread to continue after a spurious wakeup or after another thread has already changed the buffer. A producer could add to a full buffer, or a consumer could try to remove from an empty one.
- Replacing `notifyAll()` with `notify()` can wake a thread waiting for the wrong condition. Other threads that could make progress may stay asleep, causing a hang.
- Because thread scheduling is nondeterministic, the failure may be intermittent. A successful run does not prove the change is correct.

# Task 4 - Thread pools, Future and virtual threads
## Why are virtual threads faster for this I/O-bound workload?

Each simulated download spends most of its time waiting for a 200 ms sleep, which represents network I/O. With a fixed pool of 20 platform thread, only about 20 tasks can run at once; the rest wait for a thread to become available. Virtual threads are lightweight, so the executor can create one per task without creating 500 platform threads. When a virtual thread blocks, the JVM can schedule other virtual threads on the available platform threads. This usually makes virtual threads much faster for I/O-bound work. Virtual threads do not automatically speed up CPU-bound work; the prime-counting task benefits from parallel execution across CPU cores instead.