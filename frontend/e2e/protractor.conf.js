// protractor.conf.js
exports.config = {
    specs: ['./e2e/**/*.e2e-spec.ts'],  // Path to your E2E test files
    capabilities: {
      browserName: 'chrome',
      chromeOptions: {
        args: ['--headless', '--disable-gpu', '--window-size=800x600']
      }
    },
    directConnect: true,
    baseUrl: 'http://localhost:4200/',
    framework: 'jasmine',
    jasmineNodeOpts: {
      showColors: true,
      defaultTimeoutInterval: 1000,
      print: function () {}
    },
    onPrepare() {
      require('ts-node').register({
        project: 'e2e/*.e2e.json'  // Path to your tsconfig.e2e.json file
      });
    }
  };
  