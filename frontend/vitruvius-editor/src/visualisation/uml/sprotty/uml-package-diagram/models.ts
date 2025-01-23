import { SNode, SEdge } from "sprotty-protocol"

export interface Package extends SNode {
    name: string;
}

export interface DependencyRelationship extends SEdge {
    name: string;
    from: string;
    to: string;
}

export interface Comment extends SNode {
    text: string;
}