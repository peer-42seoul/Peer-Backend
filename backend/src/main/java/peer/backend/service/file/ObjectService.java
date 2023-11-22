package peer.backend.service.file;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;
import peer.backend.exception.IllegalArgumentException;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Getter
@RequiredArgsConstructor
@Service
public class ObjectService {
    @Data
    private static class TokenRequest {
        public TokenRequest(String tenantId, String username, String password) {
            this.auth.setTenantId(tenantId);
            this.auth.getPasswordCredentials().setUsername(username);
            this.auth.getPasswordCredentials().setPassword(password);
        }

        private Auth auth = new Auth();

        @Data
        public static class Auth {
            private String tenantId;
            private PasswordCredentials passwordCredentials = new PasswordCredentials();
        }

        @Data
        public static class PasswordCredentials {
            private String username;
            private String password;
        }
    }
    private String tokenId = null;
    private OffsetDateTime tokenExpireTime = null;
    @Value("${nhn.objectStorage.storageUrl}")
    private String storageUrl;
    @Value("${nhn.objectStorage.authUrl}")
    private String authUrl;
    @Value("${nhn.objectStorage.tenantId}")
    private String tenantId;
    @Value("${nhn.objectStorage.username}")
    private String username;
    @Value("${nhn.objectStorage.password}")
    private String password;
    @Value("${nhn.objectStorage.containerName}")
    private String containerName;
    private final Tika tika = new Tika();
    private final RestTemplate restTemplate;

    private void requestToken() {
        String identityUrl = this.authUrl + "/tokens";
        TokenRequest tokenRequest = new TokenRequest(tenantId, username, password);
        // 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        HttpEntity<TokenRequest> httpEntity = new HttpEntity<>(tokenRequest, headers);

        // 토큰 요청
        ResponseEntity<String> response = this.restTemplate.exchange(identityUrl, HttpMethod.POST, httpEntity, String.class);
        String body = response.getBody();
        this.tokenId = body.substring(body.indexOf("id") + 5, body.indexOf("expires") - 3);
        this.tokenExpireTime = OffsetDateTime.parse(body.substring(body.indexOf("expires") + 10, body.indexOf("tenant") - 3));
    }

    private String getUrl(@NotNull String folderName, @NotNull String objectName) {
        return this.getStorageUrl() + "/" + containerName + "/" + folderName + "/" + objectName;
    }

    private String mimeTypeCheck(byte[] bytes, String type) {
        String mimeType = tika.detect(bytes);

        if (!mimeType.startsWith(type)) {
            throw new IllegalArgumentException(type + " 타입이 아닙니다.");
        }
        return mimeType;
    }


    public String uploadObject(String folderName, final String base64String, String typeCheck) {
        if (this.tokenId == null || this.tokenExpireTime.isBefore(OffsetDateTime.now())) {
            this.requestToken();
        }
        byte[] fileData = Base64.getDecoder().decode(base64String);
        String contentType = mimeTypeCheck(fileData, typeCheck);
        String objectName = UUID.randomUUID() + "." + FileService.getExtensionFromMimeType(contentType);
        String url = this.getUrl(folderName, objectName);
        if (base64String == null) {
            return null;
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileData);

        // InputStream을 요청 본문에 추가할 수 있도록 RequestCallback 오버라이드
        final RequestCallback requestCallback = request -> {
            request.getHeaders().add("X-Auth-Token", tokenId);
            IOUtils.copy(inputStream, request.getBody());
        };

        // 오버라이드한 RequestCallback을 사용할 수 있도록 설정
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setBufferRequestBody(false);
        RestTemplate restTemplate = new RestTemplate(requestFactory);

        HttpMessageConverterExtractor<String> responseExtractor = new HttpMessageConverterExtractor<>(String.class, restTemplate.getMessageConverters());

        // API 호출
        restTemplate.execute(url, HttpMethod.PUT, requestCallback, responseExtractor);

        return storageUrl + "/" + containerName + "/" + folderName + "/" + objectName;
    }

    public void deleteObject(String imageUrl) {
        if (this.tokenId == null || this.tokenExpireTime.isBefore(OffsetDateTime.now())) {
            this.requestToken();
        }

        // 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", tokenId);
        HttpEntity<String> requestHttpEntity = new HttpEntity<>(null, headers);

        // API 호출
        this.restTemplate.exchange(imageUrl, HttpMethod.DELETE, requestHttpEntity, String.class);
    }
}
