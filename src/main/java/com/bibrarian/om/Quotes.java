/**
 * Copyright (c) 2013-2014, bibrarian.com
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
package com.bibrarian.om;

import com.jcabi.aspects.Immutable;
import java.io.IOException;

/**
 * Quotes.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 1.0
 */
@Immutable
public interface Quotes {

    /**
     * Get by id.
     * @param number Quote number
     * @return Quote
     * @throws IOException If fails
     */
    Quote get(long number) throws IOException;

    /**
     * Iterate them.
     * @return Quotes
     */
    Iterable<Quote> iterate();

    /**
     * Refine with a new term.
     * @param term Term
     * @return The quotes
     */
    Quotes refine(String term);

    /**
     * Add new quote.
     * @param book Book
     * @param text Quote text
     * @param pages Pages
     * @return Quote created
     * @throws IOException If fails
     */
    Quote add(Book book, String text, String pages) throws IOException;

    /**
     * When quote not found.
     */
    final class QuoteNotFoundException extends IOException {
        /**
         * Serialization marker.
         */
        private static final long serialVersionUID = 6540914607613240525L;
        /**
         * Ctor.
         * @param cause Cause
         */
        public QuoteNotFoundException(final String cause) {
            super(cause);
        }
    }
}
