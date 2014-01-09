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
package dk.nsi.haiba.epimibaimporter.dao;

import java.util.Collection;

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
         * @param sourceColumn
         *            - the name of the source column to convert to a classification column
         * @return the corresponding name from the classification table
         */
        public String getClassificationColumnForSourceColumn(String sourceColumn);

        /**
         * @return the column where the id is found in the source table
         */
        public String getSourceIdColumn();
    }
}
