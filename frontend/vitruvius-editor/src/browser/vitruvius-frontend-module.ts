/*
 * Generated using theia-extension-generator
 */
import {FrontendApplicationContribution, WidgetFactory, bindViewContribution} from '@theia/core/lib/browser';
import { VitruviusEditProjectContribution, VitruviusDeleteProjectContribution, VitruviusHelpCommandContribution, VitruviusImportProjectContribution, VitruviusLoadProjectContribution, VitruviusRefreshProjectContribution, VitruviusMenuContribution } from './vitruvius-contribution';
import { CommandContribution, MenuContribution } from '@theia/core/lib/common';
import { ContainerModule } from '@theia/core/shared/inversify';
import {WidgetContribution} from './display-view-widget-contribution';
import {DisplayViewWidget} from './display-view-widget';
import {BackendServer} from '../backend-communication/BackendServer';
import {DisplayViewService} from '../backend-communication/DisplayViewService';
import {ConnectionService} from '../backend-communication/ConnectionService';

export default new ContainerModule(bind => {
	// Backend communication
	bind("Url").toConstantValue('http://localhost:8080');
	bind(BackendServer).toSelf().inSingletonScope();
	bind(ConnectionService).toSelf().inSingletonScope();
	bind(DisplayViewService).toSelf().inSingletonScope();
	// Ui stuff
    bind(CommandContribution).to(VitruviusHelpCommandContribution);
    bind(CommandContribution).to(VitruviusEditProjectContribution);
    bind(CommandContribution).to(VitruviusDeleteProjectContribution);
    bind(CommandContribution).to(VitruviusImportProjectContribution);
    bind(CommandContribution).to(VitruviusLoadProjectContribution);
    bind(CommandContribution).to(VitruviusRefreshProjectContribution);
    bind(MenuContribution).to(VitruviusMenuContribution);

	bindViewContribution(bind, WidgetContribution);
    bind(FrontendApplicationContribution).toService(WidgetContribution);
    bind(DisplayViewWidget).toSelf();
    bind(WidgetFactory).toDynamicValue(ctx => ({
        id: DisplayViewWidget.ID,
        createWidget: () => ctx.container.get<DisplayViewWidget>(DisplayViewWidget)
    })).inSingletonScope();

});
