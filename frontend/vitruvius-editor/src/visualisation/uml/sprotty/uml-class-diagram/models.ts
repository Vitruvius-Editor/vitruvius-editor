import { SNode, SEdge } from "sprotty-protocol"

export interface Class extends SNode {
    name: string;
}

export interface Generalization extends SEdge {
    from: string;
    to: string;
}