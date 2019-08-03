package com.wemall.core.query;

import com.wemall.core.query.support.IPageList;
import com.wemall.core.query.support.IQuery;
import java.util.List;
import java.util.Map;

public class PageList
    implements IPageList {
    private int rowCount;
    private int pages;
    private int currentPage;
    private int pageSize;
    private List result;
    private IQuery query;

    
    	
    @Override
	public String toString() {
		return "PageList [rowCount=" + rowCount + ", pages=" + pages + ", currentPage=" + currentPage + ", pageSize="
				+ pageSize + ", result=" + result + ", query=" + query + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + currentPage;
		result = prime * result + pageSize;
		result = prime * result + pages;
		result = prime * result + ((query == null) ? 0 : query.hashCode());
		result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
		result = prime * result + rowCount;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PageList other = (PageList) obj;
		if (currentPage != other.currentPage)
			return false;
		if (pageSize != other.pageSize)
			return false;
		if (pages != other.pages)
			return false;
		if (query == null) {
			if (other.query != null)
				return false;
		} else if (!query.equals(other.query))
			return false;
		if (result == null) {
			if (other.result != null)
				return false;
		} else if (!result.equals(other.result))
			return false;
		if (rowCount != other.rowCount)
			return false;
		return true;
	}

	public PageList(){
    }

    public PageList(IQuery q){
        this.query = q;
    }

    public void setQuery(IQuery q){
        this.query = q;
    }

    public List getResult(){
        return this.result;
    }

    public void doList(int pageSize, int pageNo, String totalSQL, String queryHQL){
        doList(pageSize, pageNo, totalSQL, queryHQL, null);
    }

    public void doList(int pageSize, int pageNo, String totalSQL, String queryHQL, Map params){
        List rs = null;
        this.pageSize = pageSize;
        if (params != null) this.query.setParaValues(params);
        int total = this.query.getRows(totalSQL);

        if (total > 0){
            this.rowCount = total;
            this.pages = ((this.rowCount + pageSize - 1) / pageSize);
            int intPageNo = pageNo > this.pages ? this.pages : pageNo;
            if (intPageNo < 1)
                intPageNo = 1;
            this.currentPage = intPageNo;
            if (pageSize > 0){
                this.query.setFirstResult((intPageNo - 1) * pageSize);
                this.query.setMaxResults(pageSize);
            }
            rs = this.query.getResult(queryHQL);
        }
        this.result = rs;
    }

    public int getPages(){
        return this.pages;
    }

    public int getRowCount(){
        return this.rowCount;
    }

    public int getCurrentPage(){
        return this.currentPage;
    }

    public int getPageSize(){
        return this.pageSize;
    }

    public int getNextPage(){
        int p = this.currentPage + 1;
        if (p > this.pages) p = this.pages;

        return p;
    }

    public IQuery getQuery() {
		return query;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setResult(List result) {
		this.result = result;
	}

	public int getPreviousPage(){
        int p = this.currentPage - 1;
        if (p < 1) p = 1;

        return p;
    }
}
