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

self=$(dirname "$0")
yaml=$1
target=$2

if [ -z "${yaml}" ]; then
    yaml="${self}/redirects.yml"
fi
if [ -z "${target}" ]; then
    target="${self}/gh-pages"
fi

yq --version

mkdir -p "${target}"
head=$(git rev-parse --short HEAD)
echo "Git head SHA: ${head}"
(
    echo '<html><body>'
    echo '<p>Hello, world!</p>'
    echo "<p>SHA: <tt>${head}</tt>.</p>"
    echo '</body></html>'
) > "${target}/index.html"

pairs=$(yq eval '. | to_entries [] | "\(.key) \(.value)"' "${yaml}")
echo "Theare are $(echo "${pairs}" | wc -l | xargs) pairs in ${yaml}"
while IFS= read -r pair; do
    key=$(echo "${pair}" | cut -f1 -d' ')
    href=$(echo "${pair}" | cut -f2 -d' ')
    file=${target}/${key}.html
    cat > "${file}" <<EOT
    <!DOCTYPE html>
    <html lang='en-US'>
    <head>
        <meta charset='utf-8'><title>Redirecting&hellip;</title>
        <link rel='canonical' href='${href}'>
        <script>location='${href}'</script>
        <meta http-equiv='refresh' content='0; url=${href}'>
        <meta name="robots" content="noindex">
    </head>
    <body>
        <h1>Redirecting&hellip;</h1>
        <a href='${href}'>Click here if you are not redirected.</a>
    </body>
    </html>
EOT
    echo "${file} created (${href})"
done <<< "${pairs}"
