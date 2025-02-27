import { Container } from "@theia/core/shared/inversify";
import { WidgetManager } from "@theia/core/lib/browser";
import { SourceCodeVisualizer } from "./SourceCodeVisualizer";
import { TextWidget } from "./TextWidget";
import { Content } from "../../model/Content";

describe("SourceCodeVisualizer", () => {
    let container: Container;
    let visualizer: SourceCodeVisualizer;
    let widgetManager: jest.Mocked<WidgetManager>;

    beforeEach(() => {
        container = new Container();
        widgetManager = {
            getOrCreateWidget: jest.fn()
        } as unknown as jest.Mocked<WidgetManager>;

        container.bind(WidgetManager).toConstantValue(widgetManager);
        container.bind(SourceCodeVisualizer).toSelf();
        visualizer = container.get(SourceCodeVisualizer);
    });

    it("should visualize content by creating a TextWidget", async () => {
        const content: Content = {
            visualizerName: "TextVisualizer",
            windows: [{
                name: "TestWindow",
                content: "Test Content"
            }]
        };

        const mockWidget = {
            updateContent: jest.fn()
        } as unknown as TextWidget;

        widgetManager.getOrCreateWidget.mockResolvedValue(mockWidget);

        const result = await visualizer.visualizeContent(content);

        expect(widgetManager.getOrCreateWidget).toHaveBeenCalledWith(TextWidget.ID, "TestWindow");
        expect(mockWidget.updateContent).toHaveBeenCalledWith("Test Content");
        expect(result).toBe(mockWidget);
    });

    it("should handle empty content", async () => {
        const content: Content = {
            visualizerName: "TextVisualizer",
            windows: [{
                name: "EmptyWindow",
                content: ""
            }]
        };

        const mockWidget = {
            updateContent: jest.fn()
        } as unknown as TextWidget;

        widgetManager.getOrCreateWidget.mockResolvedValue(mockWidget);

        const result = await visualizer.visualizeContent(content);

        expect(widgetManager.getOrCreateWidget).toHaveBeenCalledWith(TextWidget.ID, "EmptyWindow");
        expect(mockWidget.updateContent).toHaveBeenCalledWith("");
        expect(result).toBe(mockWidget);
    });
});