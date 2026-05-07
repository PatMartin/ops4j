# Ops4J Workspace

<<<<<<< HEAD
This repository contains the core Ops4J runtime, the main distribution package, and a set of focused extension modules for databases, HTTP, AI, machine learning, scripting, and visualization.

At a high level, Ops4J is a plugin-driven pipeline framework for structured data processing. The projects in this workspace are split between a shared foundation, a bundled distribution, and modules that add domain-specific operations.

## Projects in This Workspace

| Project | Role |
| --- | --- |
| [ops4j-core](./ops4j-core/) | Shared runtime and core operations used by the rest of the workspace. |
| [ops4j](./ops4j/) | Main distribution project that brings the modules together and includes the primary documentation set. |
| [ai-ops](./ai-ops/) | Adds AI-oriented operations for question answering and related LLM workflows. |
| [groovy-ops](./groovy-ops/) | Adds Groovy-based scripting and templating support. |
| [http-ops](./http-ops/) | Adds HTTP and embedded web server operations. |
| [jdbc-ops](./jdbc-ops/) | Adds relational database support through JDBC operations. |
| [mongo-ops](./mongo-ops/) | Adds MongoDB-oriented operations. |
| [smile-ops](./smile-ops/) | Adds machine learning support based on Smile. |
| [visual-ops](./visual-ops/) | Adds visualization and diagram-oriented operations. |

## How the Projects Fit Together

- `ops4j-core` is the common foundation for the module projects.
- `ops4j` depends on the core module and the extension modules to provide a broader distribution surface.
- `http-ops` and `visual-ops` both build on `groovy-ops` in addition to the core runtime.
- The remaining modules extend the core runtime with focused capabilities such as JDBC, MongoDB, AI, or machine learning.

## Documentation Entry Points

- Start with [ops4j/README.md](./ops4j/README.md) for the main project summary.
- See [ops4j/ops4j_README.md](./ops4j/ops4j_README.md) for a fuller overview of the pipeline model and examples.
- Browse [ops4j/docs](./ops4j/docs/) for broader project documentation and recipes.
- Use each module README for module-specific context:
  - [ai-ops/README.md](./ai-ops/README.md)
  - [groovy-ops/README.md](./groovy-ops/README.md)
  - [http-ops/README.md](./http-ops/README.md)
  - [jdbc-ops/README.md](./jdbc-ops/README.md)
  - [mongo-ops/README.md](./mongo-ops/README.md)
  - [visual-ops/README.md](./visual-ops/README.md)

## Typical Reading Order

If you are new to the workspace, the fastest path is:

1. Read [ops4j/README.md](./ops4j/README.md).
2. Review [ops4j/ops4j_README.md](./ops4j/ops4j_README.md) for examples and concepts.
3. Move into the specific module README that matches the capability you need.

This root README is intentionally brief so it can stay focused on repository structure and navigation.
=======
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

>>>>>>> 4d377cd1db058729ddd1d888a7de1d29142d2a42
