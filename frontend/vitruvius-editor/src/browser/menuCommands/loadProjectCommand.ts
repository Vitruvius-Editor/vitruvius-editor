import {
  Command,
  CommandContribution,
  CommandRegistry,
  MessageService,
  QuickPickService,
} from "@theia/core";
import { inject, injectable } from "@theia/core/shared/inversify";
import { ConnectionService } from "../../backend-communication/ConnectionService";
import { DisplayViewWidgetContribution } from "../displayViewWidgetContribution";
import {VisualisationWidgetRegistry} from "../../visualisation/VisualisationWidgetRegistry";
import {Connection} from "../../model/Connection";

/**
 * Command to load a saved project from the server.
 */
export const LoadProjectCommand: Command = {
  id: "LoadProjectCommand.command",
  label: "Vitruvius Load Project",
};

/**
 * Command contribution to load a saved project from the server.
 */
@injectable()
export class VitruviusLoadProjectContribution implements CommandContribution {
  @inject(MessageService)
  protected readonly messageService!: MessageService;
  @inject(ConnectionService)
  protected readonly connectionService!: ConnectionService;
  @inject(QuickPickService)
  protected readonly quickPickService!: QuickPickService;
  @inject(DisplayViewWidgetContribution)
  protected readonly displayViewWidgetContribution!: DisplayViewWidgetContribution;
  @inject(VisualisationWidgetRegistry)
  protected readonly visualisationWidgetRegistry!: VisualisationWidgetRegistry;

  /**
   * Register the command to load a saved project from the server.
   * @param registry The command registry to register the command.
   */
  registerCommands(registry: CommandRegistry): void {
    registry.registerCommand(LoadProjectCommand, {
      // Load all available connections from the server.
      execute: () =>
        this.connectionService
          .getConnections()
          .then((res) => {
            // Convert the connections to quick pick items.
            let items = res.map((connection) => {
              return {
                label: connection.name,
                // Load the project with the selected connection, if picked.
                execute: () =>
                  this.displayViewWidgetContribution.widget.then((widget) => {
                    if(widget.getConnection() != null) {
                      this.visualisationWidgetRegistry.getWidgetsByConnection(widget.getConnection() as Connection).forEach(widgetData => widgetData.widget.close());
                    }
                    widget.loadProject(connection)
                  }),
              };
            });
            // Show the quick pick items.
            this.quickPickService.show(items);
          })
          .catch((_err) =>
            this.messageService.error("Couldn't connect to backend."),
          ),
    });
  }
}
