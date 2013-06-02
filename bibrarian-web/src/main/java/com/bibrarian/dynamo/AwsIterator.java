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
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteItemResult;
import com.amazonaws.services.dynamodbv2.model.ReturnConsumedCapacity;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.jcabi.aspects.Loggable;
import com.jcabi.aspects.Tv;
import com.jcabi.log.Logger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import lombok.ToString;

/**
 * Iterator of items in AWS SDK.
 *
 * <p>The class is mutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
@Loggable(Loggable.DEBUG)
@ToString
final class AwsIterator implements Iterator<Item> {

    /**
     * AWS credentials.
     */
    private final transient Credentials credentials;

    /**
     * Conditions.
     */
    private final transient Conditions conditions;

    /**
     * Frame.
     */
    private final transient AwsFrame frame;

    /**
     * Table name.
     */
    private final transient String name;

    /**
     * Last scan result (mutable).
     */
    private final transient AtomicReference<ScanResult> result =
        new AtomicReference<ScanResult>();

    /**
     * Position inside the scan result, last seen, starts with -1 (mutable).
     */
    private transient int position;

    /**
     * Public ctor.
     * @param creds Credentials
     * @param frm Frame object
     * @param label Table name
     * @param conds Conditions
     * @checkstyle ParameterNumber (5 lines)
     */
    protected AwsIterator(final Credentials creds, final AwsFrame frm,
        final String label, final Conditions conds) {
        this.credentials = creds;
        this.frame = frm;
        this.name = label;
        this.conditions = conds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        synchronized (this.result) {
            return this.items().size() - this.position > 1;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Item next() {
        synchronized (this.result) {
            ++this.position;
            final Item item = new AwsItem(
                this.credentials,
                this.frame,
                this.name,
                new Attributes(this.items().get(this.position))
            );
            return item;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove() {
        synchronized (this.result) {
            final AmazonDynamoDB aws = this.credentials.aws();
            final List<Map<String, AttributeValue>> items =
                new ArrayList<Map<String, AttributeValue>>(
                    this.result.get().getItems()
                );
            final DeleteItemResult res = aws.deleteItem(
                new DeleteItemRequest().withExpected(
                    new Attributes(items.remove(this.position)).asKeys()
                )
            );
            aws.shutdown();
            this.result.get().setItems(items);
            Logger.debug(
                this,
                "#remove(): item #%d removed from DynamoDB, %.2f units",
                this.position,
                res.getConsumedCapacity().getCapacityUnits()
            );
        }
    }

    /**
     * Get items available for iteration.
     * @return List of items
     */
    private List<Map<String, AttributeValue>> items() {
        if (this.result == null) {
            this.reload();
        }
        if (this.position >= this.result.get().getCount()) {
            this.reload();
        }
        return this.result.get().getItems();
    }

    /**
     * Re-load new portion of data, no matter what we have now.
     */
    private void reload() {
        final AmazonDynamoDB aws = this.credentials.aws();
        final ScanRequest request = new ScanRequest()
            .withTableName(this.name)
            .withAttributesToGet(AwsTable.class.cast(this.frame.table()).keys())
            .withReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL)
            .withScanFilter(this.conditions)
            .withLimit(Tv.HUNDRED);
        if (this.result.get() != null
            && this.result.get().getLastEvaluatedKey() != null) {
            request.setExclusiveStartKey(
                this.result.get().getLastEvaluatedKey()
            );
        }
        this.result.set(aws.scan(request));
        this.position = -1;
        aws.shutdown();
        Logger.debug(
            this,
            "#reload(): loaded %d item(s) from '%s', %.2f units, %d scanned",
            this.result.get().getCount(),
            this.name,
            this.result.get().getConsumedCapacity().getCapacityUnits(),
            this.result.get().getScannedCount()
        );
    }

}
