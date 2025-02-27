import { DiagramExtractor } from "./DiagramExtractor";
import { VisualisationWidget } from "../VisualisationWidget";
import { Content } from "../../model/Content";

describe("DiagramExtractor", () => {
    let extractor: DiagramExtractor;
    let mockWidget: jest.Mocked<VisualisationWidget<any>>;

    beforeEach(() => {
        extractor = new DiagramExtractor();
        mockWidget = {
            getLabel: jest.fn(),
            getContent: jest.fn()
        } as unknown as jest.Mocked<VisualisationWidget<any>>;
    });

    it("should extract content from the widget", async () => {
        mockWidget.getLabel.mockReturnValue("TestWidget");
        mockWidget.getContent.mockReturnValue("TestContent");

        const content: Content = await extractor.extractContent(mockWidget);

        expect(content.visualizerName).toBe("UmlVisualizer");
        expect(content.windows).toHaveLength(1);
        expect(content.windows[0].name).toBe("TestWidget");
        expect(content.windows[0].content).toBe("TestContent");
    });
});