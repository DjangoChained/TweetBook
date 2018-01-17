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

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Décrit une publication générique d'un utilisateur.
 * @author rouchete et waxinp
 */
@MappedSuperclass
public class Post extends Activity implements Serializable {

    private static final long serialVersionUID = 1L;
    private String content;
    
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Post && super.equals(object) && this.content.equals(((Post) object).content);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.content);
        return hash;
    }

    @Override
    public String toString() {
        return this.content;
    }
    
}
