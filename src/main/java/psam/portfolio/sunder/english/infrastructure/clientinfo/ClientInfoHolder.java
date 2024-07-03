package psam.portfolio.sunder.english.infrastructure.clientinfo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClientInfoHolder {

    private static final ThreadLocal<ClientInfo> local = new ThreadLocal<>();

    public static void syncClientInfo(String username, String remoteIp) {
        local.set(new ClientInfo(username, remoteIp));
    }

    public static String getUsername() {
        if (local.get() == null) {
            return null;
        }
        return local.get().username();
    }

    public static String getRemoteIp() {
        if (local.get() == null) {
            return null;
        }
        return local.get().remoteIp();
    }

    public static void releaseClientInfo() {
        local.remove();
    }
}
