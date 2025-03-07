import { test, expect } from '@playwright/test';
import {beforeEach, afterEach} from '../hooks';

test.beforeEach(beforeEach);
test.afterEach(afterEach);

test('Load project', async ({ page }) => {
  await page.goto('http://localhost:3000/#/home/project');
  await page.locator('[id="theia\\:menubar"]').getByText('Vitruvius').click();
  await page.getByText('Vitruvius Import Project').click();
  await page.getByRole('combobox', { name: 'input' }).fill('Example project');
  await page.getByRole('combobox', { name: 'input' }).press('Enter');
  await page.getByRole('combobox', { name: 'input' }).fill('Example description');
  await page.getByRole('combobox', { name: 'input' }).press('Enter');
  await page.getByRole('combobox', { name: 'input' }).fill('localhost');
  await page.getByRole('combobox', { name: 'input' }).press('Enter');
  await page.getByRole('combobox', { name: 'input' }).fill('8000');
  await page.getByRole('combobox', { name: 'input' }).press('Enter');
  await page.locator('[id="shell-tab-widget\\:display-views"] > .theia-tab-icon-label > .p-TabBar-tabIcon').click();
  await expect(page.locator('[id="widget\\:display-views"]')).toContainText('The following views are avaliable for the loaded project:');
  await page.locator('[id="theia\\:menubar"]').getByText('Vitruvius').click();
  await page.getByText('Vitruvius Load Project', { exact: true }).click();
  await expect(page.getByLabel('Type to narrow down results.').locator('a')).toContainText('Example project');
});

test('Load invalid project', async ({ page }) => {
  await page.goto('http://localhost:3000/');
  await page.locator('[id="theia\\:menubar"]').getByText('Vitruvius').click();
  await page.getByText('Vitruvius Import Project').click();
  await page.getByRole('combobox', { name: 'input' }).fill('Example Project');
  await page.getByRole('combobox', { name: 'input' }).press('Enter');
  await page.getByRole('combobox', { name: 'input' }).fill('Example Description');
  await page.getByRole('combobox', { name: 'input' }).press('Enter');
  await page.getByRole('combobox', { name: 'input' }).fill('invalidhost.com');
  await page.getByRole('combobox', { name: 'input' }).press('Enter');
  await page.getByRole('combobox', { name: 'input' }).fill('1234');
  await page.getByRole('combobox', { name: 'input' }).press('Enter');
  await page.locator('[id="shell-tab-widget\\:display-views"] > .theia-tab-icon-label > .p-TabBar-tabIcon').click();
  await expect(page.locator('[id="widget\\:display-views"]')).toContainText('Currently no Vitruvius project is loaded.');
  await page.locator('[id="theia\\:menubar"]').getByText('Vitruvius').click();
  await page.getByText('Vitruvius Load Project', { exact: true }).click();
  await expect(page.locator('label')).toContainText('Example Project');
});
