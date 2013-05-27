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

import com.jcabi.aspects.Immutable;
import javax.validation.constraints.NotNull;

/**
 * DynamoDB region.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id: BaseRs.java 2344 2013-01-13 18:28:44Z guard $
 */
@Immutable
public interface Region {

    /**
     * Get one table.
     * @param name Table name
     * @return Table
     */
    @NotNull
    Table table(String name);

    /**
     * Prefixed.
     */
    final class Simple implements Region {
        /**
         * Credentials.
         */
        private final transient Credentials credentials;
        /**
         * Public ctor.
         * @param creds Credentials
         */
        public Simple(final Credentials creds) {
            this.credentials = creds;
        }
        @Override
        public Table table(final String name) {
            return new AwsTable(this.credentials, this, name);
        }
    }

    /**
     * Prefixed.
     */
    final class Prefixed implements Region {
        /**
         * Original region.
         */
        private final transient Region origin;
        /**
         * Prefix to add.
         */
        private final transient String prefix;
        /**
         * Public ctor.
         * @param region Original region
         * @param pfx Prefix to add to all tables
         */
        public Prefixed(final Region region, final String pfx) {
            this.origin = region;
            this.prefix = pfx;
        }
        @Override
        public Table table(final String name) {
            return this.origin.table(String.format("%s-%s", this.prefix, name));
        }
    }

}