/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import beans.FriendshipActivity;
import java.util.ArrayList;

/**
 *
 * @author pierant
 */
public interface FriendshipActivityDao {
    FriendshipActivity create(FriendshipActivity post) throws DAOException;
    
    ArrayList<FriendshipActivity> getAll() throws DAOException;
    FriendshipActivity get(int id) throws DAOException;
    ArrayList<FriendshipActivity> getByHuman(int id_human) throws DAOException;
    
    void delete(int id) throws DAOException;
}
