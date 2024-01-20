// // e2e/src/app.po.ts
// import { browser, by, element } from 'protractor';

// export class AppPage {
//   // Navigate to the root of the application
//   navigateTo() {
//     return browser.get('/http://localhost:4200/login');
//   }
// }
// login.po.ts

import { browser, by, element } from 'protractor';

export class LoginPage {
  private usernameInput = element(by.id('loginId'));
  private passwordInput = element(by.id('password'));
  private loginButton = element(by.id('login-button'));
  private welcomeMessage = element(by.id('welcome-message'));

  navigateToLoginPage() {
    return browser.get('http://localhost:4200/login');
  }

  enterCredentials(username: string, password: string) {
    this.usernameInput.sendKeys(username);
    this.passwordInput.sendKeys(password);
  }

  clickLoginButton() {
    this.loginButton.click();
  }

  getUsernameInput() {
    return this.usernameInput;
  }

  getPasswordInput() {
    return this.passwordInput;
  }

  getLoginButton() {
    return this.loginButton;
  }

  navigateToHomePage() {
    // Assume there is a navigation link on the home page, adjust as needed
    return element(by.id('home-page')).click();
  }

  getNavItem(tabName: string) {
    // Assume there are navigation tabs with unique identifiers, adjust as needed
    return element(by.id(`${tabName}-tab`));
  }
}
