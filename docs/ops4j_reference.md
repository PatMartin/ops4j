# Ops4J Reference Manual

A complete reference for all operations, node operations, input sources, and output destinations across every Ops4J module.

---

## Table of Contents

- [Overview](#overview)
- [Core Concepts](#core-concepts)
  - [Op vs NodeOp](#op-vs-nodeop)
  - [OpData](#opdata)
  - [Pipeline Syntax](#pipeline-syntax)
  - [NodeOp Syntax in map](#nodeop-syntax-in-map)
- [ops4j-core — Operations](#ops4j-core--operations)
  - [map](#map)
  - [filter](#filter)
  - [sort](#sort)
  - [head (jhead)](#head-jhead)
  - [tail](#tail)
  - [flatten](#flatten)
  - [unwind](#unwind)
  - [remove-nulls](#remove-nulls)
  - [to-lower (Op)](#to-lower-op)
  - [to-upper (Op)](#to-upper-op)
  - [shuffle](#shuffle)
  - [sort](#sort)
  - [stream](#stream)
  - [stream-lines](#stream-lines)
  - [print](#print)
  - [noop](#noop)
  - [benchmark](#benchmark)
  - [pause](#pause)
  - [simulate](#simulate)
  - [xray](#xray)
  - [pipeline](#pipeline)
  - [route](#route)
  - [backlog](#backlog)
  - [shell](#shell)
- [ops4j-core — Node Operations](#ops4j-core--node-operations)
  - [Type Conversion](#type-conversion)
  - [Text Operations](#text-operations)
  - [Time and Date](#time-and-date)
  - [Math and Statistics](#math-and-statistics)
  - [Sequence and Counter](#sequence-and-counter)
  - [Array Operations](#array-operations)
  - [JSON Operations](#json-operations)
  - [Cryptography](#cryptography)
  - [Data Generators](#data-generators)
- [ops4j-core — Input Sources](#ops4j-core--input-sources)
  - [file (source)](#file-source)
  - [csv](#csv)
  - [text (source)](#text-source)
- [ops4j-core — Output Destinations](#ops4j-core--output-destinations)
  - [file (destination)](#file-destination)
- [groovy-ops Module](#groovy-ops-module)
  - [groovy-template](#groovy-template)
  - [eval (NodeOp)](#eval-nodeop)
- [jdbc-ops Module](#jdbc-ops-module)
  - [jdbc-stream](#jdbc-stream)
  - [jdbc-insert](#jdbc-insert)
  - [jdbc-create](#jdbc-create)
  - [jdbc-drop](#jdbc-drop)
- [mongo-ops Module](#mongo-ops-module)
  - [mongo-stream](#mongo-stream)
  - [mongo-insert](#mongo-insert)
- [http-ops Module](#http-ops-module)
  - [http-get](#http-get)
  - [http-server](#http-server)
- [ai-ops Module](#ai-ops-module)
  - [ask](#ask)
  - [prompt](#prompt)
  - [rag](#rag)
  - [query-image](#query-image)
  - [gen-image](#gen-image)
  - [draw](#draw)
- [smile-ops Module](#smile-ops-module)
  - [smile-cluster](#smile-cluster)
  - [NLP Node Operations](#nlp-node-operations)
- [visual-ops Module](#visual-ops-module)
  - [viz-flow](#viz-flow)
  - [viz-tree](#viz-tree)
  - [viz-sequence](#viz-sequence)
- [Common Options](#common-options)
- [Recipes and Use Cases](#recipes-and-use-cases)

---

## Overview

Ops4J is a JSON-first data pipeline framework that works natively in the shell as composable CLI commands. Every operation consumes and produces a stream of JSON documents (`OpData`), making pipelines easy to compose with the Unix pipe operator.

```bash
# Generate 1 million synthetic people and load them into MongoDB
map -D 1000000 /=gen-person: | mongo-insert -c people | benchmark

# Explore data interactively in the browser
cat data.json | http-server

# Ask an AI model a question
ask "What are the top 5 sorting algorithms and their complexity?"
```

---

## Core Concepts

### Op vs NodeOp

| Kind | Interface | Granularity | Usage |
|------|-----------|-------------|-------|
| **Op** | `org.ops4j.inf.Op` | Whole record — receives and returns `List<OpData>` | Filtering, routing, sorting, database I/O, HTTP, etc. |
| **NodeOp** | `org.ops4j.inf.NodeOp` | Single JSON node — receives and returns `JsonNode` | Type coercion, math, string transforms, data generation |

NodeOps are not used standalone at the shell. They are embedded inside `map` using the syntax `<dest>=<nodeop>(<args>):`.

### OpData

`OpData` wraps an `ObjectNode` (a JSON object). Every operation in a pipeline receives one `OpData` per invocation and may return zero, one, or many `OpData` records.

- Return `input.asList()` — pass through unchanged.
- Return `OpData.emptyList()` — filter the record out.
- Return multiple items — fan-out (e.g., `unwind`).

### Pipeline Syntax

```bash
<source-op> | <transform-op> | <transform-op> | <sink-op>
```

The `-D <count>` flag on `map` drives the pipeline by repeating `count` times using a seed document (default empty `{}`).

```bash
map -D 5 /name=gen-first: /last=gen-last:
# Output: 5 JSON records, each with a random "name" and "last" field
```

### NodeOp Syntax in map

NodeOps are referenced with `<destination>=<nodeop>(<space-separated-args>):` notation inside `map`:

```
/dest=<nodeop>:              # zero-arg nodeop
/dest=<nodeop>(-opt val):   # nodeop with options
/dest=/src                  # copy /src to /dest
/dest=now:                  # set dest to current timestamp
/dest=gen-person:            # generate a full person object at dest
```

The destination is a JSON Pointer path (starts with `/`). Use `/` to replace the entire document.

---

## ops4j-core — Operations

### map

**Command:** `map`  
**Class:** `org.ops4j.op.MapJson`

The most-used operation. Maps JSON documents from one form to another using `<dest>=<source>` or `<dest>=<nodeop>(<args>):` mappings. Drives pipelines with `-D`.

| Option / Argument | Description |
|-------------------|-------------|
| `<dest>=<source>` (positional, 0..*) | One or more mappings. Source can be a JSON Pointer, a NodeOp expression, or a literal. |
| `-D <count>` | Drive pipeline — repeat `count` times using a seed document. |
| `-N <name>` | Override operation name. |
| `-C <view>` | Configuration view. |
| `-L <level>` | Log level. |

**Examples:**

```bash
# Copy entire document (identity transform)
cat data.json | map /=/

# Add a timestamp to every record
cat data.json | map /timestamp=now:

# Extract only two fields from each record
cat records.json | map /id=/id /name=/name

# Generate 100 people with sequential IDs
map -D 100 /=gen-person: /id=seq:

# Rename a field
cat data.json | map /full_name=/name /age=/age_years

# Set a fixed literal value (wrap in quotes in the shell)
cat data.json | map /status='active'

# Compute a running average of a score field
cat scores.json | map /ravg=/score(avg -w 50):

# Yesterday's epoch timestamp (offset -86400000 ms)
map -D 1 /yesterday='now(-offset=-86400000)'
```

---

### filter

**Command:** `filter`  
**Class:** `org.ops4j.op.Filter`

Keeps or discards records based on include/exclude JSONPath or expression conditions. With no arguments, filters (drops) every record.

| Option | Description |
|--------|-------------|
| `-i`, `--includes <expr>` | Keep a record only if all include expressions evaluate to true. May be specified multiple times. |
| `-x`, `--excludes <expr>` | Drop a record if any exclude expression evaluates to true. May be specified multiple times. |

**Examples:**

```bash
# Keep only records where age > 30
cat people.json | filter -i '/age > 30'

# Keep records from California
cat people.json | filter -i '/state == "CA"'

# Exclude records with null names
cat data.json | filter -x '/name == null'

# Multiple include conditions (AND logic — all must be true)
cat orders.json | filter -i '/amount > 100' -i '/status == "shipped"'

# Use filter to discard all records (null sink)
cat data.json | filter

# Chain: generate people, keep only those in certain states
map -D 10000 /=gen-person: | filter -i '/state == "TX" || /state == "CA"'
```

---

### sort

**Command:** `sort`  
**Class:** `org.ops4j.op.Sort`

Buffers the entire stream and emits records sorted by the specified field(s). Sorts numerically when the field contains numbers, lexicographically otherwise.

| Option | Description |
|--------|-------------|
| `-by <fieldName>` | Field to sort by. Defaults to the first field in the document if not specified. |
| `-r`, `--reverse` | Sort in descending order. |

> **Note:** `sort` buffers all records in memory. For very large streams, ensure sufficient heap space.

**Examples:**

```bash
# Sort people by last name alphabetically
cat people.json | sort -by last

# Sort transactions by amount descending
cat transactions.json | sort -by amount -r

# Sort by first field (default)
cat data.json | sort

# Generate people and sort by state then city
map -D 500 /=gen-person: | sort -by state
```

---

### head (jhead)

**Command:** `jhead`  
**Class:** `org.ops4j.op.Head`

Outputs the first N records from the stream and discards the rest.

| Argument | Description |
|----------|-------------|
| `<count>` (positional, required) | Number of records to pass through. |

**Examples:**

```bash
# First 10 records
cat data.json | jhead 10

# Preview a MongoDB collection
mongo-stream -c events | jhead 5

# Generate 1000 records, take only the first 3
map -D 1000 /=gen-person: | jhead 3
```

---

### tail

**Command:** `tail`  
**Class:** `org.ops4j.op.Tail`

Emits the last N records from the stream. Buffers the entire stream.

| Argument | Description |
|----------|-------------|
| `<count>` (positional, required) | Number of records to emit at the end. |

**Examples:**

```bash
# Last 5 records
cat events.json | tail 5

# Most recent 10 log entries (if log is sorted chronologically)
cat log-events.json | tail 10
```

---

### flatten

**Command:** `flatten`  
**Class:** `org.ops4j.op.Flatten`

Flattens a nested JSON document into a single-level document with dot-separated keys.

**Examples:**

```bash
# Input:  {"person": {"name": "Alice", "age": 30}}
# Output: {"person.name": "Alice", "person.age": 30}
cat nested.json | flatten

# Flatten before loading into a columnar store
cat complex.json | flatten | jdbc-insert 'INSERT INTO flat_table ...'

# Use flatten before xray to analyze field distribution
cat complex.json | flatten | xray
```

---

### unwind

**Command:** `unwind`  
**Class:** `org.ops4j.op.Unwind`

Expands array fields — producing one output record per array element, with the array field replaced by the individual element value.

| Argument | Description |
|----------|-------------|
| `<path>...` (positional, 0..*) | One or more JSON Pointer paths to unwind. |

**Examples:**

```bash
# Input:  {"order": "123", "items": ["A", "B", "C"]}
# Output: three records: {order:123,items:A}, {order:123,items:B}, {order:123,items:C}
cat orders.json | unwind /items

# Unwind tags array
cat articles.json | unwind /tags

# Generate data with arrays then unwind
map -D 10 /=gen-person: /scores='array(85 90 78)': | unwind /scores
```

---

### remove-nulls

**Command:** `remove-nulls`  
**Class:** `org.ops4j.op.RemoveNulls`

Removes all null-valued fields from every document in the stream.

**Examples:**

```bash
# Clean up records before insertion
cat dirty.json | remove-nulls | jdbc-insert 'INSERT INTO ...'

# Remove nulls before sending to AI
cat records.json | remove-nulls | ask "Summarize this data"

# Pipeline: generate, remove nulls, benchmark
map -D 10000 /=gen-person: | remove-nulls | benchmark
```

---

### to-lower (Op)

**Command:** `to-lower`  
**Class:** `org.ops4j.op.ToLower`

Converts all string values in every document to lowercase. Recurses into nested objects and arrays.

**Examples:**

```bash
# Normalize all string fields to lowercase
cat data.json | to-lower

# Lowercase before text comparison or indexing
cat contacts.json | to-lower | mongo-insert -c contacts_lower

# Generate people and lowercase everything
map -D 100 /=gen-person: | to-lower
```

---

### to-upper (Op)

**Command:** `to-upper`  
**Class:** `org.ops4j.op.ToUpper`

Converts all string values in every document to uppercase. Recurses into nested objects and arrays.

**Examples:**

```bash
# Normalize all string fields to uppercase
cat data.json | to-upper

# Uppercase for display / report generation
cat names.json | to-upper | groovy-template -t report.gt

# Round-trip: upper then lower returns original case
cat data.json | to-upper | to-lower
```

---

### shuffle

**Command:** `shuffle`  
**Class:** `org.ops4j.op.Shuffle`

Randomly reorders records using a sliding window. During streaming it probabilistically emits records from the window buffer; at stream end it drains all remaining buffered records in random order.

| Option | Description |
|--------|-------------|
| `-w`, `--window <n>` | Shuffle window size. Default: `100`. Larger window = more randomness, more memory. |

**Examples:**

```bash
# Shuffle a dataset for random sample splits
cat dataset.json | shuffle -w 500

# Randomize order before training data generation
map -D 10000 /=gen-person: | shuffle | jhead 1000

# Small window shuffle
cat records.json | shuffle -w 10
```

---

### stream

**Command:** `stream`  
**Class:** `org.ops4j.op.Stream`

Streams records from a JSON file (newline-delimited JSON or JSON array). Acts as a source operation.

| Option / Argument | Description |
|-------------------|-------------|
| `<input>` (positional, required) | Path or URL of the JSON file to stream. |
| `--array` | Read entire file into a single array record. |
| `--limit <n>` | Maximum records to stream. `0` = unlimited (default). |

**Examples:**

```bash
# Stream a JSON file
stream people.json | filter -i '/state == "CA"'

# Stream with a limit
stream large.json --limit 1000 | benchmark

# Stream and transform
stream records.json | map /ts=now: | mongo-insert -c timestamped
```

---

### stream-lines

**Command:** `stream-lines`  
**Class:** `org.ops4j.op.StreamLines`

Streams a text file line-by-line, wrapping each line as a JSON `{"value": "<line>"}` record.

| Argument | Description |
|----------|-------------|
| `<input>` (positional, required) | Path to the text file. |

**Examples:**

```bash
# Stream log file lines
stream-lines /var/log/app.log | filter -i '/value != ""'

# Process each line with AI
stream-lines questions.txt | ask "Answer this question: {{/value}}"

# Stream lines and extract structure with groovy
stream-lines data.csv | map /csv=/value
```

---

### print

**Command:** `print`  
**Class:** `org.ops4j.op.Print`

Prints one or more messages to stderr for each record, with optional interpolation of record fields using `{{/fieldname}}` syntax. Records pass through unchanged.

| Argument | Description |
|----------|-------------|
| `<message>...` (positional, 0..*) | Messages to print. Supports `{{/field}}` interpolation. |

**Examples:**

```bash
# Print each record as it flows through
cat data.json | print "Processing: {{/name}}"

# Debug intermediate values in a pipeline
cat data.json | map /upper=to-upper: | print "UPPER={{/upper}}" | ...

# Print multiple fields
cat orders.json | print "Order {{/id}}: ${{/amount}} — {{/status}}"
```

---

### noop

**Command:** `noop`  
**Class:** `org.ops4j.op.NoOp`

Pass-through operation that does nothing except optionally log each record. Useful as a placeholder or in routing configurations.

**Examples:**

```bash
# Basic pass-through
cat data.json | noop

# Use as a default destination in route testing
cat data.json | route -t RR 'filter -i /active' noop
```

---

### benchmark

**Command:** `benchmark`  
**Class:** `org.ops4j.op.Benchmark`

Measures throughput (transactions per second) for the stream. Reports a final summary and optional progress reports. Records pass through unchanged.

| Argument | Description |
|----------|-------------|
| `<threshold>` (positional, 0..1) | Report progress every N transactions. `0` = final summary only (default). |

**Examples:**

```bash
# Measure throughput of a generation pipeline
map -D 1000000 /=gen-person: | benchmark

# Progress report every 100k records
map -D 1000000 /=gen-person: | benchmark 100000

# Measure insert throughput to MongoDB
map -D 100000 /=gen-person: | mongo-insert -c perf_test | benchmark 10000

# Measure transform throughput
cat big.json | map /ts=now: /id=seq: | benchmark 50000 > output.json
```

Output sample:
```
***********************************************************
TPS: 487234.2, TXNS: 1000000, Duration: 2.052 seconds
***********************************************************
```

---

### pause

**Command:** `pause`  
**Class:** `org.ops4j.op.Pause`

Introduces a configurable delay (in milliseconds) between records. Useful for rate-limiting, simulating slow producers, or throttling pipelines.

| Argument | Description |
|----------|-------------|
| `<millis>` (positional, required) | Milliseconds to pause per record. Default: `1000`. |

**Examples:**

```bash
# One-second delay between records
cat events.json | pause 1000 | http-server

# Slow feed simulation (100ms between records)
map -D 1000 /=gen-person: | pause 100 | mongo-insert -c slow_feed

# Throttle to ~10 records/second
cat stream.json | pause 100
```

---

### simulate

**Command:** `simulate`  
**Class:** `org.ops4j.op.Simulate`

Performs a configurable number of CPU iterations per record with no I/O. Used to simulate CPU-bound workloads for benchmarking and performance testing.

| Option | Description |
|--------|-------------|
| `-i`, `--iterate <n>` | Number of iterations per record. Default: `0`. |

**Examples:**

```bash
# Simulate heavy CPU work per record
map -D 10000 /=gen-person: | simulate -i 1000000 | benchmark

# Compare throughput with and without simulated work
map -D 10000 /=gen-person: | benchmark 1000
map -D 10000 /=gen-person: | simulate -i 100000 | benchmark 1000
```

---

### xray

**Command:** `xray`  
**Class:** `org.ops4j.op.XRay`

Analyzes a stream and, upon close, emits one record per unique field+type combination with occurrence counts. All input records are consumed and replaced by the statistics output.

**Output schema per record:**
```json
{"fieldname": "state", "type": "STRING", "count": 5000}
```

**Examples:**

```bash
# Profile the schema of a JSON file
cat data.json | xray

# Profile a MongoDB collection
mongo-stream -c users | xray

# xray after flatten to see all nested fields
cat nested.json | flatten | xray

# Discover types in a large generated dataset
map -D 100000 /=gen-person: | xray
```

---

### pipeline

**Command:** `pipeline`  
**Class:** `org.ops4j.op.Pipeline`

Chains multiple operations into a single compound operation that can be embedded inside other routing/backlog configurations.

| Option / Argument | Description |
|-------------------|-------------|
| `<commands>...` (positional) | Space-separated list of operations to chain. |
| `-i`, `--immutable` | Run as an immutable pipeline. |

**Examples:**

```bash
# Bundle transforms into one named pipeline
pipeline 'map /ts=now: /id=seq:' 'filter -i /active' | mongo-insert -c clean

# Use inside a route for parallel processing paths
route -t SPLIT \
  'pipeline map /type=text(premium): filter -i "/type == premium"' \
  'pipeline map /type=text(standard): filter -x "/type == premium"'
```

---

### route

**Command:** `route`  
**Class:** `org.ops4j.op.Route`

Routes records to one or more downstream operations using round-robin, weighted, or split (broadcast) strategies.

| Option / Argument | Description |
|-------------------|-------------|
| `-t`, `--type <RR\|WT\|SPLIT>` | Routing type. Default: `WT` (weighted). |
| `<routes>...` (positional, 1..*) | Operations to route to. Weighted entries use `<weight>:<op>` syntax. |

**Route Types:**

| Type | Behavior |
|------|----------|
| `RR` | Round-robin — distributes records one at a time across all routes. |
| `WT` | Weighted — distributes records with relative probability based on weight prefix. |
| `SPLIT` | Broadcast — every record goes through every route sequentially. |

**Examples:**

```bash
# Round-robin across two MongoDB collections
cat data.json | route -t RR 'mongo-insert -c shard1' 'mongo-insert -c shard2'

# Weighted routing: 70% to fast path, 30% to audit
cat orders.json | route -t WT '70:noop' '30:mongo-insert -c audit'

# Broadcast (split): send every record to both a DB and an http endpoint
cat events.json | route -t SPLIT 'mongo-insert -c events' 'benchmark'
```

---

### backlog

**Command:** `backlog`  
**Class:** `org.ops4j.op.Backlog`

Runs operations concurrently using a backlog queue feeding a pool of worker threads. Useful for parallelizing I/O-bound work.

| Argument / Option | Description |
|-------------------|-------------|
| `<operations>...` (positional) | Operation(s) each worker will execute. |
| Additional options | See class-level options for thread count and queue size. |

**Examples:**

```bash
# Parallel inserts into MongoDB using backlog
map -D 100000 /=gen-person: | backlog 'mongo-insert -c people'
```

---

### shell

**Command:** `shell`  
**Class:** `org.ops4j.op.Shell`

Executes a bash command and integrates it into the pipeline. Three modes: filter (default, stdin → stdout), execute (fire-and-forget), source (no stdin, reads stdout).

| Option / Argument | Description |
|-------------------|-------------|
| `<commands>...` (positional, 1..*) | The bash command to execute. |
| `--type <EXECUTE\|FILTER\|SOURCE>` | Shell mode. Default: `FILTER`. |

**Modes:**

| Mode | Stdin | Stdout | Use case |
|------|-------|--------|----------|
| `FILTER` | JSON stream | JSON stream | Transform records using any program |
| `SOURCE` | None | JSON stream | Read records from an external command |
| `EXECUTE` | N/A | N/A | Run a side-effect command per record |

**Examples:**

```bash
# Run each record through a Python JSON filter
cat data.json | shell --type FILTER 'python3 transform.py'

# Source records from another program
shell --type SOURCE 'curl -s https://api.example.com/data'

# Execute a side-effect command per record
cat jobs.json | shell --type EXECUTE 'bash process.sh'
```

---

## ops4j-core — Node Operations

Node operations (`NodeOp`) transform a single `JsonNode`. They are embedded in `map` expressions with the syntax `/dest=<nodeop>(<args>):`.  
Common `BaseNodeOp` options apply to all node ops:

| Option | Description |
|--------|-------------|
| `-n`, `--name <name>` | Override the node op's internal name. |
| `-L`, `--log <level>` | Log level. |
| `-C`, `--config <view>` | Configuration view. |

---

### Type Conversion

#### to-lower (NodeOp)

**Command:** `to-lower`  
Converts a text node to lowercase.

```bash
map /city=to-lower:
# {"city": "NEW YORK"} → {"city": "new york"}
```

#### to-upper (NodeOp)

**Command:** `to-upper`  
Converts a text node to uppercase.

```bash
map /state=to-upper:
# {"state": "ca"} → {"state": "CA"}
```

#### to-int

**Command:** `to-int`  
Converts a text node to an integer.

```bash
map /age=to-int:
# {"age": "42"} → {"age": 42}
```

#### to-double

**Command:** `to-double`  
Converts a node to a double.

```bash
map /price=to-double:
# {"price": "9.99"} → {"price": 9.99}
```

#### to-float

**Command:** `to-float`  
Converts a node to a float.

```bash
map /ratio=to-float:
```

#### to-string (toString)

**Command:** `to-string`  
Converts a node to its string representation.

```bash
map /id_str=to-string:
# {"id": 42} → {"id": 42, "id_str": "42"}
```

#### to-month

**Command:** `to-month`  
Converts a numeric node (1–12) into a month name string.

```bash
map /month_name=to-month:
# {"month_name": 3} → {"month_name": "March"}
```

---

### Text Operations

#### text

**Command:** `text`  
Creates a text node with optional interpolation of record fields using `{{/field}}` syntax.

| Argument | Description |
|----------|-------------|
| `<text>...` (positional, 1..*) | Text fragments, joined with spaces. |

```bash
map /label='text(Hello {{/first}} {{/last}}):
# {"first":"Alice","last":"Smith"} → {"label":"Hello Alice Smith"}

map /greeting='text(Welcome to {{/city}}, {{/state}}!):
```

#### split

**Command:** `split`  
Splits a text node into a JSON array using a separator.

| Option | Description |
|--------|-------------|
| `-s`, `--separator <sep>` | Separator character. Default: `,`. |

```bash
map /tags='split(-s ,):
# {"tags": "java,groovy,json"} → {"tags": ["java","groovy","json"]}

map /parts='split(-s |):
```

#### match (IsMatch)

**Command:** `match`  
Returns a boolean node: `true` if the target field matches the regex pattern, `false` otherwise.

| Option | Description |
|--------|-------------|
| `-pattern <regex>` | Regular expression to test (automatically wrapped as `.*<pattern>.*`). |

```bash
map /is_gmail='match(-pattern gmail.com):
# {"email":"user@gmail.com"} → {"is_gmail": true}

# Use inside filter to keep only matching records
map /matches='match(-pattern ^[0-9]+$): | filter -i /matches
```

---

### Time and Date

#### now

**Command:** `now`  
Returns the current time in the specified format, with an optional offset.

| Argument | Description |
|----------|-------------|
| `<format>` (positional, 0..1) | Time format. Default: `ISO8601`. |

| Option | Description |
|--------|-------------|
| `-o`, `-offset <millis>` | Millisecond offset applied to the current time. |

**Format values:**

| Format | Example Output |
|--------|----------------|
| `ISO8601` | `2026-04-21T14:30:00.000Z` (default) |
| `ISO8601C` | `20260421T143000` |
| `SIMPLE` | `2026-04-21` |
| `YEAR` | `2026` |
| `MONTH` | `04` |
| `DAY` | `21` |
| `LOG` | `2026-04-21 14:30:00` |
| `EPOCH` | `1745236200000` (milliseconds) |
| `UNIX` / `POSIX` | `1745236200` (seconds) |

```bash
# Current ISO8601 timestamp
map /ts=now:

# Current date only
map /date='now(SIMPLE):

# Yesterday (epoch minus 86400 seconds = 86400000 ms)
map /yesterday='now(-offset=-86400000):

# Log-format timestamp
map /log_time='now(LOG):

# Unix epoch
map /epoch='now(EPOCH):
```

#### gen-date

**Command:** `gen-date`  
Generates a random date string.

```bash
map /birthday=gen-date:
```

---

### Math and Statistics

#### plus

**Command:** `plus`  
Adds a numeric constant to the target node.

| Argument | Description |
|----------|-------------|
| `<value>` (positional, required) | Numeric value to add. |

```bash
map /bumped_price='plus(5):
# {"price": 10} → {"bumped_price": 15}

# Decrement by using a negative number
map /score='plus(-10):
```

#### avg

**Command:** `avg`  
Computes a running windowed average over a series of numbers.

| Option | Description |
|--------|-------------|
| `-w`, `-window <n>` | Window size. Default: `10`. |

```bash
map /rolling_avg='avg(-w 20):
# Each record gets the running 20-point average of /rolling_avg values
```

#### min

**Command:** `min`  
Returns the running windowed minimum.

| Option | Description |
|--------|-------------|
| `-w`, `-window <n>` | Window size. Default: `10`. |

```bash
map /low='min(-w 50):
```

#### max

**Command:** `max`  
Returns the running windowed maximum.

| Option | Description |
|--------|-------------|
| `-w`, `-window <n>` | Window size. Default: `10`. |

```bash
map /high='max(-w 50):
```

#### slope

**Command:** `slope`  
Computes the slope (rate of change) of a running windowed series.

| Option | Description |
|--------|-------------|
| `-w`, `-window <n>` | Window size. Default: `10`. |

```bash
map /trend='slope(-w 30):
```

#### pct (Percentile)

**Command:** `pct`  
Computes a running windowed percentile value.

| Option | Description |
|--------|-------------|
| `-w`, `-window <n>` | Window size. Default: `100`. |
| `-p`, `-percent <n>` | Percentile to compute. Default: `95`. |

```bash
map /p95='pct(-w 100 -p 95):
map /p99='pct(-w 200 -p 99):
```

#### sin

**Command:** `sin`  
Applies the sine function to the target numeric node (input in radians).

```bash
map /y=sin:
```

#### cos

**Command:** `cos`  
Applies the cosine function to the target numeric node.

```bash
map /y=cos:
```

#### gen-data

**Command:** `gen-data`  
Generates a random integer within a range using a specified distribution.

| Option | Description |
|--------|-------------|
| `-min <n>` | Minimum value. Default: `1`. |
| `-max <n>` | Maximum value. Default: `100`. |
| `-dist <type>` | Distribution type. Default: `UNIFORM`. |

```bash
map /score='gen-data(-min 0 -max 100):
map /latency='gen-data(-min 1 -max 500):
```

#### dist (DistributionGenerator)

**Command:** `dist`  
Generates a number from a statistical distribution (uniform, normal, t-distribution, logistic).

| Option | Description |
|--------|-------------|
| `-uniform` | Use uniform distribution. |
| `-min <n>` | Minimum (uniform). |
| `-max <n>` | Maximum (uniform). |
| `-normal` | Use normal distribution. |
| `-mean <n>` | Mean. |
| `-variance <n>` | Variance. |
| `-tdist` | Use Student's t-distribution. |
| `-freedom <n>` | Degrees of freedom. |
| `-logistic` | Use logistic distribution. |
| `-s <n>` | Scale (logistic). |
| `-mu <n>` | Location (logistic). |
| `-p`, `-precision <n>` | Decimal precision. |

```bash
map /height='dist(-normal -mean 170 -variance 100):
map /weight='dist(-uniform -min 50 -max 120):
```

---

### Sequence and Counter

#### seq (Sequence)

**Command:** `seq`  
Generates a monotonically increasing sequence number per record.

| Option | Description |
|--------|-------------|
| `-s`, `-start <n>` | Starting value. Default: `1`. |
| `-i`, `-inc <n>` | Increment per record. Default: `1`. |

```bash
# Add a sequence ID to each record
map /id=seq:

# Start at 1000, increment by 10
map /order_num='seq(-s 1000 -i 10):
```

#### count (Counter)

**Command:** `count`  
Thread-safe counter that increments with each record call. Shares state across concurrent invocations.

| Option | Description |
|--------|-------------|
| `-s`, `-start <n>` | Starting value. Default: `1`. |
| `-i`, `-inc <n>` | Increment. Default: `1`. |

```bash
map /count=count:
map /row_id='count(-s 0 -i 1):
```

---

### Array Operations

#### array (CreateArray)

**Command:** `array`  
Creates a JSON array with the specified elements.

| Argument | Description |
|----------|-------------|
| `<element>...` (positional, 0..*) | Zero or more elements for the array. |

```bash
map /tags='array(java ops4j json):
# → {"tags": ["java","ops4j","json"]}

map /empty_list='array():
```

#### array-add

**Command:** `array-add`  
Appends elements to an existing array, or creates one if it doesn't exist.

| Argument | Description |
|----------|-------------|
| `<element>...` (positional, 0..*) | Elements to append. |

```bash
map /tags='array-add(extra):
```

---

### JSON Operations

#### jpath (JsonPath)

**Command:** `jpath`  
Evaluates a JSONPath expression on the target node and returns matching results as an array.

| Argument | Description |
|----------|-------------|
| `<expression>` (positional, required) | A JSONPath expression (e.g., `$.items[*].name`). |

```bash
map /names='jpath($.people[*].name):
# Extracts all names from a nested array
```

#### gen-missing (GenMissing)

**Command:** `missing`  
Sets a node to a MissingNode (absent/undefined in Jackson).

```bash
map /optional_field=missing:
```

#### gen-null (GenNull)

**Command:** `null`  
Sets a node to a NullNode.

```bash
map /deleted_at=null:
```

#### choose (RandomChoice)

**Command:** `choose`  
Randomly selects one of the provided options.

| Argument | Description |
|----------|-------------|
| `<option>...` (positional, 1..*) | Options to choose from. |

```bash
map /status='choose(active inactive pending):
map /color='choose(red green blue yellow):
map /tier='choose(bronze silver gold platinum):
```

---

### Cryptography

#### gen-key

**Command:** `gen-key`  
Generates a cryptographic key and returns it as a Base64-encoded string.

| Option | Description |
|--------|-------------|
| `-size <n>` | Key size in bits. |
| `-algorithm <alg>` | Algorithm (e.g., `AES`). Default: configured value. |

```bash
map /api_key=gen-key:
map /enc_key='gen-key(-size 256 -algorithm AES):
```

#### encrypt

**Command:** `encrypt`  
Encrypts the target node value using AES (or configured algorithm) and returns a Base64-encoded ciphertext.

| Option | Description |
|--------|-------------|
| `-algorithm <alg>` | Cipher algorithm. Default: `AES`. |
| `-size <n>` | Key size. Default: `128`. |

```bash
map /ssn=encrypt:
map /password=encrypt:
```

> Encryption key and algorithm are configured via `DEFAULT.ENCRYPTION` in the configuration.

#### decrypt

**Command:** `decrypt`  
Decrypts a Base64-encoded ciphertext node.

| Option | Description |
|--------|-------------|
| `-algorithm <alg>` | Cipher algorithm. Default: `AES`. |

```bash
map /ssn=decrypt:
```

---

### Data Generators

All generators below are `NodeOp` instances placed inside `map`. They require no target field and generate fresh synthetic data on every invocation. They use the [Java Faker](https://github.com/DiUS/java-faker) library internally.

#### gen-person

**Command:** `gen-person`  
Generates a complete person object with name, address, phones, and demographics.

**Output fields:** `first`, `last`, `cell-phone`, `work-phone`, `marital-status`, `race`, `sex`, `city`, `state`, `address`, `zip`

```bash
# Generate a person at the root
map -D 1 /=gen-person:

# Nest a person under a key
map -D 10 /person=gen-person: /id=seq:
```

#### gen-first

**Command:** `gen-first`  
Generates a random first name.

```bash
map /first=gen-first:
```

#### gen-last

**Command:** `gen-last`  
Generates a random last name.

```bash
map /last=gen-last:
```

#### gen-name

**Command:** `gen-name`  
Generates a full name string.

```bash
map /full_name=gen-name:
```

#### gen-address

**Command:** `gen-address`  
Generates a random street address.

```bash
map /address=gen-address:
```

#### gen-city

**Command:** `gen-city`  
Generates a random city name.

```bash
map /city=gen-city:
```

#### gen-state

**Command:** `gen-state`  
Generates a random US state abbreviation.

```bash
map /state=gen-state:
```

#### gen-phone

**Command:** `gen-phone`  
Generates a phone number.

```bash
map /phone=gen-phone:
```

#### gen-cell

**Command:** `gen-cell`  
Generates a cell phone number.

```bash
map /cell=gen-cell:
```

#### gen-int

**Command:** `gen-int`  
Generates a random integer.

```bash
map /score=gen-int:
```

#### gen-lat-long

**Command:** `gen-lat-long`  
Generates an object with `lat` and `lon` fields.

```bash
map /location=gen-lat-long:
```

#### gen-code

**Command:** `gen-code`  
Generates a random code string.

```bash
map /ref_code=gen-code:
```

#### gen-text

**Command:** `gen-text`  
Generates random filler text.

```bash
map /description=gen-text:
```

#### random-text

**Command:** `random-text`  
Generates random text of variable length.

```bash
map /notes=random-text:
```

#### gen-int-array

**Command:** `gen-int-array`  
Generates an array of random integers.

```bash
map /scores=gen-int-array:
```

---

## ops4j-core — Input Sources

Input sources produce `InputStream`s that are resolved by the framework and fed into a pipeline. They are typically referenced by scheme in source expressions (`csv:<location>`, `file:<location>`, etc.) rather than as standalone commands.

### file (source)

**Command:** `file`  
**Class:** `org.ops4j.io.FileSource`

Streams a file as a raw byte stream, resolved through the locator.

| Argument | Description |
|----------|-------------|
| `<input-location>` (positional, required) | Local filesystem path. |

```bash
# Used internally when the locator resolves file:// paths
stream file:/path/to/data.json
```

---

### csv

**Command:** `csv`  
**Class:** `org.ops4j.io.CsvSource`

Parses a CSV file and converts each row to a JSON object keyed by the header row column names.

| Argument / Option | Description |
|-------------------|-------------|
| `<input-location>` (positional, required) | Path or URL to the CSV file. |
| `-f`, `--format <fmt>` | CSV dialect. Default: `DEFAULT`. |

**Supported formats:** `DEFAULT`, `EXCEL`, `RFC4180`, `MONGODB_CSV`, `MONGODB_TSV`, `INFORMIX_UNLOAD`, `INFORMIX_UNLOAD_CSV`, `MYSQL`, `ORACLE`, `POSTGRESQL_CSV`, `POSTGRESQL_TEXT`, `TDF`

```bash
# Stream a CSV file
stream csv:people.csv | map /id=seq:

# Stream an Excel-format CSV
stream 'csv:data.csv(-f EXCEL)' | to-lower

# Use stdin as CSV source
cat data.csv | http-server -D csv:stdin:
```

---

### text (source)

**Command:** `text`  
**Class:** `org.ops4j.io.TextSource`

Wraps a literal text string as a stream. Primarily used for testing or injecting static content into the locator resolution system.

| Argument | Description |
|----------|-------------|
| `<text>` (positional, required) | Raw text to stream. |

```bash
stream 'text:{"name":"Alice","age":30}'
```

---

## ops4j-core — Output Destinations

### file (destination)

**Command:** `file`  
**Class:** `org.ops4j.io.FileDestination`

Writes records as JSON to a file.

| Argument | Description |
|----------|-------------|
| `<destination>` (positional, required) | Output file path. |

```bash
# Redirect output to a file (shell redirection also works)
map -D 1000 /=gen-person: > people.json

# Using explicit destination
map -D 1000 /=gen-person: | file:output.json
```

---

## groovy-ops Module

The `groovy-ops` module adds Groovy scripting capabilities to pipelines.

### groovy-template

**Command:** `groovy-template`  
**Class:** `org.ops4j.groovy.op.GroovyTemplate`

Buffers all input records and renders a [Groovy `StreamingTemplateEngine`](https://groovy-lang.org/templating.html) template at close. The template receives a `data` variable containing the complete list of records.

| Option | Description |
|--------|-------------|
| `-t`, `--template <path>` | Path to the Groovy template file. |

**Template variables:**

| Variable | Type | Description |
|----------|------|-------------|
| `data` | `List<OpData>` | All input records. |

```bash
# Render an HTML table from a JSON stream
cat people.json | groovy-template -t report.gt > report.html

# Generate a report after data transformation
map -D 1000 /=gen-person: | sort -by last | groovy-template -t table.gt

# Render a CSV from JSON
cat records.json | groovy-template -t export.gt > export.csv
```

**Example template (`report.gt`):**
```groovy
<html><body><table>
<tr><th>Name</th><th>State</th></tr>
<% data.each { row -> %>
<tr><td>${row.getJson().get("first")} ${row.getJson().get("last")}</td>
    <td>${row.getJson().get("state")}</td></tr>
<% } %>
</table></body></html>
```

---

### eval (NodeOp)

**Command:** `eval`  
**Class:** `org.ops4j.groovy.op.Eval`

Evaluates a Groovy expression with record fields bound as variables. The result is converted to a JSON node. All `java.lang.Math.*` methods are imported automatically.

| Argument | Description |
|----------|-------------|
| `<expression>` (positional, required) | Groovy expression to evaluate. |

```bash
# Compute BMI from weight (kg) and height (m)
cat health.json | map /bmi='eval(weight / (height * height)):

# Apply compound formula
cat data.json | map /result='eval(x * 2 + sqrt(y)):

# String manipulation
cat names.json | map /initials='eval(first[0] + "." + last[0] + "."):

# Conditional expression
cat scores.json | map /grade='eval(score >= 90 ? "A" : score >= 80 ? "B" : "C"):
```

---

## jdbc-ops Module

The `jdbc-ops` module provides JDBC-based operations for relational databases. Connection configuration is managed through the Ops4J configuration system (Typesafe Config).

### jdbc-stream

**Command:** `jdbc-stream`  
**Class:** `org.ops4j.jdbc.op.JdbcStream`

Executes a SQL query and streams results as JSON records (one record per row). Acts as a source operation.

| Argument | Description |
|----------|-------------|
| `<sql>` (positional, 0..1) | SQL SELECT statement. Falls back to configuration if omitted. |

```bash
# Stream all users
jdbc-stream 'SELECT * FROM users' | benchmark

# Stream with filtering
jdbc-stream 'SELECT id, name, email FROM customers WHERE active = 1' \
  | map /type='text(customer): \
  | mongo-insert -c customers

# Stream with a join
jdbc-stream 'SELECT o.id, o.amount, c.name FROM orders o JOIN customers c ON o.cust_id = c.id' \
  | to-upper \
  | sort -by amount -r \
  | jhead 100

# Pipe JDBC stream into http-server for visualization
jdbc-stream 'SELECT * FROM sales' | http-server
```

---

### jdbc-insert

**Command:** `jdbc-insert`  
**Class:** `org.ops4j.jdbc.op.JdbcInsert`

Executes a parameterized SQL INSERT for each incoming record. The SQL string supports `{{/field}}` interpolation.

| Argument | Description |
|----------|-------------|
| `<sql>` (positional, required) | SQL INSERT with `{{/field}}` placeholders. |

```bash
# Insert each record using interpolated SQL
cat people.json | jdbc-insert \
  "INSERT INTO people (first, last, city) VALUES ('{{/first}}', '{{/last}}', '{{/city}}')"

# Generate and insert 10,000 records
map -D 10000 /=gen-person: \
  | jdbc-insert "INSERT INTO persons(first,last,state) VALUES('{{/first}}','{{/last}}','{{/state}}')" \
  | benchmark
```

---

### jdbc-create

**Command:** `jdbc-create`  
**Class:** `org.ops4j.jdbc.op.JdbcCreate`

Streams records into a JDBC table that is created automatically from the first record's schema.

| Argument / Option | Description |
|-------------------|-------------|
| `<table>` (positional, 0..1) | Table name to create. Default from config. |
| `--threshold <n>` | Commit threshold (batch size). |

```bash
# Create table from schema inferred from first record
map -D 10000 /=gen-person: | jdbc-create people

# Create and commit every 1000 records
cat data.json | jdbc-create analytics_data --threshold 1000 | benchmark
```

---

### jdbc-drop

**Command:** `jdbc-drop`  
**Class:** `org.ops4j.jdbc.op.JdbcDrop`

Drops a table from the connected database.

| Argument | Description |
|----------|-------------|
| `<table>` (positional, 0..1) | Table name to drop. |

```bash
jdbc-drop old_table
jdbc-drop temp_staging
```

---

## mongo-ops Module

The `mongo-ops` module provides MongoDB connectivity. Connection is configured via the Ops4J Typesafe Config system.

### mongo-stream

**Command:** `mongo-stream`  
**Class:** `org.ops4j.mongo.op.MongoStream`

Streams documents from a MongoDB collection, optionally filtered by an aggregation pipeline.

| Option / Argument | Description |
|-------------------|-------------|
| `-c`, `-collection <name>` | Collection name. **Required.** |
| `<pipeline>...` (positional, 0..*) | Aggregation pipeline stages (JSON). Default: match all. |

```bash
# Stream entire collection
mongo-stream -c users | benchmark

# Stream with a filter (aggregation pipeline match stage)
mongo-stream -c orders '{"$match":{"status":"shipped"}}' | sort -by amount -r

# Stream with projection and sort
mongo-stream -c products \
  '{"$project":{"name":1,"price":1}}' \
  '{"$sort":{"price":-1}}' \
  | jhead 10

# Pipe into http-server for exploration
mongo-stream -c events | http-server

# Export collection to file
mongo-stream -c logs > logs.json
```

---

### mongo-insert

**Command:** `mongo-insert`  
**Class:** `org.ops4j.mongo.op.MongoInsert`

Inserts each incoming record as a document into a MongoDB collection.

| Option | Description |
|--------|-------------|
| `-c`, `-collection <name>` | Collection name. |

```bash
# Insert generated people into MongoDB
map -D 1000000 /=gen-person: | mongo-insert -c people | benchmark

# Transform then insert
cat data.json | map /ts=now: /id=seq: | mongo-insert -c timestamped_data

# Load CSV into MongoDB
stream csv:contacts.csv | mongo-insert -c contacts

# Import a JDBC table into MongoDB
jdbc-stream 'SELECT * FROM legacy_users' | mongo-insert -c users_migrated
```

---

## http-ops Module

The `http-ops` module provides HTTP client and server capabilities as pipeline operations.

### http-get

**Command:** `http-get`  
**Class:** `org.ops4j.http.op.HttpGetOp`

Performs an HTTP GET request and streams the JSON response into the pipeline. Acts as a source operation.

| Option / Argument | Description |
|-------------------|-------------|
| `--url <url>` | The URL to GET. |
| `<headers>...` (positional, 0..*) | Request headers as `name=value` pairs. |

```bash
# Stream from a REST API
http-get --url https://api.example.com/data | map /ts=now: | mongo-insert -c live_data

# With authentication header
http-get --url https://api.secure.com/users 'Authorization=Bearer token123' \
  | filter -i '/active == true' \
  | jhead 50

# Periodic polling pipeline (combined with pause)
http-get --url https://metrics.example.com/current | pause 5000 | benchmark
```

---

### http-server

**Command:** `http-server`  
**Class:** `org.ops4j.http.op.HttpServer`

Starts an embedded HTTP server and serves the incoming record stream through interactive web applications. Enables browser-based data exploration.

| Option | Description |
|--------|-------------|
| `--app <context>` | Application context path. |
| `--host <hostname>` | Bind hostname. |
| `--port <n>` | Port to listen on. |
| `--linger <n>` | Seconds to keep running after stream ends. `0` = forever. |
| `--root <dir>` | Server root directory for static files. |

```bash
# Explore a JSON dataset in the browser
cat data.json | http-server

# Explore a CSV file
cat data.csv | http-server -D csv:stdin:

# Live MongoDB collection exploration
mongo-stream -c events | http-server

# JDBC data exploration
jdbc-stream 'SELECT * FROM sales' | http-server --port 8888

# Custom app context
cat metrics.json | http-server --app analytics --port 9090
```

---

## ai-ops Module

The `ai-ops` module integrates AI language models and image APIs into Ops4J pipelines.

### ask

**Command:** `ask`  
**Class:** `org.ops4j.ai.op.AskQuestion`

Sends a question to a configured AI language model and streams the response. Supports GitHub Copilot models and other providers.

| Option / Argument | Description |
|-------------------|-------------|
| `<question>` (positional, required) | The question to ask. Supports `{{/field}}` interpolation. |
| `-m`, `--model <model>` | Chat model override. |
| `-gh <model>` | GitHub Copilot model name. |

```bash
# Simple question
ask "What is the capital of France?"

# Use different GitHub model
ask -gh meta-llama-3-70b-instruct "Explain neural networks in one paragraph"

# Per-record questions using field interpolation
cat products.json | ask "Write a product description for: {{/name}}"

# Use a different model provider
ask -m gpt-4o "Summarize the risks of technical debt"
```

---

### prompt

**Command:** `prompt`  
**Class:** `org.ops4j.ai.op.AiPrompt`

Asks a prompted question using a stored prompt file (`.pr`) that provides context/framing before the user question.

| Argument | Description |
|----------|-------------|
| `<prompt>` (positional, required) | Path to the prompt file. |
| `<params>...` (positional, 1..*) | Parameters passed to the prompt. |

```bash
# Use a novice-level explanation prompt
prompt novice.pr "Why is the sky blue?"

# Use an expert-level prompt
prompt expert.pr "Explain the CAP theorem"

# Ask AI to write an Op for you
prompt writeop.pr "an operation that computes moving averages"

# Ask AI to write a NodeOp
prompt nodeop.pr "a node operation that formats phone numbers as E.164"
```

---

### rag

**Command:** `rag`  
**Class:** `org.ops4j.ai.op.RAG`

Performs Retrieval-Augmented Generation — asks a question grounded in provided data or a pre-built RAG store.

| Argument / Option | Description |
|-------------------|-------------|
| `<question>` (positional, required) | The question. |
| `-d`, `--data <path>` | Data file(s) to include as context. |
| `-o`, `--out <name>` | Name to use when serializing a RAG store. |
| `-i`, `--in <path>` | Path to a pre-built serialized RAG store. |

```bash
# Ask about your own data
rag "What are the top 5 states by population?" -d states.json

# Build and save a RAG store from documents
rag "Summarize the main themes" -d corpus.json -o themes_rag

# Use a pre-built RAG store
rag "Answer the question" -i themes_rag

# Per-record RAG with inline data
cat questions.json | rag "{{/question}}" -d knowledge_base.json
```

---

### query-image

**Command:** `query-image`  
**Class:** `org.ops4j.ai.op.QueryImage`

Asks a vision AI model a question about an image.

| Argument / Option | Description |
|-------------------|-------------|
| `<question>` (positional, required) | Question to ask about the image. |
| `-i`, `--image <path>` | Path or URL of the image. |
| `-m`, `--max-tokens <n>` | Maximum tokens in the response. |
| `-t`, `--temp <n>` | Model temperature (creativity vs. determinism). |

```bash
# Describe image contents
query-image "What do you see in this image?" -i photo.jpg

# Suggest a filename
query-image "Suggest a filename (max 30 chars, memorable)" -i screenshot.png

# Identify objects
query-image "List all objects visible in this image" -i scene.jpg

# Using a prompt file
query-image -i diagram.png -p describe-diagram.pr
```

---

### gen-image

**Command:** `gen-image`  
**Class:** `org.ops4j.ai.op.GenImage`

Generates an image from a text description using an AI image generation model.

| Argument | Description |
|----------|-------------|
| `<description>` (positional, required) | Text description of the image to generate. |

```bash
gen-image "a tranquil mountain lake at sunrise with mist rising from the water"
gen-image "a circuit board shaped like a human brain"
gen-image "an eagle flying high in the sky next to a monarch butterfly"
```

---

### draw

**Command:** `draw`  
**Class:** `org.ops4j.ai.op.AiDraw`

Asks AI to draw a picture (alias/variant of `gen-image`).

| Argument | Description |
|----------|-------------|
| `<description>` (positional, required) | What to draw. |

```bash
draw "a neural network diagram with nodes and connections"
draw "three cats sitting on a fence watching the moon"
```

---

## smile-ops Module

The `smile-ops` module integrates the [Smile machine learning library](https://haifengl.github.io/) for clustering and NLP tasks.

### smile-cluster

**Command:** `smile-cluster`  
**Class:** `org.ops4j.smile.op.Cluster`

Performs unsupervised clustering on a stream of records. Emits each record with an added `cluster` field containing its assigned cluster ID. Supported algorithms: K-Means, X-Means, G-Means, DENCLUE, DBSCAN.

| Argument / Option | Description |
|-------------------|-------------|
| `<fields>...` (positional, 0..*) | Fields to use as feature dimensions. If omitted, all numeric fields are used. |
| `-K`, `--kmeans.clusters <n>` | K-Means: desired number of clusters. |
| `-X`, `--xmeans.clusters <n>` | X-Means: max clusters. |
| `-G`, `--gmeans.clusters <n>` | G-Means: max clusters. |
| `--denclue.sigma <n>` | DENCLUE: density attractor bandwidth. |
| `--denclue.m <n>` | DENCLUE: radius of density attractor. |
| `--dbscan.points <n>` | DBSCAN: minimum points per cluster. |

```bash
# Cluster customer data by purchase behaviord using K-Means (3 clusters)
cat customers.json | smile-cluster -K 3 /total_spend /frequency /recency

# Cluster on all numeric fields
cat sensor_data.json | smile-cluster -K 5

# DBSCAN spatial clustering
cat locations.json | smile-cluster --dbscan.points 5 /lat /lon

# Cluster then analyze distribution
map -D 10000 /=gen-person: \
  | map /score=gen-int: \
  | smile-cluster -K 4 /score \
  | sort -by cluster \
  | xray
```

---

### NLP Node Operations

All NLP node ops are in the `smile-ops` module and operate on text. They are embedded in `map` pipelines.

#### normalize

**Command:** `normalize`  
Normalizes text (Unicode normalization, lowercasing, whitespace handling).

```bash
cat articles.json | map /clean=normalize:
```

#### words

**Command:** `words`  
Tokenizes text into an array of words.

```bash
cat articles.json | map /tokens=words:
# {"body":"Hello World"} → {"tokens":["Hello","World"]}
```

#### sentences

**Command:** `sentences`  
Splits text into an array of sentences.

```bash
cat documents.json | map /sents=sentences:
```

#### keywords

**Command:** `keywords`  
Extracts the top K keywords from a text field.

```bash
cat content.json | map /kw=keywords:
```

**Full NLP pipeline example:**
```bash
cat articles.json \
  | map /clean=normalize: \
  | map /words=words: \
  | map /keywords=keywords: \
  | mongo-insert -c analyzed_articles
```

---

## visual-ops Module

The `visual-ops` module generates SVG diagrams from streaming data, using PlantUML/Mermaid-compatible renderers.

### viz-flow

**Command:** `viz-flow`  
**Class:** `org.ops4j.visual.op.VisualFlow`

Reads a stream of source→destination transition records and renders a flow/Sankey diagram as SVG.

| Option | Description |
|--------|-------------|
| `-o`, `--output <path>` | Output SVG file path. Default: `flow.svg`. |
| `-s`, `--src <path>` | JSON Pointer to the source field. Default: `/src`. |
| `-d`, `--dest <path>` | JSON Pointer to the destination field. Default: `/dest`. |
| `-e`, `--entry <value>` | Entry point node name. |
| `-g`, `--guard` | Guard against circular references. |

```bash
# Visualize user navigation flow
cat page_views.json | viz-flow -s /from_page -d /to_page -o navigation.svg

# Visualize state machine transitions
cat events.json | map /src=/prev_state /dest=/next_state | viz-flow -o states.svg

# Flow from MongoDB with circular reference protection
mongo-stream -c transitions | viz-flow -g -o user_flow.svg
```

---

### viz-tree

**Command:** `viz-tree`  
**Class:** `org.ops4j.visual.op.VisualTree`

Renders a hierarchical tree (mindmap or WBS) from the data stream.

| Option | Description |
|--------|-------------|
| `-o`, `--output <path>` | Output SVG path. Default: `tree.svg`. |
| `-t`, `--type <mindmap\|wbs>` | Diagram style. |

```bash
# Render a mindmap
cat hierarchy.json | viz-tree -t mindmap -o concepts.svg

# Render a WBS (Work Breakdown Structure)
cat project.json | viz-tree -t wbs -o breakdown.svg
```

---

### viz-sequence

**Command:** `viz-sequence`  
**Class:** `org.ops4j.visual.op.VisualSequence`

Renders a UML sequence diagram from records containing actor, target, and message fields.

| Option | Description |
|--------|-------------|
| `-o`, `--output <path>` | Output SVG path. Default: `sequence.svg`. |
| `-s`, `--src <path>` | JSON Pointer to the source/sender field. |
| `-d`, `--dst <path>` | JSON Pointer to the destination/receiver field. |
| `-c`, `--comment <path>` | JSON Pointer to the message/comment field. |

```bash
# Render a sequence diagram from API call logs
cat api_calls.json | viz-sequence \
  -s /caller -d /service -c /endpoint \
  -o api_sequence.svg

# Visualize message flow between microservices
cat service_logs.json | viz-sequence \
  -s /from_service -d /to_service -c /operation \
  -o service_diagram.svg
```

---

## Common Options

All operations inheriting from `BaseOp` or `BaseNodeOp` accept these standard options:

| Option | Description |
|--------|-------------|
| `-N`, `--name <name>` | Override the name of the operation (shown in logs). |
| `-L`, `--log <level>` | Set log level: `TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR`, `OFF`. Default: `INFO`. |
| `-C`, `--config <view>` | Configuration view path (Typesafe Config). |
| `-h`, `--help` | Display help for the operation. |

---

## Recipes and Use Cases

### Synthetic Data Generation

```bash
# Generate 1 million people as NDJSON
map -D 1000000 /=gen-person: > people.json

# Generate people with sequential IDs and timestamps
map -D 100000 /=gen-person: /id=seq: /created_at=now: > people_ts.json

# Generate records with distributions
map -D 50000 /=gen-person: \
  /age='gen-data(-min 18 -max 90): \
  /salary='dist(-normal -mean 65000 -variance 200000000): \
  /score='gen-data(-min 300 -max 850): \
  > synthetic_customers.json

# Generate data directly into MongoDB with throughput reporting
map -D 5000000 /=gen-person: \
  /id=seq: /ts=now: \
  | mongo-insert -c people \
  | benchmark 100000
```

---

### Data Transformation Pipelines

```bash
# Clean and normalize data
cat raw_contacts.json \
  | remove-nulls \
  | to-lower \
  | map /state=to-upper: \
  | map /created=now: \
  > clean_contacts.json

# Flatten nested JSON for analysis
cat nested_events.json | flatten | xray

# Split a CSV tag column into an array, then unwind it
cat articles.csv \
  | map /tags='split(-s ,): \
  | unwind /tags \
  | map /tag=to-lower: \
  > tag_records.json

# Enrich records with computed fields
cat orders.json \
  | map /ts=now: /row=seq: /total='eval(price * quantity): \
  | sort -by total -r \
  | jhead 100
```

---

### Database Integration

```bash
# CSV to MongoDB
stream csv:customers.csv | mongo-insert -c customers | benchmark

# JDBC to MongoDB (migrate a table)
jdbc-stream 'SELECT * FROM legacy_customers' \
  | map /migrated_at=now: \
  | mongo-insert -c customers \
  | benchmark 10000

# MongoDB to JDBC
mongo-stream -c users | jdbc-insert \
  "INSERT INTO pg_users(id,name,email) VALUES ('{{/id}}','{{/name}}','{{/email}}')"

# Generate and load
map -D 1000000 /=gen-person: \
  | jdbc-create person_data \
  | benchmark 50000

# Bidirectional sync: read from JDBC, enrich, write to Mongo
jdbc-stream 'SELECT * FROM products' \
  | map /enriched_at=now: /category=to-upper: \
  | mongo-insert -c products_enriched
```

---

### Data Quality and Profiling

```bash
# Profile field types in a dataset
cat data.json | xray

# Profile after flattening (includes nested fields)
cat complex.json | flatten | xray

# Find records with missing fields
cat records.json | filter -x '/name == null' -x '/email == null'

# Validate email format
cat contacts.json | map /valid_email='match(-pattern ^[^@]+@[^@]+\.[^@]+$):

# Identify and count states
cat people.json | map /state=to-upper: | sort -by state | xray
```

---

### AI Integration Workflows

```bash
# Summarize each document
cat articles.json | ask "Summarize in 3 bullet points: {{/body}}"

# Sentiment analysis
cat reviews.json | ask "Classify as positive/negative/neutral: {{/text}}" | mongo-insert -c sentiments

# Explain anomalies
cat alerts.json | filter -i '/severity == "critical"' \
  | ask "Explain this alert and suggest fixes: {{/message}}"

# Generate images from descriptions
cat product_ideas.json | gen-image "Product concept: {{/description}}"

# RAG over your own data
rag "What are the top customer complaints?" -d support_tickets.json

# Build and reuse a RAG store
rag "Index this data" -d knowledge_base.json -o kb_rag
rag "Answer: what are the pricing tiers?" -i kb_rag
```

---

### Visualization Pipelines

```bash
# Explore data in the browser
cat data.json | http-server
mongo-stream -c sales | http-server
jdbc-stream 'SELECT * FROM metrics' | http-server --port 8888

# Generate a flow diagram of state transitions
mongo-stream -c events \
  | map /src=/prev_state /dest=/next_state \
  | viz-flow -o state_flow.svg

# Sequence diagram from API logs
cat api_trace.json \
  | viz-sequence -s /caller -d /service -c /method \
  -o api_seq.svg

# NLP + clustering + visualization
cat articles.json \
  | map /words=words: /keywords=keywords: \
  | smile-cluster -K 5 \
  | sort -by cluster \
  | groovy-template -t cluster_report.gt
```

---

### Performance Benchmarking

```bash
# Baseline throughput test
map -D 1000000 /=gen-person: | benchmark

# With transforms
map -D 1000000 /=gen-person: \
  | map /ts=now: /id=seq: \
  | benchmark 100000

# End-to-end DB throughput
map -D 100000 /=gen-person: \
  | mongo-insert -c perf \
  | benchmark 10000

# Simulate CPU-bound workers with backlog
map -D 10000 /=gen-person: \
  | simulate -i 1000000 \
  | benchmark

# Parallel insert performance test
map -D 100000 /=gen-person: \
  | backlog 'mongo-insert -c parallel_test' \
  | benchmark
```

---

### Shell Integration

```bash
# Use any external program as a filter
cat records.json | shell --type FILTER 'python3 my_transform.py'

# Source data from an external command
shell --type SOURCE 'curl -s https://api.example.com/feed' \
  | map /ts=now: \
  | mongo-insert -c live_feed

# Process and email results using shell execute
cat anomalies.json | filter -i '/score > 0.9' \
  | shell --type EXECUTE 'mail -s "Alert" ops@example.com < /dev/stdin'
```
