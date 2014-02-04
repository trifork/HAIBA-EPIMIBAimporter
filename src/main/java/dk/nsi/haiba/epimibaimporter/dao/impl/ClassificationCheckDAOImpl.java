/**
 * The MIT License
 *
 * Original work sponsored and donated by National Board of e-Health (NSI), Denmark
 * (http://www.nsi.dk)
 *
 * Copyright (C) 2011 National Board of e-Health (NSI), Denmark (http://www.nsi.dk)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dk.nsi.haiba.epimibaimporter.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import dk.nsi.haiba.epimibaimporter.dao.ClassificationCheckDAO;
import dk.nsi.haiba.epimibaimporter.dao.CommonDAO;
import dk.nsi.haiba.epimibaimporter.exception.DAOException;
import dk.nsi.haiba.epimibaimporter.log.Log;

public class ClassificationCheckDAOImpl extends CommonDAO implements ClassificationCheckDAO {
    private static Log log = new Log(Logger.getLogger(ClassificationCheckDAOImpl.class));
    private JdbcTemplate aClassificationJdbc;
    private JdbcTemplate aSourceJdbc;

    public ClassificationCheckDAOImpl(JdbcTemplate classificationJdbc, JdbcTemplate source) {
        aClassificationJdbc = classificationJdbc;
        aSourceJdbc = source;
    }

    private boolean rowExists(String columnName, String value, String tableName) {
        String sql = null;
        if (MYSQL.equals(getDialect())) {
            sql = "SELECT " + columnName + " FROM " + tableName + " where " + columnName + "=? LIMIT 1";
        } else {
            // MSSQL
            sql = "SELECT TOP 1 " + columnName + " FROM " + tableName + " where " + columnName + "=?";
        }

        try {
            aClassificationJdbc.queryForObject(sql, String.class, new Object[] { value });
            return true;
        } catch (EmptyResultDataAccessException e) {
            // ignore - does not exist
        } catch (RuntimeException e) {
            throw new DAOException("Error testing existence of " + value + "@" + columnName + " in " + tableName, e);
        }
        return false;
    }

    @Override
    public Collection<String> checkClassifications(Collection<String> classificationIds, String classificationColumn,
            final ColumnMapper mapper) {
        Set<String> returnValue = new HashSet<String>();
        for (String id : classificationIds) {
            if (!rowExists(classificationColumn, id, mapper.getClassificationTable())) {
                returnValue.add(id);
            }
        }
        if (!returnValue.isEmpty()) {
            // copy
            for (String unknownId : returnValue) {
                String sql = "SELECT " + getColumnsSql(mapper.getColumnsToCopy()) + " FROM " + mapper.getSourceTable()
                        + " WHERE " + mapper.getSourceIdColumn() + "=?";
                log.debug("checkClassifications: query sql=" + sql);
                try {
                    aSourceJdbc.query(sql, new RowCallbackHandler() {
                        @Override
                        public void processRow(ResultSet rs) throws SQLException {
                            String[] columnsToCopy = mapper.getColumnsToCopy();
                            String destColumns = getDestColumns(mapper);
                            Object[] values = getValues(rs, columnsToCopy);
                            String sql = "INSERT INTO " + mapper.getClassificationTable() + "(" + destColumns
                                    + ") VALUES (" + getPlaceHoldersSql(columnsToCopy.length) + ")";
                            log.debug("checkClassifications: insert sql=" + sql);
                            aClassificationJdbc.update(sql, values);
                        }
                    }, unknownId);
                } catch (Exception e) {
                    log.error("not able to execute " + sql, e);
                }
            }
        }
        return returnValue;
    }

    protected Object[] getValues(ResultSet rs, String[] columnsToCopy) {
        Object[] returnValue = new Object[columnsToCopy.length];
        for (int i = 0; i < returnValue.length; i++) {
            try {
                returnValue[i] = rs.getObject(columnsToCopy[i]);
            } catch (SQLException e) {
                log.error("unable to extract value for column " + columnsToCopy[i], e);
            }
        }
        return returnValue;
    }

    protected String getDestColumns(ColumnMapper mapper) {
        String[] columnsToCopy = mapper.getColumnsToCopy();
        String destColumns = "";
        for (int i = 0; i < columnsToCopy.length; i++) {
            destColumns += mapper.getClassificationColumnForSourceColumn(columnsToCopy[i]);
            if (i < columnsToCopy.length - 1) {
                destColumns += ",";
            }
        }
        return destColumns;
    }

    private String getColumnsSql(String[] columnsToCopy) {
        String sql = "";
        for (int i = 0; i < columnsToCopy.length; i++) {
            sql += columnsToCopy[i];
            if (i < columnsToCopy.length - 1) {
                sql += ",";
            }
        }
        return sql;
    }

    protected String getPlaceHoldersSql(int length) {
        String returnValue = "";
        for (int i = 0; i < length; i++) {
            returnValue += "?";
            if (i < length - 1) {
                returnValue += ",";
            }
        }
        return returnValue;
    }
}
