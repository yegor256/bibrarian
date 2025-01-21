#!/bin/bash

# Copyright (c) 2025 Yegor Bugayenko
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the 'Software'), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so. The Software doesn't include files with .md extension.
# That files you are not allowed to copy, distribute, modify, publish, or sell.
#
# THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.

set -e

self=$(dirname "$0")
yaml=$1
target=$2

if [ -z "${yaml}" ]; then
    yaml="${self}/redirects.yml"
fi
if [ -z "${target}" ]; then
    target="${self}/gh-pages"
fi

rm -rf "${target}"
mkdir -p "${target}"

yq --version

head=$(git rev-parse --short HEAD)
echo "Git head SHA: ${head}"
cat > "${target}/index.html" <<EOT
<!DOCTYPE html>
<html lang='en-US'>
<head>
    <meta charset='UTF-8' />
    <meta content='width=device-width, initial-scale=1.0' name='viewport' />
    <meta content='URL shortener, URL redirector' name='keywords' />
    <meta content='Jump To The URL' name='description' />
    <title>jttu.net</title>
    <style>
        section { font-family: sans-serif; font-size: 12pt; text-align: center; }
        .outer { display:table; height:100%; left:0; position:absolute; top:0; width:100%; }
        .outer > div { display:table-cell; vertical-align:middle; }
        .outer > div > div { margin-left:auto; margin-right:auto; max-width:40em; }
    </style>
</head>
<body>
    <div class='outer'><div><div>
    <section>
        <p>This is a static URL redirector and shortener.</p>
        <p>The sources are in GitHub, in the <a href="https://github.com/yegor256/jttu">yegor256/jttu</a> repo.</p>
        <p>URLs total: $(yq '. | to_entries [] | "1"' "${yaml}" | wc -l | xargs).</p>
        <p>SHA: <tt>${head}</tt>.</p>
        <p>Updated: <tt>$(date)</tt>.</p>
    </section>
</body>
</html>
EOT

pairs=$(yq '. | to_entries [] | "\(.key) \(.value)"' "${yaml}")
echo "There are $(echo "${pairs}" | wc -l | xargs) pairs in ${yaml}"
while IFS= read -r pair; do
    key=$(echo "${pair}" | cut -f1 -d' ' | tr -d '"')
    href=$(echo "${pair}" | cut -f2 -d' ' | tr -d '"')
    file=${target}/${key}.html
    if [ -e "${file}" ]; then
        echo "There is a duplicate in the YAML: ${key}"
        exit 1
    fi
    cat > "${file}" <<EOT
    <!DOCTYPE html>
    <html lang='en-US'>
    <head>
        <meta charset='utf-8'/>
        <meta http-equiv='refresh' content='0; url=${href}'/>
        <meta name="robots" content="noindex"/>
        <link rel='canonical' href='${href}'/>
        <script>location='${href}'</script>
        <title>Redirecting&hellip;</title>
    </head>
    <body>
        <p>Redirecting&hellip;</p>
        <p><a href='${href}'>Click here</a> if you are not redirected.</p>
    </body>
    </html>
EOT
    echo "${file} created (${href})"
done <<< "${pairs}"
