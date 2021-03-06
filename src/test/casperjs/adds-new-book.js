/*globals casper:false */
casper.test.begin(
  'can add new book',
  function (test) {
    "use strict";
    casper.start(
      casper.cli.get("home") + '/add-book',
      function () {
        this.fill(
          'form#add-book',
          {
            'bibtex': '@book{west2014, title="Hello", author="Walter" }'
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
    casper.thenOpen(
      casper.cli.get("home") + '/edit-book/west2014',
      function () {
        this.fill(
          'form#edit-book',
          {
            'bibtex': '@book{west2014, title="Hello, world", author="Jeffrey" }'
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
    casper.run(
      function () {
        this.echo('book created');
        test.done();
      }
    );
  }
);
