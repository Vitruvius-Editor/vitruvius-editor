import { injectable } from "@theia/core/shared/inversify";
import {VisualisationWidget} from "./VisualisationWidget";
import {Connection} from "../model/Connection";
import {DisplayView} from "../model/DisplayView";

@injectable()
export class VisualisationWidgetRegistry{
    private widgets: WidgetData[] = [];

    registerWidget(widget: VisualisationWidget<any>, displayView: DisplayView, connection: Connection): void {
        if (this.widgets.find(widgetData => widgetData.widget.getLabel() === widget.getLabel() && widgetData.widget.constructor.name === widget.constructor.name) == undefined) {
            this.widgets.push({connection, displayView, widget})
        }
    }

    unregisterWidget(widget: VisualisationWidget<any>): void {
        this.widgets = this.widgets.filter(widgetData => widgetData.widget.getLabel() !== widget.getLabel());
    }

    getWidgets(): Readonly<WidgetData[]> {
        return this.widgets;
    }

    getWidgetsByConnection(connection: Connection) : Readonly<WidgetData[]> {
        return this.widgets.filter(widgetData => widgetData.connection.uuid === connection.uuid);
    }
}

type WidgetData = {connection: Connection, displayView: DisplayView, widget: VisualisationWidget<any>}
