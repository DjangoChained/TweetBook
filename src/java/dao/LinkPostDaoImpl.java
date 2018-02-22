/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import beans.Post;
import beans.LinkPost;
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
public class LinkPostDaoImpl extends BasicDaoImpl implements LinkPostDao {
    private DAOFactory daoFactory;
    
    public LinkPostDaoImpl(DAOFactory daoFactory){
        this.daoFactory = daoFactory;
    }
    
    private static LinkPost map( ResultSet resultSet ) throws SQLException {
        LinkPost post = new LinkPost();
        post.setId(resultSet.getInt( "id"));
        post.setDate(resultSet.getTimestamp("date").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        post.setId_human(resultSet.getInt("id_human"));
        post.setContent(resultSet.getString("content"));
        post.setUrl(resultSet.getString("url"));
        post.setTitle(resultSet.getString("title"));
        
        return post;
    }
    
    private static final String SQL_INSERT = "INSERT INTO linkpost (date, id_human, content, url, title) VALUES (?, ?, ?, ?, ?)";
    @Override
    public LinkPost create(LinkPost post) throws IllegalArgumentException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet valeursAutoGenerees = null;

        try {
            /* Récupération d'une connexion depuis la Factory */
            connexion = daoFactory.getConnection();
            preparedStatement = initialisationRequetePreparee( connexion, SQL_INSERT, true, Timestamp.valueOf(post.getDate()), post.getId_human(), post.getContent(), post.getUrl(), post.getTitle());
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

    private static final String SQL_SELECT_ALL = "SELECT id, date, id_human, content, url, title FROM linkpost";
    @Override
    public ArrayList<Post> getAll() throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        LinkPost post = null;
        ArrayList<Post> posts = new ArrayList<>();

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

    private static final String SQL_SELECT_BY_ID = "SELECT id, date, id_human, content, url, title FROM linkpost WHERE id = ?";
    @Override
    public LinkPost get(int id) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        LinkPost post = null;

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
    
    private static final String SQL_SELECT_BY_ID_HUMAN = "SELECT id, date, id_human, content, url, title FROM linkpost WHERE id_human = ?";
    @Override
    public ArrayList<Post> getByHuman(int id_human) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        LinkPost post = null;
        ArrayList<Post> posts = new ArrayList<>();

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisationRequetePreparee( connexion, SQL_SELECT_BY_ID_HUMAN, false, id_human );
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                if ( resultSet.next() ) {
                    post = map( resultSet );
                    posts.add(post);
                }
            }
            
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( resultSet, preparedStatement, connexion );
        }

        return posts;
    }

    private static final String SQL_UPDATE = "UPDATE linkpost SET date = ?, id_human = ?, content = ?, url = ?, title = ? WHERE id = ?";
    @Override
    public void update(LinkPost post) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisationRequetePreparee( connexion, SQL_UPDATE, false, post.getDate(), post.getId_human(), post.getContent(), post.getUrl(), post.getTitle(), post.getId());
            preparedStatement.executeUpdate();
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( resultSet, preparedStatement, connexion );
        }
    }

    private static final String SQL_DELETE= "DELETE FROM linkpost WHERE id = ?";
    @Override
    public void delete(int id) throws DAOException {
        super.delete(daoFactory, id, SQL_DELETE);
    }
}
