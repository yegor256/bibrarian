/**
 * Copyright (c) 2013, bibrarian.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the bibrarian.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.bibrarian.dynamo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.jcabi.aspects.Immutable;
import java.util.AbstractCollection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Frame through AWS SDK.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id: BaseRs.java 2344 2013-01-13 18:28:44Z guard $
 */
@Immutable
final class AwsFrame extends AbstractCollection<Item> implements Frame {

    /**
     * AWS credentials.
     */
    private final transient Credentials credentials;

    /**
     * Table.
     */
    private final transient Table tbl;

    /**
     * Table name.
     */
    private final transient String name;

    /**
     * Conditions.
     */
    private final transient Map<String, Condition> conditions;

    /**
     * Public ctor.
     * @param creds Credentials
     * @param table Table
     * @param label Table name
     */
    protected AwsFrame(final Credentials creds, final Table table,
        final String label) {
        this(creds, table, label, new HashMap<String, Condition>(0));
    }

    /**
     * Public ctor.
     * @param creds Credentials
     * @param table Table
     * @param table Table name
     * @param conds Conditions
     */
    protected AwsFrame(final Credentials creds, final Table table,
        final String label, final Map<String, Condition> conds) {
        this.credentials = creds;
        this.tbl = table;
        this.name = label;
        this.conditions = conds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Item> iterator() {
        final AmazonDynamoDB aws = this.credentials.aws();
        final QueryResult result = aws.query(this.request());
        final Iterator<Map<String, AttributeValue>> items =
            result.getItems().iterator();
        aws.shutdown();
        return new Iterator<Item>() {
            @Override
            public boolean hasNext() {
                return items.hasNext();
            }
            @Override
            public Item next() {
                return new AwsItem(
                    AwsFrame.this.credentials,
                    AwsFrame.this,
                    AwsFrame.this.name,
                    AwsFrame.expected(items.next())
                );
            }
            @Override
            public void remove() {
                final AmazonDynamoDB aws = AwsFrame.this.credentials.aws();
                aws.deleteItem(
                    new DeleteItemRequest()
                        .withExpected(AwsFrame.expected(items.next()))
                );
                aws.shutdown();
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        final AmazonDynamoDB aws = this.credentials.aws();
        final QueryResult result = aws.query(this.request());
        final int size = result.getCount();
        aws.shutdown();
        return size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Frame where(final String name, final Condition condition) {
        final Map<String, Condition> map = new HashMap<String, Condition>(0);
        map.putAll(this.conditions);
        map.put(name, condition);
        return new AwsFrame(this.credentials, this.tbl, this.name, map);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Table table() {
        return this.tbl;
    }

    /**
     * Query request.
     * @return The request
     */
    private QueryRequest request() {
        return new QueryRequest()
            .withTableName(this.name)
            .withKeyConditions(this.conditions);
    }

    /**
     * Convert item to expected values.
     * @param item Item values
     * @return Expected values
     */
    private static Map<String, ExpectedAttributeValue> expected(
        final Map<String, AttributeValue> item) {
        final ConcurrentMap<String, ExpectedAttributeValue> values =
            new ConcurrentHashMap<String, ExpectedAttributeValue>(item.size());
        for (Map.Entry<String, AttributeValue> entry : item.entrySet()) {
            values.put(
                entry.getKey(),
                new ExpectedAttributeValue(entry.getValue())
            );
        }
        return values;
    }

}
