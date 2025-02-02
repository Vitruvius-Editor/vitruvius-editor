import { injectable } from "@theia/core/shared/inversify";
import {
  MAIN_MENU_BAR,
  MenuContribution,
  MenuModelRegistry,
  MenuPath,
} from "@theia/core/lib/common";
import { HelpCommand } from "./menuCommands/helpCommand";
import { LoadProjectCommand } from "./menuCommands/loadProjectCommand";
import { ImportProjectCommand } from "./menuCommands/importProjectCommand";
import { RefreshProjectCommand } from "./menuCommands/refreshProjectCommand";
import { DeleteProjectCommand } from "./menuCommands/deleteProjectCommand";
import { EditProjectCommand } from "./menuCommands/editProjectCommand";

/**
 * Path to the Vitruvius menu.
 */
export namespace VitruviusMenu {
  export const VITRUVIUS: MenuPath = MAIN_MENU_BAR.concat("vitruvius");
}

/**
 * Parent class for all commands that are part of the Vitruvius menu.
 */
@injectable()
export class VitruviusMenuContribution implements MenuContribution {
  /**
   * Registers all commands that are part of the Vitruvius menu.
   * @param menus The registry to register the commands to.
   */
  registerMenus(menus: MenuModelRegistry): void {
    menus.registerSubmenu(VitruviusMenu.VITRUVIUS, "Vitruvius");

    menus.registerMenuAction(VitruviusMenu.VITRUVIUS, {
      commandId: HelpCommand.id,
      label: HelpCommand.label,
      order: '0',
    });
    menus.registerMenuAction(VitruviusMenu.VITRUVIUS, {
      commandId: LoadProjectCommand.id,
      label: LoadProjectCommand.label,
      order: '1',
    });
    menus.registerMenuAction(VitruviusMenu.VITRUVIUS, {
      commandId: ImportProjectCommand.id,
      label: ImportProjectCommand.label,
      order: '2',
    });
    menus.registerMenuAction(VitruviusMenu.VITRUVIUS, {
      commandId: RefreshProjectCommand.id,
      label: RefreshProjectCommand.label,
      order: '3',
    });
    menus.registerMenuAction(VitruviusMenu.VITRUVIUS, {
      commandId: DeleteProjectCommand.id,
      label: DeleteProjectCommand.label,
      order: '4',
    });
    menus.registerMenuAction(VitruviusMenu.VITRUVIUS, {
      commandId: EditProjectCommand.id,
      label: EditProjectCommand.label,
      order: '5',
    });
  }
}
