package websocket;

import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;

public class WebsocketHandler  implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    @Override
    public void handleClose(@NotNull WsCloseContext wsCtx) throws Exception {

    }

    @Override
    public void handleConnect(@NotNull WsConnectContext wsCtx) throws Exception {
        wsCtx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext wsCtx) throws Exception {

    }
}
