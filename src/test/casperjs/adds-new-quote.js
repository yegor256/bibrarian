/*globals casper:false */
casper.test.begin(
  'can add new quote',
  function (test) {
    "use strict";
    casper.start(
      casper.cli.get("home") + '/add-book',
      function () {
        this.fill(
          'form#add-book',
          {
            'bibtex': '@book{life2012, title="Hello, world!" }'
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
    casper.run(
      function () {
        this.echo('quote created');
        test.done();
      }
    );
  }
);
