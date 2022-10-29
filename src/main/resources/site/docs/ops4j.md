# Ops4J

Ops4J is proudly developed with the Ops4J is proudly developed with ![Java Profiler](../images/jprofiler_small.png) from the fine folks at ej-technologies.

Ops4J is a general purpose framework which brings Unix style programming to the JVM and JVM programs to the bash shell.

Ops4J is a JSON pipeline composed of smaller units called operations.  Operations small, flexible, reusable and singular of purpose whose components which behave as if they were command line utilites when run in the native OS shell while providing a fluent API for use within traditional JVM environments.

## CLI vs JVM API

 For example, suppose we would like to flatten a stream of JSON documents located in a file named `input.json` and benchmark how long the process takes to execute.  Also suppose that we have two operations named `flatten` and `benchmark` suited to their respective task.

From the CLI:

```bash
cat input.json | flatten | benchmark -O none
```

And from the Java API we might:

```java
Pipeline pipeline = Pipeline.of("flatten | benchmark").initialize().open();
List<OpData> results = pipeline.execute(input);
pipeline.close().cleanup();
```

Or we might run our workload within a single CLI command:

```bash
pipeline -D 'file(input.json)' "flatten | benchmark" -O none
```

then save the pipeline to a repository:

```bash
pipeline "flatten | benchmark" -S JSON | ops save my-pipeline
```

and later run the pipeline from the respository from the CLI:

```bash
$ pipeline -D 'file(input.json)' my-pipeline
```

or load and run the pipeline from Java API:

```java
Pipeline pipeline = Ops4J.load("my-pipeline").initialize().open();
// execute, close, cleanup
```

## Philosophy

## Option conventions

The following conventions should be applied so that 

### Operations

* Long and short names should be provided.

* Short names should be:
  
  * Prefaced by a single `-`
  
  * As short as possible
  
  * Single characters should always be used for options without arguments to allow support option clustering.
    
    * `-abc` is equivalent to `-a -b -c`

* Long names should be...
  
  * named to describe the option they represent.
  
  * prefaced by `--`, which allows support for short option clustering.
  
  * lower-case only
