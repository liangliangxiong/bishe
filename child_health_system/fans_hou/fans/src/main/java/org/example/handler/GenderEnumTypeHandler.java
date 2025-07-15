package org.example.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.example.enums.GenderEnum;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GenderEnumTypeHandler extends BaseTypeHandler<GenderEnum> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, GenderEnum parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public GenderEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int code = rs.getInt(columnName);
        return getGenderEnum(code);
    }

    @Override
    public GenderEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int code = rs.getInt(columnIndex);
        return getGenderEnum(code);
    }

    @Override
    public GenderEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int code = cs.getInt(columnIndex);
        return getGenderEnum(code);
    }

    private GenderEnum getGenderEnum(int code) {
        for (GenderEnum gender : GenderEnum.values()) {
            if (gender.getCode() == code) {
                return gender;
            }
        }
        return GenderEnum.UNKNOWN;  // 默认返回未知性别
    }
} 