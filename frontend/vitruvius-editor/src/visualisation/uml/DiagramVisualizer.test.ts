import { Container } from "@theia/core/shared/inversify";
import { WidgetManager } from "@theia/core/lib/browser";
import { ApplicationShell } from "@theia/core/lib/browser/shell/application-shell";
import { DiagramVisualizer } from "./DiagramVisualizer";
import { DiagramWidget } from "./DiagramWidget";
import { Content } from "../../model/Content";

describe("DiagramVisualizer", () => {
    let container: Container;
    let visualizer: DiagramVisualizer;
    let widgetManager: jest.Mocked<WidgetManager>;
    let shell: jest.Mocked<ApplicationShell>;

    beforeEach(() => {
        container = new Container();
        widgetManager = {
            getOrCreateWidget: jest.fn()
        } as unknown as jest.Mocked<WidgetManager>;
        shell = {
            addWidget: jest.fn(),
            activateWidget: jest.fn()
        } as unknown as jest.Mocked<ApplicationShell>;

        container.bind(WidgetManager).toConstantValue(widgetManager);
        container.bind(ApplicationShell).toConstantValue(shell);
        container.bind(DiagramVisualizer).toSelf();
        visualizer = container.get(DiagramVisualizer);
    });

    it("should visualize content by creating or retrieving a DiagramWidget", async () => {
        const content: Content = {
            visualizerName: "DiagramVisualizer",
            windows: [{
                name: "TestWindow",
                content: JSON.stringify({ nodes: [], connections: [] })
            }]
        };

        const mockWidget = {
            updateContent: jest.fn()
        } as unknown as DiagramWidget;

        widgetManager.getOrCreateWidget.mockResolvedValue(mockWidget);

        const result = await visualizer.visualizeContent(content);

        expect(widgetManager.getOrCreateWidget).toHaveBeenCalledWith(DiagramWidget.ID, "TestWindow");
        expect(mockWidget.updateContent).toHaveBeenCalledWith({ nodes: [], connections: [] });
        expect(result).toBe(mockWidget);
    });

    it("should handle empty content", async () => {
        const content: Content = {
            visualizerName: "DiagramVisualizer",
            windows: [{
                name: "EmptyWindow",
                content: JSON.stringify({ nodes: [], connections: [] })
            }]
        };

        const mockWidget = {
            updateContent: jest.fn()
        } as unknown as DiagramWidget;

        widgetManager.getOrCreateWidget.mockResolvedValue(mockWidget);

        const result = await visualizer.visualizeContent(content);

        expect(widgetManager.getOrCreateWidget).toHaveBeenCalledWith(DiagramWidget.ID, "EmptyWindow");
        expect(mockWidget.updateContent).toHaveBeenCalledWith({ nodes: [], connections: [] });
        expect(result).toBe(mockWidget);
    });
});