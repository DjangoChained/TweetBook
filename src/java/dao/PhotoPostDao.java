/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import beans.PhotoPost;
import beans.Post;
import static dao.DAO.fermeturesSilencieuses;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.ArrayList;
import static dao.DAO.initialisePreparedStatement;

/**
 *
 *
 */
public class PhotoPostDao extends BasicDao{
    private DAOFactory daoFactory;
    
    /**
     *
     * @param daoFactory
     */
    public PhotoPostDao(DAOFactory daoFactory){
        this.daoFactory = daoFactory;
    }
    
    private static PhotoPost map( ResultSet resultSet ) throws SQLException {
        PhotoPost post = new PhotoPost();
        post.setId(resultSet.getInt( "id"));
        post.setDate(resultSet.getTimestamp("date").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        post.setId_human(resultSet.getInt("id_human"));
        post.setContent(resultSet.getString("content"));
        post.setPhotoPath(resultSet.getString("photopath"));
        
        return post;
    }
    
    private static final String SQL_INSERT = "INSERT INTO photopost (date, id_human, content, photopath) VALUES (?, ?, ?, ?)";
    
    /**
     *
     * @param post
     * @return
     * @throws DAOException
     */
    public PhotoPost create(PhotoPost post) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet valeursAutoGenerees = null;

        try {
            /* Récupération d'une connexion depuis la Factory */
            connexion = daoFactory.getConnection();
            preparedStatement = initialisePreparedStatement( connexion, SQL_INSERT, true, Timestamp.valueOf(post.getDate()), post.getId_human(), post.getContent(), post.getPhotoPath());
            int status = preparedStatement.executeUpdate();
            if ( status == 0 ) {
                throw new DAOException( "Échec de la création du post, aucune ligne ajoutée dans la table." );
            }
            valeursAutoGenerees = preparedStatement.getGeneratedKeys();
            if ( valeursAutoGenerees.next() ) {
                post.setId( valeursAutoGenerees.getInt( 1 ) );
            } else {
                throw new DAOException( "Échec de la création du post en base, aucun ID auto-généré retourné." );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( valeursAutoGenerees, preparedStatement, connexion );
        }
        return post;
    }

    private static final String SQL_SELECT_ALL = "SELECT id, date, id_human, content, photopath FROM photopost ph LEFT JOIN post p ON ph.id = p.id LEFT JOIN activity a ON ph.id = a.id";
    
    /**
     *
     * @return
     * @throws DAOException
     */
    public ArrayList<PhotoPost> getAll() throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        PhotoPost post = null;
        ArrayList<PhotoPost> posts = new ArrayList<>();

        try {
            connexion = daoFactory.getConnection();
            Statement statement = connexion.createStatement();
            result = statement.executeQuery(SQL_SELECT_ALL);
            while(result.next()) {
                post = map(result);
                posts.add(post);
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( result, preparedStatement, connexion );
        }

        return posts;
    }

    private static final String SQL_SELECT_BY_ID = "SELECT id, date, id_human, content, photopath FROM photopost ph LEFT JOIN post p ON ph.id = p.id LEFT JOIN activity a ON ph.id = a.id WHERE id = ?";
    
    /**
     *
     * @param id
     * @return
     * @throws DAOException
     */
    public PhotoPost get(int id) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        PhotoPost post = null;

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisePreparedStatement( connexion, SQL_SELECT_BY_ID, false, id );
            resultSet = preparedStatement.executeQuery();
            if ( resultSet.next() ) {
                post = map( resultSet );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( resultSet, preparedStatement, connexion );
        }

        return post;
    }
    
    private static final String SQL_SELECT_BY_ID_HUMAN = "SELECT id, date, id_human, content, photopath FROM photopost ph LEFT JOIN post p ON ph.id = p.id LEFT JOIN activity a ON ph.id = a.id WHERE id_human = ?";
    
    /**
     *
     * @param id_human
     * @return
     * @throws DAOException
     */
    public ArrayList<PhotoPost> getByHuman(int id_human) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        PhotoPost post = null;
        ArrayList<PhotoPost> posts = new ArrayList<>();

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisePreparedStatement( connexion, SQL_SELECT_BY_ID_HUMAN, false, id_human );
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                post = map( resultSet );
                posts.add(post);
            }
            
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( resultSet, preparedStatement, connexion );
        }

        return posts;
    }

    private static final String SQL_UPDATE = "UPDATE photopost SET photopath = ? WHERE id = ?";
    
    /**
     *
     * @param post
     * @throws DAOException
     */
    public void update(PhotoPost post) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            super.updateActivity(daoFactory, post.getDate(), post.getId_human(), post.getId());
            super.updatePost(daoFactory, post.getId(), post.getContent());
            connexion = daoFactory.getConnection();
            preparedStatement = initialisePreparedStatement( connexion, SQL_UPDATE, false, post.getPhotoPath(), post.getId());
            preparedStatement.executeUpdate();
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( resultSet, preparedStatement, connexion );
        }
    }

    private static final String SQL_DELETE= "DELETE FROM photopost WHERE id = ?";
    
    /**
     *
     * @param id
     * @throws DAOException
     */
    public void delete(int id) throws DAOException {
        super.delete(daoFactory, id, "DELETE FROM activity WHERE id = ?");
        super.delete(daoFactory, id, "DELETE FROM post WHERE id = ");
        super.delete(daoFactory, id, SQL_DELETE);
    }
}
