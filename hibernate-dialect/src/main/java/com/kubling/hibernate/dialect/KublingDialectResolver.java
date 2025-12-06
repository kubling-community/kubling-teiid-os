package com.kubling.hibernate.dialect;

import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver;

public class KublingDialectResolver implements DialectResolver {

    @Override
    public Dialect resolveDialect(DialectResolutionInfo info) {
        if (info == null) {
            return null;
        }

        String dbName = info.getDatabaseName();

        // In the future, we might want to have multiple dialects depending on the version
//        int major = info.getDatabaseMajorVersion();
//        int minor = info.getDatabaseMinorVersion();

        // Defined in com.kubling.teiid.jdbc.ConnectionImpl
        if (dbName != null && dbName.startsWith("Kubling DBVirt Server")) {
            return new KublingDialect();
        }

        return null;
    }

}
