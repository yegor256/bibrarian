<?xml version="1.0"?>
<!--
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
 -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns="http://www.w3.org/1999/xhtml" version="2.0" exclude-result-prefixes="xs">
    <xsl:output method="xml" omit-xml-declaration="yes"/>
    <xsl:variable name="section">bibitems</xsl:variable>
    <xsl:include href="/xsl/layout.xsl"/>
    <xsl:template name="head">
        <title>
            <xsl:text>bibitems</xsl:text>
        </title>
    </xsl:template>
    <xsl:template name="content">
        <form method="post">
            <xsl:attribute name="action">
                <xsl:value-of select="/page/links/link[@rel='add']/@href"/>
            </xsl:attribute>
            <fieldset>
                <legend>New Bibitem</legend>
                <label for="bibitem"><xsl:text>BibTeX item</xsl:text></label>
                <textarea name="bibitem" rows="6"></textarea>
                <label/>
                <button type="submit" class="btn">
                    <i class="icon-circle-arrow-right icon-large"></i>
                    <xsl:text> Save</xsl:text>
                </button>
            </fieldset>
        </form>
        <h1><xsl:text>All BibTeX Items</xsl:text></h1>
        <xsl:apply-templates select="bibitems/bibitem"/>
    </xsl:template>
    <xsl:template match="bibitem">
        <p>
            <span>
                <xsl:value-of select="label"/>
            </span>
        </p>
        <pre><xsl:value-of select="tex"/></pre>
    </xsl:template>
</xsl:stylesheet>
