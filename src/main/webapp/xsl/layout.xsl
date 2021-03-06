<?xml version="1.0"?>
<!--
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
 -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns="http://www.w3.org/1999/xhtml" version="2.0"
    exclude-result-prefixes="xs">
    <xsl:template match="page">
        <xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;</xsl:text>
        <html lang="en">
            <head>
                <meta charset="UTF-8"/>
                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                <meta name="author" content="www.bibrarian.com"/>
                <link rel="stylesheet" type="text/css" media="all" href="/css/style.css?{version/revision}"/>
                <link rel="icon" type="image/gif" href="//img.bibrarian.com/logo-512x512-transparent.png?{version/revision}"/>
                <xsl:apply-templates select="." mode="head"/>
                <script type="text/javascript">//<![CDATA[
                    var _gaq = _gaq || [];
                    _gaq.push(['_setAccount', 'UA-1963507-26']);
                    _gaq.push(['_trackPageview']);
                    (function() {
                        var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
                        ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
                        var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
                    })();
                //]]></script>
            </head>
            <body>
                <div class="wrapper">
                    <header class="head">
                        <div>
                            <a href="{links/link[@rel='home']/@href}">
                                <img src="//img.bibrarian.com/logo.svg"
                                    style="width:64px;height:64px;" alt="bibrarian logo"/>
                            </a>
                        </div>
                        <ul>
                            <xsl:apply-templates select="identity"/>
                            <xsl:if test="not(identity)">
                                <li>
                                    <a href="{links/link[@rel='rexsl:github']/@href}">
                                        <xsl:text>login via Github</xsl:text>
                                    </a>
                                </li>
                            </xsl:if>
                        </ul>
                        <xsl:if test="identity">
                            <ul>
                                <li>
                                    <a href="{links/link[@rel='add']/@href}">
                                        <xsl:text>Add New Quote</xsl:text>
                                    </a>
                                </li>
                                <li>
                                    <a href="{links/link[@rel='my-tags']/@href}">
                                        <xsl:text>My Tags</xsl:text>
                                    </a>
                                </li>
                            </ul>
                        </xsl:if>
                        <form id="search" method="get" action="{links/link[@rel='home']/@href}">
                            <fieldset class="inline">
                                <input name="q" id="q" size="40" maxlength="120"
                                    autocomplete="off"
                                    value="{term}" placeholder="search by keyword..."/>
                            </fieldset>
                        </form>
                    </header>
                    <div class="main">
                        <xsl:apply-templates select="flash"/>
                        <xsl:apply-templates select="." mode="body"/>
                    </div>
                    <footer class="foot">
                        <ul>
                            <xsl:apply-templates select="version"/>
                            <xsl:apply-templates select="millis"/>
                            <li title="server load average">
                                <xsl:attribute name="class">
                                    <xsl:choose>
                                        <xsl:when test="@sla &gt; 6">
                                            <xsl:text>error</xsl:text>
                                        </xsl:when>
                                        <xsl:when test="@sla &gt; 3">
                                            <xsl:text>warning</xsl:text>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:text>inherit</xsl:text>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:attribute>
                                <xsl:value-of select="@sla"/>
                            </li>
                        </ul>
                        <ul>
                            <li>
                                <a href="http://www.teamed.io">
                                    <img src="//img.teamed.io/btn.svg" alt="teamed.io logo"/>
                                </a>
                            </li>
                        </ul>
                    </footer>
                </div>
            </body>
        </html>
    </xsl:template>
    <xsl:template match="page/millis">
        <xsl:variable name="msec" select="number(.)"/>
        <li title="page load time">
            <xsl:attribute name="class">
                <xsl:choose>
                    <xsl:when test="$msec &gt; 5000">
                        <xsl:text>error</xsl:text>
                    </xsl:when>
                    <xsl:when test="$msec &gt; 1000">
                        <xsl:text>warning</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>inherit</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:choose>
                <xsl:when test="$msec &gt; 1000">
                    <xsl:value-of select="format-number($msec div 1000, '0.0')"/>
                    <xsl:text>s</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="format-number($msec, '#')"/>
                    <xsl:text>ms</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </li>
    </xsl:template>
    <xsl:template match="version">
        <li title="deployed version">
            <xsl:value-of select="name"/>
        </li>
        <li>
            <a title="see commit in Github"
                href="https://github.com/yegor256/bibrarian/commit/{revision}">
                <xsl:value-of select="substring(revision,1,3)"/>
            </a>
        </li>
    </xsl:template>
    <xsl:template match="flash">
        <p>
            <xsl:attribute name="class">
                <xsl:text>flash </xsl:text>
                <xsl:choose>
                    <xsl:when test="level = 'INFO'">
                        <xsl:text>success</xsl:text>
                    </xsl:when>
                    <xsl:when test="level = 'WARNING'">
                        <xsl:text>warning</xsl:text>
                    </xsl:when>
                    <xsl:when test="level = 'SEVERE'">
                        <xsl:text>error</xsl:text>
                    </xsl:when>
                </xsl:choose>
            </xsl:attribute>
            <xsl:value-of select="message"/>
        </p>
    </xsl:template>
    <xsl:template match="identity">
        <li>
            <xsl:text>@</xsl:text>
            <xsl:value-of select="name"/>
        </li>
        <li>
            <a title="log out" href="{/page/links/link[@rel='rexsl:logout']/@href}">
                <xsl:text>logout</xsl:text>
            </a>
        </li>
    </xsl:template>
</xsl:stylesheet>
