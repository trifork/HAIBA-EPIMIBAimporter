package dk.nsi.haiba.epimibaimporter.dao;

import java.util.Collection;

import org.springframework.jdbc.core.JdbcTemplate;

public interface ClassificationCheckDAO {
    /**
     * Returns a list of classification ids not found but copied into the classification db from the source db
     * 
     * @param classificationIds
     *            - the id's to test in the classification table
     * @param classificationColumn
     *            - the column name of the classification id in the classification table
     * @param mapper
     *            - the mapper used to copy from source table to destination table (includes classification table name)
     * @return
     */
    public Collection<String> checkClassifications(Collection<String> classificationIds, String classificationColumn,
            ColumnMapper mapper);

    public interface ColumnMapper {
        /**
         * @return the table name of the classification table
         */
        public String getClassificationTable();

        /**
         * @return the table name of the source table
         */
        public String getSourceTable();

        /**
         * @return the source column names to copy from
         */
        public String[] getColumnsToCopy();

        /**
         * @param sourceColumn - the name of the source column to convert to a classification column
         * @return the corresponding name from the classification table 
         */
        public String getClassificationColumnForSourceColumn(String sourceColumn);

        /**
         * @return the column where the id is found in the source table
         */
        public String getSourceIdColumn();
    }
}
