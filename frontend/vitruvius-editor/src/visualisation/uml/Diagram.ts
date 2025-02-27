// Represents a diagram with nodes and connections
export type Diagram = {
    nodes: DiagramNode[], // Array of diagram nodes
    connections: DiagramConnection[], // Array of diagram connections
};

// Represents a node in the diagram
export type DiagramNode = {
    uuid: string, // Unique identifier for the node
    name: string, // Name of the node
    attributes: { // Array of attributes for the node
        uuid: string, // Unique identifier for the attribute
        visibility: VisibilityModifier, // Visibility of the attribute
        name: string, // Name of the attribute
        type: { // Type of the attribute
            uuid: string, // Unique identifier for the type
            name: string, // Name of the type
        }
    }[],
    methods: { // Array of methods for the node
        uuid: string, // Unique identifier for the method
        visibility: VisibilityModifier, // Visibility of the method
        name: string, // Name of the method
        parameters: { // Array of parameters for the method
            uuid: string, // Unique identifier for the parameter
            name: string, // Name of the parameter
            type: { // Type of the parameter
                uuid: string, // Unique identifier for the type
                name: string, // Name of the type
            }
        }[],
        returnType: { // Return type of the method
            uuid: string, // Unique identifier for the return type
            name: string, // Name of the return type
        }
    }[],
    viewRecommendations: { // Array of view recommendations for the node
        displayViewName: string, // Display name of the view
        windowName: string, // Name of the window
    }[],
};

// Represents a connection between nodes in the diagram
export type DiagramConnection = {
    uuid: string, // Unique identifier for the connection
    sourceNodeUUID: string, // UUID of the source node
    targetNodeUUID: string, // UUID of the target node
    connectionType: ConnectionType, // Type of the connection
    sourceMultiplicity: string, // Multiplicity at the source end
    targetMultiplicity: string, // Multiplicity at the target end
    connectionName: string, // Name of the connection
};

// Represents the visibility modifier for attributes and methods
export type VisibilityModifier = "PUBLIC" | "PRIVATE" | "PROTECTED";

// Represents the type of connection between nodes
export type ConnectionType = "extends" | "implements" | "association" | "import";

// Returns the symbol for a given visibility modifier
/* istanbul ignore next */
export function visibilitySymbol(visibilityModifier: VisibilityModifier): string {
    switch(visibilityModifier) {
        case "PUBLIC": return "+";
        case "PRIVATE": return "-";
        case "PROTECTED": return "#";
        default: return "";
    }
}