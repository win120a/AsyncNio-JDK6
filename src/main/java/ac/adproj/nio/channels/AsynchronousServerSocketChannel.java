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

package ac.adproj.nio.channels;

import ac.adproj.nio.model.AsyncTask;
import ac.adproj.nio.model.CompletionHandler;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.*;

/**
 * Provides a functional emulating (a subset of) implementation of AsynchronousServerSocketChannel in Java 7.
 *
 * @author Andy Cheung
 */
public class AsynchronousServerSocketChannel implements Closeable {
    private static int instanceNum = -1;
    private int threadNumber = 0;
    private BlockingQueue<Runnable> bq = new LinkedBlockingQueue<Runnable>(16);
    private ServerSocketChannel socket;
    private boolean bound = false;

    ThreadFactory threadFactory = new ThreadFactory() {
        /**
         * Constructs a new {@code Thread}.  Implementations may also initialize
         * priority, name, daemon status, {@code ThreadGroup}, etc.
         *
         * @param r a runnable to be executed by new thread instance
         * @return constructed thread, or {@code null} if the request to
         * create a thread is rejected
         */
        @Override
        public Thread newThread(Runnable r) {
            threadNumber++;
            return new Thread(r, "PoolThread - SrvSocketChannel - " + instanceNum + " - "+ threadNumber);
        }
    };

    private ExecutorService threadPool = new ThreadPoolExecutor(4, 16, 3000, TimeUnit.MILLISECONDS, bq, threadFactory);

    public AsynchronousServerSocketChannel() throws IOException {
        instanceNum++;
        socket = ServerSocketChannel.open();
    }

    public static AsynchronousServerSocketChannel open() throws IOException {
        return new AsynchronousServerSocketChannel();
    }

    public void bind(SocketAddress addr) throws IOException {
        socket.socket().bind(addr);
        bound = true;
    }

    public <A> void accept(A attachment,
                                    CompletionHandler<AsynchronousSocketChannel,? super A> handler) {
        if (!bound) {
            throw new IllegalArgumentException("This Server Socket doesn't bind on an address! ");
        }

        threadPool.execute(new AsyncTask<AsynchronousSocketChannel, A>(attachment, handler) {
            @Override
            public AsynchronousSocketChannel execute(A attachment) throws Exception {
                SocketChannel ch = socket.accept();
                return new AsynchronousSocketChannel(ch);
            }
        });
    }

    @Override
    public void close() throws IOException {
        threadPool.shutdownNow();
        socket.close();
    }
}
