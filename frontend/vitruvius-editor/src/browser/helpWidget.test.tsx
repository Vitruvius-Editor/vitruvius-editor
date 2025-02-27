import { render } from '@testing-library/react';
import '@testing-library/jest-dom';
import { HelpWidget } from './helpWidget';

describe('HelpWidget', () => {
    let widget: HelpWidget;

    beforeEach(() => {
        widget = new HelpWidget();
        (widget as any).init();
    });

    it('should initialize with correct ID and label', () => {
        expect(widget.id).toBe(HelpWidget.ID);
        expect(widget.title.label).toBe(HelpWidget.LABEL);
        expect(widget.title.caption).toBe(HelpWidget.LABEL);
        expect(widget.title.closable).toBe(true);
        expect(widget.title.iconClass).toBe('fa fa-window-maximize');
    });

    it('should render the help content', () => {
        const { container } = render(widget.render());
        expect(container.querySelector('.vitruvius-help')).toBeInTheDocument();
        expect(container.querySelector('h1')).toHaveTextContent('Vitruvius Hilfe');
    });

    it('should contain specific sections in the help content', () => {
        const { container } = render(widget.render());
        expect(container.querySelector('h1')).toHaveTextContent('Vitruvius Hilfe');
    });

});