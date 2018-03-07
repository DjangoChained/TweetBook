/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import beans.PhotoPost;
import beans.Post;
import static dao.DAOImpl.fermeturesSilencieuses;
import static dao.DAOImpl.initialisationRequetePreparee;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.ArrayList;

/**
 *
 * @author pierant
 */
public class PhotoPostDaoImpl extends BasicDaoImpl implements PhotoPostDao {
    private final DAOFactory daoFactory;
    
    public PhotoPostDaoImpl(DAOFactory daoFactory){
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
    
    private static final String SQL_INSERT = "INSERT INTO photopost (id, photopath) VALUES (?, ?)";
    @Override
    public PhotoPost create(PhotoPost post) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet valeursAutoGenerees = null;

        try {
            int id_activity = super.createActivity(daoFactory, post.getDate(), post.getId_human());
            super.createPost(daoFactory, id_activity, post.getContent());
            /* Récupération d'une connexion depuis la Factory */
            connexion = daoFactory.getConnection();
            preparedStatement = initialisationRequetePreparee( connexion, SQL_INSERT, true, post.getId(), post.getPhotoPath());
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

    private static final String SQL_SELECT_ALL = "SELECT a.id as id, date, id_human, content, photopath FROM activity a INNER JOIN post p ON a.id = p.id INNER JOIN photopost pp ON pp.id = p.id";
    @Override
    public ArrayList<PhotoPost> getAll() throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        PhotoPost post;
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

    private static final String SQL_SELECT_BY_ID = "SELECT a.id as id, date, id_human, content, photopath FROM activity a INNER JOIN post p ON a.id = p.id INNER JOIN photopost pp ON pp.id = p.id WHERE a.id = ?";
    @Override
    public PhotoPost get(int id) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        PhotoPost post = null;

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisationRequetePreparee( connexion, SQL_SELECT_BY_ID, false, id );
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
    
    private static final String SQL_SELECT_BY_ID_HUMAN = "SELECT a.id as id, date, id_human, content, photopath FROM activity a INNER JOIN post p ON a.id = p.id INNER JOIN photopost pp ON pp.id = p.id WHERE id_human = ?";
    @Override
    public ArrayList<PhotoPost> getByHuman(int id_human) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        PhotoPost post = null;
        ArrayList<PhotoPost> posts = new ArrayList<>();

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisationRequetePreparee( connexion, SQL_SELECT_BY_ID_HUMAN, false, id_human );
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

    private static final String SQL_UPDATE = "UPDATE photopost SET date = ?, id_human = ?, content = ?, photopath = ? WHERE id = ?";
    @Override
    public void update(PhotoPost post) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisationRequetePreparee( connexion, SQL_UPDATE, false, post.getDate(), post.getId_human(), post.getContent(), post.getPhotoPath(), post.getId());
            preparedStatement.executeUpdate();
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( resultSet, preparedStatement, connexion );
        }
    }

    private static final String SQL_DELETE= "DELETE FROM photopost WHERE id = ?";
    @Override
    public void delete(int id) throws DAOException {
        super.delete(daoFactory, id, SQL_DELETE);
    }
}
