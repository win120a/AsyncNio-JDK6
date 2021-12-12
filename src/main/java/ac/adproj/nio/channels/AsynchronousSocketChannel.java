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
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.*;

/**
 * Provides a functional emulating (a subset of) implementation of AsynchronousSocketChannel in Java 7.
 *
 * @author Andy Cheung
 */
public class AsynchronousSocketChannel implements Closeable {
    private final SocketChannel socket;
    private final BlockingQueue<Runnable> bq = new LinkedBlockingQueue<Runnable>(16);
    private int threadNumber = 0;
    ThreadFactory threadFactory = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            threadNumber++;
            String name = "PoolThread - AsyncSocketChannel - # " + threadNumber;
            return new Thread(r, name);
        }
    };
    private final ExecutorService threadPool = new ThreadPoolExecutor(4, 16, 3000, TimeUnit.MILLISECONDS, bq, threadFactory);

    public AsynchronousSocketChannel() throws IOException {
        socket = SocketChannel.open();
    }

    public AsynchronousSocketChannel(SocketChannel s) {
        socket = s;
    }

    public static AsynchronousSocketChannel open() throws IOException {
        return new AsynchronousSocketChannel();
    }

    public AsynchronousSocketChannel bind(SocketAddress local)
            throws IOException {
        socket.socket().bind(local);
        return this;
    }

    public AsynchronousSocketChannel shutdownInput() throws IOException {
        socket.socket().shutdownInput();
        return this;
    }

    public AsynchronousSocketChannel shutdownOutput() throws IOException {
        socket.socket().shutdownOutput();
        return this;
    }

    public SocketAddress getRemoteAddress() throws IOException {
        return socket.socket().getRemoteSocketAddress();
    }

    public <A> void connect(final SocketAddress remote,
                            A attachment,
                            CompletionHandler<Void, ? super A> handler) {
        threadPool.execute(new AsyncTask<Void, A>(attachment, handler) {

            @Override
            public Void execute(A attachment) throws Exception {
                socket.connect(remote);
                return null;
            }
        });
    }

    public Future<Void> connect(final SocketAddress remote) {
        AsyncTask<Void, Void> task = new AsyncTask<Void, Void>(null, null) {

            @Override
            public Void execute(Void attachment) throws Exception {
                socket.connect(remote);
                return null;
            }
        };

        threadPool.execute(task);

        return task;
    }

    public <A> void read(final ByteBuffer dst,
                         A attachment,
                         CompletionHandler<Integer, ? super A> handler) {

        threadPool.execute(new AsyncTask<Integer, A>(attachment, handler) {

            @Override
            public Integer execute(A attachment) throws Exception {
                return doRead(socket, dst);
            }
        });
    }

    public Future<Integer> read(final ByteBuffer dst) {
        AsyncTask<Integer, Void> task = new AsyncTask<Integer, Void>(null, null) {

            @Override
            public Integer execute(Void attachment) throws Exception {
                return doRead(socket, dst);
            }
        };

        threadPool.execute(task);

        return task;
    }

    private Integer doRead(SocketChannel socket, ByteBuffer dst) throws Exception {
        return socket.read(dst);
    }

    public Future<Integer> write(final ByteBuffer src) {
        AsyncTask<Integer, Void> task = new AsyncTask<Integer, Void>(null, null) {

            @Override
            public Integer execute(Void attachment) throws Exception {
                return doWrite(socket, src);
            }
        };

        threadPool.execute(task);

        return task;
    }

    public final <A> void write(final ByteBuffer src,
                                A attachment,
                                CompletionHandler<Integer, ? super A> handler) {
        threadPool.execute(new AsyncTask<Integer, A>(attachment, handler) {

            @Override
            public Integer execute(A attachment) throws Exception {
                return doWrite(socket, src);
            }
        });
    }

    private Integer doWrite(SocketChannel socket, ByteBuffer src) throws Exception {
        return socket.write(src);
    }

    public SocketAddress getLocalAddress() {
        return socket.socket().getLocalSocketAddress();
    }

    public boolean isOpen() {
        return socket.isOpen();
    }

    @Override
    public void close() throws IOException {
        socket.close();
        threadPool.shutdownNow();
    }
}
