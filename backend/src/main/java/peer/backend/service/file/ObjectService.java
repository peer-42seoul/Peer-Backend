package peer.backend.service.file;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.tika.Tika;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;
import peer.backend.exception.IllegalArgumentException;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Slf4j
@Service
@Getter
public class ObjectService {
    private Tika tika;
    private String tokenId;
    private String storageUrl;
    private RestTemplate restTemplate;

    public ObjectService(String storageUrl, String tokenId) {
        this.storageUrl = storageUrl;
        this.tokenId = tokenId;
        this.restTemplate = new RestTemplate();
        this.tika = new Tika();
    }

    private String getUrl(@NotNull String containerName, @NotNull String objectName) {
        return this.getStorageUrl() + "/" + containerName + "/" + objectName;
    }

    private String mimeTypeCheck(byte[] bytes, String type) {
        String mimeType = tika.detect(bytes);

        if (!mimeType.startsWith(type)) {
            throw new IllegalArgumentException(type + " 타입이 아닙니다.");
        }
        return mimeType;
    }


    public String uploadObject(String containerName, String objectName, final InputStream inputStream, final String base64String, String typeCheck) {
        String url = this.getUrl(containerName, objectName);
        if (base64String == null) {
            return null;
        }
        byte[] fileData = Base64.getDecoder().decode(base64String);

        String contentType = mimeTypeCheck(fileData, typeCheck);

        // InputStream을 요청 본문에 추가할 수 있도록 RequestCallback 오버라이드
        final RequestCallback requestCallback = new RequestCallback() {
            public void doWithRequest(final ClientHttpRequest request) throws IOException {
                request.getHeaders().add("X-Auth-Token", tokenId);
                IOUtils.copy(inputStream, request.getBody());
            }
        };

        // 오버라이드한 RequestCallback을 사용할 수 있도록 설정
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setBufferRequestBody(false);
        RestTemplate restTemplate = new RestTemplate(requestFactory);

        HttpMessageConverterExtractor<String> responseExtractor
                = new HttpMessageConverterExtractor<String>(String.class, restTemplate.getMessageConverters());

        // API 호출
        restTemplate.execute(url, HttpMethod.PUT, requestCallback, responseExtractor);

        return contentType;
    }

}
