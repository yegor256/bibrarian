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
package com.bibrarian.om;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.net.URI;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * One artifact.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id: BaseRs.java 2344 2013-01-13 18:28:44Z guard $
 */
@Immutable
public interface Artifact extends Comparable<Artifact> {

    /**
     * Book it is related to.
     * @return The book
     */
    @NotNull
    Book book();

    /**
     * Documents attached (read/write set of them).
     * @return Set of documents
     */
    @NotNull
    SortedSet<URI> hardcopies();

    /**
     * Short description of the book read.
     * @return The text
     */
    @NotNull
    String referat();

    /**
     * Change description.
     * @param text New description
     */
    @NotNull
    void referat(@NotNull String text);

    /**
     * Get all discoveries.
     * @return The discoveries
     */
    @NotNull
    Discoveries discoveries();

    /**
     * Simple implementation.
     */
    @Loggable(Loggable.DEBUG)
    @Immutable
    @ToString
    @EqualsAndHashCode(of = "bkk")
    final class Simple implements Artifact {
        /**
         * Book.
         */
        private final transient Book bkk;
        /**
         * Public ctor.
         * @param book Book to encapsulate
         */
        public Simple(@NotNull final Book book) {
            this.bkk = book;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        @NotNull
        public Book book() {
            return this.bkk;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public SortedSet<URI> hardcopies() {
            return new TreeSet<URI>();
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public String referat() {
            return "";
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void referat(final String text) {
            throw new UnsupportedOperationException();
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public Discoveries discoveries() {
            throw new UnsupportedOperationException();
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public int compareTo(final Artifact artifact) {
            return this.book().compareTo(artifact.book());
        }
    }

}
