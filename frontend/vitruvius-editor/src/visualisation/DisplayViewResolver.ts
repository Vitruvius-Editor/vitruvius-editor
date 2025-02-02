import { Visualizer } from "./Visualizer";
import { Extractor } from "./Extractor";
import { injectable } from "@theia/core/shared/inversify";
import { Content } from "../model/Content";
import { VisualisationWidget } from "./VisualisationWidget";

/**
 * The DisplayViewResolver class is responsible for managing the registration and resolution
 * of display views with their corresponding visualizers and extractors.
 */
@injectable()
export class DisplayViewResolver {
  readonly mappings: Map<string, [Visualizer, Extractor]>;

  /**
   * Creates a new DisplayViewResolver instance.
   */
  constructor() {
    this.mappings = new Map();
  }

  /**
   * Registers a display view with its corresponding visualizer and extractor.
   *
   * @param {string} viewMapper - The name of the display view.
   * @param {Visualizer} visualizer - The visualizer associated with the display view.
   * @param {Extractor} extractor - The extractor associated with the display view.
   */
  registerDisplayView(
    viewMapper: string,
    visualizer: Visualizer,
    extractor: Extractor,
  ): void {
    this.mappings.set(viewMapper, [visualizer, extractor]);
  }

  /**
   * Retrieves the widget associated with a given content.
   * @param content - The content for which the widget is being retrieved.
   */
  getWidget(content: Content): Promise<VisualisationWidget<any>> | null {
    return (
      this.mappings
        .get(content.visualizerName)?.[0]
        .visualizeContent(content) ?? null
    );
  }

  /**
   * Retrieves the content associated with a given display view and widget.
   * @param displayView - The display view for which the content is being retrieved.
   * @param widget - The widget for which the content is being retrieved.
   */
  getContent(
    widget: VisualisationWidget<any>,
  ): Promise<Content> | null {
    return (
      this.mappings
        .get(widget.getVisualizerName())?.[1]
        .extractContent(widget) ?? null
    );
  }
}
