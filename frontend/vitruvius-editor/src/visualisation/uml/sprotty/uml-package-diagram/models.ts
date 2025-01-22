import { SNode, SEdge } from "sprotty-protocol"

export interface Package extends SNode {
    name: string;
}

export interface Imports extends SEdge {
    from: string;
    to: string;
}

export interface Comment extends SNode {
    text: string;
}