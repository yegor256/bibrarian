# Static URL Shortener

[![generate](https://github.com/yegor256/jttu/actions/workflows/generate.yml/badge.svg)](https://github.com/yegor256/jttu/actions/workflows/generate.yml)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/yegor256/jttu/blob/master/LICENSE.txt)

There are a few URLs in the [`redirects.yml`][YAML] file, which you
can use as such:

`https://jttu.net/yegor256` ([click](https://jttu.net/yegor256))

Here, the `yegor256` is the key in the [YAML], while the browser is
[redirected][redirect] to this URL:

`https://www.yegor256.com/about-me.html`

Feel free to add your links.

**JTTU** stands for the "**j**ump **t**o **t**he **U**RL."

## How It Works?

On each Git push to the `master` branch, [GitHub Actions] job starts
`generate.sh`, a Bash script. It parses the [YAML] file,
finds all short names with their corresponding URLs,
and then generates one HTML file per each link, like
[this one][yegor256.html], for example.

Then, the job commits all
generated HTML files to the [`gh-pages`][gh-pages] branch of this repository.

Once new files arrive to the branch, [GitHub Pages] server picks them
up and makes them available at the [jttu.net](https://www.jttu.net)
domain, together with the `index.html` that is also generated
by the `generate.sh`.

[yaml]: https://github.com/yegor256/jttu/blob/master/redirects.yml
[redirect]: https://stackoverflow.com/questions/5411538/how-to-redirect-one-html-page-to-another-on-load
[yegor256.html]: https://github.com/yegor256/jttu/blob/gh-pages/yegor256.html
[GitHub Actions]: https://github.com/features/actions
[GitHub Pages]: https://pages.github.com/
[gh-pages]: https://github.com/yegor256/jttu/tree/gh-pages
