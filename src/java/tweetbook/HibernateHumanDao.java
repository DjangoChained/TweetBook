/*
 * Copyright (C) 2018 rouchete et waxinp
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package tweetbook;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

/**
 * Décrit un objet d'accès aux données pour les utilisateurs via Hibernate.
 * @author rouchete et waxinp
 */
public class HibernateHumanDao extends HibernateDaoSupport implements HumanDao {

    @Override
    public Human getByUsername(String username) {
        return (Human) this.getHibernateTemplate().find("from Human where username='?'", username).get(0);
    }

    @Override
    public boolean checkCredentials(String username, String password) {
        try {
            return this.getByUsername(username).checkPassword(password);
        } catch (DataAccessException d) {
            return false;
        }
    }

    @Override
    public Human getById(Long id) {
        return this.getHibernateTemplate().get(Human.class, id);
    }

    @Override
    public List<Human> getAll() {
        return this.getHibernateTemplate().find("from Human").stream().map(h -> (Human) h).collect(Collectors.toList());
    }

    @Override
    public void store(Human object) {
        this.getHibernateTemplate().saveOrUpdate(object);
    }

    @Override
    public void delete(Long id) {
        this.delete(this.getById(id));
    }

    @Override
    public void delete(Human object) {
        this.getHibernateTemplate().delete(object);
    }
    
}
