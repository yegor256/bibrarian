/**
 * Copyright (c) 2013-2017, bibrarian.com
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
 * Books.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 1.0
 */
@Immutable
public interface Books {

    /**
     * Get book by name.
     *
     * <p>Throws {@link com.bibrarian.om.Books.BookNotFoundException} if
     * this book doesn't exist.
     *
     * @param name Name of it
     * @return Book
     * @throws IOException If fails
     */
    Book get(String name) throws IOException;

    /**
     * Add new book.
     * @param bibtex Bibtex
     * @return Book created or found
     * @throws IOException If fails
     */
    Book add(String bibtex) throws IOException;

    /**
     * When book not found.
     */
    final class BookNotFoundException extends IOException {
        /**
         * Serialization marker.
         */
        private static final long serialVersionUID = 6540914607613240525L;
        /**
         * Ctor.
         * @param cause Cause
         */
        public BookNotFoundException(final String cause) {
            super(cause);
        }
    }

    /**
     * When book already exists.
     */
    final class DuplicateBookException extends IOException {
        /**
         * Serialization marker.
         */
        private static final long serialVersionUID = 6540914607613240525L;
        /**
         * Ctor.
         * @param cause Cause
         */
        public DuplicateBookException(final String cause) {
            super(cause);
        }
    }

}
