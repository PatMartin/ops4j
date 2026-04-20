# Ops4J

**Ops4J lets you build data pipelines like shell commands --- but with
structured data, Java performance, and pluggable operations.**

Think: **jq + bash pipes + Apache Camel --- in one tool.**

------------------------------------------------------------------------

# 🚀 Example (30 seconds to value)

Generate 1 million records and insert them into MongoDB:

``` bash
map -D 1000000 /=gen-person: | mongo-insert -d test -c people | benchmark
```

👉 In one pipeline, you: - generate structured JSON data\
- transform it\
- persist it\
- measure throughput

No glue code. No orchestration layer. Just composable operations.

------------------------------------------------------------------------

# 🧠 Why Ops4J exists

Modern data work often looks like this: - shell scripts glued together
with pipes\
- Python scripts for transformations\
- separate tools for databases, APIs, ML, and AI

Ops4J replaces that with a **single, composable model**:

-   🧩 Small, reusable operations\
-   🔗 Pipeline composition (like Unix pipes)\
-   📦 JSON-first data model\
-   ⚙️ Runs as CLI, shell pipeline, or embedded Java\
-   🔌 Plugin-based (DB, HTTP, AI, ML, visualization, etc.)

------------------------------------------------------------------------

# ⚡ What you can do with it

### Data generation & transformation

``` bash
map -D 1000000 /=gen-person: > people.json
```

### Database pipelines

``` bash
map /=gen-person: | mongo-insert -d mydb -c people
```

### HTTP + data exploration

``` bash
cat data.json | http-server
```

### AI-powered workflows

``` bash
ask "Explain why the sky is blue"
```

### Machine learning & clustering

``` bash
cat iris.json | smile-cluster
```

------------------------------------------------------------------------

# 🆚 Why not just use X?

  -----------------------------------------------------------------------
  Tool                      Limitation
  ------------------------- ---------------------------------------------
  **bash + jq**             Great for simple transforms, weak for complex
                            pipelines and integrations

  **Python scripts**        Flexible, but not composable or discoverable

  **Apache Camel**          Powerful, but heavy and not shell-native

  **Airflow / Spark**       Overkill for many local or exploratory
                            workflows
  -----------------------------------------------------------------------

👉 Ops4J sits in the middle: - lightweight like shell tools\
- structured like a framework\
- extensible like a platform

------------------------------------------------------------------------

# 🏁 Quick Start

``` bash
git clone https://github.com/PatMartin/ops4j
cd ops4j
mvn clean install
source ops4j/setup/env.sh
ops toc
```

Try:

``` bash
map /=gen-person:
```

------------------------------------------------------------------------

# 🧩 Core Concepts (quick version)

-   **Op** → a pipeline stage (e.g. `map`, `mongo-insert`, `benchmark`)
-   **NodeOp** → field-level transformation (e.g. `to:upper`,
    `json:path`)
-   **Pipeline** → composed operations (shell or JVM)
-   **Modules** → extend functionality (AI, DB, HTTP, ML, etc.)

------------------------------------------------------------------------

# 🔌 Modules

-   `ops4j-core` → runtime + built-in ops\
-   `jdbc-ops` → relational databases\
-   `mongo-ops` → MongoDB\
-   `http-ops` → HTTP + embedded server\
-   `ai-ops` → LLM + RAG workflows\
-   `smile-ops` → ML + clustering\
-   `groovy-ops` → scripting & templates\
-   `visual-ops` → diagrams & visualization

------------------------------------------------------------------------

# 🧪 Execution Modes

### Shell pipelines

``` bash
map /=gen-person: | mongo-insert -d test -c people
```

### Single JVM

``` bash
pipeline 'map =/gen-person: | mongo-insert -d test -c people'
```

### Embedded Java

Use Ops4J as a library inside your application.

------------------------------------------------------------------------

# 🔍 Discoverability

``` bash
ops toc
ops toc -t ops
map -h
```

------------------------------------------------------------------------

# 📘 Documentation

See `ops4j/docs/` for full documentation.

------------------------------------------------------------------------

# ⚠️ Status

Ops4J is early-stage (`0.0.1`) and evolving.

------------------------------------------------------------------------

# 🧠 One sentence summary

**Ops4J is a plugin-driven, JSON-first pipeline framework that lets you
compose data processing, storage, AI, and visualization tasks as CLI
commands, shell pipelines, or embedded JVM workflows.**
