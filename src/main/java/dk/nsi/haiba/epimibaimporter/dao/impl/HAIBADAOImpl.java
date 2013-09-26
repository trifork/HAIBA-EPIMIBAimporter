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

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import dk.nsi.haiba.epimibaimporter.dao.CommonDAO;
import dk.nsi.haiba.epimibaimporter.dao.HAIBADAO;
import dk.nsi.haiba.epimibaimporter.exception.DAOException;
import dk.nsi.haiba.epimibaimporter.log.Log;
import dk.nsi.haiba.epimibaimporter.model.Header;
import dk.nsi.haiba.epimibaimporter.model.Isolate;
import dk.nsi.haiba.epimibaimporter.model.Quantitative;

public class HAIBADAOImpl extends CommonDAO implements HAIBADAO {

	private static Log log = new Log(Logger.getLogger(HAIBADAOImpl.class));

	@Autowired
	@Qualifier("haibaJdbcTemplate")
	JdbcTemplate jdbc;

	@Override
	public void saveBakteriaemi(Header header, long transactionId) throws DAOException {

		try {
			log.debug("* Inserting Header");

			String sql = "INSERT INTO Header (HeaderId, Cprnr, Extid, Refnr, Labnr, Lar, Pname, Indate, Prdate, Result, Evaluation, Usnr, Alnr, Stnr, Avd, Mgkod) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			Object[] args = new Object[] {
				header.getHeaderId(),	
				header.getCprnr(),
				header.getExtid(),
				header.getRefnr(),
				header.getLabnr(),
				header.getLar(),
				header.getPname(),
				header.getInDate(),
				header.getPrDate(),
				header.getResult(),
				header.getEvaluationText(),
				header.getUsnr(),
				header.getAlnr(),
				header.getStnr(),
				header.getAvd(),
				header.getMgkod()
			};

			jdbc.update(sql, args);
			
			saveIsolates(header.getIsolates(), header.getHeaderId());
			saveQuantitatives(header.getQuantitatives(), header.getHeaderId());
			
			saveTransactionId(transactionId);
			log.debug("** Inserted Header");
		} catch (DataAccessException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	private void saveQuantitatives(List<Quantitative> quantitatives, long headerId) {

		log.debug("** Inserting "+quantitatives.size()+" Quantitatives");
		for (Quantitative q : quantitatives) {
			
			
			String sql = "INSERT INTO Quantitative (QuantitativeId, Analysis, Comment, EvaluationText, Qtnr, Quantity, HeaderId) VALUES (?, ?, ?, ?, ?, ?, ?)";

			Object[] args = new Object[] {
				q.getQuantitativeId(),
				q.getAnalysis(),
				q.getComment(),
				q.getEvaluationText(),
				q.getQtnr(),
				q.getQuantity(),
				headerId
			};
			jdbc.update(sql, args);
		}
		log.debug("** Inserted Quantitatives");
	}

	private void saveIsolates(List<Isolate> isolates, long headerId) {

		log.debug("** Inserting "+isolates.size()+" Isolates");
		for (Isolate isolate : isolates) {

			String sql = "INSERT INTO Isolate (IsolateId, Quantity, HeaderId) VALUES (?, ?, ?)";

			Object[] args = new Object[] {
				isolate.getIsolateId(),	
				isolate.getQuantity(),
				headerId
			};
			jdbc.update(sql, args);
		}
		log.debug("** Inserted Isolates");
		
	}

	@Override
	public void saveTransactionId(long transactionId) throws DAOException {

		String sql = "INSERT INTO EpimibaTransaction (TransactionId, TransactionProcessed) VALUES (?, ?)";
		jdbc.update(sql, transactionId, new Date());
	}
	
	@Override
	public synchronized long getLatestTransactionId() throws DAOException {
		long transactionId = -1;
		
		transactionId = jdbc.queryForLong("SELECT MAX(TransactionId) AS maxId FROM EpimibaTransaction");
		
		return transactionId;
	}
	
}
