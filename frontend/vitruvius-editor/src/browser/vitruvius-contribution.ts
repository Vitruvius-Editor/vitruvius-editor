import { injectable, inject } from '@theia/core/shared/inversify';
import { Command, CommandContribution, CommandRegistry, MAIN_MENU_BAR, MenuContribution, MenuModelRegistry, MenuPath, MessageService } from '@theia/core/lib/common';

export namespace VitruviusMenus {
    export const VITRUVIUS: MenuPath = MAIN_MENU_BAR.concat("vitruvius");
}

export const VitruviusHelpCommand: Command = {
    id: 'VitruviusHelp.command',
    label: 'Vitruvius Help'
};

export const VitruviusLoadProject: Command = {
    id: 'VitruviusLoadProject.command',
    label: 'Vitruvius Load Project'
}

export const VitruviusImportProject: Command = {
    id: 'VitruviusImportProject.command',
    label: 'Vitruvius Import Project'
}

export const VitruviusRefreshProject: Command = {
    id: 'VitruviusRefreshProject.command',
    label: 'Vitruvius Refresh Project'
}

export const VitruviusDeleteProject: Command = {
    id: 'VitruviusDeleteProject.command',
    label: 'Vitruvius Delete Project'
}

export const VitruviusChangeProject: Command = {
    id: 'VitruviusChangeProject.command',
    label: 'Vitruvius Change Project'
}

@injectable()
export class VitruviusHelpCommandContribution implements CommandContribution {
    
    @inject(MessageService)
    protected readonly messageService!: MessageService;
    registerCommands(registry: CommandRegistry): void {
        registry.registerCommand(VitruviusHelpCommand, {
            execute: () => this.messageService.info('Vitruvius Help')
        });
    }
}

@injectable()
export class VitruviusLoadProjectContribution implements CommandContribution {
    
    @inject(MessageService)
    protected readonly messageService!: MessageService;
    registerCommands(registry: CommandRegistry): void {
        registry.registerCommand(VitruviusLoadProject, {
            execute: () => this.messageService.info('Load Project')
        });
    }
}

@injectable()
export class VitruviusImportProjectContribution implements CommandContribution {
    
    @inject(MessageService)
    protected readonly messageService!: MessageService;
    registerCommands(registry: CommandRegistry): void {
        registry.registerCommand(VitruviusImportProject, {
            execute: () => this.messageService.info('Import Project')
        });
    }
}

@injectable()
export class VitruviusRefreshProjectContribution implements CommandContribution {
    
    @inject(MessageService)
    protected readonly messageService!: MessageService;
    registerCommands(registry: CommandRegistry): void {
        registry.registerCommand(VitruviusRefreshProject, {
            execute: () => this.messageService.info('Refresh Project')
        });
    }
}

@injectable()
export class VitruviusDeleteProjectContribution implements CommandContribution {
    
    @inject(MessageService)
    protected readonly messageService!: MessageService;
    registerCommands(registry: CommandRegistry): void {
        registry.registerCommand(VitruviusDeleteProject, {
            execute: () => this.messageService.info('Delete Project')
        });
    }
}

@injectable()
export class VitruviusChangeProjectContribution implements CommandContribution {
    
    @inject(MessageService)
    protected readonly messageService!: MessageService;
    registerCommands(registry: CommandRegistry): void {
        registry.registerCommand(VitruviusChangeProject, {
            execute: () => this.messageService.info('Change Project')
        });
    }
}

@injectable()
export class VitruviusMenuContribution implements MenuContribution {

    registerMenus(menus: MenuModelRegistry): void {
        menus.registerSubmenu(VitruviusMenus.VITRUVIUS, 'Vitruvius');

        menus.registerMenuAction(VitruviusMenus.VITRUVIUS, {
            commandId: VitruviusHelpCommand.id,
            label: VitruviusHelpCommand.label
        });
        menus.registerMenuAction(VitruviusMenus.VITRUVIUS, {
            commandId: VitruviusLoadProject.id,
            label: VitruviusLoadProject.label
        });
        menus.registerMenuAction(VitruviusMenus.VITRUVIUS, {
            commandId: VitruviusImportProject.id,
            label: VitruviusImportProject.label
        });
        menus.registerMenuAction(VitruviusMenus.VITRUVIUS, {
            commandId: VitruviusRefreshProject.id,
            label: VitruviusRefreshProject.label
        });
        menus.registerMenuAction(VitruviusMenus.VITRUVIUS, {
            commandId: VitruviusDeleteProject.id,
            label: VitruviusDeleteProject.label
        });
        menus.registerMenuAction(VitruviusMenus.VITRUVIUS, {
            commandId: VitruviusChangeProject.id,
            label: VitruviusChangeProject.label
        });
    }
}