# ops4j

The master repository for the Ops4J project. It bundles all modules into a single distribution and houses the project documentation.

---

## Overview

Ops4J is a plugin-driven, JSON-first pipeline framework for Java. It lets you compose data-processing, storage, AI, and visualization tasks as CLI commands, shell pipelines, or embedded JVM workflows — think Unix pipes, but with structured data and a rich library of composable operations.

```bash
# Generate 1 million person records and insert them into MongoDB
map -D 1000000 /=gen-person: | mongo-insert -d test -c people | benchmark
```

This repository serves two roles:

1. **Distribution** — its `pom.xml` depends on `ops4j-core` and every optional plugin module, so building it produces a fat JAR that contains the full set of operations.
2. **Documentation** — the `docs/` directory contains the full project reference documentation.

For a quick conceptual introduction see [ops4j_README.md](./ops4j_README.md).

---

## Core Concepts

| Concept | Description |
| --- | --- |
| **Op** | A pipeline stage. Consumes a stream of `OpData` records and produces a stream. |
| **NodeOp** | A field-level transformation applied to a `JsonNode` within a record. |
| **Pipeline** | A chain of `Op` stages, analogous to a Unix pipeline. |
| **Module** | A plugin jar that registers its operations via the Java `ServiceLoader` mechanism. |

---

## Modules Included in This Distribution

| Module | Capability |
| --- | --- |
| [ops4j-core](../ops4j-core/) | Core runtime, built-in operations, CLI, and base classes for all modules. |
| [ai-ops](../ai-ops/) | LLM question answering, image generation, RAG, and prompt templating. |
| [groovy-ops](../groovy-ops/) | Groovy scripting and template rendering inside pipelines. |
| [http-ops](../http-ops/) | Embedded HTTP/WebSocket server, HTTP client, and browser-based viewers. |
| [jdbc-ops](../jdbc-ops/) | Relational database integration via JDBC (create, insert, stream, drop). |
| [mongo-ops](../mongo-ops/) | MongoDB document insert and aggregation stream. |
| [smile-ops](../smile-ops/) | Machine learning clustering and NLP text processing. |
| [visual-ops](../visual-ops/) | PlantUML-based sequence, tree, and flow diagram generation. |

---

## Quick Start

```bash
git clone https://github.com/PatMartin/ops4j
cd ops4j
mvn clean install
source ops4j/setup/env.sh
ops toc
```

Try generating synthetic data:

```bash
map /=gen-person:
```

---

## Execution Modes

| Mode | How to use |
| --- | --- |
| **Shell pipeline** | `map /=gen-person: \| mongo-insert -d test -c people` |
| **Single JVM** | `pipeline 'map /=gen-person: \| mongo-insert -d test -c people'` |
| **Embedded Java** | Add `ops4j` as a Maven dependency and invoke `Ops4J` programmatically. |

---

## Documentation

- [ops4j_README.md](./ops4j_README.md) — narrative overview, examples, and comparisons
- [docs/](./docs/) — full reference documentation
- Each module has its own `README.md`

