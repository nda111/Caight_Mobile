package com.example.caight;

import android.widget.Toast;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketError;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

public final class WebSocketConnection
{
    private WebSocket socket = null;

    public WebSocketConnection(WebSocket ws)
    {
        if (ws == null)
        {
            throw new NullPointerException("ws == null");
        }

        socket = ws;
    }

    public WebSocketConnection(URI uri) throws NoSuchAlgorithmException, IOException, KeyManagementException
    {
        WebSocketFactory factory = new WebSocketFactory().setConnectionTimeout(5000);
        SSLContext context = null;
        context = SSLContext.getInstance("TLS");
        context.init(null, null, null);
        factory.setSSLContext(context);
        socket = factory.createSocket(uri);
    }

    public WebSocketConnection(String hostUrl) throws URISyntaxException, NoSuchAlgorithmException, IOException, KeyManagementException
    {
        this(new URI(hostUrl));
    }

    private void setAdapter()
    {
        socket.addListener(new WebSocketAdapter()
        {
            @Override
            public void onError(WebSocket websocket, WebSocketException cause)
            {

            }

            @Override
            public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception
            {

            }

            @Override
            public void onTextMessage(WebSocket websocket, String text) throws Exception
            {

            }

            @Override
            public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception
            {

            }
        });
    }
}
