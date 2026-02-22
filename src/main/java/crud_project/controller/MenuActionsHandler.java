/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crud_project.ui.controller;

/**
 * @todo Interface que deben implementar todos los controladores de vistas que incorporen
 * el menú reutilizable para poder realizar las operaciones CRUD desde el menú.
 * @author Javier Martín Uría (JMU)
 */
public interface MenuActionsHandler {
 
    public void onCreate();
    public void onRefresh();
    public void onUpdate();
    public void onDelete();
}
