/*
    Copyright (C) 2011-2020 Andy Cheung

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License Version 2 only, 
    as published by the Free Software Foundation.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

    The copyright holder permits this library as subject to the "Classpath"
    exception as provided below:

        Linking this library statically or dynamically with other modules is
        making a combined work based on this library.  Thus, the terms and
        conditions of the GNU General Public License cover the whole
        combination.

        As a special exception, the copyright holders of this library give you
        permission to link this library with independent modules to produce an
        executable, regardless of the license terms of these independent
        modules, and to copy and distribute the resulting executable under
        terms of your choice, provided that you also meet, for each linked
        independent module, the terms and conditions of the license of that
        module.  An independent module is a module which is not derived from
        or based on this library.  If you modify this library, you may extend
        this exception to your version of the library, but you are not
        obligated to do so.  If you do not wish to do so, delete this
        exception statement from your version.
*/

package ac.adproj.nio.model.function;

/**
 * Provides a "functional interface" like Consumer in JDK 8.
 *
 * @param <T> The type of argument.
 * @author Andy Cheung
 * @since 2020.5.5
 */
public interface Consumer<T> {
    /**
     * Take in the argument and do something.
     *
     * @param arg The argument.
     */
    void accept(T arg);
}
