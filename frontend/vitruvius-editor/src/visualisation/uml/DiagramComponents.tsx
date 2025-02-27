/* istanbul ignore file */
import { DefaultLinkFactory, DefaultLinkModel, DefaultNodeModel, DefaultPortModel } from '@projectstorm/react-diagrams-defaults';
import * as React from 'react';
import { DiagramEngine, LinkWidget, PointModel } from '@projectstorm/react-diagrams-core';
import { DefaultLinkPointWidget, DefaultLinkSegmentWidget } from '@projectstorm/react-diagrams-defaults';

/**
 * The UMLNode class represents a UML class node in the React diagram.
 */
export class UMLNode extends DefaultNodeModel {
  classID: string;

  constructor(classID: string, text: string) {
    super({
      name: text,
      color: 'rgb(145, 145, 145)'
    });
    this.classID = classID;
  }

  /**
   * Returns the class ID of the UML node.
   */
  getClassID(): string {
    return this.classID;
  }

  /**
   * Returns the first port of the UML node.
   */
  getPort(): DefaultPortModel {
    //@ts-ignore
    return this.ports[0];
  }
}

/**
 * The UMLRelation class represents a general UML relation in the React diagram.
 */
export class UMLRelation extends DefaultLinkModel {
  private readonly fromPort: DefaultPortModel | null;
  private readonly toPort: DefaultPortModel | null;

  constructor(type: string, label: string, from: UMLNode, to: UMLNode) {
    super({
      type: 'advanced',
      curvyness: 0,
      color: 'black',
      selectedColor: 'black'
    });

    switch (type) {
      case 'default': {
        this.fromPort = from.addPort(new DefaultPortModel(false, 'out'));
        this.toPort = to.addPort(new DefaultPortModel(true, 'in'));
        this.fromPort.link(this.toPort, new DefaultLinkFactory());
        break;
      }
      case 'advanced': {
        this.fromPort = from.addPort(new ArrowPortModel(false, 'out'));
        this.toPort = to.addPort(new ArrowPortModel(true, 'in'));
        this.fromPort.link(this.toPort, new ArrowLinkFactory());
        break;
      }
      default: {
        this.fromPort = null;
        this.toPort = null;
        break;
      }
    }

    this.setLocked(true);
    this.setSourcePort(this.fromPort);
    this.setTargetPort(this.toPort);
    this.addLabel(label);
  }
}

/**
 * The AdvancedLinkModel class represents an advanced link model in the React diagram.
 */
export class AdvancedLinkModel extends DefaultLinkModel {
  constructor() {
    super({
      type: 'advanced',
      width: 4
    });
  }
}

/**
 * The ArrowPortModel class represents a port model with arrow links in the React diagram.
 */
export class ArrowPortModel extends DefaultPortModel {
  createLinkModel(): AdvancedLinkModel {
    return new AdvancedLinkModel();
  }
}

/**
 * CustomLinkArrowWidget component renders an arrow at the end of a link.
 */
export const CustomLinkArrowWidget = (props: { color?: any; point?: any; previousPoint?: any; }) => {
  const { point, previousPoint } = props;

  const angle =
      90 +
      (Math.atan2(
              point.getPosition().y - previousPoint.getPosition().y,
              point.getPosition().x - previousPoint.getPosition().x
          ) *
          180) /
      Math.PI;

  return (
      <g className="arrow" transform={`translate(${point.getPosition().x}, ${point.getPosition().y})`}>
        <g style={{ transform: `rotate(${angle}deg)` }}>
          <g transform={'translate(0, -3)'}>
            <polygon
                points="0,10 8,30 -8,30"
                fill={props.color}
                data-id={point.getID()}
                data-linkid={point.getLink().getID()}
            />
          </g>
        </g>
      </g>
  );
};

export interface AdvancedLinkWidgetProps {
  link: DefaultLinkModel;
  diagramEngine: DiagramEngine;
  pointAdded?: (point: PointModel, event: MouseEvent) => any;
  renderPoints?: boolean;
  selected?: (event: MouseEvent) => any;
}

/**
 * AdvancedLinkWidget component renders an advanced link with points and arrows.
 */
export class AdvancedLinkWidget extends React.Component<AdvancedLinkWidgetProps> {
  generatePoint = (point: PointModel): JSX.Element => {
    return (
        <DefaultLinkPointWidget
            key={point.getID()}
            point={point as any}
            colorSelected={this.props.link.getOptions().selectedColor ?? ''}
            color={this.props.link.getOptions().color}
        />
    );
  };

  generateLink = (path: string, extraProps: any, id: string | number): JSX.Element => {
    return (
        //@ts-ignore
        <DefaultLinkSegmentWidget
            key={`link-${id}`}
            path={path}
            diagramEngine={this.props.diagramEngine}
            factory={this.props.diagramEngine.getFactoryForLink(this.props.link)}
            link={this.props.link}
            extras={extraProps}
        />
    );
  };

  addPointToLink = (event: any, index: number) => {
    if (
        !event.shiftKey &&
        !this.props.link.isLocked() &&
        this.props.link.getPoints().length - 1 <= this.props.diagramEngine.getMaxNumberPointsPerLink()
    ) {
      const position = this.props.diagramEngine.getRelativeMousePoint(event);
      const point = this.props.link.point(position.x, position.y, index);
      event.persist();
      event.stopPropagation();
      this.props.diagramEngine.getActionEventBus().fireAction({
        event,
        model: point
      });
    }
  };

  generateArrow(point: PointModel, previousPoint: PointModel): JSX.Element {
    return (
        <CustomLinkArrowWidget
            key={point.getID()}
            point={point as any}
            previousPoint={previousPoint as any}
            // @ts-ignore
            colorSelected={this.props.link.getOptions().selectedColor}
            color={this.props.link.getOptions().color}
        />
    );
  }

  render() {
    const points = this.props.link.getPoints();
    const paths = [];

    for (let j = 0; j < points.length - 1; j++) {
      paths.push(
          this.generateLink(
              LinkWidget.generateLinePath(points[j], points[j + 1]),
              {
                'data-linkid': this.props.link.getID(),
                'data-point': j,
                onMouseDown: (event: MouseEvent) => {
                  this.addPointToLink(event, j + 1);
                }
              },
              j
          )
      );
    }

    for (let i = 1; i < points.length - 1; i++) {
      paths.push(this.generatePoint(points[i]));
    }

    if (this.props.link.getTargetPort() !== null) {
      paths.push(this.generateArrow(points[points.length - 1], points[points.length - 2]));
    } else {
      paths.push(this.generatePoint(points[points.length - 1]));
    }

    return <g data-default-link-test={this.props.link.getOptions().testName}>{paths}</g>;
  }
}

/**
 * The ArrowLinkFactory class generates advanced link models and widgets.
 */
export class ArrowLinkFactory extends DefaultLinkFactory {
  constructor() {
    super('advanced');
  }

  generateModel(): AdvancedLinkModel {
    return new AdvancedLinkModel();
  }

  generateReactWidget(event: { model: DefaultLinkModel; }): JSX.Element {
    return <AdvancedLinkWidget link={event.model} diagramEngine={this.engine} />;
  }
}

export type DiagramContent = { nodes: UMLNode[], links: UMLRelation[] };