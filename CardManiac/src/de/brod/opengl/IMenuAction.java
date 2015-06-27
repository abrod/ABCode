package de.brod.opengl;

import java.util.List;

/**
 * Created by Andreas_2 on 27.06.2015.
 */
public interface IMenuAction extends IAction {

    List<IMenuAction> getSubMenu();
}
