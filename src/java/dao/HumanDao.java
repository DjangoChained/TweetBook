/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import beans.Human;
import java.util.ArrayList;

public interface HumanDao {
    void create(Human human) throws DAOException;
    
    ArrayList<Human> getAll() throws DAOException;
    Human get(int id) throws DAOException;
    Human get(String email) throws DAOException;
    
    void update(Human human) throws DAOException;
    
    void delete(int id) throws DAOException;
}
