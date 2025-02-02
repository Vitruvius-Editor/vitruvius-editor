import * as React from "react";
import {
  injectable,
  postConstruct,
  inject,
} from "@theia/core/shared/inversify";
import { ReactWidget } from "@theia/core/lib/browser/widgets/react-widget";
import { MessageService } from "@theia/core";
import { Connection } from "../model/Connection";
import { DisplayView } from "../model/DisplayView";
import { DisplayViewService } from "../backend-communication/DisplayViewService";
import { DisplayViewResolver } from "../visualisation/DisplayViewResolver";
import { Content } from "../model/Content";
import {VisualisationWidgetRegistry} from "../visualisation/VisualisationWidgetRegistry";
import {StatefulWidget} from "@theia/core/lib/browser";

@injectable()
export class DisplayViewWidget extends ReactWidget implements StatefulWidget {
  static readonly ID = "widget:display-views";
  static readonly LABEL = "Vitruvius";

  @inject(MessageService)
  protected readonly messageService!: MessageService;

  @inject(DisplayViewService)
  protected readonly displayViewService: DisplayViewService;

  @inject(DisplayViewResolver)
  protected readonly displayViewResolver: DisplayViewResolver;

  @inject(VisualisationWidgetRegistry)
  protected readonly visualisationWidgetRegsitry: VisualisationWidgetRegistry;

  private connection: Connection | null;
  private widgetItems: WidgetItem[];

  @postConstruct()
  protected init(): void {
    this.doInit();
  }

  protected async doInit(): Promise<void> {
    this.id = DisplayViewWidget.ID;
    this.title.label = DisplayViewWidget.LABEL;
    this.title.caption = DisplayViewWidget.LABEL;
    this.title.closable = true;
    this.title.iconClass = "fa fa-window-maximize"; // example widget icon.
    this.connection = null;
    this.widgetItems = [];
    this.update();
  }

  /**
   * Tries to load a new project with the given connection and if sucessful updates the widgetItems.
   * @param connection The connection to load. Null is used to unload a connection. For example if the connection got
   * deleted.
   */
  async loadProject(connection: Connection | null) {
    if (connection == null) {
      this.connection = null;
      this.widgetItems = [];
      this.update();
    } else {
      this.displayViewService
        .getDisplayViews(connection.uuid)
        .then((displayViews) => {
          // Update the widgetItems on sucess.
          this.connection = connection;
          this.widgetItems = displayViews.map((displayView) => {
            return { displayView, windows: null };
          });
        })
        .catch((_err) => {
          // Write error message to notify the user about the failure and then reset the widget items.
          this.messageService.error(
            "Couldn't connect to the given Vitruvius server.",
          );
          this.connection = null;
          this.widgetItems = [];
        })
        .finally(() => this.update());
    }
  }

  /**
   * Returns the current connection of the widget.
   */
  getConnection(): Connection | null {
    return this.connection;
  }

  /**
   * Render function that renders the widget.
   */
  render(): React.ReactElement {
    if (this.connection == null) {
      // Show info if no project is loaded
      return (
        <div>
          <p>Currently no Vitruvius project is loaded.</p>
        </div>
      );
    } else {
      // Else show a list of the widget items and its windows.
      return (
        <div>
          <p>The following views are avaliable for the loaded project:</p>
          <div className="display-view-list-container">
            <ul className="display-view-list">
              {this.widgetItems.map((widgetItem) => {
                // Map each widget item to a html list item and show the windows if the widget item is clicked.
                return (
                  <div className="display-view-container">
                    <li onClick={() => this.widgetItemClickHandler(widgetItem)} className="display-view-item">
                      {widgetItem.displayView.name}
                    </li>
                    {this.Windows(widgetItem)}
                  </div>
                );
              })}
            </ul>
          </div>
        </div>
      );
    }
  }

  /**
   * Helper function to render the list of windows for a widget item.
   * @param widgetItem The widget item to render the windows for.
   */
  Windows(widgetItem: WidgetItem): React.ReactElement {
    let windowsNonNull = widgetItem.windows ? widgetItem.windows : []; // Convert null to empty array.
    if (windowsNonNull.length != 0) {
      // Return a list of windows if there are any.
      return (
        <div className="window-container">
          <ul>
            {windowsNonNull.map((window) => (
              <div>
                <li onClick={() => this.windowClickHandler(widgetItem, window)} className="window-item">
                  {window}
                </li>
              </div>
            ))}
          </ul>
        </div>
      );
    } else {
      // Return empty div if no windows are avaliable.
      return <div></div>;
    }
  }

  /**
   * Handler for the click event on a widget item. If the windows are not loaded yet, they will be loaded.
   * @param widgetItem The widget item that was clicked.
   */
  private async widgetItemClickHandler(widgetItem: WidgetItem) {
    // Check if the windows are already loaded and if not load them.
    if (widgetItem.windows == null) {
      this.displayViewService
        .getDisplayViewWindows(
          (this.connection as Connection).uuid,
          widgetItem.displayView.name,
        )
        .then((windows) => {
          widgetItem.windows = windows ? windows : [];
          this.update();
        });
    } else {
      // If the windows are already loaded, close them.
      widgetItem.windows = null;
      this.update();
    }
  }

  /**
   * Handler for the click event on a window. Opens the window content in a new widget.
   * @param widgetItem The widget item that was clicked.
   * @param window The window that was clicked.
   */
  private async windowClickHandler(widgetItem: WidgetItem, window: string) {
    // Request the content for the selected window from the server
    this.displayViewService
      .getDisplayViewContent(
        (this.connection as Connection).uuid,
        widgetItem.displayView.name,
        { windows: [window] },
      )
      .then((content) => {
        // Show the content in a new widget.
        this.displayViewResolver
          .getWidget(content as Content)
          ?.then((widget) => {
              this.visualisationWidgetRegsitry.registerWidget(widget, widgetItem.displayView, this.connection as Connection);
              widget.show();
          });
      });
  }
  storeState(): object {
      return {connection: this.connection, widgetItems: this.widgetItems};
  }
  restoreState(oldState: object): void {
      let typedState = oldState as DisplayViewWidgetState;
      this.connection = typedState.connection;
      this.widgetItems = typedState.widgetItems;
  }

}

/**
 * Type used to represent a visual representation of a DisplayView in the widget.
 */
type WidgetItem = { displayView: DisplayView; windows: string[] | null };
type DisplayViewWidgetState = {connection: Connection, widgetItems: WidgetItem[]};
