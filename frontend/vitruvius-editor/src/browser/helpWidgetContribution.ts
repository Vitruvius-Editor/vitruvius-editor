/* istanbul ignore file */
import { injectable } from "@theia/core/shared/inversify";
import { HelpWidget } from "./helpWidget";
import {
  AbstractViewContribution,
  FrontendApplication,
  FrontendApplicationContribution,
} from "@theia/core/lib/browser";
import { Command, CommandRegistry } from "@theia/core/lib/common/command";

export const WidgetCommand: Command = { id: "widget:command" };

/**
 * Contributes the HelpWidget to the frontend application, registers the command to open the widget and loads the
 * widget on startup.
 */
@injectable()
export class HelpWidgetContribution
  extends AbstractViewContribution<HelpWidget>
  implements FrontendApplicationContribution
{
  constructor() {
    super({
      widgetId: HelpWidget.ID,
      widgetName: HelpWidget.LABEL,
      defaultWidgetOptions: { area: "main" },
      toggleCommandId: WidgetCommand.id,
    });
  }

  registerCommands(commands: CommandRegistry): void {
    commands.registerCommand(WidgetCommand, {
      execute: () => super.openView({ activate: false, reveal: true }),
    });
  }

  /**
   * Initializes the layout of the frontend application.
   * @param app The frontend application to initialize the layout for.
   */
  async initializeLayout(app: FrontendApplication): Promise<void> {
    await this.openView();
  }
}
