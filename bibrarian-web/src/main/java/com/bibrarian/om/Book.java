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
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * One book.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id: BaseRs.java 2344 2013-01-13 18:28:44Z guard $
 */
@Immutable
public interface Book {

    /**
     * Label, unique in the whole system.
     * @return The label
     */
    String label();

    /**
     * Bibitem for BibTeX.
     * @return The text of it
     */
    String bibitem();

    /**
     * Set its bibitem.
     * @param bibitem The text of it
     */
    void bibitem(String bibitem);

    /**
     * Simple implementation.
     */
    @Loggable(Loggable.DEBUG)
    @ToString
    @EqualsAndHashCode(of = { "lbl", "item" })
    final class Simple implements Book {
        /**
         * Label.
         */
        private final transient String lbl;
        /**
         * Description.
         */
        private final transient String item;
        /**
         * Public ctor.
         * @param label The label
         */
        public Simple(@NotNull final String label) {
            this(label, "");
        }
        /**
         * Public ctor.
         * @param label The label
         * @param bibitem The bibitem
         */
        public Simple(@NotNull final String label,
            @NotNull final String bibitem) {
            this.lbl = label;
            this.item = bibitem;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public String label() {
            return this.lbl;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public String bibitem() {
            return this.item;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void bibitem(final String text) {
            throw new UnsupportedOperationException();
        }
    }

}
