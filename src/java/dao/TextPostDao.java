/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import beans.Post;
import beans.TextPost;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author pierant
 */
public interface TextPostDao {
    void create(TextPost post) throws DAOException;
    
    ArrayList<TextPost> getAll() throws DAOException;
    TextPost get(int id) throws DAOException;
    ArrayList<TextPost> getByHuman(int id_human) throws DAOException;
    Map<Integer, TextPost> getHashByHuman(int id_human) throws DAOException;
    
    void update(TextPost post) throws DAOException;
    
    void delete(int id) throws DAOException;
}
