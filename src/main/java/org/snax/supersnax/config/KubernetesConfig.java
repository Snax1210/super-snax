package org.snax.supersnax.config;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.credentials.AccessTokenAuthentication;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author maoth
 * @date 2022/2/8 14:29
 * @description
 */
@Component
@Data
public class KubernetesConfig {

    private static String url = "https://192.168.12.61:6443";

    private static String token =
        "eyJhbGciOiJSUzI1NiIsImtpZCI6IkNOR0N5c3pzNUczS0xYa3FGS2RTcEN0ZXdtRGNGTEpQWTlndmMzeWNVeWcifQ"
            +
            ".eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJrdWJlcm5ldGVzLWRhc2hib2FyZCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJhZG1pbi11c2VyLXRva2VuLXh3enFtIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6ImFkbWluLXVzZXIiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiJmOTYzYjU5ZC1mMWUyLTQwODItOTNhNC05NTNjN2FjMDAwNzYiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6a3ViZXJuZXRlcy1kYXNoYm9hcmQ6YWRtaW4tdXNlciJ9.d-pcy6EFR2DYXptrfTx0PRQgDrozAJskQeZwoAgbqcDuxb8DdhJIiJk5hhdlVafsgyF0XBeiFoLTIK7jXd2PzACqvwa2WEbl5nE7Iw6C9O_OE1GZU_9AYUHYsbcz-Rxe0sx7WZu8XoADszrUrVtxfuh2XKbPwGdOY9nOjzE1Xiya4VTx9XUn7g1DeL86YJi46B6F6ByltqshHa1QFPmClGvJnRh2b6DND6nJA1OYYrSYxdqC8yrDkhpPxH8UNAzrnyPmlNjRHGBaQVj-dVgmgp_cDGY5FVKKGOt_BTwJHLar8Zu7INDE0lLwGvtjAXBI26tXIb6F114e5pIUrUUclA";

    public static ApiClient getConnection() {
        ApiClient client = new ClientBuilder().setBasePath(url)
            .setVerifyingSsl(false)
            .setAuthentication(new AccessTokenAuthentication(token))
            .build();
        Configuration.setDefaultApiClient(client);
        return client;
    }
}
