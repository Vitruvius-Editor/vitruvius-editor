import { test, expect } from '@playwright/test';

test('Test SourceCode DisplayView diplaying', async ({ page }) => {
  await page.goto('http://localhost:3000/#/home/project');
  await page.locator('[id="theia\\:menubar"]').getByText('Vitruvius').click();
  await page.getByText('Vitruvius Import Project').click();
  await page.getByRole('combobox', { name: 'input' }).fill('Test Project');
  await page.getByRole('combobox', { name: 'input' }).press('Enter');
  await page.getByRole('combobox', { name: 'input' }).fill('Test Description');
  await page.getByRole('combobox', { name: 'input' }).press('Enter');
  await page.getByRole('combobox', { name: 'input' }).fill('localhost');
  await page.getByRole('combobox', { name: 'input' }).press('Enter');
  await page.getByRole('combobox', { name: 'input' }).fill('8000');
  await page.getByRole('combobox', { name: 'input' }).press('Enter');
  await page.locator('[id="shell-tab-widget\\:display-views"] > .theia-tab-icon-label > .p-TabBar-tabIcon').click();
  await expect(page.locator('[id="widget\\:display-views"]')).toContainText('The following views are avaliable for the loaded project:');
  await expect(page.locator('[id="widget\\:display-views"]')).toContainText('SourceCode');
  await page.getByText('SourceCode').click();
  await expect(page.locator('[id="widget\\:display-views"]')).toContainText('Class1');
  await expect(page.locator('[id="widget\\:display-views"]')).toContainText('Class2');
  await page.getByText('Class1').click();
  await expect(page.locator('[id="shell-tab-textwidget\\:textwidget"]')).toContainText('Class1');
  await expect(page.getByRole('textbox')).toContainText('public class Class1 { int myIntAttribute; boolean myBooleanAttribute = true; public int myMethod(int myParameter) { return 5; } }');
  await page.getByText('Class2').click();
  await expect(page.locator('#theia-main-content-panel')).toContainText('Class2');
  await expect(page.getByRole('textbox')).toContainText('class Class2 { int myIntAttribute2 = 5; int myIntAttribute3; }');
  await page.locator('#theia-main-content-panel').getByTitle('Close').first().click();
  await page.locator('[id="shell-tab-textwidget\\:textwidget"]').getByTitle('Close').click();
});
