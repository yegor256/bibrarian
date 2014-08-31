/*globals casper:false */
casper.test.begin(
  'can remove a tag from a book',
  function (test) {
    "use strict";
    casper.start(
      casper.cli.get("home") + '/add-book',
      function () {
        this.fill(
          'form#add-book',
          {
            'bibtex': '@book{life2011, title="Hello, world!", author="Jeffrey" }'
          },
          true
        );
      }
    );
    casper.then(
      function () {
        test.assertHttpStatus(200);
      }
    );
    casper.then(
      function () {
        this.fill(
          'form#add-quote',
          {
            'text': 'this is a very nice quote',
            'pages': '123-124',
            'tag': 'books-about-life'
          },
          true
        );
      }
    );
    casper.then(
      function () {
        test.assertHttpStatus(200);
      }
    );
    casper.then(
      function () {
        this.fill(
          'form#add-tag',
          {
            'tag': 'interesting-quotes'
          },
          true
        );
      }
    );
    casper.then(
      function () {
        test.assertHttpStatus(200);
        test.assertExists('li#localhost_interesting-quotes');
      }
    );
    casper.then(
      function () {
        this.click('li#localhost_interesting-quotes a.opt');
      }
    );
    casper.then(
      function () {
        test.assertHttpStatus(200);
        test.assertDoesntExist('li#localhost_interesting-quotes');
      }
    );
    casper.run(
      function () {
        this.echo('quote created');
        test.done();
      }
    );
  }
);
