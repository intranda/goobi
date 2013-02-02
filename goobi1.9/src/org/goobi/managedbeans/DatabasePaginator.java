package org.goobi.managedbeans;
/**
 * This file is part of the Goobi Application - a Workflow tool for the support of mass digitization.
 * 
 * Visit the websites for more information. 
 *     		- http://www.goobi.org
 *     		- http://launchpad.net/goobi-production
 * 		    - http://gdz.sub.uni-goettingen.de
 * 			- http://www.intranda.com
 * 			- http://digiverso.com 
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Linking this library statically or dynamically with other modules is making a combined work based on this library. Thus, the terms and conditions
 * of the GNU General Public License cover the whole combination. As a special exception, the copyright holders of this library give you permission to
 * link this library with independent modules to produce an executable, regardless of the license terms of these independent modules, and to copy and
 * distribute the resulting executable under terms of your choice, provided that you also meet, for each linked independent module, the terms and
 * conditions of the license of that module. An independent module is a module which is not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the library, but you are not obliged to do so. If you do not wish to do so, delete this
 * exception statement from your version.
 */
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.goobi.beans.DatabaseObject;

import de.sub.goobi.forms.LoginForm;
import de.sub.goobi.helper.Helper;
import de.sub.goobi.helper.exceptions.DAOException;
import de.sub.goobi.persistence.managers.IManager;

public class DatabasePaginator implements Serializable { 
		private static final long serialVersionUID = 1571881092118205104L;
	private static final Logger logger = Logger.getLogger(DatabasePaginator.class);
	private List<? extends DatabaseObject> results;
	private int pageSize = 0;
	private int page = 0;
	private int totalResults = 0;
	private String order = "";
	private HashMap<String, String> filter = new HashMap<String, String>();
	private IManager manager;
	
	public DatabasePaginator(String order, HashMap<String, String> filter,
			IManager manager) {
		this.page = 0;
		LoginForm login = (LoginForm) Helper.getManagedBeanValue("#{LoginForm}");
        if (login == null || login.getMyBenutzer() == null) {
        	this.pageSize = 10;
		} else {
			this.pageSize = login.getMyBenutzer().getTabellengroesse().intValue();
		}
		this.order = order;
		this.filter = filter;
		this.manager = manager;
		try {
			totalResults = manager.getHitSize(order, filter);
		} catch (DAOException e) {
			logger.error("Failed to count results", e);
		}
	}

	public int getLastPageNumber() {
		int ret = new Double(Math.floor(this.totalResults / this.pageSize)).intValue();
		if (this.totalResults % this.pageSize == 0) {
			ret--;
		}
		return ret;
	}

	public List<? extends DatabaseObject> getList() {
		return hasNextPage() ? this.results.subList(0, this.pageSize) : this.results;
	}

	public int getTotalResults() {	
		return this.totalResults;
	}

	public int getFirstResultNumber() {
		return this.page * this.pageSize + 1;
	}

	public int getLastResultNumber() {
		int fullPage = getFirstResultNumber() + this.pageSize - 1;
		return getTotalResults() < fullPage ? getTotalResults() : fullPage;
	}

	public List<? extends DatabaseObject> getListReload() {
		try {
			results = manager.getList(order, filter, this.page * this.pageSize , pageSize);
		} catch (DAOException e) {
			logger.error("Failed to load paginated results", e);
		}
		return results;
	}

	/*
	 * einfache Navigationsaufgaben
	 */

	public boolean isFirstPage() {
		return this.page == 0;
	}

	public boolean isLastPage() {
		return this.page >= getLastPageNumber();
	}

	public boolean hasNextPage() {
		return this.results.size() > this.pageSize;
	}

	public boolean hasPreviousPage() {
		return this.page > 0;
	}

	public Long getPageNumberCurrent() {
		return Long.valueOf(this.page + 1);
	}

	public Long getPageNumberLast() {
		return Long.valueOf(getLastPageNumber() + 1);
	}

	public String cmdMoveFirst() {
		this.page = 0;
		return "";
	}

	public String cmdMovePrevious() {
		if (!isFirstPage()) {
			this.page--;
		}
		return "";
	}

	public String cmdMoveNext() {
		if (!isLastPage()) {
			this.page++;
		}
		return "";
	}

	public String cmdMoveLast() {
		this.page = getLastPageNumber();
		return "";
	}

	public void setTxtMoveTo(int neueSeite) {
		if (neueSeite > 0 && neueSeite <= getLastPageNumber() + 1) {
			this.page = neueSeite - 1;
		}
	}

	public int getTxtMoveTo() {
		return this.page + 1;
	}

}
