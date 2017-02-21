# server-benchmark
Tool for benchmarking different server architectures with
 remote client

### How to launch:

* ./gradlew localController -> generates build/libs/client.jar
* ./gradlew local -> generates build/libs/launcher.jar
* Put the launcher on some machine (or just launch on yours)
* Launch the client, specifying the ip of the launcher's machine (or leave blank for localhost)

Note: client.jar will create a folder "results" from where it is launched, or continue with the existing one

### How to generate plots:

Run draw_plots.py script from results folder
results should be placed into space-separated .csv-files
with names matching server architectures

Client's common benchmark results log is stored in 
results/benchmark.csv

### Notes
* first query takes longer because JVM didn't
 have time to "warm up"
* for multithread/thread-pooled servers system eventually
runs out of native thread for large number of clients
* request processing time grows quadratically as expected because
 server performs insertion sort
* UDP performs worse on large number of small requests
with short timeouts, maybe because network starts dropping
packages