import {
  injectable,
  postConstruct,
} from "@theia/core/shared/inversify";
import * as React from "react";
import { VisualisationWidget } from "../VisualisationWidget";

/**
* A Widget to visualize a table based Vitruvius view.
*/
@injectable()
export class TableWidget extends VisualisationWidget<TableEntry[]> {
    getVisualizerName(): string {
        return "TextVisualizer";
    }
	static readonly ID = "tablewidget:tablewidget";
	static readonly LABEL = "TableWidget";

	/**
	* Initializes the widget with the default id, label and initial content.
	*/
	@postConstruct()
	protected init(): void {
		this.doInit(TableWidget.ID, TableWidget.LABEL, []);
	}

	/**
	* Renders the widget containing a text area to edit the content.
	*/
	render(): React.ReactElement {
  let attributes = Object.keys(this.content[0] || {});
	return (
	  <div className="editor-container table-widget">
      <table>
        <thead>
          <tr>
            {attributes.map(attribute => (
              <th>{attribute}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {this.content.map(entry => (
            <tr>
              {attributes.map(attribute => (
                <td>{entry[attribute as keyof TableEntry].toString()}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
	  </div>
	);
	}

    getContentString(): string {
        return JSON.stringify({entries: this.content})
    }
}

