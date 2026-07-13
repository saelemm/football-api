package com.foot.adapter.persistence.jpa.type;

import entity.PositionEnum;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Custom Hibernate UserType for PostgreSQL ENUM types.
 * Handles conversion between Java PositionEnum and PostgreSQL player_position ENUM.
 */
public class PostgreSQLEnumUserType implements UserType<PositionEnum> {

    @Override
    public int getSqlType() {
        return Types.OTHER;
    }

    @Override
    public Class<PositionEnum> returnedClass() {
        return PositionEnum.class;
    }

    @Override
    public boolean equals(PositionEnum x, PositionEnum y) {
        return x == y;
    }

    @Override
    public int hashCode(PositionEnum x) {
        return x == null ? 0 : x.hashCode();
    }

    @Override
    public PositionEnum nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner)
            throws SQLException {
        String value = rs.getString(position);
        if (rs.wasNull()) {
            return null;
        }
        return PositionEnum.valueOf(value);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, PositionEnum value, int index, SharedSessionContractImplementor session)
            throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            st.setObject(index, value.name(), Types.OTHER);
        }
    }

    @Override
    public PositionEnum deepCopy(PositionEnum value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(PositionEnum value) {
        return value == null ? null : value.name();
    }

    @Override
    public PositionEnum assemble(Serializable cached, Object owner) {
        return cached == null ? null : PositionEnum.valueOf((String) cached);
    }

    @Override
    public PositionEnum replace(PositionEnum detached, PositionEnum managed, Object owner) {
        return detached;
    }
}

