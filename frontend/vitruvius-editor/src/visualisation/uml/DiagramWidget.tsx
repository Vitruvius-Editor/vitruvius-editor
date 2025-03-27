import {
  inject,
  injectable,
  postConstruct,
} from "@theia/core/shared/inversify";
import * as React from "react";
import { MessageService } from "@theia/core";
import {
  VisualisationWidget,
  VisualisationWidgetState,
} from "../VisualisationWidget";
import createEngine, {
  DiagramModel,
  CanvasWidget,
  DagreEngine,
  DiagramEngine,
  PathFindingLinkFactory,
} from "@projectstorm/react-diagrams";
import { DefaultDiagramState } from "@projectstorm/react-diagrams";
import {
  ArrowLinkFactory,
  DiagramContent,
  UMLNode,
  UMLRelation,
} from "./DiagramComponents";
import { Diagram, DiagramNode, visibilitySymbol } from "./Diagram";
import { DisplayViewService } from "../../backend-communication/DisplayViewService";
import { DisplayViewWidgetContribution } from "../../browser/displayViewWidgetContribution";
import { Connection } from "../../model/Connection";
import { DisplayViewResolver } from "../DisplayViewResolver";
import { VisualisationWidgetRegistry } from "../VisualisationWidgetRegistry";
import { DisplayView } from "../../model/DisplayView";
import { Content } from "../../model/Content";

/**
 * A Widget to visualize a UML Package Vitruvius view.
 */
@injectable()
export class DiagramWidget extends VisualisationWidget<Diagram> {
  static readonly ID = "packagediagramwidget:packagediagramwidget";
  static readonly LABEL = "DiagramWidget";

  protected engine: DiagramEngine;

  @inject(MessageService)
  protected readonly messageService!: MessageService;

  @inject(DisplayViewService)
  protected readonly displayViewService!: DisplayViewService;

  @inject(DisplayViewWidgetContribution)
  protected readonly displayViewWidgetContribution!: DisplayViewWidgetContribution;

  @inject(DisplayViewResolver)
  protected readonly displayViewResolver!: DisplayViewResolver;

  @inject(VisualisationWidgetRegistry)
  protected readonly visualisationWidgetRegistry!: VisualisationWidgetRegistry;

  /**
   * Initializes the widget with the default id, label and initial content.
   */
  @postConstruct()
  protected init(): void {
    this.doInit(DiagramWidget.ID, DiagramWidget.LABEL, {
      nodes: [],
      connections: [],
    });
  }

  constructor(props: any) {
    super(props);
    this.engine = createEngine({ registerDefaultDeleteItemsAction: false });
  }

  disableDrag = () => {
    const state = this.engine.getStateMachine().getCurrentState();
    if (state instanceof DefaultDiagramState) {
      state.dragCanvas.config.allowDrag = false;
    }
  };

  /**
   * Renders the widget containing the content as a UML Package Diagram given from the Backend.
   * Uses the Parser to parse the content and create the diagram.
   */
  render(): React.ReactElement {
    this.engine.getLinkFactories().registerFactory(new ArrowLinkFactory());

    const umlDiagram = this.createDiagramContent(this.content, "Class");
    const model = new DiagramModel();
    umlDiagram.nodes.forEach((component) => model.addNode(component));
    umlDiagram.links.forEach((link) => model.addLink(link));
    this.engine.setModel(model);
    this.disableDrag();

    const DiagramComponent: React.FC = () => {
      React.useLayoutEffect(() => {
        this.dagre();
      }, []);
      return (
        <div className="editor-container">
          <CanvasWidget className="diagram-container" engine={this.engine} />
        </div>
      );
    };

    return <DiagramComponent />;
  }

  dagre() {
    autoDistribute(this.engine);
    autoRefreshLinks(this.engine);
  }

  /**
   * Parses the diagram content and creates a DiagramContent object.
   * @param diagram The diagram content to parse.
   * @param type The type of diagram ("Class" or "Package").
   * @returns A DiagramContent object containing the parsed nodes and links.
   */
  createDiagramContent(
    diagram: Diagram,
    type: "Class" | "Package",
  ): DiagramContent {
    const nodes: UMLNode[] = [];
    const links: UMLRelation[] = [];

    const handleInputChange = (
      event: React.FormEvent<HTMLSpanElement>,
      data: any,
      key: string,
    ) => {
      data[key] = event.currentTarget.textContent || "";
    };

    const handleInputClick = (event: React.MouseEvent<HTMLSpanElement>) => {
      event.stopPropagation();
    };

    const handleNodeClick = async (
      event: React.MouseEvent<HTMLDivElement, MouseEvent>,
      nodeClass: DiagramNode,
    ) => {
      if (event.detail >= 2) {
        const connection =
          (await this.displayViewWidgetContribution.widget.then((widget) =>
            widget.getConnection(),
          )) as Connection;
        this.displayViewService
          .getDisplayViewContent(
            connection.uuid,
            nodeClass.viewRecommendations[0].displayViewName,
            { windows: [nodeClass.viewRecommendations[0].windowName] },
          )
          .then((content) => {
            // Show the content in a new widget.
            this.displayViewResolver
              .getWidget(content as Content, true)
              ?.then(async (widget) => {
                this.visualisationWidgetRegistry.registerWidget(
                  widget,
                  await this.displayViewService
                    .getDisplayViews(connection.uuid)
                    .then(
                      (displayViews) =>
                        displayViews.find(
                          (displayView) =>
                            displayView.name ===
                            nodeClass.viewRecommendations[0].displayViewName,
                        ) as DisplayView,
                    ),
                  connection,
                );
                widget.show();
              });
          });
      }
    };

    diagram.nodes.forEach((data, nodeIndex) => {
      if (type === "Class") {
        const text = (
          <div onClick={(e) => handleNodeClick(e, data)}>
            {data.nodeType !== '<<class>>' ? <div><span>{data.nodeType}<br/></span></div> : <div></div>}
            <span
              contentEditable
              spellCheck={false}
              onInput={(e) => handleInputChange(e, data, "name")}
              onClick={handleInputClick}
            >
              {data.name}
            </span>{" "}
            <br />
            <hr />
            {data.attributes.map((attr, index) => (
              <React.Fragment key={index}>
                <span
                  contentEditable
                  spellCheck={false}
                  onInput={(e) => handleInputChange(e, attr, "visibility")}
                  onClick={handleInputClick}
                >
                  {visibilitySymbol(attr.visibility)}
                </span>
                <span
                  contentEditable
                  spellCheck={false}
                  onInput={(e) => handleInputChange(e, attr, "name")}
                  onClick={handleInputClick}
                >
                  {attr.name}
                </span>
                :
                <span
                  contentEditable
                  spellCheck={false}
                  onInput={(e) => handleInputChange(e, attr.type, "name")}
                  onClick={handleInputClick}
                >
                  {attr.type.name}
                </span>
                <span
                  onClick={() => this.handlerDeleteAttribute(nodeIndex, index)}
                  style={{ cursor: "pointer", color: "red" }}
                >
                  {" "}
                  -{" "}
                </span>
                <br />
              </React.Fragment>
            ))}
            <hr />
            {data.methods.map((method, methodIndex) => (
              <React.Fragment key={methodIndex}>
                <span
                  contentEditable
                  spellCheck={false}
                  onInput={(e) => handleInputChange(e, method, "visibility")}
                  onClick={handleInputClick}
                >
                  {visibilitySymbol(method.visibility)}
                </span>
                <span
                  contentEditable
                  spellCheck={false}
                  onInput={(e) => handleInputChange(e, method, "name")}
                  onClick={handleInputClick}
                >
                  {method.name}
                </span>
                (
                {method.parameters.map((param, index) => (
                  <React.Fragment key={param.uuid}>
                    <span
                      contentEditable
                      spellCheck={false}
                      onInput={(e) => handleInputChange(e, param, "name")}
                      onClick={handleInputClick}
                    >
                      {param.name}
                    </span>
                    :
                    <span
                      contentEditable
                      spellCheck={false}
                      onInput={(e) => handleInputChange(e, param.type, "name")}
                      onClick={handleInputClick}
                    >
                      {param.type.name}
                    </span>
                    <span
                      onClick={() =>
                        this.handleDeleteParameter(
                          nodeIndex,
                          methodIndex,
                          index,
                        )
                      }
                      style={{ cursor: "pointer", color: "red" }}
                    >
                      {" "}
                      -{" "}
                    </span>
                    <span>
                      {index != method.parameters.length - 1 ? "," : ""}
                    </span>
                  </React.Fragment>
                ))}
                ):
                <span
                  contentEditable
                  spellCheck={false}
                  onInput={(e) =>
                    handleInputChange(e, method.returnType, "name")
                  }
                  onClick={handleInputClick}
                >
                  {method.returnType.name}
                </span>
                <span
                  onClick={() =>
                    this.handlerDeleteMethod(nodeIndex, methodIndex)
                  }
                  style={{ cursor: "pointer", color: "red" }}
                >
                  {" "}
                  -{" "}
                </span>
                <br />
              </React.Fragment>
            ))}
          </div>
        );
        // @ts-ignore
        const item = new UMLNode(data.uuid, text);
        nodes.push(item);
      } else if (type === "Package") {
        nodes.push(new UMLNode(data.uuid, data.name));
      }
    });

    diagram.connections.forEach((link) => {
      const fromNode = nodes.find(
        (node) => node.getClassID() === link.sourceNodeUUID,
      );
      const toNode = nodes.find(
        (node) => node.getClassID() === link.targetNodeUUID,
      );
      if (fromNode !== undefined && toNode !== undefined) {
        switch (link.connectionType) {
          case "association":
            links.push(new UMLRelation("default", link.uuid, fromNode, toNode));
          default:
            links.push(
              new UMLRelation("advanced", link.uuid, fromNode, toNode),
            );
        }
      }
    });

    return { nodes, links };
  }

  /**
   * Returns the name of the visualizer.
   * @returns The name of the visualizer.
   */
  getVisualizerName(): string {
    return "UmlVisualizer";
  }

  override restoreState(oldState: object): void {
    let typedState = oldState as VisualisationWidgetState<Diagram>;
    this.setLabel(typedState.label);
    this.updateContent(typedState.content);
    this.visualisationWidgetRegistry.registerWidget(
      this,
      typedState.displayView,
      typedState.connection,
    );
    this.dagre();
  }

  handlerDeleteAttribute(node: number, index: number) {
    this.content.nodes[node].attributes.splice(index, 1);
    this.update();
  }

  handlerDeleteMethod(node: number, index: number) {
    this.content.nodes[node].methods.splice(index, 1);
    this.update();
  }

  handleDeleteParameter(node: number, method: number, parameter: number) {
    this.content.nodes[node].methods[method].parameters.splice(parameter, 1);
    this.update();
  }
}

/* istanbul ignore next */
/**
 * Generates a Dagre engine with specific configurations.
 * @returns A configured DagreEngine instance.
 */
function genDagreEngine() {
  return new DagreEngine({
    graph: {
      rankdir: "RL",
      ranker: "longest-path",
      marginx: 25,
      marginy: 25,
    },
    includeLinks: true,
    nodeMargin: 25,
  });
}

/* istanbul ignore next */
/**
 * Distributes nodes in the diagram using the Dagre engine.
 * @param engine The DiagramEngine instance.
 */
function autoDistribute(engine: DiagramEngine) {
  const model = engine.getModel();

  const dagreEngine = genDagreEngine();
  dagreEngine.redistribute(model);

  reroute(engine);
  engine.repaintCanvas();
}

/* istanbul ignore next */
/**
 * Refreshes links in the diagram using the Dagre engine.
 * @param engine The DiagramEngine instance.
 */
function autoRefreshLinks(engine: DiagramEngine) {
  const model = engine.getModel();

  const dagreEngine = genDagreEngine();
  dagreEngine.refreshLinks(model);

  reroute(engine);
  engine.repaintCanvas();
}

/* istanbul ignore next */
/**
 * Reroutes links in the diagram.
 * @param engine The DiagramEngine instance.
 */
function reroute(engine: DiagramEngine) {
  engine
    .getLinkFactories()
    .getFactory<PathFindingLinkFactory>(PathFindingLinkFactory.NAME)
    .calculateRoutingMatrix();
}
