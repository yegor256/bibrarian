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
package com.bibrarian.dyn;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.bibrarian.dynamo.Credentials;
import com.bibrarian.dynamo.Cursor;
import com.bibrarian.dynamo.Dynamo;
import com.bibrarian.dynamo.Reverse;
import com.bibrarian.dynamo.Schema;
import com.bibrarian.om.Bibitem;
import com.bibrarian.om.Bibrarian;
import com.bibrarian.om.Bibrarians;
import com.bibrarian.om.Queryable;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.urn.URN;
import java.net.URI;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * All known bibrarians.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id: BaseRs.java 2344 2013-01-13 18:28:44Z guard $
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(callSuper = false, of = "dynamo")
public final class DynBibrarians implements Bibrarians {

    /**
     * Dynamo.
     */
    private final transient Dynamo dynamo;

    /**
     * Public ctor.
     * @param creds Credentials
     * @param prefix Prefix
     */
    public DynBibrarians(@NotNull final Credentials creds,
        @NotNull final String prefix) {
        this.dynamo = new Dynamo.Simple(
            creds,
            new Schema()
                .withTable(
                    "bibitems",
                    String.format("%s-bibitems", prefix),
                    new DynBibitem.Mapping()
                )
                .withTable(
                    "artifacts",
                    String.format("%s-artifacts", prefix),
                    new DynArtifact.Mapping()
                )
                .withTable(
                    "artifacts",
                    String.format("%s-artifacts", prefix),
                    new DynArtifact.Mapping()
                )
                .withTable(
                    "hardcopies",
                    String.format("%s-hardcopies", prefix),
                    new Reverse<URI>() {
                        @Override
                        public URI revert(final Cursor<URI> cursor,
                            final Map<String, AttributeValue> attributes) {
                            return URI.create(attributes.get("uri").getS());
                        }
                    }
                )
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bibrarian fetch(@NotNull final URN urn) {
        return new DynBibrarian(
            this.dynamo.<Bibrarian>cursor("bibrarians"), urn
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Queryable<Bibitem> bibitems() {
        return new DynQueryable<Bibitem>(
            this.dynamo.<Bibitem>cursor("bibitems")
        );
    }

}
