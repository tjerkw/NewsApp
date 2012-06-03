package com.mobepic.wadup.retriever;

import com.mobepic.wadup.Article;

/**
 * @author tjerk
 * @created 5/31/12 11:59 PM
 */
public interface ArticleRetriever {

    public Article load(String url) throws ArticleRetrieveException;
}
