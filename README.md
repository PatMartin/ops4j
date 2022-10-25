# ops4j

Ops4J is proudly developed with [Jprofiler](https://www.ej-technologies.com/images/product_banners/jprofiler_large.png).



>  PLEASE NOTE THAT THIS IS A EARLY WORK IN PROGRESS AND THAT I AM STILL WORKING ON BASIC APPLICATON ARCHITECTURE.   THE CODE WILL REMAIN RATHER FLUID FOR SOME TIME TO COME.

Ops4j is a framework which is well suited for rapid prototyping, experimentation and innovation.  Developers write small units of code we refer to as `operations`.

Operations are:

* singular of purpose
* self-documenting with command line help and descriptive names such as `mongo:insert` or `benchmark`.
* Flexible and reusable
* Interoperable with other operations
* Interoperable with external programs such as command line utilities

Operations adhere to a JSON IN / JSON OUT paradigm which allows them to be orchestrated directly from the shell.   Operations provide their own documentation in the form of online help and can be used as would any other CLI based utility.

## cli examples

### extract, transform and load

Say we wish to scrub JSON content stored in a file named `input.json`; removing blanks and nulls, then insert the record into a collection called `scrubbed` in a database named `test`.  In ops4j this is a simple one liner:

```bash
cat input.json | remove -blanks -nulls | mongo:insert -d test -c scrubbed -O NONE
```

### validation

We validate the data by streaming it back from Mongo as follows:

```bash
mongo:stream -d test -c scrubbed
```

### performance test

We can drop a benchmark in anywhere we like.  Here we test our ETL process performance.

```bash
cat input.json | remove -blanks -nulls | mongo:insert -d test -c scrubbed | benchmark -O NONE
```

### concurrency

We can run things concurrently.  A variety of concurrent operations are available.  Here we use the parallel operation to execute our data scrub and load in parallel.  The benchmark running in a single thread.

```bash
cat input.json | parallel -t 4 'remove -blanks -nulls | mongo:insert -d test -c scrubbed' | benchmark -O NONE
```

Small changes in pipeline structure can result in significant changes in pipeline architecture.  Here, we move the benchmark into the parallel pipeline so that we can check for things such as starvation.  We will receive separate benchmarks for each thread.

```bash
cat input.json | parallel -t 4 'remove -blanks -nulls | mongo:insert -d test -c scrubbed | benchmark' -O NONE
```

## java example

The previous example could also be coded directly in the JVM.

```java
JsonNodeInputStream it = JsonNodeInputStream.from(new FileInputStream("input.json"));

Pipeline p = new Pipeline()
    .add(new RemoveJson().blanks(true).nulls(true))
    .add(new MongoInsert().db("test").collection("scrubbed"))
    .add(new Benchmark())
    .initialize()
    .open();

while (it.hasNext())
{
    List<JsonNode> results = p.execute(it.next());
}

p.close().cleanup();
```

We can construct pipelines in a number of ways.

**<u>Using the pipeline DSL:</u>**

```java
Pipeline p = Pipeline.from("remove -nulls -blanks | " +
  "mongo:insert -d test -c scrubbed | benchmark");
```

Loaded from repository:

```java
Pipeline p = Ops4J.repo().load("scrub");
```

# 
