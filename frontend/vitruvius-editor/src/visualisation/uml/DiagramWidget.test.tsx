import { Container } from "@theia/core/shared/inversify";
import { MessageService } from "@theia/core";
import { render } from "@testing-library/react";
import { DiagramWidget } from "./DiagramWidget";
import { DiagramEngine, DefaultDiagramState } from "@projectstorm/react-diagrams";
import {Diagram} from "./Diagram";
import '@testing-library/jest-dom';
import {VisualisationWidgetRegistry} from "../VisualisationWidgetRegistry";

describe("DiagramWidget", () => {
    let container: Container;
    let widget: DiagramWidget;
    let messageService: jest.Mocked<MessageService>;

    beforeEach(() => {
        container = new Container();
        messageService = { info: jest.fn() } as unknown as jest.Mocked<MessageService>;

        container.bind(MessageService).toConstantValue(messageService);
        container.bind(DiagramWidget).toSelf();
        container.bind(VisualisationWidgetRegistry).toSelf()
        widget = container.get(DiagramWidget);
    });

    it("should initialize with correct ID and label", async () => {
        expect(widget.id).toBe(DiagramWidget.ID);
        expect(widget.title.label).toBe(DiagramWidget.LABEL);
        expect(widget.title.caption).toBe(DiagramWidget.LABEL);
        expect(widget.title.closable).toBe(true);
    });

    it("should create a DiagramEngine instance", () => {
        expect((widget as any).engine).toBeInstanceOf(DiagramEngine);
    });

    it("should disable drag on the canvas", () => {
        const state = (widget as any).engine.getStateMachine().getCurrentState();
        if (state instanceof DefaultDiagramState) {
            state.dragCanvas.config.allowDrag = true;
            widget.disableDrag();
            expect(state.dragCanvas.config.allowDrag).toBe(false);
        }
    });

    it("should render the diagram content", () => {
        const { container } = render(widget.render());
        expect((container.querySelector(".diagram-container")) as Element).toBeInTheDocument();
    });

    it("should create diagram content from given data", () => {
        const diagram = {
            nodes: [{ uuid: "1", name: "Node1", attributes: [], methods: [] }],
            connections: [{ uuid: "2", sourceNodeUUID: "1", targetNodeUUID: "1", connectionType: "association" }]
        } as any as Diagram;
        const content = widget.createDiagramContent(diagram, "Class");
        expect(content.nodes).toHaveLength(1);
        expect(content.links).toHaveLength(2);
    });



    it("should return the correct visualizer name", () => {
        expect(widget.getVisualizerName()).toBe("DiagramVisualizer");
    });

});