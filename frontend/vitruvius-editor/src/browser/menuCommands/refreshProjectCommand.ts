import {
  Command,
  CommandContribution,
  CommandRegistry,
  MessageService,
  QuickPickService,
} from "@theia/core";
import { inject, injectable } from "@theia/core/shared/inversify";
import { ConnectionService } from "../../backend-communication/ConnectionService";
import {VisualisationWidgetRegistry} from "../../visualisation/VisualisationWidgetRegistry";
import {DisplayViewService} from "../../backend-communication/DisplayViewService";
import {DisplayViewResolver} from "../../visualisation/DisplayViewResolver";
import {Content} from "../../model/Content";

/**
 * Command to refresh the project and synchronise the changes with the Vitruvius server
 */
export const RefreshProjectCommand: Command = {
  id: "RefreshProjectCommand.command",
  label: "Vitruvius Refresh Project",
};

/**
 * Command contribution for the refresh project command.
 */
@injectable()
export class VitruviusRefreshProjectContribution
  implements CommandContribution
{
  @inject(MessageService)
  protected readonly messageService!: MessageService;
  @inject(ConnectionService)
  protected readonly connectionService!: ConnectionService;
  @inject(QuickPickService)
  protected readonly quickPickService!: QuickPickService;
  @inject(VisualisationWidgetRegistry)
  protected readonly visualisationWidgetRegsitry!: VisualisationWidgetRegistry;
  @inject(DisplayViewService)
  protected readonly displayViewService!: DisplayViewService;
  @inject(DisplayViewResolver)
  protected readonly displayViewResolver!: DisplayViewResolver;

  /**
   * Register the refresh project command.
   * @param registry The command registry to register the command with.
   */
  registerCommands(registry: CommandRegistry): void {
    registry.registerCommand(RefreshProjectCommand, {
      execute: () => {
        let items = this.visualisationWidgetRegsitry.getWidgets().map(widgetData => {
          return {
            label: `${widgetData.displayView.name} - ${widgetData.widget.getLabel()}`,
            execute: () => {
              this.displayViewResolver.getContent(widgetData.widget)?.then(content => {
                this.displayViewService.updateDisplayViewContent(widgetData.connection.uuid, widgetData.displayView.name, content).then(res => {
                  this.visualisationWidgetRegsitry.unregisterWidget(widgetData.widget);
                  this.displayViewResolver.getWidget(res as Content)?.then(widget => {
                    this.visualisationWidgetRegsitry.registerWidget( widget, widgetData.displayView, widgetData.connection);
                  })
                }).catch(_error => this.messageService.error("Error updating the window."));
              })
            }
          }
        });
        this.quickPickService.show(items);
      }
    });
  }
}
