/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import beans.PhotoPost;
import beans.Post;
import java.util.ArrayList;

/**
 *
 * @author pierant
 */
public interface PhotoPostDao {
    PhotoPost create(PhotoPost post) throws DAOException;
    
    ArrayList<PhotoPost> getAll() throws DAOException;
    PhotoPost get(int id) throws DAOException;
    ArrayList<PhotoPost> getByHuman(int id_human) throws DAOException;
    
    void update(PhotoPost post) throws DAOException;
    
    void delete(int id) throws DAOException;
}
