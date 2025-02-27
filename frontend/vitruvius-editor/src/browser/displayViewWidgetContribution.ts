/* istanbul ignore file */
import { injectable } from "@theia/core/shared/inversify";
import { MenuModelRegistry } from "@theia/core";
import { DisplayViewWidget } from "./displayViewWidget";
import {
  AbstractViewContribution,
  FrontendApplication,
  FrontendApplicationContribution,
} from "@theia/core/lib/browser";
import { Command, CommandRegistry } from "@theia/core/lib/common/command";

export const WidgetCommand: Command = { id: "widget:command" };

/**
 * Contributes the DisplayViewWidget to the frontend application, registers the command to open the widget and loads the
 * widget on startup.
 */
@injectable()
export class DisplayViewWidgetContribution
  extends AbstractViewContribution<DisplayViewWidget>
  implements FrontendApplicationContribution
{
  constructor() {
    super({
      widgetId: DisplayViewWidget.ID,
      widgetName: DisplayViewWidget.LABEL,
      defaultWidgetOptions: { area: "left" },
      toggleCommandId: WidgetCommand.id,
    });
  }

  registerCommands(commands: CommandRegistry): void {
    commands.registerCommand(WidgetCommand, {
      execute: () => super.openView({ activate: false, reveal: true }),
    });
  }

  /**
   * Registers the menus for the DisplayViewWidget.
   * @param menus The menu model registry to register the menus.
   */
  registerMenus(menus: MenuModelRegistry): void {
    super.registerMenus(menus);
  }

  /**
   * Initializes the layout of the frontend application.
   * @param app The frontend application to initialize the layout for.
   */
  async initializeLayout(app: FrontendApplication): Promise<void> {
    await this.openView();
  }
}
