package com.kubling.teiid.core.types.basic;

import com.kubling.teiid.core.CorePlugin;
import com.kubling.teiid.core.types.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

public class ClobToJsonTransform extends Transform {

    @Override
    protected Object transformDirect(Object value) throws TransformationException {
        BaseClobType source = (BaseClobType) value;
        try (BufferedReader reader = new BufferedReader(source.getCharacterStream())) {
            StringBuilder contents = new StringBuilder();

            int chr = reader.read();
            while (chr != -1 && contents.length() < DataTypeManager.MAX_JSON_LENGTH) {
                contents.append((char) chr);
                chr = reader.read();
            }
            return new JsonType(new ClobImpl(contents.toString()));
        } catch (SQLException | IOException e) {
            throw new TransformationException(CorePlugin.Event.TEIID10080, e,
                    CorePlugin.Util.gs(CorePlugin.Event.TEIID10080, getSourceType().getName(), getTargetType().getName()));
        }
    }

    @Override
    public Class<?> getSourceType() {
        return DataTypeManager.DefaultDataClasses.CLOB;
    }

    @Override
    public Class<?> getTargetType() {
        return DataTypeManager.DefaultDataClasses.JSON;
    }
}
