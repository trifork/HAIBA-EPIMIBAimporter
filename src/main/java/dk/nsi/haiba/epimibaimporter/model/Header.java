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
package dk.nsi.haiba.epimibaimporter.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Header {
	long headerId;
	String cprnr;
	String extid;
	String refnr;
	String labnr;
	String lar;
	String pname;
	Date inDate;
	Date prDate;
	String result;
	String evaluationText;
	String usnr;
	String alnr;
	String stnr;
	String avd;
	String mgkod;
	String caseDef;
    List<Isolate> isolates = new ArrayList<Isolate>();
	List<Quantitative> quantitatives = new ArrayList<Quantitative>();
    private String commentText;
	
	public String getCaseDef() {
	    return caseDef;
	}
	public void setCaseDef(String caseDef) {
	    this.caseDef = caseDef;
	}
       
	public long getHeaderId() {
		return headerId;
	}
	public void setHeaderId(long headerId) {
		this.headerId = headerId;
	}
	public String getCprnr() {
		return cprnr;
	}
	public void setCprnr(String cprnr) {
		this.cprnr = cprnr;
	}
	public String getExtid() {
		return extid;
	}
	public void setExtid(String extid) {
		this.extid = extid;
	}
	public String getRefnr() {
		return refnr;
	}
	public void setRefnr(String refnr) {
		this.refnr = refnr;
	}
	public String getLabnr() {
		return labnr;
	}
	public void setLabnr(String labnr) {
		this.labnr = labnr;
	}
	public String getLar() {
		return lar;
	}
	public void setLar(String lar) {
		this.lar = lar;
	}
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}
	public Date getInDate() {
		return inDate;
	}
	public void setInDate(Date inDate) {
		this.inDate = inDate;
	}
	public Date getPrDate() {
		return prDate;
	}
	public void setPrDate(Date prDate) {
		this.prDate = prDate;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getEvaluationText() {
		return evaluationText;
	}
	public void setEvaluationText(String evaluationText) {
		this.evaluationText = evaluationText;
	}
	public String getUsnr() {
		return usnr;
	}
	public void setUsnr(String usnr) {
		this.usnr = usnr;
	}
	public String getAlnr() {
		return alnr;
	}
	public void setAlnr(String alnr) {
		this.alnr = alnr;
	}
	public String getStnr() {
		return stnr;
	}
	public void setStnr(String stnr) {
		this.stnr = stnr;
	}
	public String getAvd() {
		return avd;
	}
	public void setAvd(String avd) {
		this.avd = avd;
	}
	public String getMgkod() {
		return mgkod;
	}
	public void setMgkod(String mgkod) {
		this.mgkod = mgkod;
	}
	
	public List<Isolate> getIsolates() {
		return isolates;
	}
	public void setIsolates(List<Isolate> isolates) {
		this.isolates = isolates;
	}
	public void addIsolate(Isolate isolate) {
		isolates.add(isolate);
	}
	public List<Quantitative> getQuantitatives() {
		return quantitatives;
	}
	public void setQuantitatives(List<Quantitative> quantitatives) {
		this.quantitatives = quantitatives;
	}
	public void addQuantitative(Quantitative quantitative) {
		quantitatives.add(quantitative);
	}
    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }
    
    public String getCommentText() {
        return commentText;
    }
	
}
