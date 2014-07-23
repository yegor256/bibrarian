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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Tag.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 1.0
 */
@Immutable
public interface Tag {

    /**
     * Owner.
     * @return User login
     */
    String login();

    /**
     * Name of the tag.
     * @return Name
     */
    String name();

    /**
     * Simple.
     */
    @Immutable
    @EqualsAndHashCode(of = { "user", "tag" })
    @ToString
    final class Simple implements Tag {
        /**
         * Pattern.
         */
        @SuppressWarnings("PMD.UnusedPrivateField")
        private static final Pattern PTN = Pattern.compile("t:([^/]+)/(.*)");
        /**
         * Login.
         */
        private final transient String user;
        /**
         * Name.
         */
        private final transient String tag;
        /**
         * Ctor.
         * @param ref Ref
         */
        public Simple(final String ref) {
            final Matcher matcher = Tag.Simple.PTN.matcher(ref);
            if (!matcher.matches()) {
                throw new IllegalStateException(
                    String.format("invalid ref: \"%s\"", ref)
                );
            }
            this.user = matcher.group(1);
            this.tag = matcher.group(2);
        }
        /**
         * Ctor.
         * @param origin Another tag
         */
        public Simple(final Tag origin) {
            this(origin.login(), origin.name());
        }
        /**
         * Ctor.
         * @param login Login
         * @param name Name
         */
        public Simple(final String login, final String name) {
            this.user = login;
            this.tag = name;
        }
        @Override
        public String login() {
            return this.user;
        }
        @Override
        public String name() {
            return this.tag;
        }
        /**
         * Make ref.
         * @return Ref
         */
        public String ref() {
            return String.format("t:%s/%s", this.user, this.tag);
        }
    }

}
