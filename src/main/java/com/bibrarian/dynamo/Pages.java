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
package com.bibrarian.dynamo;

import com.bibrarian.om.Quotes;
import com.google.common.base.Joiner;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Tv;
import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pages of a quote.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 1.0
 */
@Immutable
final class Pages {

    /**
     * Pages pattern.
     */
    private static final Pattern PTN = Pattern.compile("(\\d+)(\\-\\d+)?");

    /**
     * Text.
     */
    private final transient String text;

    /**
     * Public ctor.
     * @param txt Text
     */
    Pages(final String txt) {
        this.text = txt;
    }

    /**
     * Normalized pages.
     * @return Pages in "pp.34-38" or "p.56, p.78" format
     * @throws Quotes.InvalidQuoteException If can't parse
     */
    public String normalized() throws Quotes.InvalidQuoteException {
        final Matcher matcher = Pages.PTN.matcher(this.text);
        final Collection<String> found = new LinkedList<String>();
        while (matcher.find()) {
            if (matcher.group(2) == null) {
                found.add(String.format("p.%s", matcher.group(1)));
            } else {
                found.add(String.format("pp.%s", matcher.group(0)));
            }
        }
        if (found.isEmpty()) {
            throw new Quotes.InvalidQuoteException(
                "at least one page should be referenced"
            );
        }
        if (found.size() > Tv.FIVE) {
            throw new Quotes.InvalidQuoteException(
                "too many page references, maximum is five"
            );
        }
        return Joiner.on(" and ").join(found);
    }

}
