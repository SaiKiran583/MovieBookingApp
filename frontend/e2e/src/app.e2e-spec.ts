// // // // e2e/app.e2e-spec.ts
// // // import { browser, by, element } from 'protractor';

// // // describe('Your Angular App', () => {
// // //   beforeEach(() => {
// // //     // Navigate to the Angular app's URL before each test
// // //     browser.get('/');
// // //   });

// // //   it('should display the app title', () => {
// // //     // Find the app title element and assert its presence
// // //     const appTitle = element(by.css('app-root h1'));
// // //     expect(appTitle.isPresent()).toBeTruthy();

// // //     // Assert the text content of the app title
// // //     appTitle.getText().then(text => {
// // //       expect(text).toContain('Your App Title');
// // //     });
// // //   });

// // //   it('should navigate to a specific page', () => {
// // //     // Find a link and click it to navigate to another page
// // //     const link = element(by.css('a[href="/some-page"]'));
// // //     link.click();

// // //     // Assert that the URL has changed to the expected page
// // //     browser.getCurrentUrl().then(url => {
// // //       expect(url).toContain('/some-page');
// // //     });

// // //     // Assert something specific on the new page
// // //     const pageTitle = element(by.css('h2'));
// // //     expect(pageTitle.isPresent()).toBeTruthy();
// // //     pageTitle.getText().then(text => {
// // //       expect(text).toContain('Some Page');
// // //     });
// // //   });

// // //   // Add more test cases for different features and scenarios
// // // });


// // // e2e/src/app.e2e-spec.ts
// // // import { AppPage } from './app.po';

// // // describe('Your Angular App', () => {
// // //   let page: AppPage;

// // //   beforeEach(() => {
// // //     page = new AppPage();
// // //     page.navigateTo();
// // //   });

// // //   it('should display the app title', () => {
// // //     expect(page.getAppTitleText()).toContain('Your App Title');
// // //   });

// // //   it('should navigate to a specific page', () => {
// // //     page.navigateToSomePage();
// // //     expect(page.getSomePageTitleText()).toContain('Some Page');
// // //   });

// // //   // Add more test cases using methods from AppPage class
// // // });

// // // e2e/app.e2e-spec.ts
// // // e2e/login.e2e-spec.ts

// // import { browser, by, element } from 'protractor';

// // describe('Login E2E Tests', () => {

// //   beforeEach(() => {
// //     browser.get('/http://localhost:4200/login'); // Adjust the URL according to your app
// //   });

// //   it('should display the login form', () => {
// //     const loginForm = element(by.tagName('form'));
// //     expect(loginForm.isPresent()).toBeTruthy();
// //   });

// //   it('should allow the user to login with valid credentials', () => {
// //     const usernameInput = element(by.id('username'));
// //     const passwordInput = element(by.id('password'));
// //     const loginButton = element(by.buttonText('Login'));

// //     // Enter valid credentials
// //     usernameInput.sendKeys('123456789');
// //     passwordInput.sendKeys('Sai@1234');

// //     // Click the login button
// //     loginButton.click();

// //   });

  

// // });
// // login.e2e-spec.ts

// import { browser, by, element } from 'protractor';

// describe('Login Page E2E Tests', () => {
//   beforeEach(() => {
//     browser.get('/http://localhost:4200/login'); // Adjust the URL according to your app
//   });

//   it('should display the login form', () => {
//     const usernameInput = element(by.id('username'));
//     const passwordInput = element(by.id('password'));
//     const loginButton = element(by.id('login-button'));

//     expect(usernameInput.isPresent()).toBeTruthy();
//     expect(passwordInput.isPresent()).toBeTruthy();
//     expect(loginButton.isPresent()).toBeTruthy();
//   });

//   it('should allow the user to login with valid credentials', () => {
//     const usernameInput = element(by.id('username'));
//     const passwordInput = element(by.id('password'));
//     const loginButton = element(by.id('login-button'));
    

//     // Enter valid credentials
//     usernameInput.sendKeys('validUsername');
//     passwordInput.sendKeys('validPassword');

//     // Click the login button
//     loginButton.click();

//   });

//   // Add more test cases for login failure, edge cases, etc.
// });
// login.e2e-spec.ts

import { browser } from 'protractor';
import { LoginPage } from './app.po';

describe('Login Page E2E Tests', () => {
  let loginPage: LoginPage;

  beforeEach(() => {
    loginPage = new LoginPage();
    loginPage.navigateToLoginPage();
  });

  it('should display the login form', () => {
    expect(loginPage.getUsernameInput().isPresent()).toBeTruthy();
    expect(loginPage.getPasswordInput().isPresent()).toBeTruthy();
    expect(loginPage.getLoginButton().isPresent()).toBeTruthy();
  });



  it('should allow the user to login with valid credentials', () => {
    loginPage.enterCredentials('123456789', 'Sai@1234');
    loginPage.clickLoginButton();

   // Wait for the home page to load
   browser.wait(() => loginPage.getNavItem('home').isPresent(), 5000);

   // Verify successful navigation to the home page
   expect(browser.getCurrentUrl()).toContain('/home');
   expect(loginPage.getNavItem('home').isPresent()).toBeTruthy();
  });

  // Add more test cases for login failure, edge cases, etc.
});
