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

import java.util.List;
import java.util.Set;

import dk.nsi.haiba.epimibaimporter.exception.DAOException;
import dk.nsi.haiba.epimibaimporter.model.CaseDef;
import dk.nsi.haiba.epimibaimporter.model.Classification;
import dk.nsi.haiba.epimibaimporter.model.Header;


public interface HAIBADAO {
	long getLatestTransactionId(int caseDef) throws DAOException;

	void saveHeader(Header header, long transactionId, int caseDef) throws DAOException;

	void saveAnalysis(List<Classification> codeList) throws DAOException;

	void saveInvestigation(List<Classification> codeList) throws DAOException;

	void saveLabSection(List<Classification> codeList) throws DAOException;

	void saveLocation(List<Classification> codeList) throws DAOException;

	void saveOrganization(List<Classification> codeList) throws DAOException;

	void saveMicroorganism(List<Classification> codeList) throws DAOException;

	void clearAnalysisTable() throws DAOException;

	void clearInvestigationTable() throws DAOException;

	void clearLabSectionTable() throws DAOException;

	void clearLocationTable() throws DAOException;

	void clearOrganizationTable() throws DAOException;

	void clearMicroorganismTable() throws DAOException;

    CaseDef[] getCaseDefs();

    Set<String> getAndCopyUnknownBanrSet(Set<String> banrInNewAnswers);

    Set<String> getAndCopyUnknownAlnrSet(Set<String> banrInNewAnswers);
}