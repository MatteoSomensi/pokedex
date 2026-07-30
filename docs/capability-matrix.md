# Capability matrix

| Capability | Status | Verification |
|---|---|---|
| Multi-module boundaries | Production-ready | `verifyModuleBoundaries` |
| Feature API contract | Production-ready baseline | `:features:favorite:api:verifyApiContract` |
| Compose adaptive UI | Demonstrative | canonical-size screenshot matrix |
| Room/Paging offline cache | Production-ready pattern | repository, DAO, migration tests |
| Demo authentication | Production-ready for local development | managed-device E2E |
| Firebase authentication/telemetry | Demonstrative | opt-in adapter; requires local project |
| WorkManager synchronization | Demonstrative | JVM orchestration tests |
| Navigation 3/list-detail | Experimental | compile/UI journey; API remains experimental |
| App Functions | Experimental | compiler integration |
| Preview Screenshot Testing | Experimental | CI validation |
| Glance widget | Demonstrative | static image limitation |
| Macrobenchmark/Baseline Profile | Production-ready tooling | dedicated benchmark module |
| Supply-chain controls | Production-ready baseline | pinned actions, dependency review, SBOM, checksums |

`SessionManager.refreshToken()` remains a documented stub because PokeAPI has no token endpoint.
Search/filter behavior remains educational rather than a scalable server-side query design.
