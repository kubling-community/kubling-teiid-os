# Hibernate Dialect

It currently supports Hibernate 6.1.7, but we are planning to move directly to version 7 once it is released, tested and
adopted mostly by Spring and Quarkus.

Take into account that from version 6.1.x to 6.2.x, the `Dialect` base class changed some important methods like `initializeFunctionRegistry`,
therefore if you need support for versions >= 6.2.x, you just need to register the same functions and column types
following the new API model, that is, even though we haven't tested it, from `Kubling` viewpoint it should not change.

