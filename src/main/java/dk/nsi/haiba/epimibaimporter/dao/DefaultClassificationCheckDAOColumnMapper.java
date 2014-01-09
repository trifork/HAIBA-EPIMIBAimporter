package dk.nsi.haiba.epimibaimporter.dao;

import dk.nsi.haiba.epimibaimporter.dao.ClassificationCheckDAO.ColumnMapper;

public class DefaultClassificationCheckDAOColumnMapper implements ColumnMapper {
    private String aClassificationTable;
    private String aSourceTable;
    private String[] aColumnsTpCopy;
    private String aSourceIdColumn;

    public DefaultClassificationCheckDAOColumnMapper(String classificationTable, String sourceTable, String[] columnsToCopy, String sourceIdColumn) {
        aClassificationTable = classificationTable;
        aSourceTable = sourceTable;
        aColumnsTpCopy = columnsToCopy;
        aSourceIdColumn = sourceIdColumn;
    }

    @Override
    public String getClassificationTable() {
        return aClassificationTable;
    }

    @Override
    public String getSourceTable() {
        return aSourceTable;
    }

    @Override
    public String[] getColumnsToCopy() {
        return aColumnsTpCopy;
    }

    @Override
    public String getClassificationColumnForSourceColumn(String sourceColumn) {
        return sourceColumn;
    }

    @Override
    public String getSourceIdColumn() {
        return aSourceIdColumn;
    }

}
