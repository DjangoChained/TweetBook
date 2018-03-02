/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import beans.Activity;
import beans.DislikeActivity;
import java.util.ArrayList;

/**
 *
 * @author pierant
 */
public interface DislikeActivityDao {
    DislikeActivity create(DislikeActivity post) throws DAOException;
    
    ArrayList<DislikeActivity> getAll() throws DAOException;
    DislikeActivity get(int id) throws DAOException;
    ArrayList<DislikeActivity> getByHuman(int id_human) throws DAOException;
    
    void delete(int id) throws DAOException;
}
