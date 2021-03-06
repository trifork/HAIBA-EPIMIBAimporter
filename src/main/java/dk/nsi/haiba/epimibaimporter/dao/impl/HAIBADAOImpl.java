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
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import dk.nsi.haiba.epimibaimporter.dao.CommonDAO;
import dk.nsi.haiba.epimibaimporter.dao.HAIBADAO;
import dk.nsi.haiba.epimibaimporter.exception.DAOException;
import dk.nsi.haiba.epimibaimporter.log.Log;
import dk.nsi.haiba.epimibaimporter.model.CaseDef;
import dk.nsi.haiba.epimibaimporter.model.Classification;
import dk.nsi.haiba.epimibaimporter.model.Header;
import dk.nsi.haiba.epimibaimporter.model.Isolate;
import dk.nsi.haiba.epimibaimporter.model.Quantitative;

@Transactional("haibaTransactionManager")
public class HAIBADAOImpl extends CommonDAO implements HAIBADAO {
    private static Log log = new Log(Logger.getLogger(HAIBADAOImpl.class));

    @Autowired
    @Qualifier("haibaJdbcTemplate")
    JdbcTemplate jdbc;

    @Value("${jdbc.haibatableprefix:}")
    private String haibaTablePrefix;

    @Override
    public void saveHeader(Header header, long transactionId, int caseDef) throws DAOException {

        try {

            String sql = null;
            if (headerIdExists(header.getHeaderId(), "Header")) {
                log.debug("* Updating Header for caseDef " + caseDef);
                sql = "UPDATE "
                        + haibaTablePrefix
                        + "Header set Cprnr=?, Extid=?, Refnr=?, Labnr=?, Lar=?, Pname=?, Indate=?, Prdate=?, Result=?, Evaluation=?, Usnr=?, Alnr=?, Stnr=?, Avd=?, Mgkod=?, CommentText=?, HAIBACaseDef=? where HeaderId=?";
            } else {
                log.debug("* Inserting Header");
                sql = "INSERT INTO "
                        + haibaTablePrefix
                        + "Header (Cprnr, Extid, Refnr, Labnr, Lar, Pname, Indate, Prdate, Result, Evaluation, Usnr, Alnr, Stnr, Avd, Mgkod, CommentText, HAIBACaseDef, HeaderId) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            }

            Object[] args = new Object[] { header.getCprnr(), header.getExtid(), header.getRefnr(), header.getLabnr(),
                    header.getLar(), header.getPname(), header.getInDate(), header.getPrDate(), header.getResult(),
                    header.getEvaluationText(), header.getUsnr(), header.getAlnr(), header.getStnr(), header.getAvd(),
                    header.getMgkod(), header.getCommentText(), header.getCaseDef(), header.getHeaderId()};

            jdbc.update(sql, args);

            // XXX also include CaseDef, tlf. 31 jan
            saveIsolates(header.getIsolates(), header.getHeaderId());
            saveQuantitatives(header.getQuantitatives(), header.getHeaderId());

            saveTransactionId(transactionId, caseDef);
            log.debug("** Inserted Header");
        } catch (DataAccessException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    private void saveQuantitatives(List<Quantitative> quantitatives, long headerId) {

        log.debug("** Saving " + quantitatives.size() + " Quantitatives");
        for (Quantitative q : quantitatives) {

            String sql = null;
            if (quantitativeIdExists(q.getQuantitativeId(), headerId, "Quantitative")) {
                sql = "UPDATE "
                        + haibaTablePrefix
                        + "Quantitative set Analysis=?, Comment=?, EvaluationText=?, Qtnr=?, Quantity=?, HeaderId=? where QuantitativeId=?";
            } else {
                sql = "INSERT INTO "
                        + haibaTablePrefix
                        + "Quantitative (Analysis, Comment, EvaluationText, Qtnr, Quantity, HeaderId, QuantitativeId) VALUES (?, ?, ?, ?, ?, ?, ?)";
            }

            Object[] args = new Object[] { q.getAnalysis(), q.getComment(), q.getEvaluationText(), q.getQtnr(),
                    q.getQuantity(), headerId, q.getQuantitativeId() };
            jdbc.update(sql, args);
        }
        log.debug("** Saved Quantitatives");
    }

    private void saveIsolates(List<Isolate> isolates, long headerId) {

        log.debug("** Saving " + isolates.size() + " Isolates");
        for (Isolate isolate : isolates) {

            String sql = null;
            if (isolateIdExists(isolate.getIsolateId(), headerId, "Isolate")) {
                sql = "UPDATE " + haibaTablePrefix + "Isolate set Quantity=?, HeaderId=?, Banr=? where IsolateId=?";
            } else {
                sql = "INSERT INTO " + haibaTablePrefix
                        + "Isolate (Quantity, HeaderId, Banr, IsolateId) VALUES (?, ?, ?, ?)";
            }

            Object[] args = new Object[] { isolate.getQuantity(), headerId, isolate.getBanr(), isolate.getIsolateId() };
            jdbc.update(sql, args);
        }
        log.debug("** Saved Isolates");

    }

    private void saveTransactionId(long transactionId, int transactionType) throws DAOException {

        String sql = "INSERT INTO " + haibaTablePrefix
                + "EpimibaTransaction (TransactionId, TransactionProcessed, TransactionType) VALUES (?, ?, ?)";
        jdbc.update(sql, transactionId, new Date(), transactionType);
    }

    @Override
    public synchronized long getLatestTransactionId(int transactionType) throws DAOException {
        long transactionId = -1;

        transactionId = jdbc.queryForLong("SELECT MAX(TransactionId) AS maxId FROM " + haibaTablePrefix
                + "EpimibaTransaction where transactionType = ?", transactionType);

        return transactionId;
    }

    @Override
    public void clearAnalysisTable() throws DAOException {
        try {
            jdbc.update("DELETE FROM " + haibaTablePrefix + "TabAnalysis");
        } catch (Exception e) {
            throw new DAOException("", e);
        }
    }

    @Override
    public void clearInvestigationTable() throws DAOException {
        try {
            jdbc.update("DELETE FROM " + haibaTablePrefix + "TabInvestigation");
        } catch (Exception e) {
            throw new DAOException("", e);
        }
    }

    @Override
    public void clearLabSectionTable() throws DAOException {
        try {
            jdbc.update("DELETE FROM " + haibaTablePrefix + "TabLabSection");
        } catch (Exception e) {
            throw new DAOException("", e);
        }
    }

    @Override
    public void clearLocationTable() throws DAOException {
        try {
            jdbc.update("DELETE FROM " + haibaTablePrefix + "TabLocation");
        } catch (Exception e) {
            throw new DAOException("", e);
        }
    }

    @Override
    public void clearOrganizationTable() throws DAOException {
        try {
            jdbc.update("DELETE FROM " + haibaTablePrefix + "TabOrganization");
        } catch (Exception e) {
            throw new DAOException("", e);
        }
    }

    @Override
    public void clearMicroorganismTable() throws DAOException {
        try {
            jdbc.update("DELETE FROM " + haibaTablePrefix + "Tabmicroorganism");
        } catch (Exception e) {
            throw new DAOException("", e);
        }
    }

    @Override
    public void saveAnalysis(List<Classification> codeList) throws DAOException {

        log.debug("** Inserting " + codeList.size() + " Analysis classifications");
        for (Classification c : codeList) {

            String sql = "INSERT INTO " + haibaTablePrefix + "TabAnalysis (TabAnalysisId, Qtnr, Text) VALUES(?,?,?)";

            Object[] args = new Object[] { c.getId(), c.getCode(), c.getText() };
            jdbc.update(sql, args);
        }
        log.debug("** Inserted Analysis classifications");
    }

    @Override
    public void saveInvestigation(List<Classification> codeList) throws DAOException {

        log.debug("** Inserting " + codeList.size() + " Investigation classifications");
        for (Classification c : codeList) {

            String sql = "INSERT INTO " + haibaTablePrefix
                    + "TabInvestigation (TabInvestigationId, Usnr, Text) VALUES(?,?,?)";

            Object[] args = new Object[] { c.getId(), c.getCode(), c.getText() };
            jdbc.update(sql, args);
        }
        log.debug("** Inserted Investigation classifications");
    }

    @Override
    public void saveLabSection(List<Classification> codeList) throws DAOException {

        log.debug("** Inserting " + codeList.size() + " LabSection classifications");
        for (Classification c : codeList) {

            String sql = "INSERT INTO " + haibaTablePrefix + "TabLabSection (TabLabSectionId, Avd, Text) VALUES(?,?,?)";

            Object[] args = new Object[] { c.getId(), c.getCode(), c.getText() };
            jdbc.update(sql, args);
        }
        log.debug("** Inserted LabSection classifications");

    }

    @Override
    public void saveLocation(List<Classification> codeList) throws DAOException {

        log.debug("** Inserting " + codeList.size() + " Location classifications");
        for (Classification c : codeList) {

            String sql = "INSERT INTO " + haibaTablePrefix + "TabLocation (TabLocationId, Alnr, Text) VALUES(?,?,?)";

            Object[] args = new Object[] { c.getId(), c.getCode(), c.getText() };
            jdbc.update(sql, args);
        }
        log.debug("** Inserted Location classifications");

    }

    @Override
    public void saveOrganization(List<Classification> codeList) throws DAOException {

        log.debug("** Inserting " + codeList.size() + " Organization classifications");
        for (Classification c : codeList) {

            String sql = "INSERT INTO " + haibaTablePrefix
                    + "TabOrganization (TabOrganizationId, Mgkod, Text) VALUES(?,?,?)";

            Object[] args = new Object[] { c.getId(), c.getCode(), c.getText() };
            jdbc.update(sql, args);
        }
        log.debug("** Inserted Organization classifications");
    }

    @Override
    public void saveMicroorganism(List<Classification> codeList) throws DAOException {
        log.debug("** Inserting " + codeList.size() + " Microorganism classifications");
        for (Classification c : codeList) {

            String sql = "INSERT INTO " + haibaTablePrefix
                    + "Tabmicroorganism (TabMicroorganismId, Banr, Text) VALUES(?,?,?)";

            Object[] args = new Object[] { c.getId(), c.getCode(), c.getText() };
            jdbc.update(sql, args);
        }
        log.debug("** Inserted Microorganism classifications");
    }

    private boolean headerIdExists(long headerId, String tableName) {
        String sql = null;
        if (MYSQL.equals(getDialect())) {
            sql = "SELECT HeaderId FROM " + tableName + " where HeaderID=? LIMIT 1";
        } else {
            // MSSQL
            sql = "SELECT TOP 1 HeaderId FROM " + haibaTablePrefix + tableName + " where HeaderID=?";
        }

        try {
            jdbc.queryForLong(sql, new Object[] { headerId });
            return true;
        } catch (EmptyResultDataAccessException e) {
            // ignore - no headerID exists
        } catch (RuntimeException e) {
            throw new DAOException("Error Fetching HeaderID from " + tableName + ", sql=" + sql, e);
        }
        return false;
    }

    private boolean isolateIdExists(long isolateId, long headerId, String tableName) {
        String sql = null;
        if (MYSQL.equals(getDialect())) {
            sql = "SELECT isolateId FROM " + tableName + " where isolateID=? and headerId=? LIMIT 1";
        } else {
            // MSSQL
            sql = "SELECT TOP 1 isolateId FROM " + haibaTablePrefix + tableName + " where isolateID=? and headerId=?";
        }

        try {
            jdbc.queryForLong(sql, new Object[] { isolateId, headerId });
            return true;
        } catch (EmptyResultDataAccessException e) {
            // ignore - no isolateID exists
        } catch (RuntimeException e) {
            throw new DAOException("Error Fetching isolateID from " + tableName + ", sql=" + sql, e);
        }
        return false;
    }

    private boolean quantitativeIdExists(long quantitativeId, long headerId, String tableName) {
        String sql = null;
        if (MYSQL.equals(getDialect())) {
            sql = "SELECT QuantitativeId FROM " + tableName + " where QuantitativeId=? and headerId=? LIMIT 1";
        } else {
            // MSSQL
            sql = "SELECT TOP 1 QuantitativeId FROM " + haibaTablePrefix + tableName
                    + " where QuantitativeId=? and headerId=?";
        }

        try {
            jdbc.queryForLong(sql, new Object[] { quantitativeId, headerId });
            return true;
        } catch (EmptyResultDataAccessException e) {
            // ignore - no QuantitativeId exists
        } catch (RuntimeException e) {
            throw new DAOException("Error Fetching QuantitativeId from " + tableName + ", sql=" + sql, e);
        }
        return false;
    }

    @Override
    public CaseDef[] getCaseDefs() {
        log.debug("** querying for CaseDef's");
        String sql = "SELECT * FROM " + haibaTablePrefix + "CaseDef";
        List<CaseDef> returnValue = jdbc.query(sql, new RowMapper<CaseDef>() {
            @Override
            public CaseDef mapRow(ResultSet rs, int rowNum) throws SQLException {
                CaseDef returnValue = new CaseDef();
                returnValue.setId(rs.getInt("id"));
                returnValue.setText(rs.getString("text"));
                return returnValue;
            }
        });
        return returnValue.toArray(new CaseDef[0]);
    }

    @Override
    public Collection<String> getAllAlnr() {
        Collection<String> returnValue = null;
        log.debug("** querying for Alnr");
        String sql = "SELECT DISTINCT Alnr FROM " + haibaTablePrefix + "Header";
        returnValue = jdbc.queryForList(sql, String.class);
        log.trace("getAllAlnr returns " + returnValue);
        return returnValue;
    }

    @Override
    public Collection<String> getAllBanr() {
        Collection<String> returnValue = null;
        log.debug("** querying for Banr");
        String sql = "SELECT DISTINCT Banr FROM " + haibaTablePrefix + "Isolate";
        returnValue = jdbc.queryForList(sql, String.class);
        log.trace("getAllBanr returns " + returnValue);
        return returnValue;
    }
}
