# Ops4J Workspace

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