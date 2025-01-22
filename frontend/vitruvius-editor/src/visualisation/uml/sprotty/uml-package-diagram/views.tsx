/** @jsx svg */
import { svg } from 'sprotty/lib/lib/jsx';
import { injectable } from 'inversify';
import { VNode } from 'snabbdom';
import { IView, RenderingContext, SNode } from 'sprotty';
import { Package } from './models';

@injectable()
export class SPackageView implements IView {
    render(node: Readonly<SNode & Package>, context: RenderingContext): VNode {
        const position = 50;
        return <g>
            <rect class-sprotty-node={true}
                width={node.size.width}
                height={node.size.height}
            >
            </rect>
            <text x={position} y={position + 5}>{node.name}</text>
        </g>;
    }
}
