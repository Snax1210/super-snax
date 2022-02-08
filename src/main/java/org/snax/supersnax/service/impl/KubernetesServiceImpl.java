package org.snax.supersnax.service.impl;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.KubeConfig;
import org.snax.supersnax.config.KubernetesConfig;
import org.snax.supersnax.service.KubernetesService;
import org.springframework.stereotype.Service;

/**
 * @author maoth
 * @date 2022/2/8 14:43
 * @description
 */
@Service
public class KubernetesServiceImpl implements KubernetesService {
    @Override
    public void getAllPods() {
        ApiClient apiClient = KubernetesConfig.getConnection();
        CoreV1Api api = new CoreV1Api();

        try {
            V1PodList list = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1Pod item : list.getItems()) {
                System.out.println(item);
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }

    }
}
