/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import beans.Post;
import beans.LinkPost;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author pierant
 */
public interface LinkPostDao {
    LinkPost create(LinkPost post) throws DAOException;
    
    ArrayList<LinkPost> getAll() throws DAOException;
    LinkPost get(int id) throws DAOException;
    ArrayList<LinkPost> getByHuman(int id_human) throws DAOException;
    Map<Integer, LinkPost> getHashByHuman(int id_human) throws DAOException;
    
    void update(LinkPost post) throws DAOException;
    
    void delete(int id) throws DAOException;
}
