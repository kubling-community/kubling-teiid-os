# Kubling Hibernate Dialect

This module provides the Hibernate ORM dialect for Kubling and publishes a `DialectResolver` for automatic discovery.
The supported Hibernate version is managed centrally in the repository root `pom.xml`.

Add the module to the application dependencies:

```xml
<dependency>
    <groupId>com.kubling</groupId>
    <artifactId>kubling-hibernate-dialect</artifactId>
    <version>${kubling.version}</version>
</dependency>
```

Hibernate can resolve the dialect from Kubling database metadata. It can also be configured explicitly with:

```properties
hibernate.dialect=com.kubling.hibernate.dialect.KublingDialect
```
