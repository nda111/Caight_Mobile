package com.example.caight;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLContext;

public final class WebSocketConnection
{
    private final WebSocketConnection This = this;

    public class Message
    {
        private Object message = null;
        private Boolean isBinary = null;

        public Message(byte[] binary)
        {
            if (binary != null)
            {
                this.message = binary;
                isBinary = true;
            }
        }

        public Message(String text)
        {
            if (text != null)
            {
                this.message = text;
                isBinary = false;
            }
        }

        public String getText()
        {
            return (String)message;
        }

        public byte[] getBinary()
        {
            return (byte[])message;
        }

        public boolean isBinaryMessage()
        {
            return isBinary != null && isBinary;
        }

        public boolean isTextMessage()
        {
            return isBinary != null && !isBinary;
        }

        public boolean isNull()
        {
            return isBinary == null;
        }

        @Override
        public String toString()
        {
            if (isTextMessage())
            {
                return getText();
            }
            else if (isBinaryMessage())
            {
                StringBuilder builder = new StringBuilder("[ ");
                for (byte b : getBinary())
                {
                    builder.append(b);
                    builder.append(' ');
                }
                builder.append(']');

                return builder.toString();
            }
            else
            {
                return "";
            }
        }
    }

    public interface RequestAdapter
    {
        void onRequest(WebSocketConnection conn);
        void onResponse(WebSocketConnection conn, Message message);
        void onClosed();
    }

    private ExecutorService thread = null;
    private WebSocket socket = null;
    private Queue<Message> messageQueue = new LinkedList<Message>();
    private boolean opened = false;

    private WebSocketAdapter customAdapter = null;
    private RequestAdapter requestAdapter = null;

    public WebSocketConnection(WebSocket ws)
    {
        if (ws == null)
        {
            throw new NullPointerException("ws == null");
        }

        socket = ws;
        setAdapter();
    }

    public WebSocketConnection(URI uri) throws NoSuchAlgorithmException, IOException, KeyManagementException
    {
        if (uri == null)
        {
            throw new NullPointerException("uri == null");
        }

        WebSocketFactory factory = new WebSocketFactory().setConnectionTimeout(5000);
        SSLContext context = null;
        context = SSLContext.getInstance("TLS");
        context.init(null, null, null);
        factory.setSSLContext(context);
        socket = factory.createSocket(uri);
        setAdapter();
    }

    public WebSocketConnection(String hostUrl) throws URISyntaxException, NoSuchAlgorithmException, IOException, KeyManagementException
    {
        this(hostUrl != null
                ? new URI(hostUrl)
                : null);
    }

    private void setAdapter()
    {
        socket.addListener(new WebSocketAdapter()
        {
            @Override
            public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception
            {
                opened = true;

                if (requestAdapter != null)
                {
                    requestAdapter.onRequest(This);
                }
                if (customAdapter != null)
                {
                    customAdapter.onConnected(websocket, headers);
                }
            }

            @Override
            public void onTextMessage(WebSocket websocket, String text) throws Exception
            {
                Message msg = new Message(text);
                messageQueue.add(msg);

                if (requestAdapter != null)
                {
                    requestAdapter.onResponse(This, msg);
                }
                if (customAdapter != null)
                {
                    customAdapter.onTextMessage(websocket, text);
                }
            }

            @Override
            public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception
            {
                Message msg = new Message(binary);
                messageQueue.add(msg);

                if (requestAdapter != null)
                {
                    requestAdapter.onResponse(This, msg);
                }
                if (customAdapter != null)
                {
                    customAdapter.onBinaryMessage(websocket, binary);
                }
            }

            @Override
            public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception
            {
                opened = false;
                thread.shutdown();
                thread = null;

                if (requestAdapter != null)
                {
                    requestAdapter.onClosed();
                }
                if (customAdapter != null)
                {
                    customAdapter.onCloseFrame(websocket, frame);
                }
            }
        });
    }

    public WebSocketConnection setAdapter(WebSocketAdapter adapter)
    {
        customAdapter = adapter;
        return this;
    }

    public WebSocketConnection setRequestAdapter(RequestAdapter adapter)
    {
        requestAdapter = adapter;
        return this;
    }

    public WebSocketAdapter getAdapter()
    {
        return customAdapter;
    }

    public WebSocket getSocket()
    {
        return socket;
    }

    public WebSocketConnection connect()
    {
        thread = Executors.newSingleThreadExecutor();
        socket.connect(thread);

        return this;
    }

    public void close()
    {
        socket.sendClose();
    }

    public void send(String text, boolean fin)
    {
        socket.sendText(text, fin);
    }

    public void send(byte[] bytes, boolean fin)
    {
        socket.sendBinary(bytes, fin);
    }

    public boolean hasReceived()
    {
        return !messageQueue.isEmpty();
    }

    public Message getLastMessageOrNull()
    {
        return hasReceived()
                ? messageQueue.remove()
                : null;
    }

    public Message waitMessage(long timeoutInMillis)
    {
        long start = Calendar.getInstance().getTimeInMillis();

        do
        {
            if (hasReceived())
            {
                return messageQueue.remove();
            }
        } while (Calendar.getInstance().getTimeInMillis() - start <= timeoutInMillis);

        return null;
    }

    public boolean isOpened()
    {
        return opened;
    }
}