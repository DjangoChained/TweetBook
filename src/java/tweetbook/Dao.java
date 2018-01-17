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
import org.springframework.transaction.annotation.Transactional;

/**
 * Décrit un objet d'accès aux données générique.
 * @author rouchete et waxinp
 * @param <T> Type d'objet accédé.
 */
public interface Dao<T> {
    @Transactional(readOnly = true)
    public T getById(Long id);
    @Transactional(readOnly = true)
    public List<T> getAll();
    @Transactional
    public void store(T object);
    @Transactional
    public void delete(Long id);
    @Transactional
    public void delete(T object);
}
