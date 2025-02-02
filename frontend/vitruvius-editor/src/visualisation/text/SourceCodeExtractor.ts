import { Content } from "../../model/Content";
import { Extractor } from "../Extractor";
import {TextWidget} from "./TextWidget";

export class SourceCodeExtractor implements Extractor {
  extractContent(widget: TextWidget): Promise<Content> {
      return new Promise((resolve, _refuse) => resolve({visualizerName: 'TextVisualizer', windows: [
            {
                name: widget.getLabel(),
                content: widget.getContent()
            }
      ]}))
  }
}
