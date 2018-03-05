/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import beans.Activity;
import beans.LikeActivity;
import java.util.ArrayList;

/**
 *
 * @author pierant
 */
public interface LikeActivityDao {
    LikeActivity create(LikeActivity post) throws DAOException;
    
    ArrayList<LikeActivity> getAll() throws DAOException;
    LikeActivity get(int id) throws DAOException;
    LikeActivity get(int id_human, int id_post) throws DAOException;
    ArrayList<LikeActivity> getByHuman(int id_human) throws DAOException;
    
    void delete(int id) throws DAOException;
}
