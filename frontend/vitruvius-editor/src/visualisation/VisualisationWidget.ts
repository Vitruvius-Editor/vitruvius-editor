import { ReactWidget } from "@theia/core/lib/browser/widgets/react-widget";
import {inject} from "@theia/core/shared/inversify";
import {VisualisationWidgetRegistry} from "./VisualisationWidgetRegistry";
import {StatefulWidget} from "@theia/core/lib/browser";
import {DisplayView} from "../model/DisplayView";
import {Connection} from "../model/Connection";

/**
 * Abstract widget that represents a ReactWidget used to visualize a Vitruvius view.
 */
export abstract class VisualisationWidget<T> extends ReactWidget implements StatefulWidget { 
  @inject(VisualisationWidgetRegistry)
  protected readonly visualisationWidgetRegistry!: VisualisationWidgetRegistry;
  private label: string;
  // The content of the widget.
  protected content: T;

  /**
   * Initializes the widget with the given id, label and initial content.
   * @param id The id of the widget.
   * @param initialLabel The label of the widget.
   * @param initialContent The initial content of the widget.
   */
  protected async doInit(
    id: string,
    initialLabel: string,
    initialContent: T,
  ): Promise<void> {
    this.id = id;
    this.setLabel(initialLabel);
    this.title.closable = true;
    this.title.iconClass = "fa fa-window-maximize"; // example widget icon.\
    this.content = initialContent;
    this.update();
  }

  // Updates the content of the widget.
  updateContent(content: T): void {
    this.content = content;
    this.update();
  }

  // Returns the content of the widget.
  getContent(): T {
    return this.content;
  }

  abstract getVisualizerName(): string;

  // Sets the label of the widget.
  setLabel(label: string): void {
    this.title.label = label;
    this.title.caption = label;
    this.label = label;
    this.update();
  }

  // Get the label of the widget
  getLabel(): string {
    return this.label;
  }

  override close(): void {
    this.visualisationWidgetRegistry.unregisterWidget(this);
    super.close();
  }

  storeState(): object {
      let widgetData = this.visualisationWidgetRegistry.getWidgets().find(w => w.widget.getLabel() === this.getLabel());
      return { content: this.getContent(), label: this.getLabel(), displayView: widgetData?.displayView, connection: widgetData?.connection};
  }
  restoreState(oldState: object): void {
      let typedState = oldState as VisualisationWidgetState<T>;
      this.setLabel(typedState.label);
      this.updateContent(typedState.content);
      this.visualisationWidgetRegistry.registerWidget(this, typedState.displayView, typedState.connection);
  }

}

type VisualisationWidgetState<T> = { content: T, label: string, displayView: DisplayView, connection: Connection };
