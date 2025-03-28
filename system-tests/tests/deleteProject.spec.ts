import { test, expect } from "@playwright/test";
import { beforeEach, afterEach } from "../hooks";

test.beforeEach(beforeEach);
test.afterEach(afterEach);

test("Test project deletion", async ({ page }) => {
  await page.goto("http://localhost:3000/#/home/project");
  await page.locator('[id="theia\\:menubar"]').getByText("Vitruvius").click();
  await page.getByText("Vitruvius Import Project").click();
  await page.getByRole("combobox", { name: "input" }).fill("Project 1");
  await page.getByRole("combobox", { name: "input" }).press("Enter");
  await page
    .getByRole("combobox", { name: "input" })
    .fill("Example Description");
  await page.getByRole("combobox", { name: "input" }).press("Enter");
  await page.getByRole("combobox", { name: "input" }).fill("localhost");
  await page.getByRole("combobox", { name: "input" }).press("Enter");
  await page.getByRole("combobox", { name: "input" }).fill("8000");
  await page.getByRole("combobox", { name: "input" }).press("Enter");
  await page.locator('[id="theia\\:menubar"]').getByText("Vitruvius").click();
  await page.getByText("Vitruvius Import Project").click();
  await page.getByRole("combobox", { name: "input" }).fill("Project 2");
  await page.getByRole("combobox", { name: "input" }).press("Enter");
  await page
    .getByRole("combobox", { name: "input" })
    .fill("Example Description");
  await page.getByRole("combobox", { name: "input" }).press("Enter");
  await page.getByRole("combobox", { name: "input" }).fill("localhost");
  await page.getByRole("combobox", { name: "input" }).press("Enter");
  await page.getByRole("combobox", { name: "input" }).fill("8000");
  await page.getByRole("combobox", { name: "input" }).press("Enter");
  await page.locator('[id="theia\\:menubar"]').getByText("Vitruvius").click();
  await page.getByText("Vitruvius Load Project", { exact: true }).click();
  await expect(page.locator("#list_id_1_0")).toContainText("Project 1");
  await page.locator('[id="theia\\:menubar"]').getByText("Vitruvius").click();
  await page.getByText("Vitruvius Load Project", { exact: true }).click();
  await expect(page.locator("#list_id_1_1")).toContainText("Project 2");
  await page
    .locator(
      '[id="shell-tab-widget\\:display-views"] > .theia-tab-icon-label > .p-TabBar-tabIcon',
    )
    .click();
  await expect(page.locator('[id="widget\\:display-views"]')).toContainText(
    "The following views are avaliable for the loaded project:",
  );
  await page.locator('[id="theia\\:menubar"]').getByText("Vitruvius").click();
  await page.getByText("Vitruvius Delete Project", { exact: true }).click();
  await page.getByRole("option", { name: "Project 2" }).locator("a").click();
  await expect(page.locator("body")).toContainText("Project deleted.");
  await page.locator('[id="theia\\:menubar"]').getByText("Vitruvius").click();
  await page.getByText("Vitruvius Load Project", { exact: true }).click();
  await expect(
    page
      .getByLabel("Type to narrow down results.")
      .locator("a")
      .filter({ hasText: "Project 1" }),
  ).toContainText("Project 1");
});
