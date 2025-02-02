import { Content } from "../../model/Content";
import { Extractor } from "../Extractor";
import {TableWidget} from "./TableWidget";

export class TableExtractor implements Extractor {
  extractContent(widget: TableWidget): Promise<Content> {
      return new Promise((resolve, _refuse) => resolve({visualizerName: 'TableVisualizer', windows: [
        {
            name: widget.getLabel(),
            content: JSON.stringify({entries: widget.getContent()})
        }
      ]}))
  }
}
