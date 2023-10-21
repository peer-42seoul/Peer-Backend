package peer.backend.service.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import peer.backend.dto.profile.response.UserImageResponse;
import peer.backend.entity.user.User;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.user.UserRepository;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;


@Slf4j
@RequiredArgsConstructor
@Service
public class ProfileImageService {

    private final UserRepository userRepository;
    private final Tika tika;

    @Value("upload/profiles/basic/profile.png")
    private String basicImageUrl;



    @Transactional
    public String getProfileImageUrl(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));
        String imageUrl = user.getImageUrl();
        if (imageUrl.isEmpty())
            return basicImageUrl;
        return imageUrl;
    }


    private void mimeTypeCheck(InputStream inputStream, String type) throws IOException {
        String mimeType = tika.detect(inputStream);
        if (!mimeType.startsWith(type)) {
            throw new IllegalArgumentException(type + " 타입이 아닙니다.");
        }
    }

    private File makeFolder(String path) throws IOException {
        File folder = new File(path);
        if (!folder.exists())
            if (!folder.mkdirs())
                throw new IOException("폴더 생성에 실패했습니다.");
        return folder;
    }

    @Transactional
    public UserImageResponse saveProfileImage(@NotNull MultipartFile uploadFile, Long userId) throws IOException {

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("유저를 찾을 수 없습니다."));

        mimeTypeCheck(uploadFile.getInputStream(), "image");

        StringBuilder builder = new StringBuilder();
        String folderPath = builder
                .append("upload").append(File.separator)
                .append("profiles").append(File.separator)
                .append(userId.toString())
                .toString();
        File folder = makeFolder(folderPath);


        String uploadFileName = uploadFile.getOriginalFilename();
        String fileName = builder
                .append(File.separator)
                .append("profile")
                .append(uploadFileName.substring(uploadFileName.lastIndexOf(".")))
                .toString();
        Path filePath = Paths.get(fileName);
        uploadFile.transferTo(filePath.toFile());

        user.setImageUrl(filePath.toUri().toString());

        return new UserImageResponse(user.getImageUrl());
    }

    public void deleteProfileIamge(Long userId) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("유저를 찾을 수 없습니다."));
        String path = user.getImageUrl();
        if (path == null)
            return ;
        File file = new File(path);
        if (!file.exists()) {
            user.setImageUrl(null);
            return;
        }
        if (!file.delete())
            throw new IOException("파일을 삭제할 수 없습니다.");
        user.setImageUrl(null);
    }



//    @Transactional
//    public ResponseEntity<byte[]> downloadImage(Long userId) throws IOException {
//        User user = userRepository.findById(userId).orElseThrow(() ->
//            new NotFoundException("fail : user not found."));
//        String url = "profile/1/profile.png";
//        S3Object o = amazonS3.getObject(new GetObjectRequest(bucket,url));
//        S3ObjectInputStream objectInputStream = ((S3Object) o).getObjectContent();
//        byte[] bytes = IOUtils.toByteArray(objectInputStream);
//
//        String fileName = URLEncoder.encode(url, "UTF-8").replaceAll("\\+", "%20");
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.setContentType(MediaType.IMAGE_PNG);
//        httpHeaders.setContentLength(bytes.length);
//        httpHeaders.setContentDispositionFormData("attachment", fileName);
//
//        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
//    }



//    private String uploadProfileImage(@NotNull MultipartFile multipartFile, String savePath) {
//        // 메타데이터 설정
//        ObjectMetadata metadata = new ObjectMetadata();
//        metadata.setContentType(multipartFile.getContentType());
//        metadata.setContentLength(multipartFile.getSize());
//        metadata.addUserMetadata("path", savePath);
//
//        try (InputStream inputStream = multipartFile.getInputStream()) {
//            amazonS3.putObject(new PutObjectRequest(bucket, savePath, inputStream, metadata)
//                    .withCannedAcl(CannedAccessControlList.PublicRead));
//        } catch (IOException e) {
//            throw new IllegalStateException("S3 파일 업로드에 실패했습니다.");
//        }
//        return amazonS3.getUrl(bucket, savePath).toString();
//    }


//    @Transactional
//    public void deleteImage(Long userId){
//        User user = userRepository.findById(userId).orElseThrow(() -> {
//            throw new NotFoundException("fail : user not found.");
//        });
//
//        String imageUrl = user.getImageUrl();
//        user.setImageUrl(null);
//        amazonS3.deleteObject(bucket, imageUrl);
//    }


}
