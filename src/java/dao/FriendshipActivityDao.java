/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import beans.FriendshipActivity;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author pierant
 */
public interface FriendshipActivityDao {
    FriendshipActivity create(FriendshipActivity post) throws DAOException;
    
    ArrayList<FriendshipActivity> getAll() throws DAOException;
    FriendshipActivity get(int id) throws DAOException;
    ArrayList<Integer> getFriends(int id_human) throws DAOException;
    ArrayList<FriendshipActivity> getByHuman(int id_human) throws DAOException;
    Map<Integer, FriendshipActivity> getHashByHuman(int id_human) throws DAOException;
    int getByFriends(int id_human, int id_friend) throws DAOException;
    
    void delete(int id) throws DAOException;
}
