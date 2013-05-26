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
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.jcabi.aspects.Immutable;
import java.util.Map;

/**
 * Frame through AWS SDK.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id: BaseRs.java 2344 2013-01-13 18:28:44Z guard $
 */
@Immutable
final class AwsItem implements Item {

    /**
     * AWS credentials.
     */
    private final transient Credentials credentials;

    /**
     * Region.
     */
    private final transient Region region;

    /**
     * Table name.
     */
    private final transient String name;

    /**
     * Values.
     */
    private final transient Map<String, AttributeValue> values;

    /**
     * Public ctor.
     * @param creds Credentials
     * @param reg Region
     * @param table Table name
     * @param vals values
     */
    protected AwsItem(final Credentials creds, final Region reg,
        final String table, final Map<String, AttributeValue> vals) {
        this.credentials = creds;
        this.region = reg;
        this.name = table;
        this.values = vals;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AttributeValue get(final String name) {
        return this.values.get(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(final String name, final AttributeValue value) {
        final AmazonDynamoDB aws = this.credentials.aws();
        final PutItemRequest request = new PutItemRequest();
        request.setTableName(this.name);
        request.setItem(this.values);
        aws.putItem(request);
        aws.shutdown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Frame frame() {
        return new AwsFrame(this.credentials, this.region, this.name);
    }

}
