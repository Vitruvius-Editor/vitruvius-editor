import { Extractor } from "../Extractor";
import { Widget } from "@theia/core/lib/browser";

export class TableExtractor implements Extractor {
	extractContent(widget: Widget): Promise<string> {
		throw new Error("Method not implemented.");
	}
}
