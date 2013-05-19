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
    <xsl:include href="/xsl/layout.xsl"/>
    <xsl:template name="head">
        <title>
            <xsl:text>artifacts</xsl:text>
        </title>
    </xsl:template>
    <xsl:template name="content">
        <h1><xsl:text>Artifacts</xsl:text></h1>
        <xsl:apply-templates select="artifacts/artifact"/>
    </xsl:template>
    <xsl:template match="artifact">
        <p>
            <a>
                <xsl:attribute name="href">
                    <xsl:value-of select="links/link[@rel='see']/@href"/>
                </xsl:attribute>
                <xsl:value-of select="bibitem/label"/>
            </a>
            <xsl:text> </xsl:text>
            <xsl:text>: </xsl:text>
            <xsl:value-of select="referat"/>
        </p>
        <ul>
            <xsl:apply-templates select="discoveries/discovery"/>
        </ul>
    </xsl:template>
    <xsl:template match="discovery">
        <li>
            <a>
                <xsl:attribute name="href">
                    <xsl:value-of select="links/link[@rel='hypothesis']/@href"/>
                </xsl:attribute>
                <xsl:value-of select="label"/>
            </a>
            <xsl:text>: </xsl:text>
            <i>
                <xsl:text>&quot;</xsl:text>
                <xsl:value-of select="quote"/>
                <xsl:text>&quot;</xsl:text>
            </i>
            <xsl:text>, </xsl:text>
            <a>
                <xsl:attribute name="href">
                    <xsl:value-of select="links/link[@rel='artifact']/@href"/>
                </xsl:attribute>
                <xsl:value-of select="artifact"/>
            </a>
            <xsl:text>, pp.</xsl:text>
            <xsl:value-of select="pages"/>
        </li>
    </xsl:template>
</xsl:stylesheet>
