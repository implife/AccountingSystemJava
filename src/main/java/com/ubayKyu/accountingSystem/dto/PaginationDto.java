package com.ubayKyu.accountingSystem.dto;

import java.util.logging.Logger;

public class PaginationDto {
    private String url;
    private int totalItemSize;
    private int itemSizeInPage = 10;
    private int pagerLinkCount = 9;

    private int currentPage;
    private int pageCount;
    private int startIndex;
    private int totalPages;

    private int _allowFront = 4;
    private int _allowBack = 4;

    private Logger logger = Logger.getLogger(PaginationDto.class.getName());

    public PaginationDto(String url, int totalItemSize, int currentPage) {
        this.url = url;
        this.setTotalItemSize(totalItemSize);

        // 計算total pages
        this.totalPages = this.countTotalPages();

        this.setCurrentPage(currentPage);

        // 計算startIndex
        this.startIndex = this.countStartIndex();

        // 計算pageCount
       this.pageCount = this.countPageCount();
    }

    public PaginationDto(String url, int totalItemSize, int currentPage, int itemSizeInPage, int pagerLinkCount) {
        this.url = url;
        this.setTotalItemSize(totalItemSize);

        this.setItemSizeInPage(itemSizeInPage);
        this.setPagerLinkCount(pagerLinkCount);

        // 計算total pages
        this.totalPages = this.countTotalPages();

        this.setCurrentPage(currentPage);

        // 計算startIndex
        this.startIndex = this.countStartIndex();

        // 計算pageCount
       this.pageCount = this.countPageCount();
    }
    
    // getter and setter
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    // private setter
    private void setTotalItemSize(int totalItemSize) {
        // 不可 < 0
        if(totalItemSize < 0){
            this.totalItemSize = 0;
            logger.info("totalItemSize can't be less than 0 -> invalid value=" + totalItemSize);
        }
        else{
            this.totalItemSize = totalItemSize;
        }
    }
    private void setItemSizeInPage(int itemSizeInPage) {
        // 不可 <= 0
        if(itemSizeInPage <= 0){
            this.itemSizeInPage = 10;
            logger.info("itemSizeInPage can't be less than 0 -> invalid value=" + itemSizeInPage);
        }
        else{
            this.itemSizeInPage = itemSizeInPage;
        }
    }
    private void setPagerLinkCount(int pagerLinkCount) {
        // 不可 <= 0
        if(pagerLinkCount <= 0){
            this.pagerLinkCount = 9;
            logger.info("pagerLinkCount can't be less than 0 -> invalid value=" + pagerLinkCount);
        }
        // 不可 > 30
        else if(pagerLinkCount > 30){
            this.pagerLinkCount = 9;
            logger.info("pagerLinkCount too big, no more than 30. -> invalid value=" + pagerLinkCount);
        }
        else{
            this.pagerLinkCount = pagerLinkCount;
            this._allowFront = pagerLinkCount / 2;
            this._allowBack = pagerLinkCount % 2 == 0 ? (pagerLinkCount / 2 - 1) : pagerLinkCount / 2;
        }
    }
    private void setCurrentPage(int currentPage) {
        // 處理currentPage
        if(currentPage < 1 || currentPage > this.totalPages){
            logger.info("Not valid \"currentPage\" -> invalid value=" + currentPage);
            this.currentPage = 1;
        }
        else{
            this.currentPage = currentPage;
        }
    }

    public int getTotalItemSize() {
        return totalItemSize;
    }
    public int getItemSizeInPage() {
        return itemSizeInPage;
    }
    public int getPagerLinkCount() {
        return pagerLinkCount;
    }
    public int getCurrentPage() {
        return currentPage;
    }

    // getter only
    public int getPageCount() {
        return pageCount;
    }
    public int getStartIndex() {
        return startIndex;
    }
    public int getTotalPages() {
        return totalPages;
    }

    private int countTotalPages(){
        int pages = this.totalItemSize / this.itemSizeInPage;
        if (this.totalItemSize % this.itemSizeInPage > 0)
            pages += 1;
        return pages == 0 ? 1 : pages;
    }

    // 使用前要處理好currentPage
    private int countStartIndex(){
        if(this.currentPage - this._allowFront <= 0 || this.countTotalPages() <= this.pagerLinkCount){
            return 1;
        }
        else if(this.currentPage + this._allowBack > this.countTotalPages()){
            return this.countTotalPages() - this.pagerLinkCount + 1;
        }
        else{
            return this.currentPage - this._allowFront;
        }
    }

    private int countPageCount(){
        return this.countTotalPages() < this.pagerLinkCount 
            ? this.countTotalPages() 
            : this.pagerLinkCount;
    }
}
