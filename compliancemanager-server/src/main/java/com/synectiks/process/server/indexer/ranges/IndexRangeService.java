/*
 * */
package com.synectiks.process.server.indexer.ranges;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.mongojack.WriteResult;

import com.synectiks.process.server.database.NotFoundException;

import java.util.SortedSet;

public interface IndexRangeService {
    IndexRange get(String index) throws NotFoundException;

    SortedSet<IndexRange> find(DateTime begin, DateTime end);

    SortedSet<IndexRange> findAll();

    WriteResult<MongoIndexRange, ObjectId> save(IndexRange indexRange);

    boolean remove(String index);

    IndexRange calculateRange(String index);
    IndexRange createUnknownRange(String index);
}
