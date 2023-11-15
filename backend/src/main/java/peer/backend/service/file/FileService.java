package peer.backend.service.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import peer.backend.exception.IllegalArgumentException;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final Tika tika;

    private String mimeTypeCheck(byte[] bytes, String type) {
        String mimeType = tika.detect(bytes);

        if (!mimeType.startsWith(type)) {
            throw new IllegalArgumentException(type + " 타입이 아닙니다.");
        }
        return mimeType;
    }

    private void mimeTypeCheck(InputStream inputStream, String type) throws IOException {
        String mimeType = tika.detect(inputStream);

        if (!mimeType.startsWith(type)) {
            throw new IllegalArgumentException(type + " 타입이 아닙니다.");
        }
    }

    private static String makeFolder(String path) throws IOException {
        File folder = new File(path);
        if (!folder.exists())
            if (!folder.mkdirs())
                throw new IOException("폴더 생성에 실패했습니다.");
        return folder.getPath();
    }

    private static String getExtensionFromMimeType(String mimeType) {
        // MIME 타입을 기반으로 파일 확장자를 매핑
        switch (mimeType.toLowerCase()) {
            case "image/jpeg":
                return "jpg";
            case "image/png":
                return "png";
            case "image/gif":
                return "gif";
            case "image/bmp":
                return "bmp";
            case "image/svg+xml":
                return "svg";
            default:
                throw new IllegalArgumentException("지원하지 않는 파일유형입니다."); // 알려진 MIME 타입이 없는 경우
        }
    }

    @Transactional
    public String saveFile(MultipartFile multipartFile, String folderPath, String typeCheck) throws IOException {
        mimeTypeCheck(multipartFile.getInputStream(), typeCheck);

        String folder = makeFolder(folderPath);
        String contentType = multipartFile.getContentType();
        String extention = contentType.substring(contentType.lastIndexOf("/") + 1);
        String fileName = new StringBuilder()
                .append(folder)
                .append(File.separator)
                .append(UUID.randomUUID())
                .append(".")
                .append(extention)
                .toString();
        Path filePath = Paths.get(fileName);
        multipartFile.transferTo(filePath.toFile());

        return filePath.toString();
    }

    @Transactional
    public String saveFile(String base64String, String folderPath, String typeCheck) throws IOException {
        //base64 디코딩
        byte[] fileData = Base64.getDecoder().decode(base64String);

        //파일 타입 확인
        String contentType = mimeTypeCheck(fileData, typeCheck);
        String extension = getExtensionFromMimeType(contentType);


        String folder = makeFolder(folderPath);
        Path path = Paths.get(folder + "/" + UUID.randomUUID() + "." + extension);
        Files.write(path, fileData);
        return(path.toString());
    }

        @Transactional
    public String updateFile(MultipartFile multipartFile, String oldFilePath, String type) throws IOException {
        File oldFile = new File(oldFilePath);
        if (oldFile.exists()){
            if (!oldFile.delete())
                throw new IOException("파일을 삭제할 수 없습니다.");
        }
        String newFilePath = oldFilePath.substring(0,oldFilePath.lastIndexOf("/"));
        return saveFile(multipartFile, newFilePath, type);
    }

    public void deleteFile(String targetFilePath) throws IOException {
        File targetFile = new File(targetFilePath);
        if (!targetFile.exists())
            return;
        if(!targetFile.delete())
            throw new IOException();
    }
}
