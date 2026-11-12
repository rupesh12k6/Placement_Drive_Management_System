package org.example.placement_drive_management.service.Impl;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    /**
     * Uploads PDF to Cloudinary as resource_type=raw (PDF stored intact,
     * never transcoded). Uses rollNo as public_id so re-uploads overwrite
     * the same file — no duplicate copies.
     */
    public String uploadResume(MultipartFile file, String rollNo) throws IOException {
        Map<?, ?> result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "resource_type", "raw",
                        "folder",        "resumes",
                        "public_id",     rollNo,
                        "overwrite",     true,
                        "use_filename",  true
                )
        );
        return (String) result.get("secure_url");
    }

    public void deleteResume(String publicId) throws IOException {
        cloudinary.uploader().destroy(
                publicId,
                ObjectUtils.asMap("resource_type", "raw")
        );
    }
    /**
     * Fetches the raw bytes of a resume PDF from Cloudinary by its URL.
     * Used by the proxy endpoints to serve the file with Content-Disposition: inline.
     */
    /**
     * Returns an InputStream for the resume PDF from Cloudinary.
     * This avoids loading the entire file into memory as a byte[].
     */
    public InputStream fetchResumeStream(String resumeUrl) throws IOException {
        URL url = new URL(resumeUrl);
        return url.openStream();
    }

    public byte[] fetchResumeBytes(String resumeUrl) throws IOException {
        URL url = new URL(resumeUrl);
        try (InputStream in = url.openStream()) {
            return in.readAllBytes();
        }
    }
}