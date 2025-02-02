import {
  inject,
  injectable,
  postConstruct,
} from "@theia/core/shared/inversify";
import * as React from "react";
import { MessageService } from "@theia/core";
import { VisualisationWidget } from "../VisualisationWidget";

/**
 * A Widget to visualize a text based Vitruvius view.
 */
@injectable()
export class TextWidget extends VisualisationWidget<string> {
  getVisualizerName(): string {
      return "TextVisualizer";
  }
  static readonly ID = "textwidget:textwidget";
  static readonly LABEL = "TextWidget";

  @inject(MessageService)
  protected readonly messageService!: MessageService;

  /**
   * Initializes the widget with the default id, label and initial content.
   */
  @postConstruct()
  protected init(): void {
    this.doInit(TextWidget.ID, TextWidget.LABEL, "/*Initial Content*/");
  }

  /**
   * Renders the widget containing a text area to edit the content.
   */
  render(): React.ReactElement {
    return (
      <div className="editor-container">
        <textarea defaultValue={this.content} className="editor-window" onChange={this.handleChange} spellCheck="false"></textarea>
      </div>
    );
  }

  /**
   * Handles the change event of the text area and updates the content.
   * @param event
   */
  handleChange(event: React.ChangeEvent<HTMLTextAreaElement>): void {
    this.content = event.target.value;
  }
}
