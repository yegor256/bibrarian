/*globals casper:false */
casper.test.begin(
  'can add new book',
  function (test) {
    "use strict";
    casper.start(
      casper.cli.get("home") + '/book',
      function () {
        this.fill(
          'form',
          {
            'bibtex': '@book { title: "Hello" }'
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
