/**
 * Copyright (c) 2009-2014, requs.org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the requs.org nor
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
grammar Bib;

@header {
    import java.util.Map;
    import java.util.HashMap;
    import org.apache.commons.lang3.StringUtils;
}

tags returns [Map<String, String> map]
    :
    { $map = new HashMap<String, String>(); }
    TYPE
    { $map.put("@", $TYPE.text); }
    NAME
    { $map.put("", $NAME.text); }
    (
        ','
        TAG
        '='
        tex
        { $map.put($TAG.text, $tex.ret); }
    )*
    CLOSE
    EOF
    ;

tex returns [String ret]
    :
    NUMBER
    { $ret = $NUMBER.text; }
    |
    QUOTED
    { $ret = $QUOTED.text; }
    |
    CURLED
    { $ret = $CURLED.text; }
    ;

fragment LOWCASE: 'a'..'z';
fragment UPCASE: 'A'..'Z';
fragment DIGIT: '0'..'9';
OPEN: '{';
CLOSE: '}';
TYPE: '@' LOWCASE+ OPEN { this.setText(this.getText().replaceAll("[^a-z]", "")); };
NUMBER: DIGIT+;
NAME: LOWCASE LOWCASE LOWCASE LOWCASE* DIGIT DIGIT;
TAG: ( LOWCASE | UPCASE )+;
QUOTED:
    '"' ('\\"' | ~'"')* '"'
    { this.setText(StringUtils.strip(this.getText(), "\"")); }
    ;
CURLED:
    OPEN (~'}')* CLOSE
    { this.setText(StringUtils.strip(this.getText(), "}{")); }
    ;
SPACE
    :
    ( ' ' | '\t' | '\n' | '\r' )+
    { skip(); }
    ;
