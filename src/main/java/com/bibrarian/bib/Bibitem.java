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
package com.bibrarian.bib;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.jcabi.aspects.Immutable;
import com.jcabi.immutable.ArrayMap;
import java.util.Map;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.TokenStream;

/**
 * Bibtex Item.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@Immutable
public final class Bibitem {

    /**
     * Tags.
     */
    private final transient ArrayMap<String, String> map;

    /**
     * Ctor.
     * @param item TeX bibitem
     * @throws BibSyntaxException If fails
     */
    public Bibitem(final String item) throws BibSyntaxException {
        this.map = new ArrayMap<String, String>(Bibitem.parse(item));
    }

    /**
     * Type.
     * @return Type
     */
    public String type() {
        return this.map.get("@");
    }

    /**
     * Name.
     * @return Name
     */
    public String name() {
        return this.map.get("");
    }

    /**
     * TeX.
     * @return TeX
     */
    public String tex() {
        return String.format(
            "@%s{%s,\n  %s\n}",
            this.type(),
            this.name(),
            Joiner.on(",\n  ").join(
                Iterables.transform(
                    this.tags(),
                    new Function<Map.Entry<String, String>, Object>() {
                        @Override
                        public Object apply(
                            final Map.Entry<String, String> ent) {
                            return String.format(
                                "%s=\"%s\"", ent.getKey(), ent.getValue()
                            );
                        }
                    }
                )
            )
        );
    }

    /**
     * Cite.
     * @return Cite
     */
    public String cite() {
        return String.format(
            "%s, %s, %s",
            this.map.get("title"),
            this.map.get("author"),
            this.map.get("publisher")
        );
    }

    /**
     * Get pure tags.
     * @return Tags
     */
    private Iterable<Map.Entry<String, String>> tags() {
        return Iterables.filter(
            this.map.entrySet(),
            new Predicate<Map.Entry<String, String>>() {
                @Override
                public boolean apply(
                    final Map.Entry<String, String> ent) {
                    return ent.getKey().matches("[a-zA-Z0-9]+");
                }
            }
        );
    }

    /**
     * Parse.
     * @param text Text to parse
     * @return Map of tags
     * @throws BibSyntaxException If fails
     */
    private static Map<String, String> parse(final String text)
        throws BibSyntaxException {
        final BibLexer lexer = new BibLexer(new ANTLRInputStream(text));
        final TokenStream tokens = new CommonTokenStream(lexer);
        final BibParser parser = new BibParser(tokens);
        final Errors errors = new Errors();
        lexer.addErrorListener(errors);
        parser.addErrorListener(errors);
        final Map<String, String> map;
        try {
            map = parser.tags().map;
        } catch (final RecognitionException ex) {
            throw new BibSyntaxException(ex);
        }
        if (!Iterables.isEmpty(errors)) {
            throw new BibSyntaxException(
                Joiner.on("; ").join(errors)
            );
        }
        return map;
    }

}
