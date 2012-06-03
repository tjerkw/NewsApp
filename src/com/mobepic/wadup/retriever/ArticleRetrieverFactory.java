package com.mobepic.wadup.retriever;

/**
 * @author tjerk
 * @created 5/31/12 11:59 PM
 */
public class ArticleRetrieverFactory {
    private final static ArticleRetriever INSTANCE = new SnacktoryRetriever();

    public static ArticleRetriever getInstance() {
        return INSTANCE;
    }

}
