import { SGraph, SEdge, SNode } from "sprotty-protocol";
import { PackageNode,PackageEdge } from "./models";

export const graph: SGraph = {
    type: 'graph',
    id: 'graph',
    children: [
        <PackageNode>{
            type: 'packageNode',
            id: 'package1',
            name: 'Package1',
            position: { x: 50, y: 50 },
            size: { width: 150, height: 100 }
        },
        <PackageNode>{
            type: 'packageNode',
            id: 'package2',
            name: 'Package2',
            position: { x: 400, y: 50 },
            size: { width: 150, height: 100 }
        },
        <PackageEdge>{
            //type: 'edge',
            type: 'packageEdge',
            id: 'edge01',
            sourceId: 'package1',
            targetId: 'package2',
            routerKind: 'manhattan',
        }
    ]
};