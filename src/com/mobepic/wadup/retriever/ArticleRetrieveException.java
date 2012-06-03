package com.mobepic.wadup.retriever;

/**
 * @author tjerk
 * @created 5/31/12 11:59 PM
 */
public class ArticleRetrieveException extends Exception {

    public ArticleRetrieveException() {
    }

    public ArticleRetrieveException(String detailMessage) {
        super(detailMessage);
    }

    public ArticleRetrieveException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ArticleRetrieveException(Throwable throwable) {
        super(throwable);
    }
}
