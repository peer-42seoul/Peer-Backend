package peer.backend.service.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import peer.backend.entity.user.User;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.user.UserRepository;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;


@Slf4j
@RequiredArgsConstructor
@Service
public class ProfileImageService {

    private final UserRepository userRepository;

    @Autowired
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("https://jweepeertest.s3.ap-northeast-2.amazonaws.com/profile/basic/basicImage.png")
    private String basicImageUrl;

    private String uploadProfileImage(@NotNull MultipartFile multipartFile, String savePath) {
        // 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        metadata.setContentLength(multipartFile.getSize());
        metadata.addUserMetadata("path", savePath);

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, savePath, inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new IllegalStateException("S3 파일 업로드에 실패했습니다.");
        }
        return amazonS3.getUrl(bucket, savePath).toString();
    }

    @Transactional
    public String getProfileImageUrl(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));
        String imageUrl = user.getImageUrl();
        if (imageUrl.isEmpty())
            return basicImageUrl;
        return imageUrl;
    }


    @Transactional
    public String saveProfileImage(@NotNull MultipartFile uploadFile, Long userId) throws IOException {
        //유저와 파일이 존재하면 삭제
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));

        String url = user.getImageUrl();
        if (url != null)
            amazonS3.deleteObject(bucket, user.getImageUrl());

        //파일 경로 생성
        String fileName = uploadFile.getOriginalFilename();
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        String savePath = "profile" + File.separator + userId.toString() + File.separator + "profile" + fileExtension;

        String result = uploadProfileImage(uploadFile, savePath);
        user.setImageUrl(result);
        userRepository.save(user);

        return result;
    }

    @Transactional
    public void deleteImage(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> {
            return new NotFoundException("fail : user not found.");
        });

        String imageUrl = user.getImageUrl();
        user.setImageUrl(null);
        amazonS3.deleteObject(bucket, imageUrl);
    }

    @Transactional
    public ResponseEntity<byte[]> downloadImage(Long userId) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            return new NotFoundException("fail : user not found.");
        });
        String url = "profile/1/profile.png";
        S3Object o = amazonS3.getObject(new GetObjectRequest(bucket,url));
        S3ObjectInputStream objectInputStream = ((S3Object) o).getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);

        String fileName = URLEncoder.encode(url, "UTF-8").replaceAll("\\+", "%20");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_PNG);
        httpHeaders.setContentLength(bytes.length);
        httpHeaders.setContentDispositionFormData("attachment", fileName);

        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
    }
}
