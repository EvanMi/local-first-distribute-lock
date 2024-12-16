# Local First Distribute Lock

Local first distribute lock aimed to reduce distribute-lock-server's concurrency pressure by using local lock first.As illustrated in figure 1, when a request want to fetch distribute lock, this request must first fetch JVM lock(local lock).By doing this, for a specific lock key, the distribute-lock-server will only bear a fixed number of concurrent machines, rather than requesting concurrent requests.
![figure-1.jpg](docs/figure-1.jpg)
<div style="text-align: center;">
  figure 1
</div>

This project was not started from scratch, but combined with existing distributed locks.Specifically, as follows.
## Combine with zookeeper
This combination provides the following capabilities:
### readWriteLock
### lock
## Combine with redisson
This combination provides the following capabilities:
### readWriteLock
### lock
### spinLock
## Combine with db
This combination provides the following capabilities:
### spinLock
