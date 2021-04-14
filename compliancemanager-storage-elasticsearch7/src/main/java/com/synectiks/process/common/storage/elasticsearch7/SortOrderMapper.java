/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch7;

import org.graylog.shaded.elasticsearch7.org.elasticsearch.search.sort.SortOrder;
import com.synectiks.process.server.indexer.searches.Sorting;

import java.util.Locale;

public class SortOrderMapper {
    public SortOrder fromSorting(Sorting sorting) {
        return SortOrder.valueOf(sorting.getDirection().toString().toUpperCase(Locale.ENGLISH));
    }
}
