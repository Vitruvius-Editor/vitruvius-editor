import {
  Command,
  CommandContribution,
  CommandRegistry,
  MessageService,
  QuickPickService,
} from "@theia/core";
import { inject, injectable } from "@theia/core/shared/inversify";
import { DisplayViewWidgetContribution } from "../displayViewWidgetContribution";
import { ConnectionService } from "../../backend-communication/ConnectionService";
import {VisualisationWidgetRegistry} from "../../visualisation/VisualisationWidgetRegistry";

/**
 * Command to delete a project.
 */
export const DeleteProjectCommand: Command = {
  id: "DeleteProjectCommand.command",
  label: "Vitruvius Delete Project",
};

/**
 * Command contribution to delete a project.
 */
@injectable()
export class VitruviusDeleteProjectContribution implements CommandContribution {
  @inject(MessageService)
  protected readonly messageService!: MessageService;
  @inject(QuickPickService)
  protected readonly quickPickService!: QuickPickService;
  @inject(DisplayViewWidgetContribution)
  protected readonly displayViewWidgetContribution!: DisplayViewWidgetContribution;
  @inject(ConnectionService)
  protected readonly connectionService!: ConnectionService;
  @inject(VisualisationWidgetRegistry)
  protected readonly visualisationWidgetRegistry!: VisualisationWidgetRegistry;

  /**
   * Register the command to delete a project.
   * @param registry The command registry to register the command.
   */
  registerCommands(registry: CommandRegistry): void {
    registry.registerCommand(DeleteProjectCommand, {
      // Request all available connections from the server.
      execute: () =>
        this.connectionService
          .getConnections()
          .then((connections) => {
            // Convert the connections to quick pick items.
            let items = connections.map((connection) => {
              return {
                label: connection.name,
                // Delete the project with the selected connection, if picked.
                execute: () => {
                  this.connectionService
                    .deleteConnection(connection.uuid)
                    .then(() => {
                      this.messageService.info("Project deleted.");
                      // Unload the project if the deleted project is currently loaded.
                      this.displayViewWidgetContribution.widget.then(
                        (widget) => {
                          if (widget.getConnection()?.uuid == connection.uuid) {
                            this.visualisationWidgetRegistry.getWidgetsByConnection(connection).forEach(widgetData => widgetData.widget.close());
                            widget.loadProject(null);
                          }
                        },
                      );
                    })
                    .catch((_err) =>
                      this.messageService.error(
                        "Couldn't connect to the Backend server.",
                      ),
                    );
                },
              };
            });
            // Show the quick pick items.
            this.quickPickService.show(items);
          })
          .catch((_err) =>
            this.messageService.error("Couldn't connect to the Backend."),
          ),
    });
  }
}
