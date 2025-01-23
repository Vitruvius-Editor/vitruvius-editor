import { SNode, SEdge } from "sprotty-protocol"

export interface Class extends SNode {
    name: string;
    attributes: string[];
    methods: string[];
}

export interface GeneralizationRelationship extends SEdge {
    from: string;
    to: string;
}

export interface AssociationRelationship extends SEdge {
    name: string;
    from: string;
    to: string;
    fromMultiplicity: string;
    toMultiplicity: string;
}

export interface AggregationRelationship extends SEdge {
    name: string;
    from: string;
    to: string;
    fromMultiplicity: string;
    toMultiplicity: string;
}