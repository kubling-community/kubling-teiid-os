# Kubling Core and Client

[![Kubling license](https://img.shields.io/badge/license-Apache%202.0-blue.svg?style=flat-square)](LICENSE)

This repository contains the shared JVM foundation used by the Kubling engine and its Java integrations. It publishes
the runtime data types, serialization contracts, JDBC client, Hibernate dialect, and Testcontainers integration needed
to communicate with Kubling.

Several artifacts in this repository cross process boundaries. Changes to public types, serialized classes, JDBC URLs,
or socket messages must therefore be coordinated with the Kubling server release that adopts them.

## Modules

| Module | Artifact | Purpose |
| --- | --- | --- |
| `common-core` | `com.kubling:kubling-common-core` | Shared runtime types, conversions, LOB support, serialization, and core utilities. |
| `client` | `com.kubling:kubling-client` | JDBC, XA, authentication, socket transport, requests, results, and metadata. |
| `hibernate-dialect` | `com.kubling:kubling-hibernate-dialect` | Hibernate ORM dialect and automatic dialect resolution. |
| `test-container` | `com.kubling:kubling-test-container` | Testcontainers integration for Java applications. |
| `build` | `com.kubling:kubling` | JDBC and source distribution assemblies. |

## Build

The project targets Java 21 and includes a Maven Wrapper:

```bash
./mvnw verify
```

Distribution assemblies are enabled explicitly:

```bash
./mvnw -Pdriver-release package
```

## Publishing

Maven Central releases are published by the `Publish to Maven Central` GitHub Actions workflow. Publishing starts
automatically when a GitHub Release is published, or manually with a required version input. In both cases, the
release version must match the Maven project version and snapshots are rejected.

Configure these GitHub Actions secrets before the first publication:

- `MAVEN_CENTRAL_USERNAME`: username generated with a Central Portal user token.
- `MAVEN_CENTRAL_PASSWORD`: password generated with the same Central Portal user token.
- `MAVEN_GPG_PRIVATE_KEY`: ASCII-armored private key used to sign the artifacts.
- `MAVEN_GPG_PASSPHRASE`: passphrase for the private key.

## Versioning

This repository has its own release cycle. Artifact versions do not need to match the Kubling server version; each
server release declares the exact versions it consumes.

## License

Licensed under the [Apache License 2.0](LICENSE). See [COPYRIGHT.txt](COPYRIGHT.txt) for upstream attribution.
